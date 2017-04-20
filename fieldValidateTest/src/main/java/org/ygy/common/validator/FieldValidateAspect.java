package org.ygy.common.validator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.Validate;
import org.ygy.common.validator.bean.ValidateResult;
import org.ygy.common.validator.handler.IValidateRuleHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component
@Aspect
public class FieldValidateAspect {
    
    @Pointcut("@annotation(org.ygy.common.validator.bean.Validate)")
    private void controllerMethod(){};
    
    @After("controllerMethod()")
    public void controllerAfter() {
    	try {
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("方法后-----------------------------");
				}
			}).start();
		} catch (Exception e) {
		}
    }

    @Around("controllerMethod()")
    public Object controllerAround(ProceedingJoinPoint point){
    	long startTime = System.currentTimeMillis();
        Object[] params = null;//入参
        Object returnValue = null;//返回值 
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        try {
            params = point.getArgs();
            request = ValidateContext.getRequest();
            // 目标类
            Class<? extends Object> targetClazz = point.getTarget().getClass();
            // 目标方法
            Method targetMethod = ((MethodSignature) point.getSignature()).getMethod();
            Validate validate = targetMethod.getAnnotation(Validate.class);
            // 复杂对象
            JSONObject jSONObject = null;
            if ( !ValidateContext.getIsFromParam()) {
                jSONObject = (com.alibaba.fastjson.JSONObject) JSONObject.toJSON(params[0]);
            }
            // xss非法字符过滤
            this.filterIllegalCharater(params, jSONObject);
            
            // 获取校验规则
            List<ValidateExpItemInfo> validateExpInfo = this.getValidateExpInfo(validate, targetClazz, targetMethod);
            
            // 字段校验
            Object fieldValue = null;
            boolean validateAll = getValidateAll(validate.all());
            List<ValidateResult> validateResults = new ArrayList<ValidateResult>();//存放字段校验不通过的结果信息
            for (ValidateExpItemInfo validateExpItemInfo : validateExpInfo) {
            	boolean pass = true;
            	if (validateExpItemInfo.isFormatCorrect()) {// 只处理符合大致校验格式的字段校验
                	// 获取要校验的字段值
                	fieldValue = this.getFieldValue(jSONObject,validateExpItemInfo,request);
                    // 判空必要性处理
                    /**
                     * "!!".equals(ruleInfo.getNotNull())--单个校验规则强制要求非空
                     * (validate.must() && !"!".equals(ruleInfo.getNotNull()))--全部设置为要求非空，单个设置为可以空优先级更高
                     * 建议-传参JAVA对应类型要求为非基本类型（换成其包装类）
                     * 对象类型初始值为null
                     */
                    if ( "!!".equals(validateExpItemInfo.getNotNull()) || (validate.must() && !"!".equals(validateExpItemInfo.getNotNull())) ) {
                        if ( null == fieldValue || "".equals(fieldValue.toString().trim())) {
                            pass = false;
                        }
                    }
                    // 具体字段校验
                    if (pass) {
                    	pass = this.fieldValidate(validateExpItemInfo, fieldValue);
                    }
                    // 校验不通过信息收集
                    if (!pass) {
                        ValidateResult validateResult = new ValidateResult();
                        validateResult.setValidateExpItemInfo(validateExpItemInfo);
                        validateResult.setFieldValue(fieldValue);           
                        validateResults.add(validateResult);
                    }
                } 
                if (!validateAll) {
                    if (!pass) {//一旦发现不符合校验规则的字段，直接报错，不再校验后续字段
                        break;
                    }
                }
            }
            System.out.println("----校验耗时------"+(System.currentTimeMillis()-startTime));
            // 校验不通过处理
            if (0 != validateResults.size()) {
                response = ValidateContext.getResponse();
                request.setAttribute("validateResults", validateResults);
                String handler  = validate.handler();
                if (null == handler || "".equals(handler)) {
                	handler = ValidateContext.getValidateErrorHandlerUrl();
                }
                request.getRequestDispatcher("/"+handler).forward(request, response);
                return null;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        //校验通过，继续后续的业务处理
        try {
            returnValue = point.proceed(params);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    /**
     * 获取是否进行全字段校验（当校验失败后）
     * @param all
     * @return
     */
    private boolean getValidateAll(String all) {
        if ("true".equals(all.trim())) {//1、注解优先
        	return true;
        } else if ("false".equals(all.trim())) {
        	return false;
        }
        return ValidateContext.getValidateAll();//2、全局配置其次
	}

	/**
     * 过滤掉非法字符
     * @param validate
     * @param params
     * @param jSONObject
     */
	private void filterIllegalCharater(Object[] params, JSONObject jSONObject) {
		boolean isFromParam = ValidateContext.getIsFromParam();// 请求参数是否可以通过getParameter获取
		if (!isFromParam && ValidateContext.getFilter()) {// 如果是已经在过滤器ValidateContextFilter中处理了，否则在这里处理
			SimpleUtil.filterIllegalCharaterFromJSONObj(jSONObject, 0);
			params[0] = JSON.parseObject(jSONObject.toJSONString(), params[0].getClass());
		}
	}
	
	/**
	 * 获取规则表达式对应的ValidateExpInfo
	 * @param validate
	 * @param targetMethod
	 * @return
	 */
    private List<ValidateExpItemInfo> getValidateExpInfo(Validate validate, Class<? extends Object> targetClazz, Method targetMethod) {
    	String url = validate.url();
        if ("".equals(url)) {
        	RequestMapping cReqMapping = (RequestMapping) targetClazz.getAnnotation(RequestMapping.class);
            RequestMapping mReqMapping = targetMethod.getAnnotation(RequestMapping.class);
            url = cReqMapping.value()[0] + "/" + mReqMapping.value()[0];
            // 去掉多余斜杠"/",如URL为: person/getById 或  person//getById 都是指向同一资源的有效URI（在浏览器看来）
            url = url.replaceAll("/{2,}", "/");
        }
        String validateExp = validate.value();
        List<ValidateExpItemInfo> validateExpParseResult = null;
        if (ValidateContext.getCache()) {//从缓存中获取，没有则解析再缓存
        	validateExpParseResult = ValidateContext.getValidateExpParseResultFromCache(url);
        	if (null == validateExpParseResult) {
        		if ("".equals(validateExp)) {
                	validateExp = ValidateContext.getValidateExp(url);
                }
        		if ("".equals(validateExp)) {//即没有在@Validate注解中填写校验表达式，配置文件中也没有
        			return new ArrayList<ValidateExpItemInfo>();
        		}
        		validateExpParseResult = SimpleUtil.parseValidateExpression(validateExp);
        		ValidateContext.saveValidateExp(url, validateExp);
        		ValidateContext.cacheValidateExpParseResult(url, validateExpParseResult);
            }
        } else {
        	if ("".equals(validateExp)) {
            	validateExp = ValidateContext.getValidateExp(url);
            }
    		if ("".equals(validateExp)) {//即没有在@Validate注解中填写校验表达式，配置文件中也没有
    			return new ArrayList<ValidateExpItemInfo>();
    		}
    		validateExpParseResult = SimpleUtil.parseValidateExpression(validateExp);
    		ValidateContext.saveValidateExp(url, validateExp);
        }
        return validateExpParseResult;
	}
    
	/**
     * 获取要校验的字段值
     * @return
     */
    private Object getFieldValue(JSONObject jSONObject,ValidateExpItemInfo ruleInfo, HttpServletRequest request) {
    	Object fieldValue = null;
    	if ( !ValidateContext.getIsFromParam()) {//非表单传值
            fieldValue = SimpleUtil.getFromJSONObject(ruleInfo.getField(), jSONObject);
        } else {
        	fieldValue = request.getParameter(ruleInfo.getField());
        }
		return fieldValue;
	}

	/**
     * 字段校验
     * @param itemInfo
     * @param fieldValue
     * @return
     */
	private boolean fieldValidate(ValidateExpItemInfo itemInfo, Object fieldValue) {
		try {
    		IValidateRuleHandler validateRuleHandler = ValidateContext.getValidateRuleHandler(itemInfo.getRuleType());
    		return validateRuleHandler.validate(itemInfo, fieldValue);
    	} catch (Exception e) {
    		System.out.println(itemInfo.getRuleType()+"型校验规则，没有相应校验处理类："+e);
		}
		return true;
	}

}
package org.ygy.common.validator;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;

@Component
@Aspect
public class FieldValidateAspect {
    
    private static Properties config = null;
    private static final String DEFAULT_VALIDATE_ERROR_HANDLER_URL = "defaultValidateErrorHandler";
    private static Map<String,IValidateRuleHandler> validateRuleHandlerList = new HashMap<String,IValidateRuleHandler>();
    
    @Pointcut("@annotation(org.ygy.common.validator.Validate)")
    private void controllerMethod(){};

    @Around("controllerMethod()")
    public Object controllerAround(ProceedingJoinPoint point){
        
        Object[] params = null;//入参
        Object returnValue = null;  //返回值 
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        try {
            params = point.getArgs();
            request = SysContext.getRequest();
            //方法上的注解
            Method method = ((MethodSignature) point.getSignature()).getMethod();
            Validate validate = method.getAnnotation(Validate.class);
            
            //获取当某个字段校验失败后、是否进行后续校验的配置信息
            boolean validateAll = validate.all();
            
            if (null == config) {
            	this.validateConfig();
            }
            //从注解上获取校验规则，否则从配置文件中获取校验规则，并去掉所有空格          
            String validateRule  = validate.value();
            if ("".equals(validateRule)) {
                String url = validate.url();
                if ("".equals(url)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    url = requestMapping.value()[0];
                }
                validateRule = config.getProperty(url).replaceAll(" ", "");
            }
            String[] fieldRules = validateRule.split("\\|");
            boolean pass = true;
            Object filedValue = null;
            List<ValidateResult> validateResults = new ArrayList<ValidateResult>();
            JSONObject jSONObject = null;
            for (String fieldRule : fieldRules){
                RuleInfo ruleInfo = this.parseRule(fieldRule);
                if (ruleInfo.isFormatCorrect()) {// 只处理符合大致校验格式的字段校验
                    if (validate.isComplexObject()) {//复杂对象
                        if (null == jSONObject) {
                            jSONObject = (com.alibaba.fastjson.JSONObject) JSONObject.toJSON(params[0]);//复杂对象解析
                        }
                        filedValue = jSONObject.get(ruleInfo.getFiled());
                    } else {
                        if ( ruleInfo.isRequestParamRule() ) {
                            filedValue = request.getParameter(ruleInfo.getFiled());
                        } else {
                            filedValue = params[ruleInfo.getPosition()];
                        }
                    }
                    /**
                     * "!!".equals(ruleInfo.getNotNull())--单个校验规则强制要求非空
                     * (validate.must() && !"!".equals(ruleInfo.getNotNull()))--全部设置为要求非空，单个设置为可以空优先级更高
                     * 建议-传参JAVA对应类型要求为非基本类型，因为基本类型即使没有传值也会被置初值，对象类型为null
                     */
                    if ( "!!".equals(ruleInfo.getNotNull()) || (validate.must() && !"!".equals(ruleInfo.getNotNull())) ) {
                        if ( null == filedValue || "".equals(filedValue.toString().trim())) {
                            pass = false;
                        }
                    }
                    if (pass) {
                        switch (ruleInfo.getRuleType()) {
                        case "must":
                            pass = this.mustValidate(ruleInfo, filedValue, validate);
                            break;
                        case "int":
                            pass = this.intValidate(ruleInfo, filedValue, validate);
                            break;
                        case "strin":
                            pass = this.strinValidate(ruleInfo, filedValue, validate);
                            break;
                        case "len":
                            pass = this.lenValidate(ruleInfo, filedValue, validate);
                            break;
                        case "date":
                            pass = this.dateFormatValidate(ruleInfo, filedValue, validate);
                            break;
                        case "reg":
                            pass = this.regValidate(ruleInfo, filedValue, validate);
                            break;
                        case "long":
                            pass = this.longValidate(ruleInfo, filedValue, validate);
                            break;
                        case "float":
                            pass = this.floatValidate(ruleInfo, filedValue, validate);
                            break;
                        case "double":
                            pass = this.doubleValidate(ruleInfo, filedValue, validate);
                            break;
                        default:// 自定义规则校验
                            pass = this.otherValidate(ruleInfo, filedValue, validate);
                            break;
                        }
                    }
                    if (!pass) {
                        ValidateResult validateResult = new ValidateResult();
                        validateResult.setFiled(ruleInfo.getFiled());
                        validateResult.setValue(filedValue);
                        validateResult.setRule(fieldRule);
                        validateResult.setResultCode("2");//校验不通过                      
                        validateResults.add(validateResult);
                    }
                } 
                if (!validateAll) {
                    if (!pass) {//一旦发现不符合校验规则的字段，直接报错，不再校验后续字段
                        break;
                    }
                }
            }
            if (0 != validateResults.size()) {
                response = SysContext.getResponse();
                request.setAttribute("validateResults", validateResults);
                String handler  = validate.handler();
                if (null == handler || "".equals(handler)) {
                	if (null != config) {
                		handler = config.getProperty("validateErrorHandlerUrl");
                	}
                	if (null == handler || "".equals(handler)) {
                		handler = DEFAULT_VALIDATE_ERROR_HANDLER_URL;
                	}
                }
                request.getRequestDispatcher("/"+handler).forward(request, response);
                return null;
            }
        } catch (Exception e) {
        }
        try {
            returnValue = point.proceed(params);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return returnValue;
    }

	/**
     * 必要性校验
     * @param ruleInfo
     * @param filedValue
	 * @param validate 
     * @return
     */
    private boolean mustValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        if (ruleInfo.getRuleContent() != null && ruleInfo.getRuleContent().length > 0) {
            if ( "true".equalsIgnoreCase(ruleInfo.getRuleContent()[0]) ) {
                if ( filedValue== null || "".equals(filedValue.toString().trim())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * int型数据校验
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean intValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            if (null == filedValue) {
                return true;
            }
            Integer value = null;
            if ( validate.isComplexObject()) {
                value = (Integer) filedValue;
            } else {
                if (ruleInfo.isRequestParamRule()) {
                    try {
                        value = Integer.parseInt((String) filedValue);
                    } catch (Exception e) {
                        return false;//非int
                    }
                } else {
                    value = (Integer) filedValue;
                }
            }
            int min = 0;
            int max = 0;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示int最小值               
                min = Integer.MIN_VALUE;
            } else {
                min = Integer.parseInt(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {//M表示int最大值             
                max = Integer.MAX_VALUE;
            } else {
                max = Integer.parseInt(ruleInfo.getRuleContent()[1]);
            }
            
            if ("(".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value >= max) {
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value > max) {
                        return false;
                    }
                }
            } else if ("[".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value >= max) {
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value > max) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 校验String型数据是否为规定中的某一个
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean strinValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            if (ruleInfo.getRuleContent() != null && ruleInfo.getRuleContent().length > 1
                    && null != filedValue && !"".equals(filedValue)) {
                return this.contains(ruleInfo.getRuleContent(), (String)filedValue);
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * len长度校验
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean lenValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            int len = Integer.parseInt(ruleInfo.getRuleContent()[0]);
            if (len > 0 && len != ((String)filedValue).length() ){
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * date日期格式校验
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean dateFormatValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(ruleInfo.getRuleContent()[0]);
            try {
                if (filedValue != null && !"".equals(filedValue.toString().trim())) {
                    dateFormat.parse((String) filedValue);
                }
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * reg正则匹配校验
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean regValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            if (null == filedValue) {
                Pattern pattern = Pattern.compile(ruleInfo.getRuleContent()[0]);
                Matcher matcher = pattern.matcher((String)filedValue);
                return matcher.matches();
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * long型数据校验
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean longValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            if (null == filedValue) {
                return true;
            }
            Long value = null;
            if ( validate.isComplexObject() ) {
                value = (Long) filedValue;
            } else {
                if (ruleInfo.isRequestParamRule()) {
                    try {
                        value = Long.parseLong((String) filedValue);
                    } catch (Exception e) {
                        return false;//非long
                    }
                } else {
                    value = (Long) filedValue;
                }
            }
            long min = 0L;
            long max = 0L;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示long最小值                
                min = Long.MIN_VALUE;
            } else {
                min = Long.parseLong(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {// M表示long最大值              
                max = Long.MAX_VALUE;
            } else {
                max = Long.parseLong(ruleInfo.getRuleContent()[1]);
            }
            if ("(".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value >= max) {
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value > max) {
                        return false;
                    }
                }
            } else if ("[".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value >= max) {
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value > max) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * float型数据校验
     * @param ruleInfo
     * @param filedValue
     * @param validate
     * @return
     */
    private boolean floatValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            if (null == filedValue) {
                return true;
            }
            Float value = null;
            if ( validate.isComplexObject() ) {
                value = (Float) filedValue;
            } else {
                if (ruleInfo.isRequestParamRule()) {
                    try {
                        value = Float.parseFloat((String) filedValue);
                    } catch (Exception e) {
                        return false;//非float
                    }
                } else {
                    value = (Float) filedValue;
                }
            }
            float min = 0L;
            float max = 0L;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示float最小值              
                min = Float.MIN_VALUE;
            } else {
                min = Float.parseFloat(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {// M表示float最大值               
                max = Float.MAX_VALUE;
            } else {
                max = Float.parseFloat(ruleInfo.getRuleContent()[1]);
            }
            if ("(".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value >= max) {
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value > max) {
                        return false;
                    }
                }
            } else if ("[".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value >= max) {
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value > max) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * double型数据校验
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean doubleValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
        try {
            if (null == filedValue) {
                return true;
            }
            double value = 0.0;
            if ( validate.isComplexObject() ) {
                value = (Double) filedValue;
            } else {
                if (ruleInfo.isRequestParamRule()) {
                    try {
                        value = Double.parseDouble((String) filedValue);
                    } catch (Exception e) {
                        return false;//非double
                    }
                } else {
                    value = (Double) filedValue;
                }
            }
            double min = 0.0;
            double max = 0.0;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示double最小值                
                min = Double.MIN_VALUE;
            } else {
                min = Double.parseDouble(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {// M表示double最大值               
                max = Double.MAX_VALUE;
            } else {
                max = Double.parseDouble(ruleInfo.getRuleContent()[1]);
            }
            double exp = 10E-10;
            if ("(".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) <= exp || Math.abs(max - value) <= exp){
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) <= exp || Math.abs(max - value) < exp){
                        return false;
                    }
                }
            } else if ("[".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) < exp || Math.abs(max - value) <= exp){
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) < exp || Math.abs(max - value) < exp){
                        return false;
                    }
                }
            }
        } catch (Exception e) {
        }
        return true;
    }
    
    /**
     * 用户自定义规则校验
     * @param ruleInfo
     * @param filedValue
     * @param validate 
     * @return
     */
    private boolean otherValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
    	try {
    		IValidateRuleHandler validateRuleHandler = validateRuleHandlerList.get(ruleInfo.getRuleType());
    		return validateRuleHandler.customValidate(ruleInfo, filedValue, validate);
    	} catch (Exception e) {
    		System.out.println(ruleInfo.getRuleType()+"没有相应校验处理类："+e);
		}
		return true;
	}

    /**
     * 将字串形式的规则解析成RuleInfo对象
     * @param fieldRule
     * @return
     */
    private RuleInfo parseRule(String fieldRule) {
        RuleInfo ruleInfo = new RuleInfo();
        try {
            String[] arr = fieldRule.split(":");
            String rule = null;
            if (arr.length == 2) {
                if (this.isInteger(arr[0])) {
                    ruleInfo.setRequestParamRule(false);
                    ruleInfo.setPosition(Integer.parseInt(arr[0]));
                    rule = arr[1];
                } else {
                	ruleInfo.setRequestParamRule(true);
                    ruleInfo.setFiled(arr[0]);
                }
                rule = arr[1];
            } else if (arr.length == 3) {
            	ruleInfo.setRequestParamRule(false);
                ruleInfo.setFiled(arr[0]);
                ruleInfo.setPosition(Integer.parseInt(arr[1]));
                rule = arr[2];
            } else {
                ruleInfo.setFormatCorrect(false);
                return ruleInfo;
            }
            int leftIndex = rule.indexOf("(");
            if (leftIndex > 0) {
                ruleInfo.setLeftSeparate("(");
            } else {
                leftIndex = rule.indexOf("[");
                if (leftIndex > 0) {
                    ruleInfo.setLeftSeparate("[");
                } else {
                    ruleInfo.setFormatCorrect(false);
                    return ruleInfo;
                }
            }
            int rightIndex = rule.indexOf(")");
            if (rightIndex > 0) {
                ruleInfo.setRightSeparate(")");
            } else {
                rightIndex = rule.indexOf("]");
                if (rightIndex > 0) {
                    ruleInfo.setRightSeparate("]");
                } else {
                    ruleInfo.setFormatCorrect(false);
                    return ruleInfo;
                }
            }     
            String ruleType = rule.substring(0, leftIndex).toLowerCase();//转换成小写，即忽略大小写
            ruleInfo.setRuleType(ruleType);    
            ruleInfo.setRuleContent(rule.substring(leftIndex+1, rightIndex).replaceAll("\"", "").replaceAll("'", "").split(","));
            ruleInfo.setNotNull(rule.substring(rightIndex+1).replaceAll(" ", ""));
        } catch (Exception e) {
            ruleInfo.setFormatCorrect(false);
        }
        return ruleInfo;
    }
    
    /**
     * 判断字串是否在字串数组中
     * @param stringArray
     * @param str
     * @return
     */
    private  boolean contains(String[] stringArray, String str) {
        List<String> tempList = Arrays.asList(stringArray);
        if(tempList.contains(str)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 判断字符串是否是整数
     */
    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    /**
     * 获取配置文件信息并做相应解析
     */
    private void validateConfig() {
    	try {
          String validateConfigFile = SysContext.getValidateConfigFile();
          if ( validateConfigFile != null && !"".equals(validateConfigFile) ) {
              InputStream in = FieldValidateAspect.class.getClassLoader().getResourceAsStream(validateConfigFile);
              if (null == in) {
            	  System.out.println(validateConfigFile+"读取失败");
              }
              config = new Properties();
              config.load(in);
          }
          if (config != null) {
        	  String addRule = config.getProperty("addRule");
        	  if ( addRule != null && !"".equals(addRule) ) {
        		  String[] rules = addRule.replaceAll(" ", "").split(",");
	        	  for (int i=0;i<rules.length;i++) {
	        		  String className = config.getProperty(rules[i]);
	        		  try {
						Class<?> clazz = Class.forName(className);
						validateRuleHandlerList.put(rules[i], (IValidateRuleHandler) clazz.newInstance());
	        		  } catch (Exception e) {
	        		  }
	        	  }	
        	  }
          }
      } catch (IOException e) {
          System.out.println("@Validate注解相关properties配置文件读取失败");
      }
    }

}
package org.ygy.common.validator;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.ygy.common.validator.bean.Validate;
import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class ValidateScanBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		Class<?> clazz = bean.getClass();
		RequestMapping cReqMapping = AnnotationUtils.findAnnotation(clazz,RequestMapping.class);
		String curl = "";
		if (null != cReqMapping) {
			curl = cReqMapping.value()[0];
		}
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
		if (methods != null) {
			for (Method method : methods) {
				Validate validate = AnnotationUtils.findAnnotation(method, Validate.class);
				if (null != validate) {
					String validateExp = validate.value();
					if (org.springframework.util.StringUtils.isEmpty(validateExp)) {
						continue;
					}
					RequestMapping mReqMapping = AnnotationUtils.findAnnotation(method,RequestMapping.class);
					String url = null;
					if (null != mReqMapping) {
						url =  curl + "/" + mReqMapping.value()[0];
						// 去掉多余斜杠"/",如URL为: person/getById 或  person//getById 都是指向同一资源的有效URI（在浏览器看来）
						url = url.replaceAll("/{2,}", "/");
					}
					System.out.println("-------------------\r\n"+validateExp);
		            System.out.println(url);
		            if (!org.springframework.util.StringUtils.isEmpty(url)) {
		            	//解析校验表达式，并构建URL与校验表达式的关系
		            	List<ValidateExpItemInfo> validateExpParseResult = SimpleUtil.parseValidateExpression(validateExp);
		            	ValidateContext.cacheValidateExpParseResult(url, validateExpParseResult);
		            }
				}
			}
		}
		return bean;
	}

}

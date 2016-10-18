package org.ygy.common.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
	/**
	 * 校验规则
	 * @return
	 */
    String value() default "";
    /**
     * 校验url,校验规则可以通过配置文件配置，但如果当前注解中存在校验规则，则优先使用，否则需要配置url，根据url去配置文件获取校验规则
     * @return
     */
    String url() default "";
    /**
     * 是否进行所有规则的校验，默认false,即当某一个校验不通过后，不再进行后续校验
     * @return
     */
    boolean all() default false;
    /**
     * 当校验不通过后的处理方式
     * @return
     */
    String handler() default "";
    
    /**
     * 入参是否复杂对象
     * @return
     */
    boolean isComplexObject() default false;
    
    /**
     * 字段必须非""、非null
     * @return
     */
    boolean must() default false;
}
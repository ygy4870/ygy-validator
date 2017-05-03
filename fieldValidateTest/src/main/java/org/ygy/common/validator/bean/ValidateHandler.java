package org.ygy.common.validator.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如果controller方法上的参数使用了该注解，则校验不通过的处理不进入统一处理，
 * 可通过该参数获取校验结果信息ArrayList<ValidateResult>
 * @author ygy
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateHandler {}

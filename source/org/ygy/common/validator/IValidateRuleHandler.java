package org.ygy.common.validator;

public interface IValidateRuleHandler {

    /**
     * 用户自定义校验规则
     * @param ruleInfo
     * @param filedValue
     * @param validate
     * @return
     */
	public boolean customValidate(RuleInfo ruleInfo, Object filedValue, Validate validate);
}
package ygy.test.fieldValidateTest.controller;

import org.ygy.common.validator.ValidateContext;
import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;
import org.ygy.common.validator.handler.IValidateRuleHandler;

public class AbsHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult(); 
		try {
			int value = 0;
			try {
				value = Integer.parseInt((String) fieldValue);
			} catch (Exception e) {
				try {
					value = (Integer) fieldValue;
				} catch (Exception e1) {
					result.setSuccess(false);
					result.setMsg("校验不通过");
					return result;
				}
			}
			int validateValue = Integer.parseInt(ruleInfo.getRuleContent()[0]);
			if (!(Math.abs(value) == validateValue)) {
				result.setSuccess(false);
				result.setMsg("校验不通过");
			}
        } catch (Exception e) {
        	e.printStackTrace();
        	result.setSuccess(false);
        	result.setMsg("校验规则书写错误");
        }
		return result;
	}

}

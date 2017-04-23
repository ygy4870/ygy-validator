package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class MustHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult(); 
		if (ruleInfo.getRuleContent() != null && ruleInfo.getRuleContent().length > 0) {
            if ( "true".equalsIgnoreCase(ruleInfo.getRuleContent()[0]) ) {
                if ( fieldValue== null || "".equals(fieldValue.toString().trim())) {
                	result.setSuccess(false);
                    result.setMsg("校验不通过");
                }
            }
        }
        return result;
	}

}

package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class MustHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		if (ruleInfo.getRuleContent() != null && ruleInfo.getRuleContent().length > 0) {
            if ( "true".equalsIgnoreCase(ruleInfo.getRuleContent()[0]) ) {
                if ( fieldValue== null || "".equals(fieldValue.toString().trim())) {
                    return false;
                }
            }
        }
        return true;
	}

}

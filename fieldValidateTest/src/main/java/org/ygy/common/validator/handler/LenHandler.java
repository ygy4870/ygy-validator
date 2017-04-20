package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class LenHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		try {
			if (null == fieldValue) {
				return true;
			}
			int len1 = Integer.parseInt(ruleInfo.getRuleContent()[0]);
            int len2 = Integer.parseInt(ruleInfo.getRuleContent()[1]);
            int len = ((String)fieldValue).length();
            if ( len < len1 || len > len2 ){//
                return false;
            }
        } catch (Exception e) {
        	return false;
        }
        return true;
	}

}

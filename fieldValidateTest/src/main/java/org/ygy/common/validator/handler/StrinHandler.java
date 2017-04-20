package org.ygy.common.validator.handler;

import org.ygy.common.validator.SimpleUtil;
import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class StrinHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		try {
            if (ruleInfo.getRuleContent() != null && ruleInfo.getRuleContent().length > 0
                    && null != fieldValue && !"".equals(fieldValue)) {
                return SimpleUtil.contains(ruleInfo.getRuleContent(), (String)fieldValue);
            }
        } catch (Exception e) {
        	System.out.println(e);
        	return false;
        }
        return true;
	}

}

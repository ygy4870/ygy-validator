package org.ygy.common.validator.handler;

import java.text.SimpleDateFormat;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class DateHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(ruleInfo.getRuleContent()[0]);
            try {
                if (fieldValue != null && !"".equals(fieldValue.toString().trim())) {
                    dateFormat.parse((String) fieldValue);
                }
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
        }
        return true;
	}

}

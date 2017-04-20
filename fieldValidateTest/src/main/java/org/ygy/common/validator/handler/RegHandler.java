package org.ygy.common.validator.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class RegHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		try {
            if (null != fieldValue) {
                Pattern pattern = Pattern.compile(ruleInfo.getRuleContent()[0]);
                Matcher matcher = pattern.matcher((String)fieldValue);
                return matcher.matches();
            }
        } catch (Exception e) {
        	return false;
        }
        return true;
	}

}

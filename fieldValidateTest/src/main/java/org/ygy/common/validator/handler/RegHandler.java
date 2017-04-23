package org.ygy.common.validator.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class RegHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult();
		try {
            if (null != fieldValue) {
                Pattern pattern = Pattern.compile(ruleInfo.getRuleContent()[0]);
                Matcher matcher = pattern.matcher((String)fieldValue);
                boolean ok = matcher.matches();
                result.setSuccess(ok);
                if (!ok) {
                	result.setMsg("校验不通过");
                }
            }
        } catch (Exception e) {
        	System.out.println(e);
        	result.setSuccess(false);
        	result.setMsg("校验规则书写错误");
        }
        return result;
	}

}

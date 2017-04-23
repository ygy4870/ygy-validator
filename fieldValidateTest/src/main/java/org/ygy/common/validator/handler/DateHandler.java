package org.ygy.common.validator.handler;

import java.text.SimpleDateFormat;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class DateHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult();
		try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(ruleInfo.getRuleContent()[0]);
            try {
                if (fieldValue != null && !"".equals(fieldValue.toString().trim())) {
                    dateFormat.parse((String) fieldValue);
                }
            } catch (Exception e) {
            	result.setSuccess(false);
            	result.setMsg("校验不通过");
            }
        } catch (Exception e) {
        	System.out.println(e);
        	result.setSuccess(false);
        	result.setMsg("校验规则书写错误");
        }
        return result;
	}

}

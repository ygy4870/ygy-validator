package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class LenHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult();
		try {
			if (null != fieldValue) {
				int len1 = Integer.parseInt(ruleInfo.getRuleContent()[0]);
	            int len2 = Integer.parseInt(ruleInfo.getRuleContent()[1]);
	            int len = ((String)fieldValue).length();
	            if ( len < len1 || len > len2 ){
	            	result.setSuccess(false);
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

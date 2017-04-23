package org.ygy.common.validator.handler;

import org.ygy.common.validator.SimpleUtil;
import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class StrinHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult(); 
		try {
            if (ruleInfo.getRuleContent() != null && ruleInfo.getRuleContent().length > 0
                    && null != fieldValue && !"".equals(fieldValue)) {
                boolean ok = SimpleUtil.contains(ruleInfo.getRuleContent(), (String)fieldValue);
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

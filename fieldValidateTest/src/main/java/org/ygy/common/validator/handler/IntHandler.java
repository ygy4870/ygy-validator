package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class IntHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult();
		try {
            if (null == fieldValue) {
                return result;
            }
            Integer value = null;
            try {
            	value = (Integer) fieldValue;
			} catch (Exception e1) {
				try {
                    value = Integer.parseInt((String) fieldValue);
                } catch (Exception e2) {
                	result.setSuccess(false);//非int
                	result.setMsg("校验不通过");
                    return result;
                }
			}
            int min = 0;
            int max = 0;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示int最小值               
                min = Integer.MIN_VALUE;
            } else {
                min = Integer.parseInt(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {//M表示int最大值             
                max = Integer.MAX_VALUE;
            } else {
                max = Integer.parseInt(ruleInfo.getRuleContent()[1]);
            }
            
            if ("(".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value >= max) {
                    	result.setSuccess(false);
                    	result.setMsg("校验不通过");
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value > max) {
                    	result.setSuccess(false);
                    	result.setMsg("校验不通过");
                    }
                }
            } else if ("[".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value >= max) {
                    	result.setSuccess(false);
                    	result.setMsg("校验不通过");
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value > max) {
                    	result.setSuccess(false);
                    	result.setMsg("校验不通过");
                    }
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

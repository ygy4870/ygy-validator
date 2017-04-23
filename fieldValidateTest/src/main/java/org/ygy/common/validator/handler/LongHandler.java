package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class LongHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult();
		try {
            if (null == fieldValue) {
                return result;
            }
            Long value = null; 
            try {
            	value = (Long) fieldValue;
			} catch (Exception e1) {
				try {
                    value = Long.parseLong((String) fieldValue);
                } catch (Exception e2) {
                	result.setSuccess(false);//非long
                	result.setMsg("校验不通过");
                    return result;
                }
			}
            long min = 0L;
            long max = 0L;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示long最小值                
                min = Long.MIN_VALUE;
            } else {
                min = Long.parseLong(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {// M表示long最大值              
                max = Long.MAX_VALUE;
            } else {
                max = Long.parseLong(ruleInfo.getRuleContent()[1]);
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

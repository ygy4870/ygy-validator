package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class LongHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		try {
            if (null == fieldValue) {
                return true;
            }
            Long value = null; 
            try {
            	value = (Long) fieldValue;
			} catch (Exception e1) {
				try {
                    value = Long.parseLong((String) fieldValue);
                } catch (Exception e2) {
                    return false;//非long
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
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value <= min || value > max) {
                        return false;
                    }
                }
            } else if ("[".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value >= max) {
                        return false;
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (value < min || value > max) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
        	return false;
        }
        return true;
	}

}

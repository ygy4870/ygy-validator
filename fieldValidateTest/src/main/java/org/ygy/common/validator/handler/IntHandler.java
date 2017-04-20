package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class IntHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		try {
            if (null == fieldValue) {
                return true;
            }
            Integer value = null;
            try {
            	value = (Integer) fieldValue;
			} catch (Exception e1) {
				try {
                    value = Integer.parseInt((String) fieldValue);
                } catch (Exception e2) {
                    return false;//非int
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

package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class FloatHandler implements IValidateRuleHandler{

	@Override
	public boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		try {
            if (null == fieldValue) {
                return true;
            }
            float value = 0.0f;
            try {
            	if (fieldValue instanceof Integer) {//10的形式
            		value = ((Integer) fieldValue).floatValue();
            	} else if (fieldValue instanceof Float) {//10.0的形式
            		value = (Float)fieldValue;
            	} else {
            		value = Float.parseFloat((String) fieldValue);
            	}
			} catch (Exception e1) {
                return false;//非float
			}
            try {
            	value = (Float) fieldValue;
			} catch (Exception e1) {
				try {
                    value = Float.parseFloat((String) fieldValue);
                } catch (Exception e2) {
                    return false;//非float
                }
			}
            float min = 0L;
            float max = 0L;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示float最小值              
                min = Float.MIN_VALUE;
            } else {
                min = Float.parseFloat(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {// M表示float最大值               
                max = Float.MAX_VALUE;
            } else {
                max = Float.parseFloat(ruleInfo.getRuleContent()[1]);
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

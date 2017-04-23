package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class FloatHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult();
		try {
            if (null == fieldValue) {
                return result;
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
				result.setSuccess(false);//非float
            	result.setMsg("校验不通过");
            	return result;
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

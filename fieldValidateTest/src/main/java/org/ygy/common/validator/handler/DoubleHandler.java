package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public class DoubleHandler implements IValidateRuleHandler{

	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult();
		try {
            if (null == fieldValue) {
                return result;
            }
            double value = 0.0;
            try {
            	if (fieldValue instanceof Integer) {//10的形式
            		value = ((Integer) fieldValue).doubleValue();
            	} else if (fieldValue instanceof Double) {//10.0的形式
            		value = (Double)fieldValue;
            	} else {
            		value = Double.parseDouble((String) fieldValue);
            	}
			} catch (Exception e1) {
				result.setSuccess(false);//非double
            	result.setMsg("校验不通过");
                return result;
			}
            double min = 0.0;
            double max = 0.0;
            if ("m".equals(ruleInfo.getRuleContent()[0])) {//m表示double最小值                
                min = Double.MIN_VALUE;
            } else {
                min = Double.parseDouble(ruleInfo.getRuleContent()[0]);
            }
            if ("M".equals(ruleInfo.getRuleContent()[1])) {// M表示double最大值               
                max = Double.MAX_VALUE;
            } else {
                max = Double.parseDouble(ruleInfo.getRuleContent()[1]);
            }
            double exp = 10E-10;
            if ("(".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) <= exp || Math.abs(max - value) <= exp){
                    	result.setSuccess(false);
                    	result.setMsg("校验不通过");
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) <= exp || Math.abs(max - value) < exp){
                    	result.setSuccess(false);
                    	result.setMsg("校验不通过");
                    }
                }
            } else if ("[".equals(ruleInfo.getLeftSeparate())) {
                if (")".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) < exp || Math.abs(max - value) <= exp){
                    	result.setSuccess(false);
                    	result.setMsg("校验不通过");
                    }
                } else if ("]".equals(ruleInfo.getRightSeparate())) {
                    if (Math.abs(value - min) < exp || Math.abs(max - value) < exp){
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

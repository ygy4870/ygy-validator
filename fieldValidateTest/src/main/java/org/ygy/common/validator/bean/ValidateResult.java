package org.ygy.common.validator.bean;


public class ValidateResult {

    private Object fieldValue;
    private ValidateExpItemInfo validateExpItemInfo;

    public Object getFieldValue() {
        return fieldValue;
    }
    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }
	public String getValidateExpItemInfo() {
//		StringBuilder content = new StringBuilder();
//		for (int i=0; i< ruleInfo.getRuleContent().length; i++) {
//			content.append(ruleInfo.getRuleContent()[i]);
//			if ( i < ruleInfo.getRuleContent().length-1) {
//				content.append(",");
//			}
//		}
//		return ruleInfo.getField() + ":" +  ruleInfo.getRuleType()
//			+ ruleInfo.getLeftSeparate() + content.toString()
//			+ ruleInfo.getRightSeparate() + ruleInfo.getNotNull();
		return validateExpItemInfo.getValidateExpItem();
	}
	public void setValidateExpItemInfo(ValidateExpItemInfo validateExpItemInfo) {
		this.validateExpItemInfo = validateExpItemInfo;
	}
}
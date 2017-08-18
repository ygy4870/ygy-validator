package org.ygy.common.validator.bean;

public class ValidateResult {
	private boolean success = true;
	private String msg;
	private Object fieldValue;
	private ValidateExpItemInfo validateExpItemInfo;

	    public Object getFieldValue() {
	        return fieldValue;
	    }
	    public void setFieldValue(Object fieldValue) {
	        this.fieldValue = fieldValue;
	    }
		public String getValidateExpItemInfo() {
//			StringBuilder content = new StringBuilder();
//			for (int i=0; i< ruleInfo.getRuleContent().length; i++) {
//				content.append(ruleInfo.getRuleContent()[i]);
//				if ( i < ruleInfo.getRuleContent().length-1) {
//					content.append(",");
//				}
//			}
//			return ruleInfo.getField() + ":" +  ruleInfo.getRuleType()
//				+ ruleInfo.getLeftSeparate() + content.toString()
//				+ ruleInfo.getRightSeparate() + ruleInfo.getNotNull();
			return validateExpItemInfo.getValidateExpItem();
		}
		public void setValidateExpItemInfo(ValidateExpItemInfo validateExpItemInfo) {
			this.validateExpItemInfo = validateExpItemInfo;
		}
		/**
		 * 默认为true
		 * @return
		 */
		public boolean getSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}

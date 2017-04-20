package org.ygy.common.validator.bean;

/**
 * 校验表达项信息
 * @author ygy
 */
public class ValidateExpItemInfo{
	
	/**
	 * 校验表达项
	 */
	private String validateExpItem;
    /**
     * 字段名称
     */
    private String field;
    /**
     * 字段校验规则类型
     */
    private String ruleType;
    /**
     * 左分隔符
     */
    private String leftSeparate;
    /**
     * 右分隔符
     */
    private String rightSeparate;
    /**
     * 校验内容
     */
    private String[] ruleContent;
    /**
     * 校验值默认为空时，不进行校验
     * 设置:"!"-显示设置为空时不进行校验，"!!"-强制要求不能为空
     */
    private String notNull;
    /**
     * 格式正确与否,默认正确
     */
    private boolean formatCorrect = true;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getLeftSeparate() {
        return leftSeparate;
    }

    public void setLeftSeparate(String leftSeparate) {
        this.leftSeparate = leftSeparate;
    }

    public String getRightSeparate() {
        return rightSeparate;
    }

    public void setRightSeparate(String rightSeparate) {
        this.rightSeparate = rightSeparate;
    }

    public String[] getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String[] ruleContent) {
        this.ruleContent = ruleContent;
    }

    public String getNotNull() {
        return notNull;
    }

    public void setNotNull(String notNull) {
        this.notNull = notNull;
    }

    public boolean isFormatCorrect() {
        return formatCorrect;
    }

    public void setFormatCorrect(boolean formatCorrect) {
        this.formatCorrect = formatCorrect;
    }

	public String getValidateExpItem() {
		return validateExpItem;
	}

	public void setValidateExpItem(String validateExpItem) {
		this.validateExpItem = validateExpItem;
	}
}
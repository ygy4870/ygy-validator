package org.ygy.common.validator;

/**
 * 字段校验规则信息
 * @author ygy
 */
public class RuleInfo{
    /**
     * 字段名称
     */
    private String filed;
    /**
     * 字段位置
     */
    private int position;
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
    
    /**
     * 是否是针对httpRequest获取参数的校验规则
     */
    private boolean isRequestParamRule;

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public boolean isRequestParamRule() {
        return isRequestParamRule;
    }

    public void setRequestParamRule(boolean isRequestParamRule) {
        this.isRequestParamRule = isRequestParamRule;
    }
}
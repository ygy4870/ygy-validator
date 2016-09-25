package org.ygy.common.validator;

public class ValidateResult {

    private String filed;
    private String rule;
    /**
     * 结果码，0-通过，1-格式错误，2-不通过
     */
    private String resultCode;
    
    public String getFiled() {
        return filed;
    }
    public void setFiled(String filed) {
        this.filed = filed;
    }
    public String getRule() {
        return rule;
    }
    public void setRule(String rule) {
        this.rule = rule;
    }
    public String getResultCode() {
        return resultCode;
    }
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
    
}

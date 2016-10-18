package validator.demo.selfRuleValidator;

import org.ygy.common.validator.RuleInfo;
import org.ygy.common.validator.IValidateRuleHandler;
import org.ygy.common.validator.Validate;

public class Abs implements IValidateRuleHandler{

	@Override
	public boolean customValidate(RuleInfo ruleInfo, Object filedValue, Validate validate) {
		try {
			if (ruleInfo.isFormatCorrect()){
				int value = 0;
				if ( ruleInfo.isRequestParamRule() ) {
					value = Integer.parseInt((String) filedValue);
				} else {
					value = (Integer)filedValue;
				}
				int validateValue = Integer.parseInt(ruleInfo.getRuleContent()[0]);
				if ( !(Math.abs(value) == validateValue) ) {
					return false;
				}
			}
		} catch (Exception e) {
		}
		return true;
	}

}

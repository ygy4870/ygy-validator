package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public interface IValidateRuleHandler {

	/**
	 * 校验逻辑
	 * @param ruleInfo
	 * @param fieldValue
	 * @return true-校验通过，false-校验不通过
	 */
	boolean validate(ValidateExpItemInfo ruleInfo, Object fieldValue);
	
	/**
	 * 解析校验表达项中的内容成需要的数据类型
	 * @param expContent
	 * @return
	 */
//	Object[] parseExpContent(String[] expContent);
}

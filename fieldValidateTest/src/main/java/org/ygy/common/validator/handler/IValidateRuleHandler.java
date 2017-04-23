package org.ygy.common.validator.handler;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.bean.ValidateResult;

public interface IValidateRuleHandler {

	/**
	 * 字段校验结果
	 * @param ruleInfo
	 * @param fieldValue
	 * @return
	 */
	ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue);
	
	/**
	 * 解析校验表达项中的内容成需要的数据类型
	 * @param expContent
	 * @return
	 */
//	Object[] parseExpContent(String[] expContent);
}

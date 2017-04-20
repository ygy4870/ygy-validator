package org.ygy.common.validator.cache;

import java.util.List;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

/**
 * 缓存策略接口
 * @author ygy
 *
 */
public interface ICacheStrategy {
	
	void put(String url, List<ValidateExpItemInfo> validateExpParseResult);
	
	List<ValidateExpItemInfo> get(String url);
}

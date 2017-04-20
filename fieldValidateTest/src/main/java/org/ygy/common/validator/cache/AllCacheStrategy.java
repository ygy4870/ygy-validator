package org.ygy.common.validator.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class AllCacheStrategy implements ICacheStrategy{
	
	/**
     * 校验表达式解析结果validateExpParseResultCache缓存
     */
    private Map<String,List<ValidateExpItemInfo>> validateExpPRC = new ConcurrentHashMap<String,List<ValidateExpItemInfo>>();

    @Override
    public void put(String url, List<ValidateExpItemInfo> validateExpParseResult) {
    	this.validateExpPRC.put(url, validateExpParseResult);
    }
    
    @Override
    public List<ValidateExpItemInfo> get(String url) {
    	return this.validateExpPRC.get(url);
    }
	
}

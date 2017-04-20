package org.ygy.common.validator.cache;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.ygy.common.validator.ValidateContext;
import org.ygy.common.validator.bean.ValidateExpItemInfo;

public class LRUCacheStrategy implements ICacheStrategy{

	/**
     * 校验表达式解析结果validateExpParseResultCache缓存<url,List<ValidateExpItemInfo>>
     */
    private Map<String,List<ValidateExpItemInfo>> validateExpPRC = new ConcurrentHashMap<String,List<ValidateExpItemInfo>>();
    
    /**
     * lru调整队列<url>
     */
    private Queue<String> lruQueue = new ConcurrentLinkedDeque<String>();

	@Override
	public void put(String url, List<ValidateExpItemInfo> validateExpParseResult) {
		//如果缓存已满,移出LRU元素，
		if (this.validateExpPRC.size() >= ValidateContext.getCacheCount()) {
			String oldUrl = this.lruQueue.poll();//移除lru队列队头元素
			this.validateExpPRC.remove(oldUrl);//同时移出缓存
		}
		//新元素缓存，并追加到LRU队列队尾
		this.lruQueue.offer(url);
		this.validateExpPRC.put(url, validateExpParseResult);
	}

	@Override
	public List<ValidateExpItemInfo> get(String url) {
		List<ValidateExpItemInfo> validateExpInfo = this.validateExpPRC.get(url);
		if (null != validateExpInfo) {
			//将该URL移到队尾
			this.lruQueue.remove(url);
			this.lruQueue.offer(url);
		}
		return validateExpInfo;
	}

}

package org.ygy.common.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.cache.ICacheStrategy;
import org.ygy.common.validator.handler.IValidateRuleHandler;

public class ValidateContext {
	
    private static ThreadLocal<HttpServletRequest> requestLocal = new ThreadLocal<HttpServletRequest>();  
    private static ThreadLocal<HttpServletResponse> responseLocal = new ThreadLocal<HttpServletResponse>();  
    /**
     * http 是否为表单数据提交
     */
    private static ThreadLocal<Boolean> isFromParamLocal = new ThreadLocal<Boolean>();
    /**
     * 校验规则处理器缓存
     */
    private static Map<String,IValidateRuleHandler> validateRuleHandlerCache = new HashMap<String,IValidateRuleHandler>();
    /**
     * 校验表达式缓存:<url,validateExp>
     */
    private static Map<String,String> validateExpCache = new HashMap<String,String>();
    /**
     * 本工具的关键字
     */
    private static String[] keywordSet = {"customRule","defineRule","defineCacheStrategy","validateErrorHandlerUrl",
    	"cache","cacheStrategy","cacheCount","filter","illegalCharacter"};
    /**
     * 当校验不通过时，转发处理的url,默认为"defaultValidateErrorHandler"
     */
    private static String validateErrorHandlerUrl = "defaultValidateErrorHandler";
    /**
     * 是否进行非法字符过滤
     */
    private static boolean filter = true;
    /**
     * 全局配置当某个字段校验不通过时，是否进行后续校验，默认false
     */
    private static boolean validateAll = false;
    /**
     * 需要过滤掉的String集
     */
    private static String[] illegalCharArray;
    /**
     * 是否对校验表达式解析结果进行缓存，默认为true
     */
    private static boolean cache = true;
    /**
     * 若cache == true，缓存策略cacheStrategy有两种：all,lru。默认为all,即全部缓存
     */
    private static String cacheStrategy = "all";
    /**
     * 缓存策略类
     */
    private static ICacheStrategy cacheStrategyClass;
    /**
     * 若cache == true 且 cacheStrategy == "lru",缓存长度生效，默认为512
     */
    private static int cacheCount = 512;
    /**
     * 工具自带配置文件路径
     */
    public static final String DEFAULT_CONFIG_PATH = "org/ygy/common/validator/defaultConfig.properties";
      
    public static HttpServletRequest getRequest(){  
        return requestLocal.get();  
    }  
      
    public static void setRequest(HttpServletRequest request){  
        requestLocal.set(request);  
    }  
      
    public static HttpServletResponse getResponse(){  
        return responseLocal.get();  
    }  
      
    public static void setResponse(HttpServletResponse response){  
        responseLocal.set(response);  
    }  

    public static Boolean getIsFromParam() {
        return isFromParamLocal.get();
    }

    public static void setIsFromParam(Boolean isFromParam) {
        isFromParamLocal.set(isFromParam);
    }

    public static String[] getIllegalCharArray() {
        return illegalCharArray;
    }

    public static void setIllegalCharArray(String[] illegalCharArray) {
        ValidateContext.illegalCharArray = illegalCharArray;
    } 
    
    public static void setValidateRuleHandler(String ruleName,IValidateRuleHandler ruleHandler) {
    	ValidateContext.validateRuleHandlerCache.put(ruleName, ruleHandler);
    }
    
    public static IValidateRuleHandler getValidateRuleHandler(String ruleName) {
    	return ValidateContext.validateRuleHandlerCache.get(ruleName);
    }
    
    public static String[] getKeywordSet() {
    	return ValidateContext.keywordSet;
    }
    
    public static void setValidateErrorHandlerUrl(String validateErrorHandlerUrl) {
    	ValidateContext.validateErrorHandlerUrl = validateErrorHandlerUrl;
    }
    
    public static String getValidateErrorHandlerUrl() {
    	return ValidateContext.validateErrorHandlerUrl;
    }
    
	public static boolean getCache() {
		return cache;
	}

	public static void setCache(boolean cache) {
		ValidateContext.cache = cache;
	}

	public static String getCacheStrategy() {
		return cacheStrategy;
	}

	public static void setCacheStrategy(String cacheStrategy) {
		ValidateContext.cacheStrategy = cacheStrategy;
	}

	public static int getCacheCount() {
		return cacheCount;
	}

	public static void setCacheCount(int cacheCount) {
		ValidateContext.cacheCount = cacheCount;
	}

	public static ICacheStrategy getCacheStrategyClass() {
		return cacheStrategyClass;
	}

	public static void setCacheStrategyClass(ICacheStrategy cacheStrategyClass) {
		ValidateContext.cacheStrategyClass = cacheStrategyClass;
	}
	
	/**
	 * 缓存校验表达式解析结果
	 * @param url
	 * @param validateExpParseResult
	 */
	public static void cacheValidateExpParseResult(String url, List<ValidateExpItemInfo> validateExpParseResult) {
		ValidateContext.getCacheStrategyClass().put(url, validateExpParseResult);
	}
	
	/**
	 * 从缓存中获取校验表达式解析结果
	 * @param url
	 * @return
	 */
	public static List<ValidateExpItemInfo> getValidateExpParseResultFromCache(String url) {
		return ValidateContext.getCacheStrategyClass().get(url);
	}

	public static void saveValidateExp(String url, String validateExp) {
		ValidateContext.validateExpCache.put(url, validateExp);
	}
	
	public static String getValidateExp(String url) {
		return ValidateContext.validateExpCache.get(url);
	}

	public static boolean getFilter() {
		return filter;
	}

	public static void setFilter(boolean filter) {
		ValidateContext.filter = filter;
	}

	public static boolean getValidateAll() {
		return validateAll;
	}

	public static void setValidateAll(boolean validateAll) {
		ValidateContext.validateAll = validateAll;
	}
    
}

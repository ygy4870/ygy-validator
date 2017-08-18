package org.ygy.common.validator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ygy.common.validator.bean.InputStreamRequest;
import org.ygy.common.validator.bean.MyHttpRequest;
import org.ygy.common.validator.bean.ParameterRequestWrapper;
import org.ygy.common.validator.bean.ValidateExpItemInfo;
import org.ygy.common.validator.cache.ICacheStrategy;
import org.ygy.common.validator.handler.IValidateRuleHandler;

import com.esotericsoftware.kryo.io.Input;

public class ValidateContextFilter implements Filter{  
	
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response,  
            FilterChain chain) throws IOException, ServletException {  
        ValidateContext.setRequest((HttpServletRequest)request);  
        ValidateContext.setResponse((HttpServletResponse)response);  
        String contenType = request.getContentType();
        HttpServletRequest req = (HttpServletRequest)request;
        String requestMethod = req.getMethod();
        
        MyHttpRequest my = new MyHttpRequest(req);
       
        
        
		BufferedReader in = new BufferedReader(new InputStreamReader(
				my.getInputStream()));
		StringBuilder sb = new StringBuilder();
//		String xmlHead = request.g;
		String xmlContent = "";
		String line = null;
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
        System.out.println("=============================="+sb);
        
        
        
//        MyHttpRequest my = new MyHttpRequest(req);
        
        
        
        /**
         * 只有GET请求和contentType=application/x-www-form-urlencoded的POST请求，request.getParameter才能获取到值
         */
        if ( "GET".equals(requestMethod.toUpperCase()) ||
                ( "POST".equals(requestMethod.toUpperCase()) && "application/x-www-form-urlencoded".equals(contenType.toLowerCase())) ) {
            ValidateContext.setIsFromParam(true);
            if (ValidateContext.getFilter()) {//过滤掉非法字符
            	Map<String, String[]> requestParams = req.getParameterMap();
            	Map<String, String[]> legalrequestParams = this.filterIllegalRequestParams(requestParams);
                HttpServletRequest requestProxy = new ParameterRequestWrapper(req, legalrequestParams);
                ValidateContext.setRequest(requestProxy);
                chain.doFilter(requestProxy, response);
            } else {
            	chain.doFilter(request, response); 
            }
        } else {
            ValidateContext.setIsFromParam(false);
            chain.doFilter(my, response);  
//            HttpServletRequest requestProxy = new InputStreamRequest(req,new ByteArrayInputStream(sb.toString().getBytes()));
//            chain.doFilter(requestProxy, response);  
        }
    }
    
    @Override  
    public void init(FilterConfig config) throws ServletException {  
    	//获取校验配置文件
        String validateConfigFile = config.getInitParameter("validateConfig");
        //加载解析配置
        this.parseConfig(validateConfigFile);
    }  
    
    /**
     * 加载多个配置并合并、解析
     * @param validateConfigFile
     */
    private void parseConfig(String validateConfigFile) {
         Properties config = null;
         try {
        	 config = SimpleUtil.loadMultiProperties(validateConfigFile,ValidateContext.DEFAULT_CONFIG_PATH);
			if (config != null) {
				/**
				 * 1-加载工具预定义规则校验处理器、用户自定义规则处理器类
				 */
				String[] defineRules = null;
				String[] customRules = null;
				String defineRule = config.getProperty("defineRule");
				String customRule = config.getProperty("customRule");
				if (null != defineRule && !"".equals(defineRule)) {
					defineRules = defineRule.replaceAll(" ", "").split(",");
				}
				if (null != customRule && !"".equals(customRule)) {
					customRules = customRule.replaceAll(" ", "").split(",");
				}
				String rules[] = SimpleUtil.concat(defineRules, customRules);
				for (int i = 0; i < rules.length; i++) {
					String className = config.getProperty(rules[i]);
					try {
						Class<?> clazz = Class.forName(className);
						ValidateContext.setValidateRuleHandler(rules[i],(IValidateRuleHandler) clazz.newInstance());
					} catch (Exception e) {
						System.out.println("预加载" + rules[i] + "型规则校验器失败：" + e);
					}
				}
				// 将校验规则处理器key存起来，避免在--步骤4--的预加载校验表达式过程中进行不必要解析
				String[] keySet = ValidateContext.getKeywordSet();
				keySet = SimpleUtil.concat(keySet, rules);
				String defineCacheStrategy = config.getProperty("defineCacheStrategy");
				if (null != defineCacheStrategy&& !"".equals(defineCacheStrategy)) {
					String[] defineCacheStrategys = defineCacheStrategy.replaceAll(" ", "").split(",");
					keySet = SimpleUtil.concat(keySet, defineCacheStrategys);
				}
				/**
				 * 2-解析校验错误处理器URL
				 */
				String validateErrorHandlerUrl = config.getProperty("validateErrorHandlerUrl");
				if (null != validateErrorHandlerUrl && !"".equals(validateErrorHandlerUrl.trim())) {
					ValidateContext.setValidateErrorHandlerUrl(validateErrorHandlerUrl);
				}
				/**
				 * 3-解析cache相关
				 */
				String cache = config.getProperty("cache");
				if (null != cache && "false".equals(cache.trim().toLowerCase())) {
					ValidateContext.setCache(false);
				}
				if (ValidateContext.getCache()) {// 使用缓存
					String cacheStrategy = config.getProperty("cacheStrategy");
					if (null != cacheStrategy && !"".equals(cacheStrategy.trim())) {
						ValidateContext.setCacheStrategy(cacheStrategy.trim().toLowerCase());
					}
					if (!"all".equals(ValidateContext.getCacheStrategy())) {
						String cacheCount = config.getProperty("cacheCount");
						if (null != cacheCount && !"".equals(cacheCount.trim())) {
							int count = 0;
							try {
								count = Integer.parseInt(cacheCount.trim());
							} catch (Exception e) {
								count = 100;
							}
							ValidateContext.setCacheCount(count);
						}
					}
					// 加载缓存策略类
					cacheStrategy = ValidateContext.getCacheStrategy();
					String classPath = config.getProperty(cacheStrategy);
					if (null == classPath || "".equals(classPath.trim())) {
						classPath = config.getProperty("all");
					}
					try {
						Class<?> clazz = Class.forName(classPath);
						ValidateContext.setCacheStrategyClass((ICacheStrategy) clazz.newInstance());
					} catch (Exception e) {
						System.out.println("预加载" + cacheStrategy + "型缓存策略类失败："+ e);
					}
				}
				/**
				 * 4-预加载校验表达式、缓存表达式解析结果
				 */
				Set<Entry<Object, Object>> entrySet = config.entrySet();
				for (Entry<Object, Object> entry : entrySet) {
					String url = (String) entry.getKey();
					// 过滤掉关键字配置行及规则处理器配置行
					if (null != keySet && SimpleUtil.contains(keySet, url)) {
						continue;
					}
					String validateExp = (String) entry.getValue();
					ValidateContext.saveValidateExp(url, validateExp);
					if (ValidateContext.getCache()) {// 缓存校验表达式解析结果
						List<ValidateExpItemInfo> validateExpParseResult = SimpleUtil.parseValidateExpression(validateExp);
						ValidateContext.cacheValidateExpParseResult(url,validateExpParseResult);
					}
				}
				/**
				 * 5-解析过滤非法字符相关
				 */
				String filter = config.getProperty("filter");
				if (null != filter && "false".equals(filter.trim().toLowerCase())) {
					ValidateContext.setFilter(false);
				}
				if (ValidateContext.getFilter()) {
					String illegalCharacter = config .getProperty("illegalCharacter");
					if (null != illegalCharacter) {
						String[] illegalCharArray = illegalCharacter.replaceAll(" ", "").toUpperCase().split(",");
						ValidateContext.setIllegalCharArray(illegalCharArray);
					}
				}
				/**
				 * 6-解析是否进行后续校验全局配置
				 */
				String validateAll = config.getProperty("validateAll");
				if (null != validateAll && "true".equals(validateAll.trim().toLowerCase())) {
					ValidateContext.setValidateAll(true);
				}
			}
		} catch (Exception e) {
			System.out.println("校验配置文件读取失败："+e);
    	} 
	}
    
	@Override  
    public void destroy() {} 
 
    /**
     * 过滤掉非法字符
     * @param requestParams
     * @return
     */
    private Map<String, String[]> filterIllegalRequestParams(Map<String, String[]> requestParams) {
        Map<String, String[]> illegalRequestParams = new HashMap<String, String[]>();
        if (requestParams.size() > 0) {
            for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String str = values[0];
                values[0] = SimpleUtil.filterIllegalCharater(ValidateContext.getIllegalCharArray(), str);
                illegalRequestParams.put(name, values);
            }
        }
        return illegalRequestParams;
    }
 
} 

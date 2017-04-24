package org.ygy.common.validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.ygy.common.validator.bean.ValidateExpItemInfo;

import com.alibaba.fastjson.JSONObject;

public class SimpleUtil {
	
	/**
	 * 递归深度
	 */
	private static final int DEPTH_OF_RECURSION= 5;
	/**
	 * 默认编码
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
     * 判断字串是否在字串数组中
     * @param stringArray
     * @param str
     * @return
     */
    public static boolean contains(String[] stringArray, String str) {
        List<String> tempList = Arrays.asList(stringArray);
        if(tempList.contains(str)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 判断字符串是否是整数
     * @param value
     * @return
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * 合并两个String[]
     * @param a
     * @param b
     * @return
     */
	public static String[] concat(String[] a, String[] b) {
		if (null == a && null == b) {
			return new String[0];
		}
		if (null == a) {
			return b;
		}
		if (null == b) {
			return a;
		}
		String[] c = new String[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
	
	/**
     * 将字串形式的规则解析成RuleInfo对象
     * @param fieldRule
     * @return
     */
	public static ValidateExpItemInfo parseRule(String fieldRule) {
        ValidateExpItemInfo ruleInfo = new ValidateExpItemInfo();
        try {
            String[] arr = fieldRule.split(":");
            String rule = null;
            if (arr.length == 2) {
            	ruleInfo.setFormatCorrect(true);
            	ruleInfo.setField(arr[0]);
            	rule = arr[1];
            }else {
                ruleInfo.setFormatCorrect(false);
                return ruleInfo;
            }
            int leftIndex = rule.indexOf("(");
            if (leftIndex > 0) {
                ruleInfo.setLeftSeparate("(");
            } else {
                leftIndex = rule.indexOf("[");
                if (leftIndex > 0) {
                    ruleInfo.setLeftSeparate("[");
                } else {
                    ruleInfo.setFormatCorrect(false);
                    return ruleInfo;
                }
            }
            int rightIndex = rule.indexOf(")");
            if (rightIndex > 0) {
                ruleInfo.setRightSeparate(")");
            } else {
                rightIndex = rule.indexOf("]");
                if (rightIndex > 0) {
                    ruleInfo.setRightSeparate("]");
                } else {
                    ruleInfo.setFormatCorrect(false);
                    return ruleInfo;
                }
            }     
            String ruleType = rule.substring(0, leftIndex).toLowerCase();//转换成小写，即忽略大小写
            ruleInfo.setRuleType(ruleType);    
            ruleInfo.setRuleContent(rule.substring(leftIndex+1, rightIndex).replaceAll("\"", "").replaceAll("'", "").split(","));
            ruleInfo.setNotNull(rule.substring(rightIndex+1).replaceAll(" ", ""));
            ruleInfo.setValidateExpItem(fieldRule);
        } catch (Exception e) {
            ruleInfo.setFormatCorrect(false);
        }
        return ruleInfo;
    }
	
	/**
	 * 将校验表达式解析成List&lt;RuleInfo&gt;
	 * @param validateExpression
	 * @return
	 */
	public static List<ValidateExpItemInfo> parseValidateExpression(String validateExpression) {
		List<ValidateExpItemInfo> ruleInfoList = new ArrayList<ValidateExpItemInfo>();
		if (null != validateExpression && !"".equals(validateExpression.trim())) {
			String[] rules = validateExpression.replaceAll(" ", "").split("\\|");
			if (rules != null && rules.length > 0) {
	        	for (String rule : rules){
	        		if (!"".equals(rule)) {
	        			ValidateExpItemInfo ruleInfo = SimpleUtil.parseRule(rule);
	        			ruleInfoList.add(ruleInfo);
	        		}
	        	}
			}
		}
		return ruleInfoList;
	}
	
	/**
	 * 从com.alibaba.fastjson.JSONObject对象中获取指定值
	 * @param key 点分形式的key,如person.age
	 * @param jsonObj
	 * @return
	 */
	public static Object getFromJSONObject(String key,JSONObject jsonObj) {
		Object obj = null;
		try {
			String[] keyArr = key.split("\\.");
			String field = null;
			for (int i=0; i<keyArr.length; i++) {
				field = keyArr[i];
				obj = jsonObj.get(field);
				if (i < keyArr.length - 1) {
					jsonObj = (JSONObject) JSONObject.toJSON(obj);
				}
			}
		} catch (Exception e) {
			System.out.println("从com.alibaba.fastjson.JSONObject对象中获取指定值失败:"+e);
		}
		return obj;
	}
	
	/**
	 * 过滤掉指定非法字符
	 * @param illegalCharArray 非法字符集
	 * @param value 
	 * @return
	 */
    public static String filterIllegalCharater(String[] illegalCharArray,String value) {
        if (illegalCharArray != null) {
            for (int i = 0; i < illegalCharArray.length; i++) {
                String illegalChar = illegalCharArray[i];
                int indexOf = value.toUpperCase().indexOf(illegalChar);
                if (indexOf > -1) {
                    String subtring = value.substring(indexOf, indexOf + illegalChar.length());
                    value = value.replaceAll(subtring, "");
                }
            }
        }
        return value;
    }
    
    /**
	 * 递归过滤掉json对象中的非法字符<br/>
	 * 初始depth=0
	 */
    public static void filterIllegalCharaterFromJSONObj(JSONObject jSONObj, int depth) {
    	depth++;
    	if (depth > DEPTH_OF_RECURSION) {
    		return;
    	}
		Set<Entry<String, Object>> entrySet = jSONObj.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			Object value = entry.getValue();
			if (value instanceof String) {// 如果是String则进行非法字符过滤
				String legalValue = SimpleUtil.filterIllegalCharater(ValidateContext.getIllegalCharArray(),(String) value);
				jSONObj.put(entry.getKey(), legalValue);
			} else {
				if ( null != value && !isWrapClass(value.getClass())) {//非基本类型包装类
					JSONObject obj = (JSONObject) JSONObject.toJSON(value);
					filterIllegalCharaterFromJSONObj(obj, depth);
				}
			}
		}
	}
    
    /**
     * 是否基本类型包装类
     * @param clz
     * @return
     */
    public static boolean isWrapClass(Class<?> clz) { 
        try { 
           return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) { 
            return false; 
        } 
    } 
    
    /**
     * 加载多个Properties配置文件，同属性值以后加载配置文件为准
     * @param resourcesPaths
     * @return
     */
    public static Properties loadMultiProperties(String... resourcesPaths){
		Properties props = new Properties();
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
		for (String location : resourcesPaths) {
			if (null == location || "".equals(location.trim())) {
				continue;
			}
			InputStream is = null;
			try {
				Resource resource = resourceLoader.getResource(location);
				is = resource.getInputStream();
				propertiesPersister.load(props, new InputStreamReader(is, DEFAULT_ENCODING));
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
						System.out.println("流关闭异常：" + e);
					}
				}
			}
		}
		return props;
	}
    
}

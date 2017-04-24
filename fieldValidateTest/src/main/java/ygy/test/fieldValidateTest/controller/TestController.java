package ygy.test.fieldValidateTest.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ygy.common.validator.bean.Validate;

@Controller
@RequestMapping("test")
public class TestController {
	
	/**
	 * 测试一：简单字段校验（get请求或表单请求）
	 */
	@Validate("name:reg(^yang.*)|sex:strin(0,1,2)|age:int(0,200)|phone:len(11)|money:double(0,100)|birthday:date(YYYY/MM/DD)")
	@ResponseBody
    @RequestMapping("getPerson")
    public Map<String,Object> getPerson(HttpServletRequest request, 
    		String name, String sex, Integer age, String phone, Double money, String birthday){
	    System.out.println(request.getParameter("age"));
	    System.out.println(age);
	    Map<String,Object> result = new HashMap<String,Object>();
	    result.put("status", "0");
	    return result;
	}
	
	/**
	 * 测试二：简单字段校验（json请求）,要求：参数放在第一个（map）
	 */
	@Validate(value="name:reg(^yang.*)!|sex:strin(0,1,2)|age:int(0,sdf)|phone:len(11,11)|money:float(0,100)|birthday:date(YYYY/MM/DD)")
	@ResponseBody
    @RequestMapping("/getPerson2")
    public Map<String,Object> getPerson2(@RequestBody Map<String,Object> map){
	    System.out.println(map);
        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	
	/**
	 * 测试三：简单字段校验（json请求）,要求：参数放在第一个（entity）
	 */
	@Validate(value="name:reg(^yang.*)|sex:strin(0,1,2)|age:int(0,200)|phone:len(11)|money:double(0,100)|birthday:date(YYYY/MM/DD)")
	@ResponseBody
    @RequestMapping("/getPerson3")
    public Map<String,Object> getPerson3(@RequestBody Person person){
//	    System.out.println(map);
//        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	
	
	/**
	 * 测试四：嵌套字段校验（json请求）-map
	 */
	@Validate(value="father.age:int(20,M)")
	@ResponseBody
    @RequestMapping("/getPerson4")
    public Map<String,Object> getPerson4(@RequestBody Map<String,Object> map){
	    System.out.println(map);
        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	
	/**
	 * 测试五：嵌套字段校验（json请求）-entity
	 */
	@Validate(value="father.age:int(20,M)")
	@ResponseBody
    @RequestMapping("/getPerson5")
    public Map<String,Object> getPerson5(@RequestBody Person person){
	    System.out.println(person);
//        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	
	/**
	 * 测试六：未开启缓存，@Validate注解优先级高于配置文件；开启缓存，配置优先级高,url优先于springmvc的路径映射
	 */
	@Validate(value="sex:strin(0,1,2)",url="testURL")
	@ResponseBody
    @RequestMapping("/getPerson6")
    public Map<String,Object> getPerson6(@RequestBody Map<String,Object> map){
	    System.out.println(map);
        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	
	/**
	 * 测试七：过滤非法字符
	 */
	@Validate(value="sex:strin(0,1,2)|name:reg(^yang.*)")
	@ResponseBody
    @RequestMapping("/getPerson7")
    public Map<String,Object> getPerson7(@RequestBody Map<String,Object> map){
	    System.out.println(map);
        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	
	/**
	 * validateAll 注解优先级高于文件配置,all="false"不进行后续校验，all="true"进行后续校验，否则已配置文件的为准
	 */
	@Validate(value="sex:strin(0,1,2)|name:reg(^yang.*)",all="false")
	@ResponseBody
    @RequestMapping("/getPerson8")
    public Map<String,Object> getPerson8(@RequestBody Map<String,Object> map){
	    System.out.println(map);
        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	
	/**
	 * 自定义校验规则测试
	 */
	@Validate("age:abs(10)")
	@ResponseBody
    @RequestMapping("/getPerson9")
    public Map<String,Object> getPerson9(@RequestBody Map<String,Object> map){
	    System.out.println(map);
        System.out.println(map.get("age"));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status", "0");
        return result;
    }
	

}

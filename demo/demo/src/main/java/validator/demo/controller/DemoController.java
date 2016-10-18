package validator.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ygy.common.validator.Validate;

@Controller
public class DemoController {
	
	//must=true表示所有字段不能为空，规定age为大于1的int整数，感叹号表示可以为空（优先级高于must）
	@Validate(value="age:int(1,M)!|status:strin(0,1,2)",must=true)
	@ResponseBody
	@RequestMapping("getInfo")
	public Map<String,Object> getInfo(HttpServletRequest request, HttpServletResponse response){
		Map<String,Object> result = new HashMap<String,Object>();
		int age = Integer.parseInt(request.getParameter("age"));
		System.out.println(age);
		result.put("status", "1");
		return result;
	}
	
	@Validate("0:int(1,M)")
	@ResponseBody
	@RequestMapping("getInfo2")
	public Map<String,Object> getInfo2(Integer age){
		Map<String,Object> result = new HashMap<String,Object>();
		System.out.println(age);
		result.put("status", "1");
		return result;
	}
	
	@Validate("0:abs(3)")//自定义规则abs,规定第一个参数绝对值为3
	@ResponseBody
	@RequestMapping("getInfo3")
	public Map<String,Object> getInfo3(Integer age){
		Map<String,Object> result = new HashMap<String,Object>();
		System.out.println(age);
		result.put("status", "1");
		return result;
	}

}

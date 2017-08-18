package ygy.test.fieldValidateTest.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.danlu.dlhttpx.HttpService;

@Controller
public class HttpDemo {
	
	
    private HttpService httpService = new HttpService(); 

	@RequestMapping("test")
	public Map<String,Object> test() {
		Map<String,Object> res= new HashMap<String,Object>();
		HttpHeaders httpHeaders = new HttpHeaders();
		ResponseEntity<Person> responseEntity = httpService.postJson4Entity("http://127.0.0.1:8081/fieldValidateTest/testPerson",
                httpHeaders,
                null,
                new ParameterizedTypeReference<Person>() {
                }, null);
		res.put("p", responseEntity.getBody());
		return res;
	}
	
	@RequestMapping("testPerson")
	@ResponseBody
	public Map<String,Object> testPerson() {
		Map<String,Object> res= new HashMap<String,Object>();
		Person p = new Person();
		p.setAge(10);
		Person f = new Person();
		f.setAge(20);
		p.setFather(f);
		res.put("p", p);
		return res;
	}
}

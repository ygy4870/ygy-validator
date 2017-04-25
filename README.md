**简介**
 - 该工具用于对方法参数字段的校验，源于“防御性编程”的理念。避免字段校验逻辑和业务代码相互嵌入所造成的混乱，让程序员专注于业务实现，保持代码的纯粹性，是该工具的目标。该工具基于使用广泛的Spring框架，定义封装校验规则的注解@Validate，使用AOP对添加了该注解的方法进行切入，实现根据规则对参数字段进行校验的目的。校验代码只是业务代码的辅助，只是为了代码流程更健壮一点，所以它应该简单一点，基本原则是不符合校验格式的不进行校验，对多个字段进行校验，当有一个校验不通过时，默认不再进行后续字段的校验，可配置。对于使用来说也应该简单（觉得用注解就比较简单）。当然将校验规则写在配置文件里也支持。对于校验结果的处理，提供默认处理方式，可以自定制。
 
**规则说明**

 - 规则形式
   **字段名：校验规则[!或!!]**
   注：其中单个感叹号表示该字段可以为空，两个表示该字段不能为空，默认可以为空
   

该工具预设了9种校验规则，为 must,int,strin,len,date,reg,long,float,double。
 - must
 
	该规则指定字段是必要的，不能为null或空串。如name:must(true)表示字段name是必要的。
	
 - int
 
	该规则指定如果该字段非null或空串，则为int型数据，且在规定大小内。如age:int(1,10)表示字段age为int型数据，且要求1<age<10。当然可以使用方括号“[ ”“]”。如age:int[1,10)要求1<=age<10，圆括号、方括号的使用与数学上的意义一样。如只要求大于某个值，可以age:int(1,M),只要求小于某个值，可以age:int(m,10)。其中小写m表示int型数据可表示的最小值，大写M表示int型数据可表示的最大值。
	
 - strin
 
	该规则指定如果该字段非null或空串，要求为指定字串数组中的一个。如status:strin(01,02,03)表示字段status必须为01、02、03其中一个。
	
 - len
 
	该规则指定如果该字段非null或空串，要求为指定长度。如phone:len(11,11)表示字段phone长度必须为11。
	
 - date
 
	该规则指定如果该字段非null或空串，要求为指定日期格式。如startTime:date(yyyy-MM-dd)表示字段startTime格式为yyyy-MM-dd。

 - reg
 
	该规则指定如果该字段非null或空串，要求匹配指定正则表达式。

 - long
 
	该规则指定如果该字段非null或空串，则为long型数据，使用方式与int型规则相似。
 - float
 
	该规则指定如果该字段非null或空串，则为float型数据，使用方式与int型规则相似。
 - double
 
	该规则指定如果该字段非null或空串，则为double型数据，使用方式与int型规则相似。
	
**配置说明**
 - 1、pom.xml添加Spring AOP相关jar包及com.alibaba.fastjson
 
 - 2、Spring配置文件启动aop注解和bean依赖注入
```xml
<beans 
//省略
	xmlns:aop="http://www.springframework.org/schema/aop" 
	xsi:schemaLocation="
//省略
        http://www.springframework.org/schema/aop 
	    http://www.springframework.org/schema/aop/spring-aop.xsd">
	<!-- 启动AOP注解 -->               
    <aop:aspectj-autoproxy proxy-target-class="true"/>
    <bean class="org.ygy.common.validator.FieldValidateAspect"/>
    <bean class="org.ygy.common.validator.DefaultValidateErrorHandler"/>
    <mvc:annotation-driven />
</beans>
```
 - 3、web.xml添加检验过滤器配置
```xml
	<filter>
	   <filter-name>fieldValidateFilter</filter-name>
	   <filter-class>org.ygy.common.validator.ValidateContextFilter</filter-class>
	   <init-param>
	       <param-name>validateConfig</param-name>
	       <param-value>classpath:conf/validate.properties</param-value>
	   </init-param>
    </filter>

	<filter-mapping>
		<filter-name>fieldValidateFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
```
其中validate.properties校验配置文件稍后进行说明。

**实例说明**

 - （1）form表单post或get请求
```java
@Validate("age:int(1,10)")
@ResponseBody
@RequestMapping("getPersonInfo")
public Map<String,Object> getPersonInfo(HttpServletRequest request, HttpServletResponse response)
```
添加注解@Validate("age:int(1,10)")要求字段age为int整数,且1<age<10

 - （2）json请求形式
```java
@Validate(value="age:int(1,M)")
@ResponseBody
@RequestMapping("/getPerson")
public Map<String,Object> getPerson(@RequestBody Person person){
```
添加注解@Validate("age:int(1,10)")要求字段age为int整数,且1<age<int型最大值
 - （3）复杂对象字段校验（最多支持5层）
```java
@Validate(value="father.age:int(20,M)")
@ResponseBody
@RequestMapping("/getPerson")
public Map<String,Object> getPerson(@RequestBody Person person){
```
添加注解@Validate(value="father.age:int(20,M)")要求入参person里的father里的字段age为int整数,且20<age<int型最大值

 - （4）@Validate中的must属性
```java
@Validate(value="age:int(1,M)!|status:strin(0,1,2)",must=true)
@ResponseBody
@RequestMapping("getInfo")
public Map<String,Object> getInfo(HttpServletRequest request){
```
添加注解@Validate，其中must=true表示所有字段不能为空，规定age为大于1的int整数，单个感叹号“!”表示age可以为空（优先级高于must）。当然must没有配置时默认为false，及所有字段不要求必要性校验。如果某个字段要求必要性校验，可以在校验表达式后添加两个感叹号“!!”即可。如@Validate(value="age:int(1,M)!!|status:strin(0,1,2)|name:len(4)")表示除了age要求必要性校验，其他字段不作要求。

 - （5）@Validate中的all属性
```java
@Validate(value="sex:strin(0,1,2)|name:reg(^yang.*)",all="true")
@ResponseBody
@RequestMapping("/getPerson8")
public Map<String,Object> getPerson8(@RequestBody Map<String,Object> map){
```
添加注解@Validate，其中all="true"表示对value中的所有字段进行校验。默认当有一字段校验失败不进行后续校验，该例中如sex字段校验失败，不再对name字段进行校验。不进行全部字段校验，反正校验已失败，可以避免后续不必要的代码执行；进行全字段校验可以收集校验错误信息，可用于精确提示，或者代码调试过程中的排错。

 - （6）@Validate中的handler属性
```java
@Validate(value="age:int(1,10)",handler="ygyHandler")
@ResponseBody
@RequestMapping("getPersonInfo")
public Map<String,Object> getPersonInfo(HttpServletRequest request, HttpServletResponse response)
//校验结果处理器如下
@ResponseBody
@RequestMapping("ygyHandler")
public Object validateHandler(HttpServletRequest request){
		List<ValidateResult> validateResults = (List<ValidateResult>) request.getAttribute("validateResults");
	//省略
}
```
其中request.getAttribute("validateResults");中存放的是校验错误结果信息。
默认情况下，不需要编写自己的校验错误处理器，有默认。如果要编写自己的默认处理器，如上即可。校验不通过处理器请求映射若为@RequestMapping("ygyHandler")，则@Validate中得添加handler="ygyHandler"。该例的校验错误处理器@Validate(value="age:int(1,10)",handler="ygyHandler")只针对当前控制器方法的校验错误处理。针对当前请求提供专门的校验不通过处理器，感觉没什么意义，该工具的目标就是为所有方法的校验提供统一的校验不通过处理器。统一了校验错误处理器的返回值规范，如果这个返回值规范与正常执行的控制器方法返回值规范一致，这就统一了所有返回值规范，有利于前端人员处理。当然这个检验错误处理可以是多种形式，比如跳转指定页面等，不局限于返回json。

 - （7）@Validate中的url属性
该属性在配置文件中有一点用，算了感觉也没什么用，懒得改了，留着他吧。

**校验配置文件validate.propeties说明**

@Validate的属性配置在校验配置文件validate.propeties中全都有对应，除此之外，校验配置文件还可以进行自定义校验规则的配置、非法字符过滤的配置及缓存配置。

- @Validate对应属性配置

（1）针对所有的请求处理方法，要求对配置了校验的字段全部都要校验,相对于@Validate中的all属性配置针对所在请求处理方法，@Validate中的优先级更高，当@Validate中没配置，已配置文件的为准，若配置文件中也没有配置，则默认不进行所有字段校验，即当有一字段校验不通过，立即返回，不进行后续字段校验
```xml
validateAll = true
```
（2）校验不通过处理器请求url配置，处理所有方法的校验结果，相对于@Validate中的handler属性配置针对所在请求处理方法，前面说过，建议使用配置文件全局配置。如果@Validate没有配置，校验配置文件中也没有该配置，执行默认处理器
```xml
validateErrorHandlerUrl = ygyHandler
```
（3）字段校验表达式，相对于@Validate中的value配置零散地分布在所在请求处理方法上，配置在校验配置文件中更便于集中管理。以"url=字段校验表达式"的形式书写，其中url为springMVC的请求处理映射url或@Validate中的url属性配置，@Validate中的url属性配置优先级更高。如果开启了缓存，配置文件和@Validate都存在校验表达式，此时以配置为准。如：
```xml
getPerson = age:int(1,M)!!|status:strin(0,1,2)|name:len(4)
getPerson1 = age:int(1,M)!!|status:strin(0,1,2)|name:len(4)
getPerson2 = age:int(1,M)!!|status:strin(0,1,2)|name:len(4)
getPerson3 = age:int(1,M)!!|status:strin(0,1,2)|name:len(4)
...
...
```
- 自定义校验规则配置

提供用户自定义字段校验规则的配置，如果自定义校验规则与预定义的规则同名，则以预定义规则为准，自定义校验规则无效。步骤如下：

（1）步骤一、实现IValidateRuleHandler接口
```java
public interface IValidateRuleHandler {
	/**
	 * 字段校验结果
	 * @param ruleInfo
	 * @param fieldValue
	 * @return
	 */
	ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue);
}
```
如以自定义绝对值校验器为例，如下：
```java
public class AbsHandler implements IValidateRuleHandler{
	@Override
	public ValidateResult validate(ValidateExpItemInfo ruleInfo, Object fieldValue) {
		ValidateResult result = new ValidateResult(); 
		try {
			int value = 0;
			try {
				value = Integer.parseInt((String) fieldValue);
			} catch (Exception e) {
				try {
					value = (Integer) fieldValue;
				} catch (Exception e1) {
					result.setSuccess(false);
					result.setMsg("校验不通过");
					return result;
				}
			}
			int validateValue = Integer.parseInt(ruleInfo.getRuleContent()[0]);
			if (!(Math.abs(value) == validateValue)) {
				result.setSuccess(false);
				result.setMsg("校验不通过");
			}
        } catch (Exception e) {
        	e.printStackTrace();
        	result.setSuccess(false);
        	result.setMsg("校验规则书写错误");
        }
		return result;
	}
}
```
（2）步骤二、validate.propeties配置如下
```xml
customRule = abs
abs = ygy.test.fieldValidateTest.controller.AbsHandler
```
customRule = abs的意思是增加添加的自定义规则为abs,如果添加多个，规则间以逗号分隔如addRule = abs,equal。
abs = org.ygy.test.Abs配置自定义规则的处理器的类完整路径

 （3）步骤三、使用
```java
@Validate("age:abs(10)")
@ResponseBody
@RequestMapping("/getPerson")
public Map<String,Object> getPerson(@RequestBody Map<String,Object> map){
```
- 非法字符过滤配置

是否进行非法字符过滤,默认为true
```xml
filter = true
```
要过滤的非法字符串，以逗号分隔
```xml
illegalCharacter = <script,<javascript,<iframe,<!--
```
- 缓存配置

是否开启校验表达式解析结果缓存，默认为false
```xml
cache = true
```
当cache = true即开启缓存时，可选择缓存策略，目前有all(全缓存)、lru(最近最少使用)两种，默认all
```xml
cacheStrategy = all
```
若cache == true 且 cacheStrategy == "lru",缓存长度生效，默认为512
```xml
cacheCount = 512
```

**几个默认**

	当没有配置校验配置文件时，会有一些默认。
	（1）当某个字段校验不通过时，默认不进行后续字段校验
	（2）默认启动非法字符过滤，只是非法字符集为空，没意思::>_<::
	（3）默认不开启缓存

**其他**

	可扩展：访问限流、访问次数统计，不知道有意义不。


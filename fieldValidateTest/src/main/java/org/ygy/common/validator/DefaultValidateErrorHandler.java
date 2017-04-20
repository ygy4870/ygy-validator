package org.ygy.common.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ygy.common.validator.bean.ValidateResult;

/**
 * 默认校验不通过处理器
 * @author ygy
 *
 */
@Controller
public class DefaultValidateErrorHandler{

	@SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("defaultValidateErrorHandler")
    public Object validateHandler(HttpServletRequest request, HttpServletResponse response){
		List<ValidateResult> validateResults = (List<ValidateResult>) request.getAttribute("validateResults");
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", "-1");
		result.put("msg", "参数不合法");
        result.put("data", validateResults);
        return result;
    }
}

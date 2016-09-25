package org.ygy.common.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefaultValidateErrorHandler{

	@SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping("defaultValidateErrorHandler")
    public Object validateHandler(HttpServletRequest request, HttpServletResponse response){
		List<ValidateResult> validateResults = (List<ValidateResult>) request.getAttribute("validateResults");
		Map<String,Object> result = new HashMap<String,Object>();
        result.put("validateResults", validateResults);
        return result;
    }
}

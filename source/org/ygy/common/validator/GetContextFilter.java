package org.ygy.common.validator;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetContextFilter implements Filter{  
    
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response,  
            FilterChain chain) throws IOException, ServletException {  
        SysContext.setRequest((HttpServletRequest)request);  
        SysContext.setResponse((HttpServletResponse)response);  
        chain.doFilter(request, response);  
    }
    
    @Override  
    public void init(FilterConfig config) throws ServletException {  
        String validateConfig = config.getInitParameter("validateConfig");
        SysContext.setValidateConfigFile(validateConfig);
    }  
    
    @Override  
    public void destroy() {} 
  
} 

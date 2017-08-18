package org.ygy.common.validator.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class InputStreamRequest extends HttpServletRequestWrapper{
	
	private HttpServletRequest request;
	
	private InputStream in;
	
	public InputStreamRequest(HttpServletRequest request,InputStream in) {
		super(request);
		this.request = request;
		this.in = in;
	}
	
	/*================================重写读流方法=================================*/

	@Override
	public BufferedReader getReader() throws IOException {
		return  new BufferedReader(new InputStreamReader(this.in));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return (ServletInputStream) this.in;
	}

	/*======================其他方法调用原逻辑========================================*/

	@Override
	public Object getAttribute(String name) {
		return this.request.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return this.request.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		this.request.setCharacterEncoding(env);
	}

	@Override
	public int getContentLength() {
		return this.request.getContentLength();
	}

	@Override
	public long getContentLengthLong() {
		return this.request.getContentLengthLong();
	}

	@Override
	public String getContentType() {
		return this.request.getContentType();
	}

	@Override
	public String getParameter(String name) {
		return this.request.getParameter(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return this.request.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return this.request.getParameterValues(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return this.request.getParameterMap();
	}

	@Override
	public String getProtocol() {
		return this.request.getProtocol();
	}

	@Override
	public String getScheme() {
		return this.request.getScheme();
	}

	@Override
	public String getServerName() {
		return this.request.getServerName();
	}

	@Override
	public int getServerPort() {
		return this.request.getServerPort();
	}

	@Override
	public String getRemoteAddr() {
		return this.request.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return this.request.getRemoteHost();
	}

	@Override
	public void setAttribute(String name, Object o) {
		this.request.setAttribute(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		this.request.removeAttribute(name);
	}

	@Override
	public Locale getLocale() {
		return this.request.getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return this.request.getLocales();
	}

	@Override
	public boolean isSecure() {
		return this.request.isSecure();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return this.request.getRequestDispatcher(path);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getRealPath(String path) {
		return this.request.getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		return this.request.getRemotePort();
	}

	@Override
	public String getLocalName() {
		return this.request.getLocalName();
	}

	@Override
	public String getLocalAddr() {
		return this.request.getLocalAddr();
	}

	@Override
	public int getLocalPort() {
		return this.request.getLocalPort();
	}

	@Override
	public ServletContext getServletContext() {
		return this.request.getServletContext();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return this.request.startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) throws IllegalStateException {
		return this.request.startAsync(servletRequest, servletResponse);
	}

	@Override
	public boolean isAsyncStarted() {
		return this.request.isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return this.request.isAsyncSupported();
	}

	@Override
	public AsyncContext getAsyncContext() {
		return this.request.getAsyncContext();
	}

	@Override
	public DispatcherType getDispatcherType() {
		return this.request.getDispatcherType();
	}

}

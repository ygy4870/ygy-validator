package org.ygy.common.validator.bean;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class ValidateHttpRequest extends HttpServletRequestWrapper {
	
	private Map<String, String[]> params; 
	
	private String body;
	
	private boolean xssFilter = false;//应该作用在所有的http请求，而不是具体的请求上
	
	private String contentType;
	
	private String method;

	public ValidateHttpRequest(HttpServletRequest request) throws IOException {
		super(request);
		init(request);
	}
	
	private void init(HttpServletRequest request) throws IOException {
		this.contentType = request.getContentType().toLowerCase();
		this.method = request.getMethod().toLowerCase();
//		if (this.contentType.startsWith("multipart/")) {
//			return;
//		}
//		if ("get".equals(this.requestMethod) ||
//                ( "post".equals(this.requestMethod) && "application/x-www-form-urlencoded".equals(this.contentType)) ) {
//			return;
//		}
		if (this.isPostJson()) {//只有为json请求才会读取流
			Charset charset = Charset.forName(getCharacterEncoding());
	        body = new String(IOUtils.toByteArray(request.getInputStream()), charset);
		}
	}

	/***********************************/
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (this.isPostJson()) {
			return new RequestCachingInputStream(this.body.getBytes(getCharacterEncoding()));
		}
	    return super.getInputStream();
	}

	@Override
    public BufferedReader getReader() throws IOException {
		if (this.isPostJson()) {
			return new BufferedReader(new InputStreamReader(this.getInputStream(), this.getCharacterEncoding()));
		}
        return super.getReader();
    }

    private static class RequestCachingInputStream extends ServletInputStream {
        
        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

		@Override
		public boolean isFinished() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void setReadListener(ReadListener readListener) {
			// TODO Auto-generated method stub
			
		}
    }
    
    /***********************************/
    
    @Override
    public final String getCharacterEncoding() {
        return super.getCharacterEncoding() != null ? super.getCharacterEncoding() : "UTF-8";
    }
    
    /**
     * 是否为json请求
     * @return
     */
    private boolean isPostJson() {
    	return "post".equals(this.method) && "application/json".equals(this.contentType);
    }
}

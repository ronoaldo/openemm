package org.agnitas.util;

public class HttpException extends Exception {
	private static final long serialVersionUID = -3519288978067750949L;
	
	private String url;
	private int httpCode;
	
	public HttpException(String url, int httpCode) {
		super("HttpRequest returned httpresponsecode " + httpCode + " for url " + url);
		this.url = url;
		this.httpCode = httpCode;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the httpCode
	 */
	public int getHttpCode() {
		return httpCode;
	}

	/**
	 * @param httpCode the httpCode to set
	 */
	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}
}

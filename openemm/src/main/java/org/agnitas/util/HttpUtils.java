package org.agnitas.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class HttpUtils {
	public static final String SECURE_HTTP_PROTOCOL_SIGN = "https://";
	public static final String HTTP_PROTOCOL_SIGN = "http://";
			
	public static String convertToParameterString(Map<String, Object> parameterMap) {
		StringBuilder returnValue = new StringBuilder();
		
		if (parameterMap != null) {
			try {
				for (Entry<String, Object> entry : parameterMap.entrySet()) {
					if (returnValue.length() > 0)
						returnValue.append("&");
					returnValue.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
					returnValue.append("=");
					if (entry.getValue() != null)
						returnValue.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return returnValue.toString();
	}
	
	public static String executeHttpGetRequest(String httpUrl, Map<String, Object> httpGetParameter) throws HttpException {
		return executeHttpRequest(httpUrl, httpGetParameter, null);
	}
	
	public static String executeHttpPostRequest(String httpUrl, Map<String, Object> httpPostParameter) throws HttpException {
		return executeHttpRequest(httpUrl, null, httpPostParameter);
	}
	
	public static String executeHttpRequest(String httpUrlString, Map<String, Object> httpGetParameter, Map<String, Object> httpPostParameter) throws HttpException {
		if (StringUtils.isBlank(httpUrlString)) {
			throw new RuntimeException("Invalid empty URL for http request");
		}
		
		StringBuilder input = new StringBuilder();
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String urlString = httpUrlString.toLowerCase();
		
		// Check for protocol "https://" or "http://" (fallback: "http://")
		if (!urlString.startsWith(SECURE_HTTP_PROTOCOL_SIGN)
				&& !urlString.startsWith(HTTP_PROTOCOL_SIGN))
			urlString = HTTP_PROTOCOL_SIGN + urlString;
			
		try {
			if (httpGetParameter != null && httpGetParameter.size() > 0) {
				// Prepare Get parameter data
				String getParameterString = convertToParameterString(httpGetParameter);
				urlString += "?" + getParameterString;
			}

			HttpURLConnection urlConnection = (HttpURLConnection) new URL(urlString).openConnection();
			
			if (httpPostParameter != null && httpPostParameter.size() > 0) {
				// Send post parameter data
				urlConnection.setDoOutput(true);
			    out = new OutputStreamWriter(urlConnection.getOutputStream());
			    out.write(HttpUtils.convertToParameterString(httpPostParameter));
			    out.flush();
			}
			
			int responseCode = urlConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					input.append(inputLine);
					input.append("\n");
				}
			} else {
				throw new HttpException(urlString, responseCode);
			}
		} catch (HttpException e) {
			throw e;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
		
		return input.toString();
	}
}

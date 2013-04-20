package org.agnitas.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

public class LinkcheckWorker implements Runnable {
	
	private static final transient Logger logger = Logger.getLogger( LinkcheckWorker.class);
	
	int timeout = 0; 	// default, no timeout.
	// contains a list with links. if the given URL is valid and reachable,
	// the given link will be removed from the link-list. Vector because its thread-save. 
	Vector<String> links;
	String linkToCheck = null;
	
	/** 
	 * @param timeout
	 * @param links
	 * @param linkToCheck
	 * 
	 */
	public LinkcheckWorker(int timeout, Vector<String> links, String linkToCheck) {
		this.timeout = timeout;
		this.links = links;
		this.linkToCheck = linkToCheck;
	}
	
	
	@Override
	public void run() {
		boolean failure = false;
		boolean dynamic = false;
		
		// check if the link has dynamic content.
		dynamic = dynamicLinkCheck();		
		if (dynamic) {
			if( logger.isInfoEnabled()) {
				logger.info( "Link is dynamic - no checking for: " + linkToCheck);
			}
			
			failure = false;
		} else {
			failure = netBasedTest();
		}
		
		// remove working link from failure-list
		if (!failure) {
			if( logger.isInfoEnabled()) {
				logger.info( "Link is working: " + linkToCheck);
			}
			
			links.remove(linkToCheck);
		}
	}
	
	/**
	 * this method checks, if the given link contains dynamic content like ##AGNUID##
	 * if thats the case, we wont check the link anymore.
	 * If failure = true, the link is dynamic and has to be removed. "failure" here means dynamic.
	 * @return
	 */
	private boolean dynamicLinkCheck() {
		boolean dynamic = false;
		Pattern pattern = Pattern.compile ("##([^#]+)##");
		Matcher aMatch = pattern.matcher(linkToCheck);
		if (aMatch.find() ) {
			// found dynamic content
			return true;
		} 
		return dynamic;		
	}
	
	/**
	 * this method checks, if the given link works. It gets a real connection
	 * to the given server and tries to fetch some answers.
	 * @param failure
	 * @return
	 */
	private boolean netBasedTest() {
		boolean failure = false;
		URL url;
		try {						
			if( logger.isInfoEnabled()) {
				logger.info( "Checking link: " + linkToCheck);
			}
			
			url = new URL(linkToCheck);	// just for checking, we could use the plain String...
			HttpClient client = new HttpClient();
			// create get-method.
			GetMethod get = new GetMethod(url.toString());
			get.getParams().setParameter("http.socket.timeout", new Integer(timeout));
			get.getParams().setParameter("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2 (.NET CLR 3.5.30729)");
			
			// lets start working...
			client.executeMethod(get);
			
			// check response code
			if (get.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND)  {
				failure = true;
			}			
		}
		catch (MalformedURLException e) {
			if( logger.isInfoEnabled()) {
				logger.info( "Link URL malformed: " + linkToCheck);				// This is no "real error", this is a test result for the link. So we can log this at INFO leven
			}
			
			failure = true;
		}
		catch (UnknownHostException e) {
			if( logger.isInfoEnabled()) {
				logger.info( "Unknown host: " + linkToCheck);					// This is no "real error", this is a test result for the link. So we can log this at INFO leven
			}
			
			failure = true;			
		}
		catch (IOException e1) {
			logger.warn( "I/O error testing URL: " + linkToCheck, e1); 			// This is no "real error", this is a test result for the link. Since this could be any IO problem, let us report this at WARN level
			
			// some other connection problem, but link was found, so don't add it to invalid links.
			// invalidlinks.add(fullUrl);			
		}
		return failure;
	}	
	

}

package org.agnitas.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.agnitas.dao.impl.SyncObject;

public class SerializeRequestFilter implements Filter {

	private static final String SYNC_OBJECT_KEY = "SYNC_OBJECT_KEY";
	
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		final HttpSession session =  ((HttpServletRequest)request).getSession();
		synchronized (getSynchronizationObject(session)) {
			chain.doFilter(request, response);
		}

	}

	private static synchronized Object getSynchronizationObject( HttpSession session)  {
		SyncObject syncObject =   (SyncObject) session.getAttribute(SYNC_OBJECT_KEY);
		if ( syncObject == null ) {
			syncObject = new SyncObject();			
			session.setAttribute(SYNC_OBJECT_KEY,syncObject);
		}		
		return syncObject;
	}
	
	
	public void init(FilterConfig arg0) throws ServletException {
	
	}
	
	

}

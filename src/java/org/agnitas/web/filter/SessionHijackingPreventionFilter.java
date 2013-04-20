package org.agnitas.web.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.agnitas.util.NetworkUtil;
import org.apache.log4j.Logger;

/**
 * Servlet filter that hardens the web application against session hijacking.
 * 
 * The filter checks, if an IP address is bound to the session. If so, the filter checks, if the bound IP address is
 * the same as the current IP address of the client. In case of different IP addresses, the session gets invalidated.
 *
 * @author md
 */
public class SessionHijackingPreventionFilter implements Filter {
	
	/** Name of the session attribute containing the bound IP address. */
	private static final String IP_ATTRIBUTE = SessionHijackingPreventionFilter.class.getCanonicalName() + ".IP_ADDRESS";
	
	/** Logger. */
	private static final transient Logger logger = Logger.getLogger( SessionHijackingPreventionFilter.class);

	/** Set of white-listed IP addresses. Request from these addresses are forwarded directly without checking. */
	private Set<String> whitelistedAddresses;
	
	@Override
	public void init( FilterConfig config) throws ServletException {
		if( logger.isInfoEnabled()) {
			logger.info( "Initializing SessionHijackingPreventionFilter");
			logger.info( "Session attribute for client IP address is " + IP_ATTRIBUTE);
		}
		
		try {
			whitelistedAddresses = createIpWhitelist( config.getInitParameter( "ip-whitelist"));
			
		} catch( Exception e) {
			logger.error( "Error create IP whitelist", e);
			
			throw new ServletException( "Error listing local IP addresses", e);
		}
	}

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if( logger.isDebugEnabled()) {
			logger.debug( "Starting new request handling");
		}
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession( false);
		
		if( session != null) {
    		String clientIpAddress = request.getRemoteAddr();
    		String sessionIpAddress = (String) session.getAttribute( IP_ATTRIBUTE);
    				
    		if( logger.isInfoEnabled())
    			logger.info( "client IP address is " + clientIpAddress);
    		
    		if( logger.isDebugEnabled())
    			logger.info( "request URI is " + httpRequest.getRequestURI());
    		
    		if( isIpWhitelisted( clientIpAddress)) {
    			if( logger.isInfoEnabled()) {
    				logger.info( "Found client IP address in list of local addresses, proceeding without further handling");
    			}
    		} else {
	    		if( sessionIpAddress != null) {
	    			if( logger.isInfoEnabled())
	    				logger.info( "Found IP address bound to session " + session.getId() + " is " + sessionIpAddress);
	    			
	    			if( !sessionIpAddress.equals( clientIpAddress)) {
	    				logger.warn( "IP addresses does not match - invalidating session " + session.getId() + " (session: " + sessionIpAddress + ", client: " + clientIpAddress + ")");
	    				
	    				session.invalidate();
	    				
	    				session = httpRequest.getSession();
	    				session.setAttribute( IP_ATTRIBUTE, clientIpAddress);
	    			} else {
	    				if( logger.isInfoEnabled())
	    					logger.info( "IP addresses are matching");
	    			}
	    		} else {
	    			if( logger.isInfoEnabled())
	    				logger.info( "No IP address bound to session " + session.getId() + " - binding IP address");
	    			
	    			session.setAttribute( IP_ATTRIBUTE, clientIpAddress);
	    		}
    		}
		} else {
			if( logger.isInfoEnabled())
			logger.info( "No session available, proceeding without further handling");
			
			if( logger.isDebugEnabled())
				logger.debug( "Request URI: " + httpRequest.getRequestURI());
		}
			
		
		// Propagate request to next filter in chain
		if( chain != null)
			chain.doFilter( request, response);
	}
	
	@Override
	public void destroy() {
		// Dones nothing
	}

	/**
	 * Creates a set of all local IP adresses.
	 * 
	 * @return Set of local IP addresses
	 * 
	 * @throws SocketException on errors retrieving local IP addresses
	 */
	private Set<String> listLocalIpAddresses() throws SocketException {
		if( logger.isInfoEnabled())
			logger.info( "Listing local IP addresses");
		
		List<InetAddress> inetAddrList = NetworkUtil.listLocalInetAddresses();
		Set<String> result = new HashSet<String>();
		
		for( InetAddress addr : inetAddrList) {
			if( logger.isDebugEnabled()) {
				logger.debug( "local IP address " + addr.getHostAddress());
			}
			result.add( addr.getHostAddress());
		}
		
		return result;
	}
	
	private Set<String> getConfiguredWhitelistedIpAddresses( String configuredIpWhitelist) {
		Set<String> set = new HashSet<String>();

		if( configuredIpWhitelist != null) {
			if( logger.isDebugEnabled())
				logger.debug( "Manually configured IP whitelist: " + configuredIpWhitelist);
			
			String[] parts = configuredIpWhitelist.split( ",");
			
			for( String ipAddress : parts) {
				ipAddress = ipAddress.trim();
				
				if( logger.isDebugEnabled())
					logger.debug( "Manual white-listed IP: " + ipAddress);

				set.add( ipAddress);
			}
			
		} else {
			if( logger.isInfoEnabled())
				logger.info( "No manually configured IP whitelist");
		}
		
		return set;
	}
	
	private Set<String> createIpWhitelist( String configuredIpWhitelist) throws Exception {
		Set<String> set = new HashSet<String>();
		
		set.addAll( listLocalIpAddresses());
		set.addAll( getConfiguredWhitelistedIpAddresses( configuredIpWhitelist));
		
		return set;
	}
	
	private boolean isIpWhitelisted( String ipAddress) {
		return whitelistedAddresses.contains( ipAddress);
	}
}

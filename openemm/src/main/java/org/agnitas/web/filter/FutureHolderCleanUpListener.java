package org.agnitas.web.filter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Cleans up the future-holder when the users sessions ends. Maybe the user close his browser while a long running task hasn't finished.
 * The task would orphan otherwise.
 * The future holder is a map which controls which running futures are bound to which session.  
 * @author ms
 *
 */
public class FutureHolderCleanUpListener implements HttpSessionListener{


	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {

		HttpSession session = sessionEvent.getSession();
		String sessionID = session.getId();
		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		AbstractMap<String, Future> futureHolder = (AbstractMap<String, Future>) applicationContext.getBean("futureHolder");
		Set<String> keySet =  futureHolder.keySet();
		List<String> keysToRemove = new ArrayList<String>();
		
		for(String key:keySet) {
			if(key.endsWith("@"+sessionID) ) {
				keysToRemove.add(key);
			}
		}	
		
		for(String removeMe : keysToRemove) {
			futureHolder.remove(removeMe);
		}
		
	}

}

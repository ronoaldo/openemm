package org.agnitas.emm.extension;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PluginContext {
	public HttpServletRequest getServletRequest();
	public HttpServletResponse getServletResponse();
	public void includeJspFragment( String relativeUrl) throws IOException, ServletException;
}

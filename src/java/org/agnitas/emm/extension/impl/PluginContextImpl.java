package org.agnitas.emm.extension.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.emm.extension.PluginContext;

public class PluginContextImpl implements PluginContext {

	private final String pluginId;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	
	public PluginContextImpl( String pluginId, HttpServletRequest request, HttpServletResponse response) {
		this.pluginId = pluginId;
		this.request = request;
		this.response = response;
	}
	
	@Override
	public HttpServletRequest getServletRequest() {
		return this.request;
	}
	
	@Override
	public HttpServletResponse getServletResponse() {
		return this.response;
	}
	
	@Override
	public void includeJspFragment( String relativeUrl) throws IOException, ServletException {
		request.getRequestDispatcher( "plugins/" + this.pluginId + "/" + relativeUrl).include(  request, response);
	}

}

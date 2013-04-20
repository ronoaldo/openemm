package org.agnitas.emm.extension.examples;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.agnitas.emm.extension.AnnotatedDispatchingEmmFeatureExtension;
import org.agnitas.emm.extension.PluginContext;
import org.agnitas.emm.extension.exceptions.ExtensionException;
import org.java.plugin.registry.Extension;
import org.springframework.context.ApplicationContext;

public class ShowExtensionPoint extends AnnotatedDispatchingEmmFeatureExtension {

	@Override
	public void setup( PluginContext pluginContext, Extension extension, ApplicationContext context) throws ExtensionException {
		HttpServletRequest request = pluginContext.getServletRequest();
		
		request.setAttribute( "sidemenu_active", "none");
		request.setAttribute( "sidemenu_sub_active", "none");
		request.setAttribute( "agnTitleKey", "tab");
		request.setAttribute( "agnSubtitleKey", "tab");
		request.setAttribute( "agnSubtitleValue", "XYZ");
		request.setAttribute( "agnNavigationKey", "tab");
		request.setAttribute( "agnHighlightKey", "tab");
		request.setAttribute( "agnNavHrefAppend", "EXTENSIONPOINT=" + extension.getExtendedPointId());
		/*
		request.setAttribute( "agnPluginId", extension.getDeclaringPluginDescriptor().getId());
		request.setAttribute( "agnExtensionId", extension.getId());
		*/
	}

	@Override
	public void unspecifiedTarget( PluginContext pluginContext, Extension extension, ApplicationContext context) throws Throwable {
		PrintWriter out = pluginContext.getServletResponse().getWriter();
		
		out.println( "<script type='text/javascript'>alert('Extension point ID: " + extension.getExtendedPointId() + "')</script>");
	}

}

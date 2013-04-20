package org.agnitas.util;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class HelpUtils {

    public static String getHelpUrl(HttpServletRequest request) {
		StringBuilder manualIndexUrl = new StringBuilder("");
		String language = AgnUtils.getAdmin(request).getAdminLang().toLowerCase();
		manualIndexUrl.append("/manual/");
		manualIndexUrl.append(language);
		manualIndexUrl.append("/html/index.html");
		return manualIndexUrl.toString();
	}

    public static String getHelpPageUrl(HttpServletRequest req) {
		String langId = AgnUtils.getAdmin(req).getAdminLang().toLowerCase();
        String pageKey = (String) req.getAttribute("agnHelpKey");
        @SuppressWarnings("unchecked")
		String fileName = ((Map<String,String>) req.getSession().getAttribute("docMapping")).get(pageKey);
        String result = fileName == null ? "/manual/" + langId + "/html/index.html" : "/manual/" + langId + "/html/" + fileName;
		return result;
	}
}

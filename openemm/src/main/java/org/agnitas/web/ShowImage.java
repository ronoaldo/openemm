/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.web;

import org.agnitas.beans.MailingComponent;
import org.agnitas.dao.MailingComponentDao;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.TimeoutLRUMap;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShowImage extends HttpServlet {

    private static final long serialVersionUID = -595094416663851734L;
	private TimeoutLRUMap cacheMap=null;
    /**
     * If image not present in the cache, load it from database.<br>
     * If the image was found, it is stored into the cache. <br>
     * If image was not found, recorded "image not found" string into image data. <br>
     * Write image to response
     * <br><br>
     */
    public void service(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException {
        ServletOutputStream out = null;
        DeliverableImage aImage = null;
        MailingComponent comp = null;
    
        if(cacheMap == null) {    
             cacheMap = (TimeoutLRUMap) WebApplicationContextUtils.getWebApplicationContext(this.getServletContext()).getBean("imageCache");
        }
        if(req.getParameter("ci") == null || req.getParameter("mi") == null || req.getParameter("name") == null) {
            return;
        }
        
        if(req.getParameter("name").length() == 0) {
            return;
        }
        
        String cacheKey = req.getParameter("ci") + "-" + req.getParameter("mi") + "-" + req.getParameter("name");
        aImage = (DeliverableImage) cacheMap.get(cacheKey);
        
        if(aImage == null) {
            try {
                MailingComponentDao mDao = (MailingComponentDao) WebApplicationContextUtils.getWebApplicationContext(this.getServletContext()).getBean("MailingComponentDao");
                comp = mDao.getMailingComponentByName(Integer.parseInt(req.getParameter("mi")), Integer.parseInt(req.getParameter("ci")), req.getParameter("name"));
            } catch (Exception e) {
                AgnUtils.logger().error("Exception: "+e);
                AgnUtils.logger().error(AgnUtils.getStackTrace(e));
                return;
            }
           
            if(comp != null) {
                aImage = new DeliverableImage();
                aImage.mtype = comp.getMimeType();
                aImage.imageData = comp.getBinaryBlock();
                cacheMap.put(cacheKey, aImage);
                AgnUtils.logger().debug("added to cache: "+cacheKey);
            } else {
                aImage = new DeliverableImage();
                aImage.mtype = "text/html";
                aImage.imageData = "image not found".getBytes();
//                cacheMap.put(cacheKey, aImage);
                AgnUtils.logger().debug("added not found to cache: "+cacheKey);
            }
        }
        
        if(aImage != null) {
            try {
                res.setContentType(aImage.mtype);
                out = res.getOutputStream();
                out.write(aImage.imageData);
                out.flush();
                out.close();
            } catch (Exception e) {
                AgnUtils.logger().error(e.getMessage());
            }
        }
    }
    
    private class DeliverableImage {
        public byte[] imageData;
        public String mtype;
        public java.util.Date changeDate;
    }
}

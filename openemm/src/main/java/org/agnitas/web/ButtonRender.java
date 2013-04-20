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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.agnitas.beans.EmmLayoutBase;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.agnitas.util.TimeoutLRUMap;

public class ButtonRender extends HttpServlet {
    
    private static final long serialVersionUID = 2230190517295451462L;
	protected Font ttfFontS=null;
    protected Font ttfFontNN=null;
    protected Font ttfFontNH=null;
    protected String realPath=null;
    protected TimeoutLRUMap buttonCache=null;
    
    /**
     * Initialization.
     */
    public void init(ServletConfig config) throws ServletException {
        ServletContext aContext=config.getServletContext();
        
        buttonCache=new TimeoutLRUMap(500, 30000); // 500 entrys / 5000 ms
        
        // -Djava.awt.headless=true should be set in startup-script
        System.setProperty("java.awt.headless", "true");
        AgnUtils.logger().info("init: JDK "+System.getProperty("java.version"));
        
        try {
            ttfFontS=new Font("SansSerif", Font.BOLD, 14);
            ttfFontNN=new Font("SansSerif", Font.PLAIN, 18);
            ttfFontNH=new Font("SansSerif", Font.BOLD, 18);
        } catch (Exception e) {
            AgnUtils.logger().error("init: "+e.getMessage());
        }
        
        try {
            realPath=aContext.getRealPath("/");
        } catch (Exception e) {
            AgnUtils.logger().error("init: "+e.getMessage());
        }
        super.init(config);
    }
    
    /**
     * Draws the buttons.
     */
    
	public void doGet(HttpServletRequest req, HttpServletResponse response)
				throws IOException, ServletException {
		int buttonType=0;
		Image baseImage=null;
		BufferedImage image=null;
		Font theFont=null;
		Graphics2D g=null;
		EmmLayoutBase aLayout=null;
		double yPos=-1.0;
		double xPos=-1.0;
        
		if(req.getParameter("msg")==null) {
			AgnUtils.logger().info("doGet: no message");
			return;
		}
        
		try {
			xPos=Double.parseDouble(req.getParameter("lm"));
		} catch (Exception e) {
			xPos=-1.0;
		}
        
		try {
			buttonType=Integer.parseInt(req.getParameter("t"));
		} catch (Exception e) {
			buttonType=0; // Default
		}
       
		if(req.getSession().getAttribute("emmLayoutBase")!=null) {
			aLayout=(EmmLayoutBase)req.getSession().getAttribute("emmLayoutBase");
		}
		String localestring = "";
		Locale aLoc=null;

		if(req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)!=null) {
			aLoc=(Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
			localestring=aLoc.toString();
		} else {
			localestring=req.getLocale().toString();
			aLoc=req.getLocale();
		}
        
		String cacheKey=req.getParameter("msg")+"_"+xPos+"_"+buttonType+"_"+aLayout.getId()+"_"+localestring;
		byte[] theImage=(byte[])this.buttonCache.get(cacheKey);
        
		if(theImage==null) {
			String message=SafeString.getLocaleString(req.getParameter("msg"), aLoc);
			String	imageUrl=null;
			Color	color=null;
                        
			switch(buttonType) {
				case 1:	imageUrl=realPath+aLayout.getImagesURL()+"/button_nn.gif";
					theFont=ttfFontNN;
					color=Color.black;
					break;
                    
				case 2:	imageUrl=realPath+aLayout.getImagesURL()+"/button_nh.gif";
					theFont=ttfFontNH;
					color=Color.white;
					break;

				case 3:
					imageUrl=realPath+aLayout.getImagesURL()+"/button_g.gif";
					theFont=ttfFontS;
					color=Color.black;
					break;
				default:
					imageUrl=realPath+aLayout.getImagesURL()+"/button_s.gif";
					theFont=ttfFontS;
					color=Color.black;
					break;
			}
			try {
				baseImage=ImageIO.read(new File(imageUrl));
			} catch (Exception e) {
				AgnUtils.logger().error("doGet: "+e.getMessage());
			}
			image = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			g=image.createGraphics();
			g.setColor(color);
            
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.drawImage(baseImage, null, null);
			g.setFont(theFont);

			FontMetrics aMetrics=g.getFontMetrics(theFont);
			LineMetrics aLine=aMetrics.getLineMetrics(message, g);
                
			yPos=(baseImage.getHeight(null)/2)+((aLine.getAscent()+aLine.getDescent())/2);
			yPos=yPos-(aLine.getDescent());
			if(xPos==-1.0) {
				xPos=(baseImage.getWidth(null)-aMetrics.getStringBounds(message, g).getWidth())/2;
			}
			g.drawString(message, (int)xPos, (int)yPos);
			// Send image to the web browser

			ByteArrayOutputStream aBOut=new ByteArrayOutputStream();
            
			ImageIO.write(image, "png", aBOut);
			theImage=aBOut.toByteArray();
			this.buttonCache.put(cacheKey, theImage);
		}
		response.setContentType("image/png");  // Assign correct content-type
		ServletOutputStream aOut=response.getOutputStream();
		aOut.write(theImage);
	}
}

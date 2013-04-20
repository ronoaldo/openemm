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
package	org.agnitas.util;

import javax.servlet.ServletException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class	ExceptionProperties	extends Properties	{
	private static final long serialVersionUID = 2335686698133734099L;
	private	Throwable	exception;

	private void	load(Class cl) {
		Class	prev=cl.getSuperclass();

		if(prev != null)
			load(prev);

		try {
			ResourceBundle	res=PropertyResourceBundle.getBundle("exceptions."+cl.getName(), Locale.GERMAN);
			Enumeration keys=res.getKeys();

			while(keys.hasMoreElements()) {
				String	name=(String) keys.nextElement();

				setProperty(name,res.getString(name));
			}
		} catch(Exception e) {
			;
		}
	}

	public	ExceptionProperties(Exception e) {
		exception=e;
		if(e instanceof ServletException) {
			Throwable sub=((ServletException) e).getRootCause();
			if(sub != null)
				exception=sub;
		}
		String	message=exception.getMessage();

		if(message == null)
			message = "";

		StringTokenizer	msg=new StringTokenizer(message,"$");

		load(exception.getClass());
		if(msg.hasMoreTokens()) {
			Object[]	obj={	null,null,null,null,null,
						null,null,null,null,null
					};
			String	id=msg.nextToken();
			String	ret=getProperty(id);

			if(ret == null)
				ret=id;
			for(int c=0;c < 10 && msg.hasMoreTokens();c++)
				obj[c]=msg.nextElement();
                        AgnUtils.logger().info("ret: "+ret+" Obj: "+obj);
			setProperty("Message",MessageFormat.format(ret,obj));
			if((ret=getProperty(id+".Solution")) != null)
				setProperty("Solution",ret);
		} else
			setProperty("Message",message);
	}

	public void	printStackTrace(PrintWriter dst) {
		exception.printStackTrace(dst);
	}

	public void	printStackTrace(PrintStream dst) {
		exception.printStackTrace(dst);
	}
}


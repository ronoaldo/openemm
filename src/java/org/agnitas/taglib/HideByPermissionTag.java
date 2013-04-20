package org.agnitas.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.agnitas.beans.Admin;
import org.agnitas.util.AgnUtils;

public class HideByPermissionTag extends TagSupport {
    private static final long serialVersionUID = 4305002485480895206L;

    protected String token;    

    //***************************************
    //* Implementations for Tag
    //***************************************

    public void setToken(String mode) {
        if(mode!=null) {
            token=mode;
        } else {
            token = "";
        }
    }

    /**
     * permission control
     */
    @Override
    public int	doStartTag() throws JspException {
		Admin aAdmin = AgnUtils.getAdmin(pageContext);
        if(aAdmin!=null)
            if(aAdmin.permissionAllowed(token))
                return TagSupport.SKIP_BODY;

        return EVAL_BODY_INCLUDE;
    }

}

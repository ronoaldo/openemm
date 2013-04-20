package org.agnitas.util;


import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.InvalidReferenceEventHandler;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.util.introspection.Info;

public class EventHandler implements InvalidReferenceEventHandler,
        NullSetEventHandler, MethodExceptionEventHandler {

      ActionErrors errors = new ActionErrors();

      public EventHandler(Context ctx) {
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(this);
        ec.attachToContext(ctx);
      }
    @Override
    public Object methodException(Class aClass, String method, Exception e) throws Exception {
        String error = "an " + e.getClass().getName() + " was thrown by the " + method
        + " method of the " + aClass.getName() + " class [" + StringEscapeUtils.escapeHtml(e.getMessage().split("\n")[0]) + "]";
        errors.add(error, new ActionMessage("Method exception: " + error));
        return error;
    }

    @Override
    public boolean shouldLogOnNullSet(String s, String s1) {
        return false;
    }

    @Override
    public Object invalidGetMethod(Context context, String s, Object o, String s1, Info info) {
        String str = "Error in line " + info.getLine() + ", column " + info.getColumn() + ": ";
        errors.add(str,new ActionMessage(str + "Null reference " + s + "."));
        return null;
    }

    @Override
    public boolean invalidSetMethod(Context context, String s, String s1, Info info) {
        return false;
    }

    @Override
    public Object invalidMethod(Context context, String s, Object o, String s1, Info info) {
        String str = "Error in line " + info.getLine() + ", column " + info.getColumn() + ": ";
        errors.add(str, new ActionMessage(str + "Invalid method "+s+"."));
        return null;
    }

    public ActionErrors getErrors() {
        return errors;
    }
}


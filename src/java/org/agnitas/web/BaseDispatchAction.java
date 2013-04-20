package org.agnitas.web;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.agnitas.web.forms.StrutsFormBase;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

public class BaseDispatchAction extends DispatchAction {

    protected void initTableParams(int columnNum, StrutsFormBase form) {
        if (form.getColumnwidthsList() == null) {
            form.setColumnwidthsList(getInitializedColumnWidthList(columnNum));
        }
        if (form.getNumberofRows() < 0) {
            form.setNumberofRows(StrutsFormBase.DEFAULT_NUMBER_OF_ROWS);
        }
    }

    protected List<String> getInitializedColumnWidthList(int size) {
        List<String> columnWidthList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            columnWidthList.add("-1");
        }
        return columnWidthList;
    }

    protected void showSavedMessage(HttpServletRequest request) {
        ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("default.changes_saved"));
        saveMessages(request, messages);
    }
}

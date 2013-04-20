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

package org.agnitas.beans.factory.impl;

import org.agnitas.actions.ActionOperation;
import org.agnitas.actions.ops.*;
import org.agnitas.beans.factory.ActionOperationFactory;

public class ActionOperationFactoryImpl implements ActionOperationFactory {

    public ActionOperation newActionOperation(String type) {
        if (type.equals("GetArchiveList"))      return new GetArchiveList();
        if (type.equals("GetArchiveMailing"))   return new GetArchiveMailing();
        if (type.equals("ActivateDoubleOptIn")) return new ActivateDoubleOptIn();
        if (type.equals("SubscribeCustomer"))   return new SubscribeCustomer();
        if (type.equals("UnsubscribeCustomer")) return new UnsubscribeCustomer();
        if (type.equals("UpdateCustomer"))      return new UpdateCustomer();
        if (type.equals("GetCustomer"))         return new GetCustomer();
        if (type.equals("ExecuteScript"))       return new ExecuteScript();
        if (type.equals("SendMailing"))         return new SendMailing();
        if (type.equals("ServiceMail"))         return new ServiceMail();
        return null;

    }
}

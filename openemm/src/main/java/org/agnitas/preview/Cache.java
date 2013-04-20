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
package org.agnitas.preview;

import  java.util.Hashtable;
import  org.agnitas.backend.MailgunImpl;

class Cache {
    /** previous/next memeber */
    protected Cache     prev, next;
    /** creation date in epoch */
    protected long      ctime;
    /** mailingID we cache */
    protected long      mailingID;
    /** my instance of the mailgun */
    private MailgunImpl mailgun;
    /** my options for executing the mailgun */
    private Hashtable <String, Object>
                opts;

    protected Cache (long nMailingID, long nCtime, String text, boolean createAll, Preview creator) throws Exception {
        prev = null;
        next = null;
        ctime = nCtime;
        mailingID = nMailingID;
        mailgun = (MailgunImpl) creator.mkMailgun ();
        mailgun.initializeMailgun ("preview:" + mailingID);
        opts = new Hashtable <String, Object> ();
        if (text != null)
            opts.put ("preview-input", text);
        opts.put ("preview-create-all", createAll);
        mailgun.prepareMailgun (opts);
    }

    protected void release () throws Exception {
        mailgun.done ();
        mailgun = null;
    }

    protected Page makePreview (long customerID, String selector, boolean anon,
                    boolean convertEntities, boolean legacyUIDs,
                    boolean cachable, Preview creator) throws Exception {
        Page output = creator.mkPage ();

        opts.put ("preview-for", new Long (customerID));
        opts.put ("preview-output", output);
        opts.put ("preview-anon", new Boolean (anon));
        if (selector != null)
            opts.put ("preview-selector", selector);
        opts.put ("preview-convert-entities", new Boolean (convertEntities));
        opts.put ("preview-legacy-uids", new Boolean (legacyUIDs));
        opts.put ("preview-cachable", new Boolean (cachable));
        mailgun.executeMailgun (opts);
        return output;
    }
}

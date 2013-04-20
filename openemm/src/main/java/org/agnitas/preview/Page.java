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

public interface Page {
    public abstract void addContent (String key, String value);
    public abstract void setError (String msg);
    public abstract String getError ();
    public abstract Hashtable <String, Object> compatibilityRepresentation ();
    public abstract byte[] getPartByID (String id, String charset, boolean escape);
    public abstract byte[] getHeaderPart (String charset, boolean escape);
    public abstract byte[] getHeaderPart (String charset);
    public abstract byte[] getTextPart (String charset, boolean escape);
    public abstract byte[] getTextPart (String charset);
    public abstract byte[] getHTMLPart (String charset, boolean escape);
    public abstract byte[] getHTMLPart (String charset);
    public abstract byte[] getMHTMLPart (String charset, boolean escape);
    public abstract byte[] getMHTMLPart (String charset);
    public abstract String getByID (String id, boolean escape);
    public abstract String getStrippedByID (String id, boolean escape);
    public abstract String getHeader (boolean escape);
    public abstract String getHeader ();
    public abstract String getText (boolean escape);
    public abstract String getText ();
    public abstract String getHTML (boolean escape);
    public abstract String getHTML ();
    public abstract String getStrippedHTML (boolean escape);
    public abstract String getStrippedHTML ();
    public abstract String getMHTML (boolean escape);
    public abstract String getMHTML ();
    public abstract String getStrippedMHTML (boolean escape);
    public abstract String getStrippedMHTML ();
    public abstract String[] getIDs ();
    public abstract String[] getAttachmentNames ();
    public abstract byte[] getAttachment (String name);
    public abstract String[] getHeaderFields (String field);
    public abstract String getHeaderField (String field, boolean escape);
    public abstract String getHeaderField (String field);
}

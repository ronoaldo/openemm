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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.web.forms;

import org.agnitas.web.ImportBaseFileAction;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Base form that handles file panel
 *
 * @author Vyacheslav Stepanov
 */
public class ImportBaseFileForm extends StrutsFormBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6568670303380973540L;

	/**
     * property that stores current uploaded csv file name that will
     * be displayed in file panel
     */
    protected String currentFileName;

    /**
     * the uploaded csv-file
     */
    protected FormFile csvFile;

    public FormFile getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(FormFile csvFile) {
        this.csvFile = csvFile;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    /**
     * @return true if there's a csv file uploaded
     */
    public boolean getHasFile() {
        return !StringUtils.isEmpty(currentFileName);
    }

    @Override
    public ActionErrors formSpecificValidate(ActionMapping actionMapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        if (AgnUtils.parameterNotEmpty(request, "upload_file")
        		&& !AgnUtils.parameterNotEmpty(request, "remove_file")
        		&& !fileExists(request)) {
            try {
                if (csvFile == null || csvFile.getFileSize() <= 0) {
                    errors.add("csvFile", new ActionMessage("error.import.no_file"));
                } else {
                    if (!csvFile.getFileName().equals(URLEncoder.encode(csvFile.getFileName(), "utf-8"))) {
                        errors.add("csvFile", new ActionMessage("error.mailing.hosted_image_filename"));
                    }
                }
            } catch (IOException e) {
                errors.add("csvFile", new ActionMessage("error.import.no_file"));
            }
        }
        
        return errors;
    }

    private boolean fileExists(HttpServletRequest request) {
        return request.getSession().getAttribute(ImportBaseFileAction.CSV_ORIGINAL_FILE_NAME_KEY) != null;
    }
}
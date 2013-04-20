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

package org.agnitas.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.agnitas.beans.Admin;
import org.agnitas.util.AgnUtils;
import org.agnitas.web.forms.ImportBaseFileForm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

/**
 * Base action that manages csv file uploading and storing. When user uploads
 * csv file it is stored to struts temporary directory and the location of
 * file is stored to session attribute "stored-csv-file-path", the name of
 * file is stored to "original-csv-file-name". Later the file can be used by
 * subclasses of ImportBaseFileAction.
 * User will also have possibility to remove current uploaded file and upload
 * another one.
 * <p/>
 * The UI part is "filePanel.tag", the model part is {@link ImportBaseFileForm}
 *
 * @author Vyacheslav Stepanov
 */
public abstract class ImportBaseFileAction extends StrutsActionBase {

    public static final String CSV_FILE_PATH_KEY = "stored-csv-file-path";
    public static final String CSV_ORIGINAL_FILE_NAME_KEY = "original-csv-file-name";

    protected boolean fileUploadPerformed;

    protected boolean fileRemovePerformed;

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * <br>
     * Stores uploaded csv file to temporary directory or removes existing csv file depending on request attributes ("remove_file" or "upload_file")
     * <br>
     * @param mapping The ActionMapping used to select this instance
     * @param form    The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing. Should contain parameters "remove_file" or "upload_file".
     * @param res     The HTTP response we are creating
     * @throws java.io.IOException            if an input/output error occurs
     * @throws javax.servlet.ServletException if a servlet exception occurs
     * @return destination to logon page if user is not logged in or NULL
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse res)
            throws IOException, ServletException {

        // Validate the request parameters specified by the user
        ImportBaseFileForm aForm;
        ActionMessages errors = new ActionMessages();

        fileUploadPerformed = false;
        fileRemovePerformed = false;

        if (!AgnUtils.isUserLoggedIn(request)) {
            return mapping.findForward("logon");
        }
        if (form != null) {
            aForm = (ImportBaseFileForm) form;
        } else {
            aForm = new ImportBaseFileForm();
        }

        try {
            if (AgnUtils.parameterNotEmpty(request, "remove_file")) {
                removeStoredCsvFile(request);
                fileRemovePerformed = true;
            } else if (AgnUtils.parameterNotEmpty(request, "upload_file") &&
                    StringUtils.isEmpty(getCurrentFileName(request))) {
                errors.add(storeCsvFile(request, aForm.getCsvFile()));
            }
            aForm.setCurrentFileName(getCurrentFileName(request));
        }
        catch (Exception e) {
            AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return null;
    }

    /**
     * Stores uploaded csv file to temporary directory and the location is
     * store to session. So the file can be used later.
     *
     * @param request request
     * @param csvFile uploaded csv file
     * @return errors that happened
     */
    private ActionErrors storeCsvFile(HttpServletRequest request, FormFile csvFile) {
        ActionErrors errors = new ActionErrors();
        HttpSession session = request.getSession();
        String savePath = generateSavePath(session);
        File file = new File(savePath);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            inputStream = csvFile.getInputStream();
            outputStream = new FileOutputStream(file, false);
            IOUtils.copy(inputStream, outputStream);
            removeStoredCsvFile(request);
            session.setAttribute(CSV_FILE_PATH_KEY, savePath);
            session.setAttribute(CSV_ORIGINAL_FILE_NAME_KEY, csvFile.getFileName());
            fileUploadPerformed = true;
        } catch (IOException e) {
            errors.add("csvFile", new ActionMessage("error.import.no_file"));
            return errors;
        } finally {
        	IOUtils.closeQuietly(inputStream);
        	IOUtils.closeQuietly(outputStream);
        }
        return errors;
    }

    /**
     * Generated path in temporary directory for saving uploaded csv-file
     *
     * @param session current session
     * @return generated path
     */
    private String generateSavePath(HttpSession session) {
        int adminId = ((Admin) session.getAttribute("emm.admin")).getAdminID();
        Random random = new Random();
        int randomNum = random.nextInt();
        String savePath = getTempDir().getAbsolutePath() + File.separator +
                "tmp_csv_file_" + adminId + "_" + randomNum + ".csv";
        return savePath;
    }

    /**
     * Removes csv file that was uploaded earlier. Removes the file itself,
     * cleans session attribute that stores path to file
     *
     * @param request request
     */
    public void removeStoredCsvFile(HttpServletRequest request) {
        String filePath = (String) request.getSession().getAttribute(CSV_FILE_PATH_KEY);
        if (filePath == null) {
            return;
        }
        File csvFile = new File(filePath);
        if (csvFile.exists()) {
            csvFile.delete();
        }
        request.getSession().setAttribute(CSV_FILE_PATH_KEY, null);
        request.getSession().setAttribute(CSV_ORIGINAL_FILE_NAME_KEY, null);
    }

    /**
     * Gets the file name for showing it in file panel
     *
     * @param request request
     * @return file name
     */
    protected String getCurrentFileName(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(CSV_ORIGINAL_FILE_NAME_KEY);
    }

    /**
     * Gets current uploaded csv file
     *
     * @param request request
     * @return csv file if it's present or null in other case
     */
    protected File getCurrentFile(HttpServletRequest request) {
        String filePath = (String) request.getSession().getAttribute(CSV_FILE_PATH_KEY);
        if (filePath == null) {
            return null;
        }
        File csvFile = new File(filePath);
        return csvFile;
    }


}
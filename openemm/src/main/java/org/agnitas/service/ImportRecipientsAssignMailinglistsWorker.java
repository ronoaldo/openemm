package org.agnitas.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.concurrent.Callable;

import org.agnitas.beans.CustomerImportStatus;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.dao.ImportProfileDao;
import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.service.impl.NewImportWizardServiceImpl;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.EmailAttachment;
import org.agnitas.util.EmmCalendar;
import org.agnitas.util.ImportCsvGenerator;
import org.agnitas.util.ImportReportEntry;
import org.agnitas.util.ImportUtils;
import org.agnitas.util.importvalues.CheckForDuplicates;
import org.agnitas.util.importvalues.ImportMode;
import org.agnitas.util.importvalues.NullValuesAction;
import org.agnitas.web.forms.NewImportWizardForm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.ValidatorResults;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author viktor 11-Aug-2010 2:50:45 PM
 */
public class ImportRecipientsAssignMailinglistsWorker implements Callable, Serializable {

    private NewImportWizardForm aForm;
    private ImportProfileDao importProfileDao;
    private MessageSourceAccessor messageSourceAccessor;
    private Locale locale;
    private TimeZone zone;

    public ImportRecipientsAssignMailinglistsWorker(NewImportWizardForm aForm, ImportProfileDao importProfileDao, MessageSourceAccessor messageSourceAccessor, Locale locale, TimeZone zone) {
        this.aForm = aForm;
        this.importProfileDao = importProfileDao;
        this.messageSourceAccessor = messageSourceAccessor;
        this.locale = locale;
        this.zone = zone;

    }

    public Object call() throws Exception {
        // assign mailinglists (should take 90% of progress bar)
        assignMailinglists(aForm);
        // generateReportData (should take 10% of progress bar)
        generateReportData(aForm);
        return null;
    }

    private void assignMailinglists(NewImportWizardForm aForm) {
        storeAssignedMailingLists(aForm.getListsToAssign(), aForm);
    }

    /**
     * Stores mailing list assignments to DB, stored assignment statistics to form
     *
     * @param assignedLists mailing lists that were assigned by user
     * @param aForm         form
     */
    private void storeAssignedMailingLists(List<Integer> assignedLists, NewImportWizardForm aForm) {
        final NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        ImportProfile profile = wizardHelper.getImportProfile();

        Map<Integer, Integer> statisitcs = aForm.getImportWizardHelper().getImportRecipientsDao().assiggnToMailingLists(assignedLists,
                wizardHelper.getCompanyId(), aForm.getDatasourceId(), profile.getImportMode(), wizardHelper.getAdminId(), wizardHelper);
        // store data to form for result page
        aForm.setMailinglistAssignStats(statisitcs);
        List<Mailinglist> allMailingLists = aForm.getAllMailingLists();
        List<Mailinglist> assignedMailingLists = new ArrayList<Mailinglist>();
        for (Mailinglist mailinglist : allMailingLists) {
            if (assignedLists.contains(mailinglist.getId())) {
                assignedMailingLists.add(mailinglist);
            }
        }
        aForm.setAssignedMailingLists(assignedMailingLists);
    }

    /* Methods moved from NewImportWizardAction */


    /**
     * Generates report data: import statistics, recipient files; sends report email
     *
     * @param aForm   a form
     */
    private void generateReportData(NewImportWizardForm aForm) {
        NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        CustomerImportStatus status = aForm.getImportWizardHelper().getStatus();
        ImportProfile profile = aForm.getImportWizardHelper().getImportProfile();

        generateResultStatistics(aForm, status);
        wizardHelper.setCompletedPercent(91);

        Collection<ImportReportEntry> reportEntries = generateReportData(status, profile);
        wizardHelper.setCompletedPercent(92);

        final Map<String, String> statusReportMap = localizeReportItems(reportEntries);
        wizardHelper.setCompletedPercent(93);

        aForm.getImportWizardHelper().log(aForm.getDatasourceId(), status.getInserted() + status.getUpdated(), ImportUtils.describeMap(statusReportMap));
        aForm.setReportEntries(reportEntries);
        generateResultFiles(aForm);

        sendReportEmail(aForm);
        wizardHelper.setCompletedPercent(100);
    }

    private void generateResultStatistics(NewImportWizardForm aForm, CustomerImportStatus status) {
        ImportRecipientsDao dao = aForm.getImportWizardHelper().getImportRecipientsDao();
        final NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        int adminId = wizardHelper.getAdminId();
        int datasourceId = aForm.getDatasourceId();
        Integer[] types = {NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID};
        int page = 0;
        int rowNum = NewImportWizardService.BLOCK_SIZE;
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = null;
        while (recipients == null || recipients.size() == rowNum) {
            recipients = dao.getRecipientsByTypePaginated(types, page, rowNum, adminId, datasourceId);
            for (ValidatorResults validatorResults : recipients.values()) {
                for (CSVColumnState column : aForm.getImportWizardHelper().getColumns()) {
                    if (column.getImportedColumn()) {
                        if (!ImportUtils.checkIsCurrentFieldValid(validatorResults, column.getColName())) {
                            if (column.getColName().equals("email")) {
                                status.addError(NewImportWizardServiceImpl.EMAIL_ERROR);
                            } else if (column.getColName().equals("mailtype")) {
                                status.addError(NewImportWizardServiceImpl.MAILTYPE_ERROR);
                            } else if (column.getColName().equals("gender")) {
                                status.addError(NewImportWizardServiceImpl.GENDER_ERROR);
                            } else if (column.getType() == CSVColumnState.TYPE_DATE) {
                                status.addError(NewImportWizardServiceImpl.DATE_ERROR);
                            } else if (column.getType() == CSVColumnState.TYPE_NUMERIC) {
                                status.addError(NewImportWizardServiceImpl.NUMERIC_ERROR);
                            }
                        }
                    }
                }
            }
            page++;
        }
    }

        /**
     * Sends import report if profile has emailForReport
     *
     * @param aForm   a form
     */
    private void sendReportEmail(NewImportWizardForm aForm) {
        ImportProfile profile = importProfileDao.getImportProfileById(aForm.getDefaultProfileId());
        String address = profile.getMailForReport();
        if (!GenericValidator.isBlankOrNull(address) && GenericValidator.isEmail(address)) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
            Collection<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
            File invalidRecipientsFile = aForm.getInvalidRecipientsFile();
            File fixedRecipientsFile = aForm.getFixedRecipientsFile();
            EmailAttachment invalidRecipients = createZipAttachment(invalidRecipientsFile,
                    "invalid_recipients.zip", bundle.getString("import.recipients.invalid"));
            if (invalidRecipients != null) {
                attachments.add(invalidRecipients);
            }
            EmailAttachment fixedRecipients = createZipAttachment(fixedRecipientsFile,
                    "fixed_recipients.zip", bundle.getString("import.recipients.fixed"));
            if (fixedRecipients != null) {
                attachments.add(fixedRecipients);
            }
            EmailAttachment[] attArray = attachments.toArray(new EmailAttachment[]{});
            String subject = bundle.getString("import.recipients.report");
            String message = generateReportEmailBody(bundle, aForm);
            message = subject + ":\n" + message;
            ImportUtils.sendEmailWithAttachments( AgnUtils.getDefaultValue( "import.report.from.address"), AgnUtils.getDefaultValue( "import.report.from.name"), address, subject, message, attArray);
        }
    }

    /**
     * Generates body of report email
     *
     * @param bundle message resource bundle
     * @param aForm  a form
     * @return body of email containing import statistics
     */
    private String generateReportEmailBody(ResourceBundle bundle, NewImportWizardForm aForm) {
        String body = "";
        for (ImportReportEntry entry : aForm.getReportEntries()) {
            body = body + bundle.getString(entry.getKey()) + ": " + entry.getValue() + "\n";
        }
		if (aForm.getAssignedMailingLists() != null) {
	        for (Mailinglist mlist : aForm.getAssignedMailingLists()) {
	            String mlistStr = mlist.getShortname() + ": " + aForm.getMailinglistAssignStats().get(mlist.getId()) +
	                    " " + bundle.getString(aForm.getMailinglistAddMessage()) + "\n";
	            body = body + mlistStr;
        	}
		}
        return body;
    }

    /**
     * Generates report statistics: errros, update information, datasource id
     *
     * @param status  recipient import status
     * @param profile import profile
     * @return collection of statistics entries
     */
    private Collection<ImportReportEntry> generateReportData(CustomerImportStatus status, ImportProfile profile) {
        Collection<ImportReportEntry> reportValues = new ArrayList<ImportReportEntry>();
        reportValues.add(new ImportReportEntry("import.csv_errors_email",
                String.valueOf(status.getError(NewImportWizardService.EMAIL_ERROR))));
        reportValues.add(new ImportReportEntry("import.csv_errors_blacklist",
                String.valueOf(status.getError(NewImportWizardService.BLACKLIST_ERROR))));
        reportValues.add(new ImportReportEntry("import.csv_errors_double",
                String.valueOf(status.getError(NewImportWizardService.EMAILDOUBLE_ERROR))));
        reportValues.add(new ImportReportEntry("import.csv_errors_numeric",
                String.valueOf(status.getError(NewImportWizardService.NUMERIC_ERROR))));
        reportValues.add(new ImportReportEntry("import.csv_errors_mailtype",
                String.valueOf(status.getError(NewImportWizardService.MAILTYPE_ERROR))));
        reportValues.add(new ImportReportEntry("import.csv_errors_gender",
                String.valueOf(status.getError(NewImportWizardService.GENDER_ERROR))));
        reportValues.add(new ImportReportEntry("import.csv_errors_date",
                String.valueOf(status.getError(NewImportWizardService.DATE_ERROR))));
        reportValues.add(new ImportReportEntry("import.csv_errors_linestructure",
                String.valueOf(status.getError(NewImportWizardService.STRUCTURE_ERROR))));
        reportValues.add(new ImportReportEntry("import.RecipientsAllreadyinDB",
                String.valueOf(status.getAlreadyInDb())));
        reportValues.add(new ImportReportEntry("import.result.imported",
                String.valueOf(status.getInserted())));
        reportValues.add(new ImportReportEntry("import.result.updated",
                String.valueOf(status.getUpdated())));
        if (profile.getImportMode() == ImportMode.ADD.getIntValue() ||
                profile.getImportMode() == ImportMode.ADD_AND_UPDATE.getIntValue()) {
            reportValues.add(new ImportReportEntry("import.result.datasource_id",
                    String.valueOf(status.getDatasourceID())));
        }
        return reportValues;
    }


    /**
     * Creates attachment from zip-file
     *
     * @param recipientsFile file
     * @param name           name of attachment
     * @param description    attachment description
     * @return created attachment
     */
    private EmailAttachment createZipAttachment(File recipientsFile, String name, String description) {
        EmailAttachment attachment;
        if (recipientsFile != null) {
            try {
                byte[] data = FileUtils.readFileToByteArray(recipientsFile);
                attachment = new EmailAttachment(name, data, "application/zip", description);
                return attachment;
            } catch (IOException e) {
                AgnUtils.logger().error("Error creating attachment: " + AgnUtils.getStackTrace(e));
            }
        }
        return null;
    }

        /**
     * Method generates result recipient files (valid, invalid, fixed)
     * and stores them to form. If there are no recipients of some type
     * (i.e. invalid) the corresponding form file will be set to null
     *
     * @param aForm   a form
     */
    private void generateResultFiles(NewImportWizardForm aForm) {
        NewImportWizardService wizardHelper = aForm.getImportWizardHelper();
        // generate valid recipients file
        File validRecipients = createRecipientsCsv(aForm,
                new Integer[]{NewImportWizardService.RECIPIENT_TYPE_VALID,
                        NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT}, "valid_recipients");
        aForm.setValidRecipientsFile(validRecipients);
        wizardHelper.setCompletedPercent(94);
        // generate invalid recipietns file (invalid by wrong field values + other invalid: blacklisted etc.)
        File invalidRecipients = createRecipientsCsv(aForm,
                new Integer[]{NewImportWizardService.RECIPIENT_TYPE_INVALID,
                        NewImportWizardService.RECIPIENT_TYPE_FIELD_INVALID}, "invalid_recipients");
        aForm.setInvalidRecipientsFile(invalidRecipients);
        wizardHelper.setCompletedPercent(95);
        // generate fixed recipients file (fixed on error edit page)
        File fixedRecipients = createRecipientsCsv(aForm,
                new Integer[]{NewImportWizardService.RECIPIENT_TYPE_FIXED_BY_HAND}, "fixed_recipients");
        aForm.setFixedRecipientsFile(fixedRecipients);
        wizardHelper.setCompletedPercent(96);
        // generate duplicate recipients file
        File duplicateRecipients = createDuplicateRecipientsCsv(aForm,
                new Integer[]{NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_IN_NEW_DATA_RECIPIENT,
                        NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT}, "duplicate_recipients");
        aForm.setDuplicateRecipientsFile(duplicateRecipients);
        wizardHelper.setCompletedPercent(97);
        //generate result file
        File resultFile = generateResultFile(aForm);
        aForm.setResultFile(resultFile);
        wizardHelper.setCompletedPercent(98);

    }

        /**
     * Method generates recipients csv-file for the given types of recipients.
     * If there are no recipients of such type(s) in temporary table the
     * returned file will be null.
     *
     * @param aForm    a form
     * @param types    types of recipients to include in csv-file
     * @param fileName file name start part (random number will be appended)
     * @return generated csv-file
     */
    private File createRecipientsCsv(NewImportWizardForm aForm, Integer[] types, String fileName) {
        ImportRecipientsDao recipientDao = aForm.getImportWizardHelper().getImportRecipientsDao();
        int adminId = aForm.getImportWizardHelper().getAdminId();
        int datasourceId = aForm.getStatus().getDatasourceID();

        int recipientCount = recipientDao.getRecipientsCountByType(types, adminId, datasourceId);
        if (recipientCount == 0) {
            return null;
        }

        ImportProfile profile = importProfileDao.getImportProfileById(aForm.getDefaultProfileId());
        ImportCsvGenerator generator = new ImportCsvGenerator();
        CSVColumnState[] columns = aForm.getImportWizardHelper().getColumns();

        generator.createCsv(profile, columns, fileName);
        int page = 0;
        int rowNum = NewImportWizardService.BLOCK_SIZE;
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = null;
        while (recipients == null || recipients.size() == rowNum) {
            recipients = recipientDao.getRecipientsByTypePaginated(types, page, rowNum, adminId, datasourceId);
            generator.writeDataToFile(recipients.keySet(), columns, profile);
            page++;
        }
        File file = generator.finishFileGeneration();
        return file;
    }

      /**
     * Method generates recipients csv-file for the duplicate type of recipients.
     * If there are no recipients of such type(s) in temporary table the
     * returned file will be null.
     *
     * @param aForm    a form
     * @param types    types of recipients to include in csv-file
     * @param fileName file name start part (random number will be appended)
     * @return generated csv-file
     */
    private File createDuplicateRecipientsCsv(NewImportWizardForm aForm, Integer[] types, String fileName) {
        ImportRecipientsDao recipientDao = aForm.getImportWizardHelper().getImportRecipientsDao();
        int adminId = aForm.getImportWizardHelper().getAdminId();
        int datasourceId = aForm.getStatus().getDatasourceID();

        int recipientCount = recipientDao.getRecipientsCountByType(types, adminId, datasourceId);
        if (recipientCount == 0) {
            return null;
        }

        ImportProfile profile = importProfileDao.getImportProfileById(aForm.getDefaultProfileId());
        ImportCsvGenerator generator = new ImportCsvGenerator();
        CSVColumnState[] columns = aForm.getImportWizardHelper().getColumns();

        //add custom filed source of the duplicate
        final List<CSVColumnState> csvColumnStates = Arrays.asList(columns);
        List<CSVColumnState> columnsList = new ArrayList<CSVColumnState>();
        columnsList.addAll(csvColumnStates);
        final CSVColumnState sourceOfDuplicate = new CSVColumnState("SourceOfDuplicate", false, CSVColumnState.TYPE_CHAR);
        columnsList.add(sourceOfDuplicate);

        generator.createCsv(profile, columnsList.toArray(columns), fileName);
        int page = 0;
        int rowNum = NewImportWizardService.BLOCK_SIZE;
        HashMap<ProfileRecipientFields, ValidatorResults> recipients = null;
        for (Integer type : types) {
            recipients = null;
            page = 0;
            while (recipients == null || recipients.size() == rowNum) {
                recipients = recipientDao.getRecipientsByTypePaginated(new Integer[]{type}, page, rowNum, adminId, datasourceId);
                generator.writeDataToFileForDuplication(recipients.keySet(), columnsList.toArray(columns), profile, type);
                page++;
            }
        }
        File file = generator.finishFileGeneration();
        return file;
    }

    private File generateResultFile(NewImportWizardForm aForm) {
        String log_entry = "\n* * * * * * * * * * * * * * * * * *\n";
        EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
        my_calendar.changeTimeWithZone(zone);
        java.util.Date my_time = my_calendar.getTime();
        String datum = my_time.toString();
        java.text.SimpleDateFormat format01 = new java.text.SimpleDateFormat("yyyyMMdd");
        String aktDate = format01.format(my_calendar.getTime());

        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        String modeString = bundle.getString(ImportMode.getPublicValue(aForm.getStatus().getMode()));
        String doubletteString = bundle.getString(CheckForDuplicates.getPublicValue(aForm.getStatus().getDoubleCheck()));
        String ignoreString = bundle.getString(NullValuesAction.getPublicValue(aForm.getStatus().getIgnoreNull()));

        NewImportWizardService importWizardHelper = aForm.getImportWizardHelper();
        File resultFile = null;
        try {
            File systemUploadDirectory = AgnUtils.createDirectory(AgnUtils.getDefaultValue("system.upload"));
            resultFile = File.createTempFile(aktDate + "_", ".txt", systemUploadDirectory);
        } catch (IOException e) {
            AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
        }

        log_entry += datum + "\n";
        log_entry += "company_id: " + importWizardHelper.getCompanyId() + "\n";
        log_entry += "admin_id: " + importWizardHelper.getAdminId() + "\n";
        log_entry += "datasource_id: " + aForm.getDatasourceId() + "\n";
        log_entry += "mode: " + modeString + "\n";
        log_entry += "doublette-check: " + doubletteString + "\n";
        log_entry += "ignore null-values: " + ignoreString + "\n";
        log_entry += "separator: " + aForm.getStatus().getSeparator() + "\n";
        log_entry += "delimiter: " + aForm.getStatus().getDelimiter() + "\n";
        log_entry += "key-column: " + aForm.getStatus().getKeycolumn() + "\n";
        log_entry += "charset: " + aForm.getStatus().getCharset() + "\n";
        log_entry += "  csv_errors_email: " + aForm.getStatus().getError(NewImportWizardService.EMAIL_ERROR) + "\n";
        log_entry += "  csv_errors_blacklist: " + aForm.getStatus().getError(NewImportWizardService.BLACKLIST_ERROR) + "\n";
        log_entry += "  csv_errors_double: " + aForm.getStatus().getError(NewImportWizardService.EMAILDOUBLE_ERROR) + "\n";
        log_entry += "  csv_errors_numeric: " + aForm.getStatus().getError(NewImportWizardService.NUMERIC_ERROR) + "\n";
        log_entry += "  csv_errors_mailtype: " + aForm.getStatus().getError(NewImportWizardService.MAILTYPE_ERROR) + "\n";
        log_entry += "  csv_errors_gender: " + aForm.getStatus().getError(NewImportWizardService.GENDER_ERROR) + "\n";
        log_entry += "  csv_errors_date: " + aForm.getStatus().getError(NewImportWizardService.DATE_ERROR) + "\n";
        log_entry += "  csv_errors_linestructure: " + aForm.getStatus().getError(NewImportWizardService.STRUCTURE_ERROR) + "\n\n";

        if (aForm.getStatus().getUpdated() >= 0) {
            log_entry += "  csv records allready in db: " + aForm.getStatus().getAlreadyInDb() + "\n";
        }

        NewImportWizardService wizardHelper = aForm.getImportWizardHelper();

        if (wizardHelper.getImportProfile().getImportMode() == ImportMode.ADD.getIntValue() ||
                wizardHelper.getImportProfile().getImportMode() == ImportMode.ADD_AND_UPDATE.getIntValue()) {
            log_entry += "  inserted: " + aForm.getStatus().getInserted() + "\n";
        }

        if (wizardHelper.getImportProfile().getImportMode() == ImportMode.UPDATE.getIntValue() ||
                wizardHelper.getImportProfile().getImportMode() == ImportMode.ADD_AND_UPDATE.getIntValue()) {
            log_entry += "  updated: " + aForm.getStatus().getUpdated() + "\n";
        }

        log_entry += "* * * * * * * * * * * * * * * * * * *\n";

        try {
            Writer output = null;
            try {
                //use buffering
                AgnUtils.createDirectory(AgnUtils.getDefaultValue("system.upload"));
                output = new BufferedWriter(new FileWriter(resultFile, true));
                output.write(log_entry);
            } finally {
                //flush and close both "output" and its underlying FileWriter
                if (output != null) output.close();
            }
        } catch (Exception e) {
            AgnUtils.logger().error("execute: " + e + "\n" + AgnUtils.getStackTrace(e));
        }
        return resultFile;
    }


    private Map<String, String> localizeReportItems(Collection<ImportReportEntry> reportEntries) {
        final Map<String, String> statusReportMap = new HashMap<String, String>();
        for (ImportReportEntry entry : reportEntries) {
            final String messageKey = entry.getKey();
            final String localizedMessage = messageSourceAccessor.getMessage(messageKey, locale);
            statusReportMap.put(localizedMessage, entry.getValue());
        }
        return statusReportMap;
    }



}

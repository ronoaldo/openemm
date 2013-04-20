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

package org.agnitas.util;

import org.agnitas.beans.ColumnMapping;
import org.agnitas.beans.ImportProfile;
import org.agnitas.beans.ProfileRecipientFields;
import org.agnitas.service.csv.Toolkit;
import org.agnitas.service.impl.CSVColumnState;
import org.agnitas.service.NewImportWizardService;
import org.agnitas.util.importvalues.Charset;
import org.agnitas.util.importvalues.Separator;
import org.agnitas.util.importvalues.TextRecognitionChar;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Class generates recipients result files from recipients beans
 *
 * @author Vyacheslav Stepanov
 */
public class ImportCsvGenerator {

    private File csvFile = null;

    private PrintWriter outWriter = null;

    /**
     * Creates recipients csv-file
     *
     * @param profile       import profile
     * @param columnsInFile file columns
     * @param fileName      name of file to generate
     */
    public void createCsv(ImportProfile profile, CSVColumnState[] columnsInFile, String fileName) {
        List<ColumnMapping> columnMapping = profile.getColumnMapping();

        // create file for download
        initOutput(profile, fileName);
        if (outWriter == null) {
            return;
        }

        // write first line - column headers
        String delimiter = TextRecognitionChar.getValue(profile.getTextRecognitionChar());
        String separator = Separator.getValue(profile.getSeparator());
        for (CSVColumnState columnInFile : columnsInFile) {
            String columnName = getFileColumn(columnMapping, columnInFile.getColName());
            if (columnName == null) {
                columnName = columnInFile.getColName();
            }
            outWriter.print(delimiter + columnName + delimiter);
            if (columnInFile != columnsInFile[columnsInFile.length - 1]) {
                outWriter.print(separator);
            }
        }
        outWriter.print("\n");
    }

    /**
     * Writes recipients to csv-file
     *
     * @param recipients    list of recipients
     * @param columnsInFile file columns
     * @param profile       import profile
     */
    public void writeDataToFile(Collection<ProfileRecipientFields> recipients, CSVColumnState[] columnsInFile, ImportProfile profile) {
        String delimiter = TextRecognitionChar.getValue(profile.getTextRecognitionChar());
        String separator = Separator.getValue(profile.getSeparator());
        for (ProfileRecipientFields fieldsBean : recipients) {
            for (CSVColumnState columnInFile : columnsInFile) {
                String columnValue = "";
                try {
                    columnValue = Toolkit.getValueFromBean(fieldsBean, columnInFile.getColName());
                } catch (Exception e) {
                    AgnUtils.logger().error("Field value not found while creating csv " +
                            "file from recipients beans" + AgnUtils.getStackTrace(e));
                }
                outWriter.print(delimiter + columnValue + delimiter);
                if (columnInFile != columnsInFile[columnsInFile.length - 1]) {
                    outWriter.print(separator);
                }
            }
            outWriter.print("\n");
        }
    }

    /**
         * Writes recipients to csv-file
         *
         * @param recipients    list of recipients
         * @param columnsInFile file columns
         * @param profile       import profile
         */
        public void writeDataToFileForDuplication(Collection<ProfileRecipientFields> recipients, CSVColumnState[] columnsInFile, ImportProfile profile, Integer type) {
            String delimiter = TextRecognitionChar.getValue(profile.getTextRecognitionChar());
            String separator = Separator.getValue(profile.getSeparator());
            for (ProfileRecipientFields fieldsBean : recipients) {
                for (CSVColumnState columnInFile : columnsInFile) {
                    String columnValue = "";
                    if (columnInFile.getColName().equalsIgnoreCase("SourceOfDuplicate")) {
                        if (type == NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_IN_NEW_DATA_RECIPIENT) {
                            columnValue = "file";
                        } else if (type == NewImportWizardService.RECIPIENT_TYPE_DUPLICATE_RECIPIENT) {
                            columnValue = "db";
                        }
                    } else {

                        try {
                            columnValue = Toolkit.getValueFromBean(fieldsBean, columnInFile.getColName());
                        } catch (Exception e) {
                            AgnUtils.logger().error("Field value not found while creating csv " +
                                    "file from recipients beans" + AgnUtils.getStackTrace(e));
                        }
                    }
                    outWriter.print(delimiter + columnValue + delimiter);
                    if (columnInFile != columnsInFile[columnsInFile.length - 1]) {
                        outWriter.print(separator);
                    }
                }
                outWriter.print("\n");
            }
        }
    
    /**
     * Finishes writing of recipients file
     *
     * @return created file
     */
    public File finishFileGeneration() {
        outWriter.close();
        return csvFile;
    }

    /**
     * Method creates zip-file, puts it to temporary directory, adds one
     * entry to zip-file and sets print writer to it
     *
     * @param importProfile import profile
     * @param fileName      file name start
     */
    private void initOutput(ImportProfile importProfile, String fileName) {
        try {
            File systemUploadDirectory = AgnUtils.createDirectory(AgnUtils.getDefaultValue("system.upload"));
            csvFile = File.createTempFile(fileName + "_", ".zip", systemUploadDirectory);
            ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(csvFile));
            zipStream.putNextEntry(new ZipEntry(fileName + ".csv"));
            String charset = Charset.getValue(importProfile.getCharset());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(zipStream, charset);
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            outWriter = new PrintWriter(writer);
        } catch (IOException e) {
            AgnUtils.logger().error("Error during creating temporary recipient-file"
                    + AgnUtils.getStackTrace(e));
            outWriter = null;
            csvFile = null;
        }
    }

    /**
     * Method looks for dbColumn in mappings and returns corresponding
     * fileColumn
     *
     * @param mappings list of column mappings
     * @param dbColumn database column
     * @return corresponding file column in found (null otherwise)
     */
    private String getFileColumn(List<ColumnMapping> mappings, String dbColumn) {
        for (ColumnMapping columnMapping : mappings) {
            if (dbColumn.equals(columnMapping.getDatabaseColumn())) {
                return columnMapping.getFileColumn();
			}
		}
		return null;
	}
}

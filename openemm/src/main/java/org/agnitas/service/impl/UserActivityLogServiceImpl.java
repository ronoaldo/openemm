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
package org.agnitas.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agnitas.beans.Admin;
import org.agnitas.beans.AdminEntry;
import org.agnitas.beans.impl.PaginatedListImpl;
import org.agnitas.service.UserActivityLogService;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvTokenizer;
import org.agnitas.util.UserActivityLogActions;
import org.agnitas.web.forms.UserActivityLogForm;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.displaytag.pagination.PaginatedList;

/**
 * @author Viktor Gema
 */
public class UserActivityLogServiceImpl implements UserActivityLogService {

	@Override
    public PaginatedList getUserActivityLogByFilter(UserActivityLogForm aForm, int pageNumber, int rownums, int adminID, String sort, String direction, List<AdminEntry> admins) throws ParseException, IOException {

        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        final Date today = df.parse(df.format(new Date()));
        final Date fromDate = df.parse(aForm.getFromDate());
        final Date toDate = df.parse(aForm.getToDate());


        final Logger logger = AgnUtils.userlogger();
        DailyRollingFileAppender appender = (DailyRollingFileAppender) logger.getAppender("USERLOGS");
        final File currentDaylogFile = new File(appender.getFile());
        final File[] files = currentDaylogFile.getParentFile().listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.startsWith(currentDaylogFile.getName()) && !name.equals(currentDaylogFile.getName());
            }
        });
        if (aForm.getAll() == 0) {
            pageNumber = 1;
        }
        AgnUtils.logger().info("Found " + files.length + " log files");
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File file1, File file2) {
                Date file1Date = null;
                Date file2Date = null;
                String file1String = file1.getName().substring(currentDaylogFile.getName().length());
                if (!StringUtils.isEmpty(file1String)) {
                    try {
                        file1Date = df.parse(file1String.substring(1));
                    } catch (ParseException e) {
                        //do nothing
                    }
                }
                String file2String = file2.getName().substring(currentDaylogFile.getName().length());
                if (!StringUtils.isEmpty(file2String)) {
                    try {
                        file2Date = df.parse(file2String.substring(1));
                    } catch (ParseException e) {
                        //do nothing
                    }
                }
                if (file1Date != null && file2Date != null) {
                    if (file1Date.before(file2Date)) {
                        return -1;
                    } else if (file1Date.after(file2Date)) {
                        return 1;
                    }
                }
                return 0;
            }
        });
        int offset = (pageNumber - 1) * rownums;
        List<Map> result = new ArrayList<Map>();
        int rowCount = 0;
        int totalRows = 0;
        int[] counters = processFile(aForm, rownums, admins, df, today, fromDate, toDate, currentDaylogFile, offset, result, rowCount, totalRows, currentDaylogFile);
        rowCount = counters[0];
        totalRows = counters[1];
        for (int i = files.length - 1; i >= 0; i--) {
            File currentFile = files[i];
            int[] currentCounters = processFile(aForm, rownums, admins, df, today, fromDate, toDate, currentDaylogFile, offset, result, rowCount, totalRows, currentFile);
            rowCount = currentCounters[0];
            totalRows = currentCounters[1];
        }
        PaginatedListImpl paginatedList = new PaginatedListImpl(result, totalRows, rownums, pageNumber, sort, direction);
        return paginatedList;
    }

    private int[] processFile(UserActivityLogForm aForm, int rownums, List<AdminEntry> admins, DateFormat df, Date today, Date fromDate, Date toDate, File currentDayLogFile, int offset, List<Map> result, int rowCount, int totalRows, File currentFile) throws ParseException, IOException {
        List<Map> intermediateResult = new ArrayList<Map>();
        String currentDateString = currentFile.getName().substring(currentDayLogFile.getName().length());
        Date currentFileDate = null;
        if (!StringUtils.isEmpty(currentDateString)) {
            currentFileDate = df.parse(currentDateString.substring(1));
        }
        if ((currentFileDate == null && (today.equals(fromDate) || today.equals(toDate))) || (currentFileDate != null && ((currentFileDate.after(fromDate) && currentFileDate.before(toDate) || currentFileDate.equals(fromDate) || currentFileDate.equals(toDate))))) {
            AgnUtils.logger().info("Starting to process log file " + currentFile.getName());
            final InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(currentFile));
            final BufferedReader in = new BufferedReader(inputStreamReader);
            while (in.ready()) {
                final String line = in.readLine();
                if (line == null) {
                    break;
                }
                try {
                    String[] row = new CsvTokenizer(line, " ", "").toArray();
                    AgnUtils.logger().info("Successfully parsed log record: '" + line);
                    putLogIntoList(intermediateResult, row, 0);

                } catch (Exception e) {
                    AgnUtils.logger().error("Failed to parse log record: '" + line);
                    AgnUtils.logger().error("Structure log file error", e);
                }
            }
        } else {
            AgnUtils.logger().info("Skipping log file " + currentFile.getName());
        }
        if (!intermediateResult.isEmpty()) {
            for (Map map : intermediateResult) {
                boolean firstCondition = (StringUtils.isEmpty(aForm.getUsername()) || aForm.getUsername().equals("0")) && checkUsernameByCompanyId(map.get("username") + ":", admins);
                boolean secondCondition = aForm.getUsername() != null && aForm.getUsername().equals(map.get("username"));
                if (firstCondition || secondCondition) {
                    if (aForm.getUserActivityLogAction() == UserActivityLogActions.ANY.getIntValue() || actionMachesRequest(map, aForm) || notLoginOrLogout(map, aForm)|| loginOrLogout(map, aForm)) {
                        if (rownums > result.size()) {
                            if (rowCount >= offset) {
                                result.add(map);
                            }
                            rowCount++;
                        }
                        totalRows++;
                    }
                }
            }
        }
        return new int[]{rowCount, totalRows};
    }

    private boolean notLoginOrLogout(Map map,UserActivityLogForm aForm){
        return (aForm.getUserActivityLogAction() == UserActivityLogActions.ANY_WITHOUT_LOGIN.getIntValue() && !((map.get("description")).equals(" login")||(map.get("description")).equals(" logout")));
    }

    private boolean loginOrLogout(Map map,UserActivityLogForm aForm){
        return (aForm.getUserActivityLogAction() == UserActivityLogActions.LOGIN_LOGOUT.getIntValue() && ((map.get("description")).equals(" login")||(map.get("description")).equals(" logout")));
    }

    private boolean actionMachesRequest(Map map,UserActivityLogForm aForm){
        return (UserActivityLogActions.getLocalValue(aForm.getUserActivityLogAction()).equals(map.get("action")));
    }

    private boolean checkUsernameByCompanyId(String usernameFromFile, List admins) {
        for(int i=0; i<admins.size(); i++){
            String username = "";
            if(admins.get(i) instanceof Admin){
                Admin admin = (Admin) admins.get(i);
                username = admin.getUsername();
            } else {
                AdminEntry adminEntry = (AdminEntry)admins.get(i);
                username = adminEntry.getUsername();
            }
            if ((username + ":").equals(usernameFromFile)){
                return true;
            }
        }
        return false;
    }

    private void putLogIntoList(List<Map> result, String[] row, int dayOffset) throws ParseException, Exception {
        Map newBean = createRowBean(row);
        result.add(dayOffset, newBean);
    }

    private Map createRowBean(String[] row) throws ParseException, Exception {
        Map newBean = new HashMap();
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            newBean.put("date", dateFormat.parse(row[0] + " " + row[1].substring(0, row[1].indexOf(','))));
            newBean.put("username", row[2].length() != 0 ? row[2].substring(0, row[2].length() - 1) : "");
            newBean.put("action", row[3]);
            String description = "";
            for (int i = 4; i < row.length; i++) {
                description = description + " " + row[i];
            }
            newBean.put("description", description);
            AgnUtils.logger().info("Created log record object: date=" + newBean.get("date") + "; username=" + newBean.get("username") + "; action=" + newBean.get("action") + "; description=" + newBean.get("description"));
        } catch (Exception e) {
            AgnUtils.logger().error("Failed to create record object from values: '" + Arrays.toString(row) + "'", e);
            throw e;
        }
        return newBean;
    }


}

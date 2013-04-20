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
package org.agnitas.backend;

import java.util.HashMap;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import org.agnitas.util.Const;
import org.agnitas.util.Log;

/**
 * Collects the number of generated mails and stores
 * them in the database
 */
public class BillingCounter {
    /**
     * Current number of mails per mailtype
     */
    private long mailtypeCounter[];

    /**
     * Number of mails since last write to database
     */
    private long current_mails = 0;

    /**
     * Statement to store mails in database
     */
    private String  prep = null;
    private HashMap <String, Object>
            prepData = null;

    /**
     * Reference to configuration
     */
    private Data data = null;

    /**
     * Total number of mails
     */
    private long total_mails = 0;

    /**
     * Write world_mailing_backend_log_tbl
     */
    private boolean writeWorldLog = false;

    /**
     * Constructor
     * Creates an INSERT-statement for the number of mails
     * and throws an exception if the insert was failed.
     * Creates an UPDATE-statement for the status-id and tries to execute it.
     * Loggs if failes.
     *
     * @param data global data reference
     */
    public BillingCounter(Data data) throws Exception {
        this.data = data;
        mailtypeCounter = new long[Const.Mailtype.MAX];
        SimpleJdbcTemplate  jdbc = null;
        String  query = "INSERT INTO mailing_backend_log_tbl " +
                "(status_id, mailing_id, current_mails, total_mails, " + data.dbase.timestamp + ", creation_date) " +
                "VALUES (" + data.maildrop_status_id + ", " + data.mailing_id + ", 0, 0, " + data.dbase.sysdate + ", " + data.dbase.sysdate + ")";

        try {
            // init row in billing table2
            jdbc = data.dbase.request (query);
            jdbc.update (query);
            data.logging (Log.VERBOSE, "billing", "Init mailing_backend_log done.");
        } catch (Exception e) {
            data.logging (Log.ERROR, "billing", "Unable to init mailing_backend_log_tbl");
            throw new Exception ("Error: Could not setup mailing_backend_log_tbl: " + e.toString ());
        } finally {
            data.dbase.release (jdbc, query);
        }

        prep = "UPDATE mailing_backend_log_tbl SET current_mails = :currentMails, total_mails = :totalMails, " +
             data.dbase.timestamp + " = " + data.dbase.sysdate +" WHERE status_id = :statusID";
        prepData = new HashMap <String, Object> (3);
        prepData.put ("totalMails", data.totalReceivers);
        prepData.put ("statusID", data.maildrop_status_id);
    }

    /**
     * Adds a single mail to logging.
     *
     * @param mailtype of this mail
     */
    public void sadd (int mailtype) {
        total_mails++;
        current_mails++;
        if (mailtype < 0) {
            mailtype = 0;
        } else if (mailtype >= mailtypeCounter.length) {
            mailtype = 1;
        }
        mailtypeCounter[mailtype]++;
    }

    /**
     * update log table with current number of mails
     *
     * @param mail_count current number of mails
     */
    protected void update_log (long mail_count) throws Exception {
        SimpleJdbcTemplate  jdbc = null;

        prepData.put ("currentMails", mail_count);
        try {
            jdbc = data.dbase.request (prep, prepData);
            jdbc.update (prep, prepData);
            data.logging (Log.DEBUG, "billing", "Update log done at message no:" + mail_count);
        } catch (Exception e){
            data.logging (Log.ERROR, "billing", "Unable to update mailing_backend_log_tbl: " + e);
            throw new Exception ("ERROR! Could not update billing table with current_mails: " + e);
        } finally {
            data.dbase.release (jdbc, prep, prepData);
        }
    }

    /**
     * enable writing of world log entry
     */
    public void markAsWorldmailing () {
        writeWorldLog = true;
    }

    /***
     * write it to the database -- after all mails were written
     *
     * @param block_size size of an output block
     * @param block_count number of blocks generated
     */
    public void write_db (long block_size, long block_count) throws Exception {
        String end_backend_log =
            "UPDATE mailing_backend_log_tbl " +
            "SET current_mails = " + total_mails +  ", total_mails = " + total_mails + " " +
            "WHERE status_id = " + data.maildrop_status_id;
        String wend_backend_log =
            "INSERT INTO world_mailing_backend_log_tbl (mailing_id, current_mails, total_mails, " + data.dbase.timestamp + ", creation_date) " +
            "VALUES (" + data.mailing_id + ", " + total_mails + ", " + total_mails + ", " + data.dbase.sysdate + ", " + data.dbase.sysdate + ")";
        String currentQuery = null;
        SimpleJdbcTemplate  jdbc = null;

        try {
            currentQuery = end_backend_log;
            jdbc = data.dbase.request (currentQuery);
            jdbc.update (currentQuery);
            data.logging (Log.VERBOSE, "billing", "Final update backend_log done.");
            if (data.isWorldMailing () && writeWorldLog && (wend_backend_log != null)) {
                try {
                    data.dbase.release (jdbc, currentQuery);
                    currentQuery = wend_backend_log;
                    jdbc = data.dbase.request (currentQuery);
                    jdbc.update (currentQuery);
                } catch (Exception e) {
                    data.logging (Log.WARNING, "billing", "Unable to insert record into world_mailing_backend_log_tbl: " + e);
                }
            }
        } catch (Exception e){
            data.logging (Log.ERROR, "billing", "Unable to update mailing_backend_log_tbl using " + end_backend_log + ": " + e);
            throw new Exception ("Error writing final mailing_backend_tbl values: " + e);
        } finally {
            data.dbase.release (jdbc, currentQuery);
        }
    }

    /**
     * Write some runtime information to the logfile
     */
    public void output() {
        if (data.islog (Log.NOTICE)) {
            data.logging (Log.NOTICE, "billing", "Total mail message" + Log.exts (total_mails) + " written: " + total_mails);
            for (int i = 0; i < Const.Mailtype.MAX; ++i) {
                if (mailtypeCounter[i] > 0) {
                    data.logging (Log.NOTICE, "billing",
                          "Mailtype " + i + ": " + mailtypeCounter[i] +
                          " message" + Log.exts (mailtypeCounter[i]));
                }
            }
        }
    }

    /**
     * Write some final information to the logfile
     */
    public void debug_out () {
        if (data.islog (Log.DEBUG)) {
            data.logging (Log.DEBUG, "billing", "Mailtype/Number:");
            for (int n = 0; n < Const.Mailtype.MAX; ++n) {
                if (mailtypeCounter[n] > 0) {
                    data.logging (Log.DEBUG, "billing", "\t" + n + "/" + mailtypeCounter[n]);
                }
            }
        }
    }
}

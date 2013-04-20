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
package org.agnitas.service;

import org.agnitas.dao.ImportRecipientsDao;
import org.agnitas.service.impl.CSVColumnState;
import org.displaytag.pagination.PaginatedList;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * @author Viktor Gema
 */
public class ImportErrorRecipientQueryWorker implements Callable, Serializable {

    private ImportRecipientsDao dao;
    private Integer adminId;
    private CSVColumnState[] columns;
    private String sort;
    private String direction;
    private int previousFullListSize;
    private int page;
    private int rownums;
    private int datasourceId;


    public ImportErrorRecipientQueryWorker(ImportRecipientsDao dao, Integer adminId,
                                           String sort, String direction, int page, int rownums, int previousFullListSize, CSVColumnState[] columns, Integer datasourceId) {
        this.dao = dao;
        this.adminId = adminId;
        this.datasourceId = datasourceId;
        this.sort = sort;
        this.direction = direction;
        this.page = page;
        this.rownums = rownums;
        this.previousFullListSize = previousFullListSize;
        this.columns = columns;
    }

    public PaginatedList call() throws Exception {
        return dao.getInvalidRecipientList(columns, sort, direction, page, rownums, previousFullListSize, adminId, datasourceId);
    }

}

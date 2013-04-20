package org.agnitas.dao;


import java.util.Map;

public interface DocMappingDao {
   public String getHelpFilename(String helpKey);

    /**
     *Reads all values of pagekey and filename from doc_mapping_tbl table of database
     *
     * @return map of pagekey and filename for all records of doc_mapping_tbl table of database
     */
   public Map<String,String> getDocMapping();
}

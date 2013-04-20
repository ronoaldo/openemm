package org.agnitas.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.agnitas.dao.DocMappingDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class DocMappingDaoImpl implements DocMappingDao{
    private DataSource dataSource;

    @Override
    public String getHelpFilename(String helpKey) {
        return null;
    }

    @Override
    public Map<String,String> getDocMapping(){
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String sql = "select pagekey, filename from doc_mapping_tbl";
        List<Map> tmpList = template.queryForList(sql);
        HashMap<String, String> result = new HashMap<String, String>();
        if(tmpList != null){
           for(Map map : tmpList){
               String pagekey = (String) map.get("pagekey");
               String filename = (String) map.get("filename");
               result.put(pagekey,filename);
           }
        }
        return result;
    }

    public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}

package org.agnitas.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.impl.DynamicTagContentImpl;
import org.agnitas.dao.DynamicTagContentDao;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class DynamicTagContentDaoImpl implements DynamicTagContentDao {

	private static final Logger logger = Logger.getLogger( DynamicTagContentDaoImpl.class);
	
	private DataSource dataSource;
	
	private final RowMapper dynContentRowMapper = new RowMapper() {

		@Override
		public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
			DynamicTagContent content = new DynamicTagContentImpl();
			
			content.setId(resultSet.getInt("dyn_content_id"));
			content.setTargetID(resultSet.getInt("target_id"));
			content.setDynOrder(resultSet.getInt("dyn_order"));
			content.setDynContent(resultSet.getString("dyn_content"));
			content.setDynName(resultSet.getString("dyn_name"));
			content.setMailingID(resultSet.getInt("mailing_id"));
			content.setDynNameID(resultSet.getInt("dyn_name_id"));
			content.setCompanyID(resultSet.getInt("company_id"));
			
			return content;
		}
		
	};

	
	@Override
	public boolean deleteContent(int companyID, int contentID) {
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    	String deleteContentSQL = "DELETE from dyn_content_tbl WHERE dyn_content_id = ? AND company_id = ?";

    	Object[] params = new Object[]{contentID, companyID};
    	int affectedRows = 0;
    	affectedRows = jdbcTemplate.update(deleteContentSQL, params );
    	return affectedRows > 0;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public DynamicTagContent getContent(int companyID, int contentID) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			return (DynamicTagContent) jdbcTemplate.queryForObject(
					"SELECT dyn_content_tbl.dyn_content_id, dyn_content_tbl.target_id, dyn_content_tbl.dyn_order, dyn_content_tbl.dyn_content, dyn_name_tbl.dyn_name, dyn_name_tbl.mailing_id, dyn_content_tbl.company_id, dyn_content_tbl.dyn_name_id " +
					"FROM dyn_content_tbl, dyn_name_tbl " +
					"WHERE dyn_content_tbl.dyn_content_id = ? " +
					"AND dyn_content_tbl.company_id = ? " +
					"AND dyn_content_tbl.dyn_name_id = dyn_name_tbl.dyn_name_id"
					, new Object[] { contentID, companyID }, this.dynContentRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn( "Got no result", e);
			
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DynamicTagContent> getContentList(int companyID, int mailingID) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return (List<DynamicTagContent>) jdbcTemplate.query(
				"SELECT dyn_content_tbl.dyn_content_id, dyn_content_tbl.target_id, dyn_content_tbl.dyn_order, dyn_content_tbl.dyn_content, dyn_name_tbl.dyn_name, dyn_name_tbl.mailing_id, dyn_content_tbl.company_id, dyn_content_tbl.dyn_name_id " +
				"FROM dyn_content_tbl, dyn_name_tbl " +
				"WHERE dyn_content_tbl.dyn_name_id IN (SELECT dyn_name_id FROM dyn_name_tbl WHERE mailing_id = ? AND company_id = ?) " +
				"AND dyn_content_tbl.company_id = ? " +
				"AND dyn_content_tbl.dyn_name_id = dyn_name_tbl.dyn_name_id"
        , new Object[] { mailingID, companyID, companyID }, this.dynContentRowMapper);
	}


	@Override
	public void updateContent(DynamicTagContent dynamicTagContent) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("update dyn_content_tbl " +
				"set target_id = ?, " +
				"dyn_order = ?, " +
				"dyn_content = ? " +
				"WHERE dyn_content_id = ? " +
				"AND company_id = ?"
		, new Object[] { dynamicTagContent.getTargetID(),
				dynamicTagContent.getDynOrder(),
				dynamicTagContent.getDynContent(),
				dynamicTagContent.getId(),
				dynamicTagContent.getCompanyID() });
	}
	
}

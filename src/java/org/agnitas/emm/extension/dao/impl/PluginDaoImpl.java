package org.agnitas.emm.extension.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.agnitas.emm.extension.dao.PluginDao;
import org.agnitas.emm.extension.data.PluginData;
import org.agnitas.emm.extension.data.impl.PluginDataImpl;
import org.agnitas.emm.extension.exceptions.UnknownPluginException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class PluginDaoImpl implements PluginDao {

	private static class PluginDataRowMapper implements ParameterizedRowMapper<PluginData> {
		
		@Override
		public PluginData mapRow( ResultSet resultSet, int row) throws SQLException {
			PluginData pluginData = new PluginDataImpl();
			
			pluginData.setPluginId( resultSet.getString( "plugin_id"));
			pluginData.setActivatedOnStartup( resultSet.getBoolean( "activate_on_startup"));
			
			return pluginData;
		}
		
	}
	
	// --------------------------------------------------------------------- Business Logic
	
	private static final String GET_PLUGIN_DATA_SQL = "SELECT * FROM plugins_tbl WHERE plugin_id=?";
	private static final String UPDATE_PLUGIN_DATA_SQL = "UPDATE plugins_tbl SET activate_on_startup=? WHERE plugin_id=?";
	private static final String INSERT_PLUGIN_DATA_SQL = "INSERT INTO plugins_tbl (plugin_id, activate_on_startup) VALUES (?, ?)";
	private static final String DELETE_PLUGIN_DATA_SQL = "DELETE FROM plugins_tbl WHERE plugin_id=?";
	
	@Override
	public PluginData getPluginData( String pluginId) throws UnknownPluginException {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( this.dataSource);
		
		List<PluginData> list = template.query( GET_PLUGIN_DATA_SQL, new PluginDataRowMapper(), pluginId);
		
		if( list.size() == 0) {
			throw new UnknownPluginException( pluginId);
		} else {
			return list.get( 0);
		}
	}

	@Override
	public void savePluginData(PluginData pluginData) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( this.dataSource);

		int updated = template.update( UPDATE_PLUGIN_DATA_SQL, pluginData.isActivatedOnStartup(), pluginData.getPluginId());
		
		if( updated == 0) {
			template.update( INSERT_PLUGIN_DATA_SQL, pluginData.getPluginId(), pluginData.isActivatedOnStartup());
		}
	}
	
	@Override
	public void removePluginData( String pluginId) {
		SimpleJdbcTemplate template = new SimpleJdbcTemplate( this.dataSource);
		
		template.update( DELETE_PLUGIN_DATA_SQL, pluginId);
	}


	// --------------------------------------------------------------------- Dependency Injection
	private DataSource dataSource;
	
	public void setDataSource( DataSource dataSource) {
		this.dataSource = dataSource;
	}
}

package org.agnitas.dao.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.agnitas.beans.Campaign;
import org.agnitas.beans.impl.CampaignImpl;
import org.springframework.jdbc.core.RowMapper;

public class CampaignRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet resultSet, int row) throws SQLException {
		
		Campaign campaign = new CampaignImpl();
		campaign.setId(resultSet.getBigDecimal("campaign_id").intValue());
		campaign.setCompanyID(resultSet.getBigDecimal("company_id").intValue());
		campaign.setDescription(resultSet.getString("description"));
		campaign.setShortname(resultSet.getString("shortname"));
		
		return campaign;
	}

}

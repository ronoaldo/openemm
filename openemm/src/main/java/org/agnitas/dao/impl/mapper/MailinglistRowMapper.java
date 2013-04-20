package org.agnitas.dao.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.agnitas.beans.Mailinglist;
import org.agnitas.beans.impl.MailinglistImpl;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

public class MailinglistRowMapper implements ParameterizedRowMapper<Mailinglist> {

	@Override
	public Mailinglist mapRow( ResultSet rs, int row) throws SQLException {
		Mailinglist mailinglist = new MailinglistImpl();

		mailinglist.setCompanyID( rs.getInt( "company_id"));
		mailinglist.setDescription( rs.getString( "description"));
		mailinglist.setId( rs.getInt( "mailinglist_id"));
		mailinglist.setShortname( rs.getString( "shortname"));

		return mailinglist;
	}


}

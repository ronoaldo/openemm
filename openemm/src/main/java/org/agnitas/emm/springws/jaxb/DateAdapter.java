package org.agnitas.emm.springws.jaxb;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date>{
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	@Override
	public String marshal(Date dt) throws Exception {
        return format.format(dt);
    }

	@Override
	public Date unmarshal(String s) throws Exception {
        return format.parse(s);
    }

}

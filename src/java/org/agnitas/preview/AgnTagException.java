package org.agnitas.preview;

import java.util.List;
import java.util.Vector;

// Throw it when the TagCheckImpl decides that tag or content contains errors
public class AgnTagException extends RuntimeException {
	
	
	private static final long serialVersionUID = -4720583899796412192L;
	private List<String[]> report; // each element of the report is an array with 3 elements :  [0]=the block which contains the error(s), [1]= the tag which is wrong, [2] = an error description
	private Vector<String> failures;
	
	public AgnTagException(String message,List<String[]> report, Vector<String> failures) {
		super(message);
		this.report = report;
		this.failures = failures;
	}
	
	public AgnTagException(String message,List<String[]> report ) {
		super(message);
		this.report = report;
	}
	
	public List<String[]> getReport() {
		return report;
	}
	public Vector<String> getFailures() {
		return failures;
	}
	
	
}

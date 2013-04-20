package org.agnitas.util;

public class CsvDataException extends Exception {
	private static final long serialVersionUID = 5128483227215628395L;
	
	private int errorLineNumber = -1;
	
	public CsvDataException(int errorLineNumber) {
		super();
		this.errorLineNumber = errorLineNumber;
	}

	public CsvDataException(String message, int errorLineNumber, Throwable cause) {
		super(message, cause);
		this.errorLineNumber = errorLineNumber;
	}

	public CsvDataException(String message, int errorLineNumber) {
		super(message);
		this.errorLineNumber = errorLineNumber;
	}

	public CsvDataException(int errorLineNumber, Throwable cause) {
		super(cause);
		this.errorLineNumber = errorLineNumber;
	}

	public int getErrorLineNumber() {
		return errorLineNumber;
	}
}

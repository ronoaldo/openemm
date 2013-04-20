package org.agnitas.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class CsvReader implements Closeable {
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final char DEFAULT_SEPARATOR = ',';
	public static final char DEFAULT_STRING_QUOTE = '"';
	
	private char separator;
	private char stringQuote;
	private boolean useStringQuote;
	private InputStream inputStream;
	private Charset encoding;
	
	private boolean lineBreakInDataAllowed = true;
	private boolean escapedStringQuoteInDataAllowed = true;
	
	private boolean singleReadStarted = false;
	private int numberOfColumns = -1;
	private BufferedReader inputReader = null;
	private int readLines = 0;
	private int readCharacters = 0;
	
	private boolean fillMissingTrailingColumnsWithNull = false;

	public CsvReader(InputStream inputStream) {
		this(inputStream, Charset.forName(DEFAULT_ENCODING), DEFAULT_SEPARATOR, DEFAULT_STRING_QUOTE);
	}

	public CsvReader(InputStream inputStream, String encoding) {
		this(inputStream, Charset.forName(encoding), DEFAULT_SEPARATOR, DEFAULT_STRING_QUOTE);
	}
	
	public CsvReader(InputStream inputStream, Charset encoding) {
		this(inputStream, encoding, DEFAULT_SEPARATOR, DEFAULT_STRING_QUOTE);
	}
	
	public CsvReader(InputStream inputStream, char separator) {
		this(inputStream, Charset.forName(DEFAULT_ENCODING), separator, DEFAULT_STRING_QUOTE);
	}
	
	public CsvReader(InputStream inputStream, String encoding, char separator) {
		this(inputStream, Charset.forName(encoding), separator, DEFAULT_STRING_QUOTE);
	}
	
	public CsvReader(InputStream inputStream, Charset encoding, char separator) {
		this(inputStream, encoding, separator, DEFAULT_STRING_QUOTE);
	}

	public CsvReader(InputStream inputStream, char separator, Character stringQuote) {
		this(inputStream, Charset.forName(DEFAULT_ENCODING), separator, stringQuote);
	}
	
	public CsvReader(InputStream inputStream, String encoding, char separator, Character stringQuote) {
		this(inputStream, Charset.forName(encoding), separator, stringQuote);
	}
	
	public CsvReader(InputStream inputStream, Charset encoding, char separator, Character stringQuote) {
		this.inputStream = inputStream;
		this.encoding = encoding;
		this.separator = separator;
		if (stringQuote != null) {
			this.stringQuote = stringQuote;
			this.useStringQuote = true;
		} else {
			this.useStringQuote = false;
		}
		
		if (this.encoding == null) {
			throw new IllegalArgumentException("Encoding is null");
		} else if (this.inputStream == null) {
			throw new IllegalArgumentException("InputStream is null");
		} else if (AgnUtils.anyCharsAreEqual(this.separator, '\r', '\n')) {
			throw new IllegalArgumentException("Separator '" + this.separator + "' is invalid");
		} else if (useStringQuote && AgnUtils.anyCharsAreEqual(this.separator, this.stringQuote, '\r', '\n')) {
			throw new IllegalArgumentException("Stringquote '" + this.stringQuote + "' is invalid");
		}
	}
	
	public boolean isFillMissingTrailingColumnsWithNull() {
		return fillMissingTrailingColumnsWithNull;
	}

	public void setFillMissingTrailingColumnsWithNull(boolean fillMissingTrailingColumnsWithNull) {
		this.fillMissingTrailingColumnsWithNull = fillMissingTrailingColumnsWithNull;
	}
	
	public List<String> readNextCsvLine() throws IOException, CsvDataException {
		if (inputReader == null) {
			if (inputStream == null) {
				throw new IllegalStateException("CsvReader is already closed");
			}
			inputReader = new BufferedReader(new InputStreamReader(inputStream, encoding));
		}
		
		readLines++;
		singleReadStarted = true;
		List<String> returnList = new ArrayList<String>();
		StringBuilder nextValue = new StringBuilder();
		boolean insideString = false;
		int nextCharInt = -1;
		
		while ((nextCharInt = inputReader.read()) != -1) {
			readCharacters++;
			char nextChar = (char) nextCharInt;
			if (useStringQuote && nextChar == stringQuote) {
				insideString = !insideString;
				nextValue.append(nextChar);
			} else if (!insideString) {
				if (nextChar == '\r' || nextChar == '\n') {
					if (nextValue.length() > 0) {
						returnList.add(parseValue(nextValue.toString()));
					}
					if (returnList.size() > 0) {
						if (numberOfColumns != -1 && numberOfColumns != returnList.size()) {
							if (numberOfColumns == returnList.size() + 1 || (numberOfColumns > returnList.size() && fillMissingTrailingColumnsWithNull)) {
								while (returnList.size() < numberOfColumns) {
									returnList.add(null);
								}
							} else {
								throw new CsvDataException("Inconsistent number of values in line " + readLines + " (expected: " + numberOfColumns + " was: " + returnList.size() + ")", readLines);
							}
						}
						numberOfColumns = returnList.size();
						return returnList;
					}
				} else if (nextChar == separator) {
					returnList.add(parseValue(nextValue.toString()));
					nextValue = new StringBuilder();
				} else {
					nextValue.append(nextChar);
				}
			} else { // insideString
				if ((nextChar == '\r' || nextChar == '\n') && !lineBreakInDataAllowed) {
					throw new CsvDataException("Not allowed linebreak in data in line " + readLines, readLines);
				} else {
					nextValue.append(nextChar);
				}
			}
		}
		
		if (insideString) {
			close();
			throw new IOException("Unexpected EOF after quoted csv-value was started");
		} else {
			if (nextValue.length() > 0) {
				returnList.add(parseValue(nextValue.toString()));
			}
		
			if (returnList.size() > 0) {
				if (numberOfColumns != -1 && numberOfColumns != returnList.size()) {
					if (numberOfColumns == returnList.size() + 1 || (numberOfColumns > returnList.size() && fillMissingTrailingColumnsWithNull)) {
						while (returnList.size() < numberOfColumns) {
							returnList.add(null);
						}
					} else {
						throw new CsvDataException("Inconsistent number of values in line " + readLines + " (expected: " + numberOfColumns + " was: " + returnList.size() + ")", readLines);
					}
				}
				numberOfColumns = returnList.size();
				return returnList;
			} else {
				close();
				return null;
			}
		}
	}
	
	public List<List<String>> readAll() throws IOException, CsvDataException {
		if (singleReadStarted) {
			throw new IllegalStateException("Single readNextCsvLine was called before readAll");
		}
		
		try {
			List<List<String>> csvValues = new ArrayList<List<String>>();
			List<String> lineValues;
			while ((lineValues = readNextCsvLine()) != null) {
				csvValues.add(lineValues);
			}
			return csvValues;
		} finally {
			close();
		}
	}
	
	private String parseValue(String rawValue) throws CsvDataException {
		String returnValue = rawValue;
		String stringQuoteString = Character.toString(stringQuote);
		
		if (StringUtils.isNotEmpty(returnValue)) {
			if (useStringQuote
					&& returnValue.charAt(0) == stringQuote
					&& returnValue.charAt(returnValue.length() - 1) == stringQuote) {
				returnValue = returnValue.substring(1, returnValue.length() - 1);
				returnValue = returnValue.replace(stringQuoteString + stringQuoteString, stringQuoteString);
			}
			returnValue = returnValue.replace("\r\n", "\n").replace('\r', '\n');
		}
		
		if (!escapedStringQuoteInDataAllowed && returnValue.indexOf(stringQuote) >= 0) {
			throw new CsvDataException("Not allowed stringquote in data in line " + readLines, readLines);
		} 

		return returnValue;
	}

	@Override
	public void close() {
		IOUtils.closeQuietly(inputReader);
		inputReader = null;
		IOUtils.closeQuietly(inputStream);
		inputStream = null;
	}

	public int getReadLines() {
		return readLines;
	}
	
	public int getReadChracters() {
		return readCharacters;
	}

	public boolean isLineBreakInDataAllowed() {
		return lineBreakInDataAllowed;
	}

	public void setLineBreakInDataAllowed(boolean lineBreakInDataAllowed) {
		this.lineBreakInDataAllowed = lineBreakInDataAllowed;
	}

	public boolean isEscapedStringQuoteInDataAllowed() {
		return escapedStringQuoteInDataAllowed;
	}

	public void setEscapedStringQuoteInDataAllowed(boolean escapedStringQuoteInDataAllowed) {
		this.escapedStringQuoteInDataAllowed = escapedStringQuoteInDataAllowed;
	}
	
	/**
	 * This method reads the stream to the end and counts all csv value lines,
	 * which can be less than the absolute linebreak count of the stream for the reason of quoted linebreaks.
	 * The result also contains the first line, which may consist of columnheaders.
	 * 
	 * @return
	 * @throws IOException
	 * @throws CsvDataException
	 */
	public int getCsvLineCount() throws IOException, CsvDataException {
		if (singleReadStarted) {
			throw new IllegalStateException("Single readNextCsvLine was called before getCsvLineCount");
		}
		
		try {
			int csvLineCount = 0;
			while (readNextCsvLine() != null) {
				csvLineCount++;
			}
			return csvLineCount;
		} finally {
			close();
		}
	}
	
	public static List<String> parseCsvLine(char separator, char stringQuote, String csvLine) throws IOException {
		List<String> returnList = new ArrayList<String>();
		StringBuilder nextValue = new StringBuilder();
		boolean insideString = false;
		
		for (char nextChar : csvLine.toCharArray()) {
			if (nextChar == stringQuote) {
				insideString = !insideString;
				nextValue.append(nextChar);
			} else if (!insideString) {
				if (nextChar == separator) {
					returnList.add(parseValue(stringQuote, nextValue.toString()));
					nextValue = new StringBuilder();
				} else {
					nextValue.append(nextChar);
				}
			} else { // insideString
				nextValue.append(nextChar);
			}
		}
		
		if (insideString) {
			throw new IOException("Unexpected EOL after quoted csv-value was started");
		} else {
			if (nextValue.length() > 0) {
				returnList.add(parseValue(stringQuote, nextValue.toString()));
			}
		
			if (returnList.size() > 0) {
				return returnList;
			} else {
				return null;
			}
		}
	}
	

	
	private static String parseValue(char stringQuote, String rawValue) {
		String returnValue = rawValue;
		String stringQuoteString = Character.toString(stringQuote);
		
		if (StringUtils.isNotEmpty(returnValue)) {
			if (returnValue.charAt(0) == stringQuote
					&& returnValue.charAt(returnValue.length() - 1) == stringQuote) {
				returnValue = returnValue.substring(1, returnValue.length() - 1);
				returnValue = returnValue.replace(stringQuoteString + stringQuoteString, stringQuoteString);
			}
			returnValue = returnValue.replace("\r\n", "\n").replace('\r', '\n');
		} 

		return returnValue;
	}
}

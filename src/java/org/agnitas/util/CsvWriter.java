package org.agnitas.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class CsvWriter extends Writer {
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final char DEFAULT_SEPARATOR = ',';
	public static final char DEFAULT_STRING_QUOTE = '"';
	public static final String DEFAULT_LINEBREAK = "\n";
	
	private char separator;
	private String separatorString;
	private char stringQuote;
	private String stringQuoteString;
	private String doubleStringQuoteString;
	private boolean useStringQuote;
	private String lineBreak;
	private OutputStream outputStream;
	private Charset encoding;
	private boolean alwaysQuote = false;
	private boolean quoteAllStrings = false;
	
	private int writtenLines = 0;
	private int numberOfColumns = -1;
	private BufferedWriter outputWriter = null;

	public CsvWriter(OutputStream outputStream) {
		this(outputStream, Charset.forName(DEFAULT_ENCODING), DEFAULT_SEPARATOR, DEFAULT_STRING_QUOTE, DEFAULT_LINEBREAK);
	}

	public CsvWriter(OutputStream outputStream, String encoding) {
		this(outputStream, Charset.forName(encoding), DEFAULT_SEPARATOR, DEFAULT_STRING_QUOTE, DEFAULT_LINEBREAK);
	}
	
	public CsvWriter(OutputStream outputStream, Charset encoding) {
		this(outputStream, encoding, DEFAULT_SEPARATOR, DEFAULT_STRING_QUOTE, DEFAULT_LINEBREAK);
	}
	
	public CsvWriter(OutputStream outputStream, char separator) {
		this(outputStream, Charset.forName(DEFAULT_ENCODING), separator, DEFAULT_STRING_QUOTE, DEFAULT_LINEBREAK);
	}
	
	public CsvWriter(OutputStream outputStream, String encoding, char separator) {
		this(outputStream, Charset.forName(encoding), separator, DEFAULT_STRING_QUOTE, DEFAULT_LINEBREAK);
	}
	
	public CsvWriter(OutputStream outputStream, Charset encoding, char separator) {
		this(outputStream, encoding, separator, DEFAULT_STRING_QUOTE, DEFAULT_LINEBREAK);
	}
	
	public CsvWriter(OutputStream outputStream, char separator, Character stringQuote) {
		this(outputStream, Charset.forName(DEFAULT_ENCODING), separator, stringQuote, DEFAULT_LINEBREAK);
	}
	
	public CsvWriter(OutputStream outputStream, char separator, Character stringQuote, String lineBreak) {
		this(outputStream, Charset.forName(DEFAULT_ENCODING), separator, stringQuote, lineBreak);
	}

	public CsvWriter(OutputStream outputStream, String encoding, char separator, Character stringQuote) {
		this(outputStream, Charset.forName(encoding), separator, stringQuote, DEFAULT_LINEBREAK);
	}
	
	public CsvWriter(OutputStream outputStream, Charset encoding, char separator, Character stringQuote, String lineBreak) {
		this.outputStream = outputStream;
		this.encoding = encoding;
		this.separator = separator;
		separatorString = Character.toString(separator);
		this.lineBreak = lineBreak;
		if (stringQuote != null) {
			this.stringQuote = stringQuote;
			stringQuoteString = Character.toString(stringQuote);
			doubleStringQuoteString = stringQuoteString + stringQuoteString;
			this.useStringQuote = true;
		} else {
			this.useStringQuote = false;
		}
		
		if (this.encoding == null) {
			throw new IllegalArgumentException("Encoding is null");
		} else if (this.outputStream == null) {
			throw new IllegalArgumentException("OutputStream is null");
		} else if (AgnUtils.anyCharsAreEqual(this.separator, '\r', '\n')) {
			throw new IllegalArgumentException("Separator '" + this.separator + "' is invalid");
		} else if (useStringQuote && AgnUtils.anyCharsAreEqual(this.separator, this.stringQuote, '\r', '\n')) {
			throw new IllegalArgumentException("Stringquote '" + this.stringQuote + "' is invalid");
		} else if (!this.lineBreak.equals("\r") && !this.lineBreak.equals("\n") && !this.lineBreak.equals("\r\n")) {
			throw new IllegalArgumentException("Given linebreak is invalid");
		}
	}
	
	public boolean isAlwaysQuote() {
		return alwaysQuote;
	}

	public void setAlwaysQuote(boolean alwaysQuote) {
		this.alwaysQuote = alwaysQuote;
		if (alwaysQuote) {
			quoteAllStrings = false;
		}
	}

	public boolean isQuoteAllStrings() {
		return quoteAllStrings;
	}

	public void setQuoteAllStrings(boolean quoteAllStrings) {
		this.quoteAllStrings = quoteAllStrings;
		if (quoteAllStrings) {
			alwaysQuote = false;
		}
	}

	public void writeValues(Object... values) throws Exception {
		writeValues(Arrays.asList(values));
	}
	
	public void writeValues(List<? extends Object> values) throws CsvDataException, IOException {
		if (numberOfColumns != -1 && (values == null || numberOfColumns != values.size())) {
			throw new CsvDataException("Inconsistent number of values after " + writtenLines + " written lines (expected: " + numberOfColumns + " was: " + (values == null ? "null" : values.size()) + ")", writtenLines);
		}
		
		if (outputWriter == null) {
			if (outputStream == null) {
				throw new IllegalStateException("CsvWriter is already closed");
			}
			outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream, encoding));
		}
		
		boolean isFirst = true;
		for (Object value : values) {
			if (!isFirst) {
				outputWriter.write(separator);
			}
			isFirst = false;
			
			outputWriter.write(escapeValue(value));
		}
		outputWriter.write(lineBreak);

		writtenLines++;
		numberOfColumns = values.size();
	}
	
	public void writeAll(List<List<? extends Object>> valueLines) throws Exception {
		for (List<? extends Object> valuesOfLine : valueLines) {
			writeValues(valuesOfLine);
		}
	}
	
	private String escapeValue(Object value) throws CsvDataException {
		String valueString = "";
		if (value != null) {
			valueString = value.toString();
		}
		
		if (alwaysQuote || (quoteAllStrings && value instanceof String) || valueString.contains(separatorString) || valueString.contains("\r") || valueString.contains("\n") || (useStringQuote && valueString.contains(stringQuoteString))) {
			if (!useStringQuote) {
				throw new CsvDataException("StringQuote was deactivated but is needed for csv-value after " + writtenLines + " written lines", writtenLines);
			} else {
				StringBuilder escapedValue = new StringBuilder(); 
				escapedValue.append(stringQuote);
				escapedValue.append(valueString.replace(stringQuoteString, doubleStringQuoteString));
				escapedValue.append(stringQuote);
				return escapedValue.toString();
			}
		} else {
			return valueString;
		}
	}

	@Override
	public void close() {
		IOUtils.closeQuietly(outputWriter);
		outputWriter = null;
		IOUtils.closeQuietly(outputStream);
		outputStream = null;
	}
	
	public int getWrittenLines() {
		return writtenLines;
	}

	/**
	 * Not supported method
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		throw new IOException("Write by offset and length is not supported by " + getClass().getSimpleName());
	}

	@Override
	public void flush() throws IOException {
		if (outputWriter != null) {
			outputWriter.flush();
		}
	}
	
	public static String getCsvLine(char separator, char stringQuote, List<? extends Object> values) {
		return getCsvLine(separator, stringQuote, values.toArray());
	}
	
	public static String getCsvLine(char separator, char stringQuote, Object... values) {
		StringBuilder returnValue = new StringBuilder();
		String separatorString = Character.toString(separator);
		String stringQuoteString = Character.toString(stringQuote);
		String doubleStringQuoteString = stringQuoteString + stringQuoteString;
		if (values != null) {
			for (Object value : values) {
				if (returnValue.length() > 0) {
					returnValue.append(separator);
				}
				if (value != null) {
					String valueString = value.toString();
					if (valueString.contains(separatorString) || valueString.contains("\r") || valueString.contains("\n") || valueString.contains(stringQuoteString)) {
						returnValue.append(stringQuoteString);
						returnValue.append(valueString.replace(stringQuoteString, doubleStringQuoteString));
						returnValue.append(stringQuoteString);
					} else {
						returnValue.append(valueString);
					}
				}
			}
		}
		return returnValue.toString();
	}
}

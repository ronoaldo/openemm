package org.agnitas.exceptions;

/**
 * @author: Igor
 */
public class EncodingError {
    private String strWithError;
    private int line;
    private int column;
    private char invalidChar;

    public EncodingError(String strWithError, int line, int column){
        this(strWithError, line, column, '\0');
    }

    public EncodingError(String strWithError, int line, int column, char invalidChar){
           this.strWithError = strWithError;
           this.line = line;
           this.column = column;
           this.invalidChar = invalidChar;
    }

    public String getStrWithError() {
        return strWithError;
    }

    public void setStrWithError(String strWithError) {
        this.strWithError = strWithError;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public char getInvalidChar() {
        return invalidChar;
    }

    public void setInvalidChar(char invalidChar) {
        this.invalidChar = invalidChar;
    }
}

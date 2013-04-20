package org.agnitas.emm.extension.sqlparser;

/**
 * Utility class used by the states of the SQL parser.
 * 
 * @author md
 */
class StatementBuffer {
	/** Wrapped StringBuffer.*/
	private StringBuffer buffer;
	
	/**
	 * Creates a new StatementBuffer instance.
	 */
	public StatementBuffer() {
		this.buffer = new StringBuffer();
	}
	
	/**
	 * Appends a char to the buffered statement. Since the parameter is of type int,
	 * nothing happens for values less than zero.
	 * 
	 * @param i character to append
	 */
	public void appendChar( int i) {
		if( i < 0)
			return;
		
		buffer.append( (char) i);
	}
	
	/**
	 * Appends a char to the buffered statement.
	 * 
	 * @param c character to append
	 */
	public void appendChar( char c) {
		buffer.append( c);
	}
	
	@Override
	public String toString() {
		return this.buffer.toString().trim();
	}
	
	/**
	 * Clears the statement buffer. Internally, a new StringBuffer is created.
	 */
	public void clear() {
		this.buffer = new StringBuffer();
	}
}

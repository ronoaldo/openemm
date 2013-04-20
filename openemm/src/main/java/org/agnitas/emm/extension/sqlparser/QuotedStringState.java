package org.agnitas.emm.extension.sqlparser;

/**
 * Implementation of the ParserState interface.
 * 
 * This state indicates, that the parser is now inside a string. To distinguish between the three possible
 * string types (single-quoted, double-quoted, backtick-quoted) the constructor takes the delimiter character.
 * 
 * @author md
 * @see ParserState
 */
class QuotedStringState implements ParserState {

	/** String delimiter. */
	private final char delimiter;
	
	/** The only possible successor state. */
	private ParserState commandState;
	
	/** 
	 * Creates a new QuotedStringState for strings with given delimiter character.
	 * 
	 * @param delimiter string delimiter
	 */
	QuotedStringState( char delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
	 * Sets the only reachable state.
	 * 
	 * @param commandState CommandState
	 */
	void setReachableStates( CommandState commandState) {
		this.commandState = commandState;
	}
	
	@Override
	public ParserState processChar(int c, StatementBuffer sb) {
		if( c == -1)
			return null;
		
		sb.appendChar( c);
		
		if( c == delimiter)
			return commandState;
		else
			return this;
	}

}

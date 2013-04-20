package org.agnitas.emm.extension.sqlparser;

/**
 * Interface for an internal parser state.
 * 
 * @author md
 */
interface ParserState {
	
	/**
	 * Process given character.
	 * 
	 * @param c character to process
	 * @param sb buffer holding the current statement
	 * 
	 * @return the next state
	 */
	ParserState processChar( int c, StatementBuffer sb);
}

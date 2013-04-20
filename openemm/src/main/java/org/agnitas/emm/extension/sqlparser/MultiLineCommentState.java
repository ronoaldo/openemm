package org.agnitas.emm.extension.sqlparser;

/**
 * Implementation of the ParserState interface.
 * 
 * This state indicates, that the parser is now inside a muli-line comment.
 * 
 * @author md
 * @see ParserState
 */
class MultiLineCommentState implements ParserState {

	/** Successor state when "*" is read. */
	private ParserState possibleMultiLineCommentEndState;

	/**
	 * Set successor state.
	 * 
	 * @param possibleMultiLineCommentEndState PossibleMultiLineCommentEndState 
	 */
	public void setReachableStates( PossibleMultiLineCommentEndState possibleMultiLineCommentEndState) {
		this.possibleMultiLineCommentEndState = possibleMultiLineCommentEndState;
	}
	
	@Override
	public ParserState processChar(int c, StatementBuffer sb) {
		if( c == -1)
			return null;
		else if( c == '*')
			return possibleMultiLineCommentEndState;
		else
			return this;
	}

}

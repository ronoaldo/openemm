package org.agnitas.emm.extension.sqlparser;

/**
 * Implementation of the ParserState interface.
 * 
 * This state indicates, that the parser is now inside a single-line comment.
 * 
 * @author md
 * @see ParserState
 */
class SingleLineCommentState implements ParserState {

	/** Reference to the only possible successor state. */
	private ParserState commandState;

	/**
	 * Set the instance of the CommandState.
	 * 
	 * @param commandState CommandState
	 */
	public void setReachableStates(CommandState commandState) {
		this.commandState = commandState;
	}

	@Override
	public ParserState processChar(int c, StatementBuffer sb) {
		if( c == -1)
			return null;
		else if( c == 13 || c == 10)
			return commandState;
		else {
			return this;
		}
	}

}

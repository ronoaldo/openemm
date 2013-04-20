package org.agnitas.emm.extension.sqlparser;

/**
 * Implementation of the ParserState interface.
 * 
 * This state indicates, that the parser is possibly inside a single-line comment. 
 * This means, that the first of the two "-" was read.
 * 
 * @author md
 * @see ParserState
 */
class PossibleSingleLineCommentState implements ParserState {

	/** Successor state when the second "-" is read. */
	private ParserState singleLineCommentState;
	
	/** Successor state when something else than "-" is read. */
	private ParserState commandState;
	
	/**
	 * Sets the successor states.
	 * 
	 * @param commandState CommandState
	 * @param singleLineCommentState SingleLineCommentState
	 */
	public void setReachableStates(CommandState commandState, SingleLineCommentState singleLineCommentState) {
		this.commandState = commandState;
		this.singleLineCommentState = singleLineCommentState;
	}

	@Override
	public ParserState processChar(int c, StatementBuffer sb) {
		if( c == -1)
			return null;
		else if( c == '-')
			return singleLineCommentState;
		else {
			sb.appendChar( '-');
			sb.appendChar( c);
			
			return commandState;
		}
	}

}

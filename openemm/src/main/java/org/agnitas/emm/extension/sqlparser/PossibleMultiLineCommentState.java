package org.agnitas.emm.extension.sqlparser;

/**
 * Implementation of the ParserState interface.
 * 
 * This state indicates, that the parser is possibly inside a multi-line comment.
 * This means that "/" was read.
 * 
 * @author md
 * @see ParserState
 */
class PossibleMultiLineCommentState implements ParserState {

	/** Successor state when "*" is read. */
	private ParserState multiLineCommentState;
	
	/** Successor state when something else than "*" is read. */
	private ParserState commandState;

	/**
	 * Set the successor states.
	 * 
	 * @param commandState CommandState
	 * @param multiLineCommentState MultiLineCommentState
	 */
	public void setReachableStates(CommandState commandState, MultiLineCommentState multiLineCommentState) {
		this.commandState = commandState;
		this.multiLineCommentState = multiLineCommentState;
	}
	
	@Override
	public ParserState processChar(int c, StatementBuffer sb) {
		if( c == -1)
			return null;
		else if( c == '*')
			return multiLineCommentState;
		else if( c == '/') {
			sb.appendChar( '/');
			return this;
		} else {
			sb.appendChar( '/');
			sb.appendChar( c);
			
			return commandState;
		}
			
	}

}

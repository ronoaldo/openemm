package org.agnitas.emm.extension.sqlparser;

/**
 * Implementation of the ParserState interface.
 * 
 * This state indicates, that the parser will possibly leave a multi-line comment.
 * This means, that the "*" was read.
 * 
 * @author md
 * @see ParserState
 */
public class PossibleMultiLineCommentEndState implements ParserState {
	
	/** Successor state when "/" is read. */
	private ParserState commandState;
	
	/** Successor state when something else than "/" is read. */
	private ParserState multiLineCommentState;

	/**
	 * Set the successor states.
	 * 
	 * @param multiLineCommentState MultiLineCommentState
	 * @param commandState CommandState
	 */
	public void setReachableStates( MultiLineCommentState multiLineCommentState, CommandState commandState) {
		this.multiLineCommentState = multiLineCommentState;
		this.commandState = commandState;
	}
	
	@Override
	public ParserState processChar(int c, StatementBuffer sb) {
		if( c == -1)
			return null;
		else if( c == '/')
			return commandState;
		else if( c == '*') {
			sb.appendChar( '*');
			return this;
		} else
			return multiLineCommentState;
	}

}

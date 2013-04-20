package org.agnitas.emm.extension.sqlparser;

import java.util.List;

/**
 * Implementation of the ParserState interface.
 * 
 * This state indicates, that the parser is at command level. This
 * means, that the parser is not inside a string or comment.
 * 
 * @author md
 * @see ParserState
 */
class CommandState implements ParserState {
	
	/** List of parsed statements. */
	private final List<String> statements;
	
	/** Successor state when " is read. */
	private ParserState doubleQuotedStringState;
	
	/** Successor state when ' is read. */
	private ParserState singleQuotedStringState;
	
	/** Successor state when ` is read. */
	private ParserState backtickQuotedStringState;

	/** Successor state when "-" is read. */
	private ParserState possibleSingleLineCommentState;

	/** Successor state when "/" is read. */
	private ParserState possibleMultiLineCommentState;
	
	/**
	 * Creates a new command state and binds list to hold parsed statements.
	 * 
	 * @param statements List to hold parsed statements
	 */
	public CommandState( List<String> statements) {
		this.statements = statements;
	}
	
	/**
	 * Set successor states.
	 * 
	 * @param singleQuotedStringState state for single-quoted strings
	 * @param doubleQuotedStringState state for double-quoted strings
	 * @param backtickQuotedStringState state for backtick-quoted strings
	 * @param possibleSingleLineCommentState state for possible single-line comments
	 * @param possibleMultiLineCommentState state for possible multi-line comments
	 */
	void setReachableStates(QuotedStringState singleQuotedStringState,
			QuotedStringState doubleQuotedStringState,
			QuotedStringState backtickQuotedStringState,
			PossibleSingleLineCommentState possibleSingleLineCommentState,
			PossibleMultiLineCommentState possibleMultiLineCommentState) {
		this.singleQuotedStringState = singleQuotedStringState;
		this.doubleQuotedStringState = doubleQuotedStringState;
		this.backtickQuotedStringState = backtickQuotedStringState;
		this.possibleSingleLineCommentState = possibleSingleLineCommentState;
		this.possibleMultiLineCommentState = possibleMultiLineCommentState;
	}

	@Override
	public ParserState processChar(int c, StatementBuffer sb) {
		// Note: unusedStringBuffer is not used here, it is only here by interface specification
		
		switch( c) {
		case '/':		// Possible begin of multi-line comment
			return possibleMultiLineCommentState;
			
		case '-':		// Possible begin of single-line comment
			return possibleSingleLineCommentState;
			
		case '\'':		// Begin of single-quoted string
			sb.appendChar( c);
			return singleQuotedStringState;
			
		case '\"':		// Begin of double-quoted string
			sb.appendChar( c);
			return doubleQuotedStringState;
			
		case '`':		// Begin of backtick-quoted string
			sb.appendChar( c);
			return backtickQuotedStringState;
			
		case ';':
			// Do not append the semicolon or the JDBC driver of Oracle reports errors
			this.statements.add( sb.toString());
			sb.clear();
			
			return this;
			
		case -1:
			return null;
		
		default:
			sb.appendChar( c);
			return this;
		}
	}

}

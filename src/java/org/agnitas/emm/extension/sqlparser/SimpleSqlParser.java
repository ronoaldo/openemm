package org.agnitas.emm.extension.sqlparser;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

/**
 * A simple SQL parser. This parser is only used to split a SQL script
 * containing multiple SQL statements into a list of single statements
 * removing all comments.
 * 
 * Technically, this class uses the <i>State Pattern</i>. (see http://en.wikipedia.org/wiki/State_pattern)
 * 
 * @author md
 *
 * @see http://en.wikipedia.org/wiki/State_pattern
 */
public class SimpleSqlParser {
	
	/** State, when the parser is in the "command" level of the statement. (Not in string or comments) */
	private final CommandState commandState;

	/** State indicating that the parser is currently reading characters inside a double-quoted string. */
	private final QuotedStringState doubleQuotedStringState;

	/** State indicating that the parser is currently reading characters inside a single-quoted string. */
	private final QuotedStringState singleQuotedStringState;

	/** State indicating that the parser is currently reading characters inside a backtick-quoted string. */
	private final QuotedStringState backtickQuotedStringState;
	
	/** State indicating that the parser is possibly reading a single-line comment. (The first "-" was read). */
	private final PossibleSingleLineCommentState possibleSingleLineCommentState;

	/** State indicating that the parser is currently reading a single-line comment. (The both "-" were read). */
	private final SingleLineCommentState singleLineCommentState;

	/** State indicating that the parser is possibly reading a multi-line comment. (The "/" was read). */
	private final PossibleMultiLineCommentState possibleMultiLineCommentState;

	/** State indicating that the parser is currently reading a multi-line comment. ("/*" was read). */
	private final MultiLineCommentState multiLineCommentState;
	
	/** State indicating that the parser is possibly leaving the multi-line comment ("*" was read). */
	private final PossibleMultiLineCommentEndState possibleMultiLineCommentEndState;

	/** List of previously parsed statements. */
	private final List<String> statements;
	
	/** Reader to read the script character by character. */
	private final Reader reader;
	
	/** The current state of the parser. */
	private ParserState currentState;

	/**
	 * Creates a new parser and binds the given Reader as source.
	 * 
	 * @param reader Reader yielding the SQL script.
	 */
	public SimpleSqlParser( Reader reader) {
		this.reader = reader;
		this.statements = new Vector<String>();
		
		// Create the internal states
		this.commandState = new CommandState( statements);
		this.backtickQuotedStringState = new QuotedStringState( '`');
		this.singleQuotedStringState = new QuotedStringState( '\'');
		this.doubleQuotedStringState = new QuotedStringState( '"');
		this.possibleSingleLineCommentState = new PossibleSingleLineCommentState();
		this.singleLineCommentState = new SingleLineCommentState();
		this.possibleMultiLineCommentState = new PossibleMultiLineCommentState();
		this.multiLineCommentState = new MultiLineCommentState();
		this.possibleMultiLineCommentEndState = new PossibleMultiLineCommentEndState();
		
		// Define the transitions between the states
		setStateTransitions();
		
		// Begin in CommandState
		this.currentState = commandState;
	}
	
	/**
	 * Setup the transitions between the states.
	 */
	private void setStateTransitions() {
		this.commandState.setReachableStates( singleQuotedStringState, doubleQuotedStringState, backtickQuotedStringState, possibleSingleLineCommentState, possibleMultiLineCommentState);
		this.singleQuotedStringState.setReachableStates( commandState);
		this.doubleQuotedStringState.setReachableStates( commandState);
		this.backtickQuotedStringState.setReachableStates( commandState);
		this.possibleSingleLineCommentState.setReachableStates( commandState, this.singleLineCommentState);
		this.singleLineCommentState.setReachableStates( commandState);
		this.possibleMultiLineCommentState.setReachableStates( commandState, multiLineCommentState);
		this.multiLineCommentState.setReachableStates( possibleMultiLineCommentEndState);
		this.possibleMultiLineCommentEndState.setReachableStates( multiLineCommentState, commandState);
	}
	
	/**
	 * Parses the SQL script reading character by character for the bound Reader.
	 * 
	 * @return List of parsed SQL statements
	 * 
	 * @throws IOException on errors reading from the bound Reader
	 */
	public List<String> parse() throws IOException {
		StatementBuffer buffer = new StatementBuffer();
		
		while( currentState != null) {
			currentState = currentState.processChar(reader.read(), buffer);
		}
		
		this.statements.add( buffer.toString());
		
		return statements;
	}
	
}

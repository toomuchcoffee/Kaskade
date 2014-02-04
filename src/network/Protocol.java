/*
 * Created on 30.09.2006
 */
package network;

import gameelements.GameSituation;
import gameelements.Position;
import player.Player;

/**
 * A collection of constants and utility methods used by the 'Kaskade'-protocol.
 */
class Protocol
{
	/**
	 * An argument representing the client.
	 */
	public static final String ARG_CLIENT = "c";
	
	/**
	 * An argument representing the server.
	 */
	public static final String ARG_SERVER = "s";
	
	/**
	 * An argument representing neither client, nor server, but neutrum.
	 */
	public static final String ARG_NEUTRAL = "n";
	
	/**
	 * The 'board'-command.
	 */
	public static final String COMMAND_BOARD = "board";
	
	/**
	 * The 'init'-command.
	 */
	public static final String COMMAND_INIT = "init";
	
	/**
	 * The 'initialized'-command.
	 */
	public static final String COMMAND_INITIALIZED = "initialized";
	
	/**
	 * The 'move'-command.
	 */
	public static final String COMMAND_MOVE = "move";
	
	/**
	 * The 'bye'-command.
	 */
	public static final String COMMAND_BYE = "bye";
	
	/**
	 * The 'error'-command.
	 */
	public static final String COMMAND_ERROR = "error";
	
	/**
	 * The 'exit'-command.
	 */
	public static final String COMMAND_EXIT = "exit";
	
	/**
	 * The 'help'-command.
	 */
	public static final String COMMAND_HELP = "help";
	
	/**
	 * The tag used to indicate a comment, that should be ignored by the server.
	 */
	public static final String COMMENT_TAG = ">";
	
	/**
	 * The tag used to indicate the start of a commands parameter list.
	 */
	public static final String COMMAND_PARAM_START = "(";
	
	/**
	 * The tag used to indicate the end of a commands parameter list.
	 */
	public static final String COMMAND_PARAM_END = ")";
	
	/**
	 * The tag used to separate a command's parameters from each other.
	 */
	public static final String COMMAND_PARAM_SEPARATOR = ";";
	
	/**
	 * The tag used to separate a parameter's key and value.
	 */
	public static final String PARAM_KEYVAL_SEPARATOR = "=";
	
	/**
	 * The parameter name for user. 
	 */
	public static final String PARAM_USER = "user";
	
	/**
	 * The parameter name for row.
	 */
	public static final String PARAM_ROW = "row";
	
	/**
	 * The parameter name for an x-coordinate.
	 */
	public static final String PARAM_X = "x";
	
	/**
	 * The parameter name for a y-coordinate.
	 */
	public static final String PARAM_Y = "y";
	
	/**
	 * The parameter name for the winning player name.
	 */
	public static final String PARAM_WINNER = "winner";
	
	/**
	 * The parameter name for the beginning player.
	 */
	public static final String PARAM_BEGIN = "begin";
	
	/**
	 * The parameter name for the x-dimension of the board.
	 */
	public static final String PARAM_DIM_X = "xDim";
	
	/**
	 * The parameter name for the y-dimension of the board.
	 */
	public static final String PARAM_DIM_Y = "yDim";
	
	/**
	 * The parameter name for the board setup.
	 */
	public static final String PARAM_SETUP = "setup";
	
	/**
	 * The parameter name for the message.
	 */
	public static final String PARAM_MESSAGE = "message";
	
	/**
	 * The tag for indicating an end of a command line.
	 */
	public static final String END_OF_CMD_LINE = System.getProperty("line.separator");

	/**
	 * List of commands that never use parameters.
	 */
	public static final String[] CMDS_WITHOUT_PARAMS = new String[]{ COMMAND_BYE, COMMAND_EXIT, COMMAND_HELP };
	
	/**
	 * The help string.
	 */
	public static final String MSG_HELP =
		"> syntax: <command> (<parameters>)" + Protocol.END_OF_CMD_LINE
	  + "> parameter-syntax: list of key=value pairs, separated by ';'" + Protocol.END_OF_CMD_LINE
	  + ">" + Protocol.END_OF_CMD_LINE
	  + "> commands:" + Protocol.END_OF_CMD_LINE
	  + ">  help - shows this help" + Protocol.END_OF_CMD_LINE
	  + ">  init - initialize a board" + Protocol.END_OF_CMD_LINE
	  + ">         parameters:" + Protocol.END_OF_CMD_LINE
	  + ">          xDim - board size in horizontal orientation" + Protocol.END_OF_CMD_LINE
	  + ">          yDim - board size in vertical orientation " + Protocol.END_OF_CMD_LINE
	  + ">                 (accepted board size ranges fom 3x3 to 20x20 fields)" + Protocol.END_OF_CMD_LINE
	  + ">          begin - =s  => server (me) has first move" + Protocol.END_OF_CMD_LINE
	  + ">                  =c  => client (you) has first move" + Protocol.END_OF_CMD_LINE
	  + ">          setup - (optional) initial board setup as a stream of digit-character pairs" + Protocol.END_OF_CMD_LINE
	  +	">                  for each field, beginning from top left (x=0;y=0), " + Protocol.END_OF_CMD_LINE
	  + ">                  continuing line-by-line until bottom right" + Protocol.END_OF_CMD_LINE
	  + ">                  The digits represent the number of stones and the characters specify the owner " + Protocol.END_OF_CMD_LINE
	  +	">                  ('s' for server, 'c' for client or 'n' for neutral)." + Protocol.END_OF_CMD_LINE
	  + ">                  Default is an empty board setup." + Protocol.END_OF_CMD_LINE
	  + ">          user - client user name" + Protocol.END_OF_CMD_LINE
	  + ">         Example for a simple 3x3 board:" + Protocol.END_OF_CMD_LINE
	  + ">          init (xDim=3;yDim=3;begin=c;setup=0n0n0n1s2c0n0n1s0n;user=foobar)" + Protocol.END_OF_CMD_LINE
	  + ">  move - make your turn" + Protocol.END_OF_CMD_LINE
	  + ">         parameters:" + Protocol.END_OF_CMD_LINE
	  + ">          x - horizontal position" + Protocol.END_OF_CMD_LINE
	  + ">          y - vertical position" + Protocol.END_OF_CMD_LINE
	  + ">  exit - goodbye";
	
	/**
	 * Returns true, if the given command allows the use of parameters.
	 * @param cmdName the name of the command.
	 * @return true, if the given command allows the use of parameters.
	 */
	protected static boolean isCommandWithParams(String cmdName)
	{
		for (int i=0; i<CMDS_WITHOUT_PARAMS.length; i++)
			if (CMDS_WITHOUT_PARAMS[i].equals(cmdName))
				return false;
		
		return true;
	}
	
	
	/**
	 * Generates a string representation of the game situation according to the
	 * 'setup' in an 'initialized'-command.
	 * @param server the player that represents the server according to the 
	 * 'Kaskade'-protocol.
	 * @param situation the game situation to reprsent.
	 * @return a string representation of the given game situation.
	 */
	protected static String buildBoardString(Player server, GameSituation situation)
	{
		StringBuffer str = new StringBuffer();
		for (int y=0; y<situation.getDimY(); y++)
		{
			for (int x=0; x<situation.getDimX(); x++)
			{
				Position aPos = new Position(x, y);
				String fieldString = buildFieldStringForPosition(server, situation, aPos);
				str.append(fieldString);
			}
		}
		return str.toString();
	}
	
	/**
	 * Generates a string representation of a single field within a board as 
	 * used in 'setup' and 'board'.
	 * @param server the player that represents the server according to the 
	 * 'Kaskade'-protocol.
	 * @param situation the game situation the field belongs to.
	 * @param aPos the position of the field to represent.
	 * @return a string representation of a single field.
	 */
	protected static String buildFieldStringForPosition(Player server, GameSituation situation, Position aPos)
	{
		StringBuffer s = new StringBuffer();
		s.append( situation.getTokens(aPos) );
		if (situation.getColor(aPos) == null)
		{
			s.append( ARG_NEUTRAL );
		}
		else if (situation.getColor(aPos) == server.getColor())
		{
			s.append( ARG_SERVER );
		}
		else
		{
			s.append( ARG_CLIENT );
		}
		return s.toString();
	}
	
}

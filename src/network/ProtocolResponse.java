/*
 * Created on 11.01.2007
 */
package network;

import gameelements.GameSituation;
import gameelements.Position;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import player.Player;

/**
 * A collection of methods that generate command strings that are being posted
 * towards a remote server.
 */
public class ProtocolResponse extends Protocol
{

	/**
	 * Generates a 'move'-command from a move.
	 * @param move the move that should be generatet as command.
	 * @return a command for the given move.
	 */
	public static String postMove(Position move)
	{
		Map<String, String> args = new HashMap<String, String>(); 
		args.put( PARAM_X, String.valueOf( move.getX() ) );
		args.put( PARAM_Y, String.valueOf( move.getY() ) );
		return generateCmd( COMMAND_MOVE, args );
	}
	
	/**
	 * Generates a set of 'board'-commands to represent the current game 
	 * situation.
	 * @param server the player that stands for the server according to the 
	 * defined 'Kaskade'-protocol.
	 * @param situation the game situation that should be represented via board
	 * commands
	 * @return a set of commands representing a game situation.
	 */
	public static String[] postBoard(Player server, GameSituation situation)
	{
		String [] boardCmds = new String[ situation.getDimY() ];
		
		for (int y=0; y<situation.getDimY(); y++)
		{
			StringBuffer argv = new StringBuffer();
			for (int x=0; x<situation.getDimX(); x++)
			{
				Position aPos = new Position(x, y);
				String fieldString = buildFieldStringForPosition(server, situation, aPos);
				argv.append(fieldString);
			}
			Map<String, String> dict = new HashMap<String, String>();
			dict.put( PARAM_ROW, argv.toString() );
			boardCmds[y] = generateCmd( COMMAND_BOARD, dict );
		}
		return boardCmds;
	}
	
	/**
	 * Generates an 'init'-command. 
	 * @param server the player that represents the server according to the 
	 * defined 'Kaskade'-protocol.
	 * @param situation the game situation at initialization.
	 * @param clientBegins indicates if the client according to the 'Kaskade'
	 * -protocol begins the game.
	 * @param user the name of the client.
	 * @return a command for initializing a game with the given parameters.
	 */
	public static String postInit(Player server, GameSituation situation, boolean clientBegins, String user)
	{
		Map<String, String> args = new HashMap<String, String>();
		
		args.put( PARAM_DIM_X, String.valueOf( situation.getDimX() ) );
		args.put( PARAM_DIM_Y, String.valueOf( situation.getDimY() ) );
		args.put( PARAM_BEGIN, clientBegins ? ARG_CLIENT : ARG_SERVER );
		args.put( PARAM_SETUP, buildBoardString( server, situation ) );
		args.put( PARAM_USER, user );
		
		return generateCmd( COMMAND_INIT, args );
	}
	
	/**
	 * Generates an 'initialized'-command.
	 * @param user the name of the player that sends the command.
	 * @return a command for confirming an initialized state.
	 */
	public static String postInitialized(String user)
	{
		Map<String, String> args = new HashMap<String, String>();
		args.put( PARAM_USER, user );
		return generateCmd( COMMAND_INITIALIZED, args );
	}
	
	/**
	 * Generates an 'error'-command.
	 * @param msg the error message.
	 * @return a command for sending an error.
	 */
	public static String postError(String msg)
	{
		if (msg == null)
		{
			msg = "unknown error";
		}
		Map<String, String> args = new HashMap<String, String>();
		args.put( PARAM_MESSAGE , msg );
		return generateCmd( COMMAND_ERROR, args );
	}
	
	/**
	 * Generates a 'bye'-command.
	 * @param winnerS indicates if the server (according to the 'Kaskade'
	 * -protocol) is the winner.
	 * @return a command to say bye.
	 */
	public static String postBye(boolean winnerS)
	{
		Map<String, String> args = new HashMap<String, String>();
		args.put( PARAM_WINNER , winnerS ? ARG_SERVER : ARG_CLIENT );
		return generateCmd( COMMAND_BYE, args );
	}
	
	private static String generateCmd(String cmdName, Map<String, String> args)
	{
		StringBuffer cmd = new StringBuffer( cmdName );
		cmd.append( COMMAND_PARAM_START );
		
		// Parameter erstellen
		int count = 0;
		for ( Iterator<String> e = args.keySet().iterator(); e.hasNext(); )
		{
			if (count>0 && count<args.size())
			{
				cmd.append( COMMAND_PARAM_SEPARATOR );
			}
			String key = e.next();
			String val = args.get(key);
			cmd.append(key);
			cmd.append( PARAM_KEYVAL_SEPARATOR );
			cmd.append(val);
			count++;
		}
		
		cmd.append( COMMAND_PARAM_END );
		
		cmd.append( END_OF_CMD_LINE );
		
		return cmd.toString();
	}
	
}

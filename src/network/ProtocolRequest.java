/*
 * Created on 11.01.2007
 */
package network;

import gameelements.Game;
import gameelements.GameColor;
import gameelements.GameBoard;
import gameelements.Position;
import gameelements.GameSituation.FieldSetup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A collection of methods that interpret command strings that are being 
 * received from a remote server.
 */
public class ProtocolRequest extends Protocol
{
	
	private static Map<String, String> parseCommand(String cmdName, String cmdLine) 
	throws IllegalCommandException
	{
		cmdLine = cmdLine.trim();
		
		int paramStart = cmdLine.indexOf(COMMAND_PARAM_START);
		int paramEnd = cmdLine.indexOf(COMMAND_PARAM_END);
		
		String cmdString = null;
		String argString = null;
		
		if (isCommandWithParams(cmdName))
		{
			// check if paranthesis are at right positions 
			if (paramStart < cmdName.length() || paramEnd < cmdLine.length()-1)
				throw new IllegalCommandException("paranthesis error");
			
			cmdString = cmdLine.substring(0, paramStart).trim();
			argString = cmdLine.substring(paramStart+1, paramEnd).trim();
		}
		else
		{
			cmdString = cmdLine;
		}
		
		if (!cmdName.equals(cmdString))
			throw new IllegalCommandException("illegal command: " + cmdString);
		
		return extractArgumentMap(argString);
	}
	
	private static Map<String, String> extractArgumentMap(String argStr) 
	throws IllegalCommandException
	{
		Map<String, String> argMap = new HashMap<String, String>();
		
		if (argStr == null)
			return argMap;
		
		String[] args = argStr.split( COMMAND_PARAM_SEPARATOR );
		for (int i=0; i<args.length; i++)
		{
			String[] keyval = args[i].split( PARAM_KEYVAL_SEPARATOR );
			if (keyval.length != 2)
			{
				throw new IllegalCommandException("illegal argument");
			}
			argMap.put(keyval[0].trim(), keyval[1].trim());
		}
		
		return argMap;
	}
	
	/**
	 * Interprets an 'init'-command.
	 * @param initCmd the 'init'-command to interpret.
	 * @return an argument map for initializing a game.
	 * @throws IllegalCommandException
	 */
	public static Map<String, String> getInit(String initCmd) throws IllegalCommandException
	{
		Map<String, String> argMap = 
			ProtocolRequest.parseCommand( COMMAND_INIT, initCmd );
		
		String dimXStr, dimYStr;
		if ( (dimXStr = argMap.get( PARAM_DIM_X )) == null )
		{
			throw new IllegalCommandException("missing argument: " + Protocol.PARAM_DIM_X);
		}
		if ( (dimYStr = argMap.get( PARAM_DIM_Y )) == null )
		{
			throw new IllegalCommandException("missing argument: " + Protocol.PARAM_DIM_Y);
		}
		
		int dimX, dimY;
		try
		{
			dimX = Integer.valueOf( dimXStr );
			dimY = Integer.valueOf( dimYStr );
		}
		catch( NumberFormatException e)
		{
			throw new IllegalCommandException("illegal format for dimension argument");
		}
		
		if (dimX < Game.NR_OF_FIELDS_MIN || dimY < Game.NR_OF_FIELDS_MIN)
		{
			throw new IllegalCommandException("size lower than " + Game.NR_OF_FIELDS_MIN + " not supported");
		}
		if (dimY > Game.NR_OF_FIELDS_MAX || dimY > Game.NR_OF_FIELDS_MAX)
		{
			throw new IllegalCommandException("size higher than " + Game.NR_OF_FIELDS_MAX + " not supported");
		}
		
		if ( argMap.get( PARAM_BEGIN ) == null )
		{
			throw new IllegalCommandException("missing argument: " + PARAM_BEGIN);
		}
		if ( argMap.get( PARAM_USER ) == null )
		{
			throw new IllegalCommandException("missing argument: " + PARAM_USER);
		}
		
		return argMap;
	}
	
	/**
	 * Interprets a 'setup'-argument that comes with an 'init'-command.
	 * @param dimX the x-dimension of the board.
	 * @param dimY the y-dimension of the board.
	 * @param serverBegins indicates if the server according to the 'Kaskade'
	 * -protocol begins.
	 * @param setupArg the setup argument for the board to be initialized.
	 * @param begin the beginning user according to the 'Kaskade'-protocol, 
	 * either 's' or 'c'.
	 * @return a list of field setups to help setup a game situation.
	 * @throws IllegalCommandException
	 */
	public static List<FieldSetup> getSetup(int dimX, int dimY, boolean serverBegins, String setupArg, String begin) 
	throws IllegalCommandException
	{
		GameColor color = serverBegins ? GameColor.WHITE : GameColor.BLACK;
		
		if (setupArg.length() != dimX*dimY*2)
		{
			throw new IllegalCommandException("illegal setup, setup not matching board size");
		}
		
		List<FieldSetup> fieldSetups = new ArrayList<FieldSetup>();
		
		int countS = 0;
		int countC = 0;
		int index = 0;
		int x = 0;
		int y = 0;
		while (index < setupArg.length())
		{
			String aFieldString = setupArg.substring(index, index+2);
			FieldSetup aFieldSetup = new FieldSetup();
			
			int tokens;
			String numberString = aFieldString.substring(0,1);
			try
			{
				tokens = Integer.valueOf( numberString );
			}
			catch(NumberFormatException e)
			{
				throw new IllegalCommandException("illegal number: " + numberString);
			}
			
			Position pos = new Position(x, y);
			
			String colString = aFieldString.substring(1,2);
			if (colString.equals(ARG_NEUTRAL)) 
			{
				if (tokens != 0)
				{
					throw new IllegalCommandException("illegal setup, neutral field cannot hold tokens");
				}
			}
			else
			{
				GameBoard gb = new GameBoard(dimX, dimY);
				if (tokens >= gb.getLimit( pos ))
				{
					throw new IllegalCommandException("illegal setup, field x=" + pos.getX() + ", y=" + pos.getY() + " holds too many tokens");
				}
				aFieldSetup.tokens = tokens;
				if (colString.equals(ARG_SERVER))
				{
					aFieldSetup.color = color;
					countS++;
				}
				else
				{
					aFieldSetup.color = color.getOppositeColor();
					countC++;
				}
			}
			
			aFieldSetup.position = pos;
			fieldSetups.add(aFieldSetup);
			index += 2;
			if (x != 0 && x % (dimX-1) == 0)
			{
				x = 0; 
				y++;
			}
			else
			{
				x++;
			}
		}
		
		// if only one field is occupied, it must not be in the color of the beginning server
		if (countC + countS == 1)
			if (countC == 1 && begin.equals(ARG_CLIENT) || countS == 1 && begin.equals(ARG_SERVER))
				throw new IllegalCommandException("illegal setup, board owned by one player");
		
		// board is not allowed to be owned by one player at setup.
		if (countC + countS > 1)
			if (countC > 0 && countS == 0 || countC == 0 && countS > 0)
				throw new IllegalCommandException("illegal setup, board owned by one player");
		
		return fieldSetups;
	}
	
	/**
	 * Interprets an 'initialized'-command.
	 * @param cmdInitialized the 'initialized'-command to interpret.
	 * @return the name of the user that sent the command.
	 * @throws IllegalCommandException
	 */
	public static String getInitialized(String cmdInitialized) 
	throws IllegalCommandException
	{
		Map <String, String> argMap = parseCommand(COMMAND_INITIALIZED, cmdInitialized);
		return argMap.get(PARAM_USER);
	}
	
	/**
	 * Interprets a move command.
	 * @param cmdMove the 'move'-command to interpret.
	 * @return the interpreted move from the command.
	 * @throws IllegalCommandException
	 */
	public static Position getMove(String cmdMove) 
	throws IllegalCommandException
	{
		Map<String, String> argMap = parseCommand(COMMAND_MOVE, cmdMove);
		
		String xStr, yStr;
		if ( (xStr = argMap.get( PARAM_X )) == null )
		{
			throw new IllegalCommandException("missing argument: " + PARAM_X);
		}
		if ( (yStr = argMap.get( PARAM_Y )) == null )
		{
			throw new IllegalCommandException("missing argument: " + PARAM_Y);
		}
		
		Integer argX = null;
		Integer argY = null;
		
		try
		{
			argX = Integer.valueOf( xStr );
			argY = Integer.valueOf( yStr );
		}
		catch (NumberFormatException e)
		{
			throw new IllegalCommandException("illegal format for position argument");
		}
		
		return new Position(argX, argY);
	}
	
	/**
	 * Interprets a set of 'board'-commands.
	 * @param cmdsBoard a list of 'board'-commands.
	 * @return a string representation for the board.
	 * @throws IllegalCommandException
	 */
	public static String getBoard(List<String> cmdsBoard) throws IllegalCommandException
	{
		StringBuffer rep = new StringBuffer();
		for (Iterator<String> it = cmdsBoard.iterator(); it.hasNext();)
		{
			Map args = parseCommand(COMMAND_BOARD, it.next());
			rep.append( args.get(PARAM_ROW) );
		}
		return rep.toString();
	}
	
	/**
	 * Interprets an 'error'-command.
	 * @param errCmd the 'error'-command to interpret.
	 * @return the error message.
	 * @throws IllegalCommandException
	 */
	public static String getError(String errCmd) throws IllegalCommandException
	{
		Map<String, String> argMap = parseCommand(COMMAND_ERROR, errCmd);
		return argMap.get(PARAM_MESSAGE);
	}

	/**
	 * Interprets a 'help'-command.
	 * @param helpCmd the 'help'-command to interpret.
	 * @return a help message.
	 * @throws IllegalCommandException
	 */
	public static String getHelp(String helpCmd) throws IllegalCommandException
	{
		parseCommand(COMMAND_HELP, helpCmd); // for throwing parsing errors
		return MSG_HELP;
	}

}

/*
 * Created on 03.01.2007
 */
package network;

import gameelements.ConsoleGame;
import gameelements.Game;
import gameelements.GamePreferences;
import gameelements.Position;
import gameelements.GameSituation.FieldSetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import player.ConsolePlayer;
import player.Player;

/**
 * A class that works as server for a remote client by communicating with the
 * console player and synchronizing the local game with the console output.
 */
public class Service implements Runnable
{
	/**
	 * The socket the service is connected to.
	 */
	private Socket socket;
	
	/**
	 * The print stream the service prints out to.
	 */
	private PrintStream output;
	
	/**
	 * Indicates if a game is already initialized with the init-command.
	 */
	private boolean initialized = false;
	
	/**
	 * The player using the service client.
	 */
	private ConsolePlayer player;
	
	/**
	 * The welcome message for ne connections.
	 */
	private static final String MSG_WELCOME = 
		">Welcome to Geralds Kaskade !" + Protocol.END_OF_CMD_LINE
	  + ">Maximum accepted board size is a total of 20x20 fields." + Protocol.END_OF_CMD_LINE
	  + ">Type 'help' for a list of commands." + Protocol.END_OF_CMD_LINE
	  + ">Be gentle ;-)";
	
	/**
	 * Initiates a new service that is connected to the given socket.
	 * @param socket the socket the service is connected to.
	 */
	Service(Socket socket)
	{
		this.socket = socket;
	}
	
	/**
	 * Reads out the users requests on console and interprets them.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		BufferedReader input;
		try
		{
			input = new BufferedReader(
					new InputStreamReader( socket.getInputStream()) );

			output = new PrintStream( socket.getOutputStream() );

			output.println( MSG_WELCOME );

			while (!socket.isClosed()) 
			{
				String line = input.readLine();
				try
				{
					if (line != null)
					{
						if (line.startsWith( Protocol.COMMENT_TAG ))
						{
							continue; // ignore!
						}
						else if (line.startsWith(Protocol.COMMAND_HELP))
						{
							receiveHelp(line);
						}
						else if (line.startsWith(Protocol.COMMAND_INIT))
						{
							receiveInit(line);
						}
						else if (line.startsWith(Protocol.COMMAND_MOVE))
						{
							receiveMove(line);
						}
						else if (line.startsWith(Protocol.COMMAND_EXIT))
						{
							receiveExit(line);
						}
						else
						{
							throw new IllegalCommandException("illegal command");
						}
					}
				}
				catch(IllegalCommandException e) 
				{
					respondWithError(e);
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void receiveHelp(String helpCmd) throws IllegalCommandException
	{
		String help = ProtocolRequest.getHelp(helpCmd);
		output.println(help);
	}
	
	private void receiveInit(String initCmd) throws IllegalCommandException
	{
		if ( initialized )
		{
			throw new IllegalCommandException("already initialized, please move or exit");
		}
		
		Map<String, String> args = ProtocolRequest.getInit(initCmd );
		
		GamePreferences prefs = GamePreferences.getNewGamePreferences();
		
		prefs.setDimX( Integer.valueOf( args.get( Protocol.PARAM_DIM_X ) ) );
		prefs.setDimY( Integer.valueOf( args.get( Protocol.PARAM_DIM_Y ) ) );
		
		String begin = args.get( Protocol.PARAM_BEGIN );
		String user = args.get( Protocol.PARAM_USER );
		
		boolean serverBegins = (begin.equals(Protocol.ARG_SERVER));
		
		String defaultName = GamePreferences.DEFAULT_PLAYER_NAME;
		
		prefs.setPlayerName(0, serverBegins ? defaultName : user);
		prefs.setPlayerName(1, serverBegins ? user : defaultName);
		prefs.setPlayerType(0, serverBegins ? Player.TYPE_COMPUTER : Player.TYPE_CONSOLE);
		prefs.setPlayerType(1, serverBegins ? Player.TYPE_CONSOLE : Player.TYPE_COMPUTER);
		
		List<FieldSetup> setup = null;
		String setupString = args.get( Protocol.PARAM_SETUP );
		if (setupString != null)
		{
			setup = ProtocolRequest.getSetup(prefs.getDimX(), prefs.getDimY(), serverBegins, setupString, begin);
		}
		prefs.setSetup(setup);
		
		ConsoleGame game = new ConsoleGame(prefs);
		game.startGame();
		
		this.player = game.getConsolePlayer();
		this.player.setService(this);
		
		initialized = true;
		
		this.respondInit();
	}

	private void respondInit()
	{
		StringBuffer cmd = new StringBuffer();
		
		cmd.append( ProtocolResponse.postInitialized( this.player.getOpponent().getPlayerName() ) );
		
		String[] boardCmds = ProtocolResponse.postBoard( this.player.getOpponent(), this.player.getSituation() );
		for (int i=0; i<boardCmds.length; i++)
		{
			cmd.append(boardCmds[i]);
		}
		
		output.print( cmd.toString() );
	}
	
	
	private void receiveMove(String moveCmd) throws IllegalCommandException
	{
		if ( !initialized )
		{
			throw new IllegalCommandException("not initialized, please initialize first");
		}
		
		Position move = ProtocolRequest.getMove( moveCmd );
		
		if (move.getX() < 0 || move.getX() >= getGame().getSituation().getDimX() || 
			move.getY() < 0 || move.getY() >= getGame().getSituation().getDimY())
		{
			throw new IllegalCommandException("illegal move, move outside of board");
		}
		
		this.player.setNextMove( move );
	}
	
	
	/**
	 * Creates a 'bye'-response on the console.
	 * @param winnerS indicates if the local server is the winner of the game.
	 */
	public void respondWithBye(boolean winnerS)
	{
		String byeCmd = ProtocolResponse.postBye( winnerS );
		output.print( byeCmd );
		
		this.endGame();
	}

	private void endGame()
	{
		try
		{
			socket.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	

	/**
	 * Creates a 'move'-command on the console.
	 * @param move the responded move.
	 */
	public void respondWithMove(Position move)
	{
		StringBuffer cmd = new StringBuffer();
		
		cmd.append( ProtocolResponse.postMove( move ) );
		
		if (!getGame().isGameOver())
		{
			String[] boardCmds = 
				ProtocolResponse.postBoard( this.player.getOpponent(), getGame().getSituation() );
			for (int i=0; i<boardCmds.length; i++)
			{
				cmd.append(boardCmds[i]);
			}
		}
		output.print( cmd.toString() );
	}
	
	/**
	 * Creates an error message on the console.
	 * @param e the exception the error message is created for.
	 */
	public void respondWithError(Exception e)
	{
		String errMsg = ProtocolResponse.postError( e.getMessage() );
		output.print( errMsg );
		System.err.println("error: " + e.getMessage());
	}
	
	private void receiveExit(String exitCmd) throws IllegalCommandException
	{
		if ( ! exitCmd.trim().equals(Protocol.COMMAND_EXIT) )
		{
			throw new IllegalCommandException("illegal command arguments");
		}
		
		if (getGame() != null)
		{
			getGame().stopGame();
		}
		
		output.println( Protocol.COMMAND_BYE );
		
		this.endGame();
	}

	private Game getGame()
	{
		if (player == null)
			return null;
		else
			return this.player.getGame();
	}
}

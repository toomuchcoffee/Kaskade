/*
 * Created on 19.09.2006
 */
package network;

import gameelements.GamePreferences;
import gameelements.GameSituation;
import gameelements.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import player.Player;

/**
 * The client is used to connect to a remote server, to manage the 
 * communication between remote server and local client and to synchronize the
 * remote and local game.
 */
public class Client
{
	/**
	 * The socket the client is connected to.
	 */
	private Socket socket = null;
	
	/**
	 * A queue where the command lines are being collected before they get 
	 * sent to the remote server.
	 */
	private Queue<String> responseCmdsQueue = new LinkedList<String>();
	
	/**
	 * Number of rows of the game board used in the game.
	 */
	private int rows;
	
	/**
	 * Collects all board state strings that are being generated during client activity.
	 */
	private Stack<String> boardStates = new Stack<String>();
	
	/**
	 * A queue where 'board'-commands, received from the server, are being 
	 * collected, before they get processed.
	 */
	private List<String> boardCmdsQueue = new LinkedList<String>();

	/**
	 * The player the client is used by.
	 */
	private Player player;
	
	/**
	 * Initiates a client for the given player.
	 * @param player the player the client is created for.
	 */
	public Client(Player player)
	{
		this.player = player;
	}
	
	/**
	 * Requests an init from the remote server.
	 * @param situation the current local situation.
	 * @param clientBegins true, if the client's player begins, else false.
	 * @param user the name of the client's player.
	 */
	public void requestInit(GameSituation situation, boolean clientBegins, String user)
	{
		rows = situation.getDimY();
		
		String request = ProtocolResponse.postInit( player, situation, clientBegins, user);
		
		int responseLines = (clientBegins ? 1 : 2) * (1 + rows);	// 'initialized' and rows * 'boards' for initial state
																// plus 'move' und rows * 'boards' after first turn
		
		this.callRemoteServer( request.toString(), responseLines ); // fill response queue
	}

	/**
	 * Requests a move from the remote server.
	 * @return a move from the server.
	 * @throws IllegalCommandException
	 */
	public Position requestMove() throws IllegalCommandException
	{
		if (player.getGame().getTurn() > 1)
		{
			String request = ProtocolResponse.postMove(player.getGame().getLatestMove());
			
			this.callRemoteServer( request.toString(), 1 + rows ); // 'move' and rows * 'boards' per turn
		}
		
		Position move = null;
		
		move = processResponseCmdsQueue();
		
		return move;
	}
	
	/**
	 * Checks if local and remote boards are the same.
	 * @throws RemoteGameSynchronisationException
	 */
	public void validate() throws RemoteGameSynchronisationException
	{
		String strServerBoard = this.getCurrentBoardString();
		String strClientBoard = Protocol.buildBoardString( player, player.getGame().getSituation() );
		if (strServerBoard == null || strClientBoard == null)
			return;
		if ( strServerBoard.startsWith(Protocol.COMMAND_BYE) )
		{
			System.out.println("no validation, server said bye");
		}
		else if ( !strServerBoard.equals(strClientBoard) )
		{
			throw new RemoteGameSynchronisationException("invalid board state on server or client");
		}
		else
		{
			System.out.println("boards validated");
		}
	}
	
	private Position processResponseCmdsQueue() throws IllegalCommandException
	{
		Position retval = null;
		
		while ( ! responseCmdsQueue.isEmpty() )
		{
			String cmd = responseCmdsQueue.remove();
			
			if (cmd.startsWith(Protocol.COMMAND_INITIALIZED))
			{
				player.setPlayerName( ProtocolRequest.getInitialized(cmd) );
			}
			else if ( cmd.startsWith(Protocol.COMMAND_BOARD))
			{
				boardCmdsQueue.add( cmd );
				
				// check if queue is full, then save board state
				if ( boardCmdsQueue.size() > rows )
					throw new RuntimeException("too many 'board' commands in queue");
				if ( boardCmdsQueue.size() == rows )
				{
					String boardString = ProtocolRequest.getBoard( boardCmdsQueue );
					boardStates.add( boardString );
					boardCmdsQueue = new LinkedList<String>(); // und entleeren
				}
			}
			else if ( cmd.startsWith(Protocol.COMMAND_MOVE) )
			{
				retval = ProtocolRequest.getMove(cmd);
			}
			else if ( cmd.startsWith(Protocol.COMMAND_BYE) )
			{
				boardStates.add( cmd );
			}
		}
		
		return retval;
	}
	
	private String getCurrentBoardString()
	{
		return boardStates.isEmpty() ? null : boardStates.peek();
	}
	
	private void callRemoteServer(String callString, int expectedResponseLines)
	{
		try 
		{
			if (socket != null && socket.isClosed())
				throw new RuntimeException("socket already closed");
			
			if (socket == null)
			{
				socket = new Socket( GamePreferences.getInstance().getRemoteServerHost(), GamePreferences.getInstance().getRemoteServerPort() );
				System.out.println("socket created");
			}
			
			PrintStream output = new PrintStream( socket.getOutputStream() );
			output.println( callString.trim() );
			
			BufferedReader input = new BufferedReader(
					new InputStreamReader( socket.getInputStream()) );
			
			String line;
			while ( expectedResponseLines > 0 )
			{
				line = input.readLine();

				if (line.startsWith( Protocol.COMMENT_TAG ))
					continue;
				
				else if (line.contains(Protocol.COMMAND_ERROR))
				{
					String error = ProtocolRequest.getError(line);
					RemoteGameSynchronisationException e = new RemoteGameSynchronisationException(error);
					player.getGame().notifyWithError(e);
				}
				else if (line.startsWith(Protocol.COMMAND_BYE))
				{
					expectedResponseLines = 0; // hier erwarten wir keine 'boards' mehr!
					
					if (socket != null)
					{
						socket.close();
						System.out.println("socket has been closed");
					}
				}
				
				responseCmdsQueue.add(line);
				expectedResponseLines--;
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalCommandException e)
		{
			e.printStackTrace();
		}
	}

}

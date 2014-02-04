/*
 * Created on 17.09.2006
 */
package gui;

import gameelements.Game;
import gameelements.GamePreferences;
import gameelements.IllegalMoveException;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import network.RemoteGameSynchronisationException;
import network.Server;
import network.ServerPreferences;

/**
 * This class represents the main window of the application. It contains the 
 * menu, the game area, and is parent to all dialogue windows. 
 * This class also triggers the initial start of the server at application 
 * start.
 * As there can only be one main window at a time, this class is designed as 
 * singleton. This allows access to the main window from everywhere in the 
 * application without the need of passing a reference of the window object to 
 * everywhere. 
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener, Observer
{
	/**
	 * The single instance of this class.
	 */
	private static MainFrame instance;
	
	/**
	 * Default frame width at start of application.
	 */
	private int frameWidth = 600;
	
	/**
	 * Default frame height at start of application
	 */
	private int frameHeight = 600;
	
	/**
	 * Starting location on screen. Used by main window and child windows.
	 */
	private Point startLoc;
	
	/**
	 * Disabled Textfield for display of game status.
	 */
	private JTextField tfStatus1 = new JTextField("application started");
	
	/**
	 * Disabled Textfield for display of game status.
	 */
	private JTextField tfStatus2 = new JTextField("");
	
	/**
	 * Disabled Textfield for display of game status.
	 */
	private JTextField tfStatus3 = new JTextField("");
	
	/**
	 * Disabled Textfield for display of game status.
	 */
	private JTextField tfStatus4 = new JTextField("");
	
	/**
	 * This is the panel where the the game panel will be put into on start of
	 * as game, and where it will be removed from after game ends.
	 */
	private JPanel actionPanel;
	
	/**
	 * The current game that is being played on screen.
	 */
	private Game game;
	
	/**
	 * Returns the instance of this singleton class. If no instance existsts,
	 * a new instance will be created.
	 * @return the only instance of this class.
	 */
	public static MainFrame getInstance()
	{
		if (instance == null)
		{
			instance = new MainFrame();
		}
		return instance;
	}
	
	/**
	 * Private Constructor due to realization of singleton pattern.
	 */
	private MainFrame()
	{
		super("Kaskade");
		
		setJMenuBar( new MainMenuBar( this ) );
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		
		// status display on bottom
		JPanel pStatus = new JPanel(new GridLayout(1, 4));
		cp.add(pStatus, BorderLayout.SOUTH);
		
		tfStatus1.setEditable(false);
		pStatus.add( tfStatus1 );
		
		tfStatus2.setEditable(false);
		pStatus.add( tfStatus2 );
		
		tfStatus3.setEditable(false);
		pStatus.add( tfStatus3 );
		
		tfStatus4.setEditable(false);
		pStatus.add( tfStatus4 );
		
		// main panel in center
		actionPanel = new JPanel();
		cp.add( actionPanel, BorderLayout.CENTER );
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize(frameWidth, frameHeight);
		
		// locate at center of screen
		Dimension screenDim = getToolkit().getScreenSize();
		this.startLoc = new Point((screenDim.width-getSize().width )/2, (screenDim.height-this.getSize().height )/2);
		this.setLocation(startLoc);
	}
	
	/**
	 * In the moment of setting the component visible, the game server is also 
	 * started by this frame.
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b)
	{
		if (b)
		{
			this.startServer();
		}
		
		super.setVisible(b);
	}

	private void startServer()
	{
		int portNr = ServerPreferences.getInstance().getServerPort();
		
		try
		{
			Server.getInstance().startServer(portNr);
		} 
		catch (IOException e)
		{
			serverStartExceptionDialog(portNr);
			e.printStackTrace();
		}
		
		ServerPreferences.getInstance().setServerPort(portNr);
	}
	
	
	/**
	 * Shows a message window which informs the user, that the server could
	 * not be started on application start or server restart.
	 * @param portNr the port number the server could not start on.
	 */
	void serverStartExceptionDialog(int portNr)
	{
		JOptionPane.showMessageDialog(
				MainFrame.getInstance(), 
				"Server could not be started on port " + portNr +"."
			  + "\nPlease choose a different port under 'Settings->Server'");
	}
	

	/**
	 * Sets a status text into the textfield.
	 * @param status the status text to set
	 */
	public void setStatus1(String status)
	{
		this.tfStatus1.setText(status);
	}
	
	/**
	 * Sets a status text into the textfield.
	 * @param status the status text to set
	 */
	public void setStatus2(String status)
	{
		this.tfStatus2.setText(status);
	}
	
	/**
	 * Sets a status text into the textfield.
	 * @param status the status text to set
	 */
	public void setStatus3(String status)
	{
		this.tfStatus3.setText(status);
	}
	
	/**
	 * Sets a status text into the textfield.
	 * @param status the status text to set
	 */
	public void setStatus4(String status)
	{
		this.tfStatus4.setText(status);
	}
	
	/**
	 * Returns the main panel in which games are displayed. Enables outside
	 * classes to access the panel in order to remove existing panels or add
	 * new ones.
	 * @return the main panel of the window, where the game is being drawn.
	 */
	Container getActionPanel()
	{
		return actionPanel;
	}
	
	/**
	 * Takes menu actions for starting or stopping a game, program exit, 
	 * display options, and remote server settings.  
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		// new game
		if ( e.getActionCommand().equals( MainMenuBar.ACTION_CMD_NEW_GAME ) )
		{
			if (game != null)
			{
				game.stopGame();
			}
			this.newGameDialog();
			return;
		}
		// stop current game
		else if ( e.getActionCommand().equals( MainMenuBar.ACTION_CMD_STOP_GAME ) )
		{
			this.stop();
			return;
		}
		// exit program
		else if ( e.getActionCommand().equals( MainMenuBar.ACTION_CMD_EXIT ) )
		{
			this.exit();
			return;
		}
		
		// call display settings
		else if ( e.getActionCommand().equals( MainMenuBar.ACTION_CMD_DISPLAY_SETTINGS ) )
		{
			this.displaySettingsDialog();
			return;
		}
		
		// call remote server settings
		else if ( e.getActionCommand().equals( MainMenuBar.ACTION_CMD_SERVER_SETTINGS ) )
		{
			this.serverSettingsDialog();
			return;
		}
		
		// undo turn
		else if ( e.getActionCommand().equals( MainMenuBar.ACTION_CMD_UNDO_TURN ) );
		{
			this.undoTurn();
		}
	}
	
	/**
	 * Releases memory from gui and exits program.
	 */
	void exit()
	{
		setVisible(false);
		dispose();
		System.exit(0);
	}
	
	/**
	 * Stops current game.
	 */
	void stop()
	{
		if (game != null)
		{
			game.stopGame();
		}
		
		actionPanel.removeAll();
		validate();
	}
	
	void undoTurn()
	{
		if (game != null && game.canUndo())
		{
			game.undo();
		}
		else
		{
			JOptionPane.showMessageDialog(this, "nothing do undo");
		}
	}

	/**
	 * Calls dialogue window for setting preferences and starting a new game.
	 */
	void newGameDialog()
	{
		// stop running game
		this.stop();
		
		// call dialogue window
		JDialog dialog = new NewGameDialog();
		dialog.setVisible(true);
	}
	
	/**
	 * Calls dialogue window for setting display options like animation speed.
	 */
	void displaySettingsDialog()
	{
		JDialog dialog = new DisplayOptionsDialog();
		dialog.setVisible(true);
	}
	
	/**
	 * Calls dialogue window for setting address and port of remote server for
	 * remote server games.
	 */
	void serverSettingsDialog()
	{
		JDialog dialog = new ServerSettingsDialog();
		dialog.setVisible(true);
	}
	
	
	/**
	 * Removes old game, creates new game model, corresponding view, connects 
	 * them, and starts the game.
	 */
	public void createGame()
	{
		// remove old game
		MainFrame.getInstance().getActionPanel().removeAll();
		this.repaint();
		
		// create new game
		game = new Game(GamePreferences.getInstance());
		
		// create view
		GamePanel view = new GamePanel(game);
		
		game.connectView( view );
		
		view.setVisible(true);
		MainFrame.getInstance().getActionPanel().add( view );
		
		MainFrame.getInstance().validate();
		
		game.startGame();
	}

	/**
	 * Observes the game model, if any exceptions are being thrown, and in case
	 * of an exception creates an alert window for displaying the error 
	 * message.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if (arg instanceof IllegalMoveException)
		{
			this.illegalMoveExceptionDialog();
		}
		
		if (arg instanceof RemoteGameSynchronisationException)
		{
			this.remoteGameExceptionDialog();
		}
	}
	
	/**
	 * Calls an alert window that displays an 'illegal move'-message..
	 */
	void illegalMoveExceptionDialog()
	{
		JOptionPane.showMessageDialog(this, "illegal move - please try again");
	}
	
	/**
	 * Calls an alert window that displays a 'remote server error'-message.
	 */
	void remoteGameExceptionDialog()
	{
		game.stopGame();
		
		int retval = JOptionPane.showConfirmDialog(this, 
				"synchronization with remote server was not successful, " +
				"game has been stopped" +
				"\n\ntry new game?");
		
		if (retval == JOptionPane.YES_OPTION)
		{
			MainFrame.getInstance().newGameDialog();
		}
		else if (retval == JOptionPane.NO_OPTION)
		{
			MainFrame.getInstance().exit();
		}
	}

	/**
	 * Returns the starting position for the window.
	 * @return the starting position of the window.
	 */
	public Point getStartLoc()
	{
		return startLoc;
	}

}

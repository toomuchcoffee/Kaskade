/*
 * Created on 26.09.2006
 */
package gui;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Class holding all main menus of the application in one main menu bar.
 */
@SuppressWarnings("serial")
public class MainMenuBar extends JMenuBar
{
	/**
	 * Action command to create a new game.
	 */
	public static final String ACTION_CMD_NEW_GAME = "newGame";
	
	/**
	 * Action command to stop a game.
	 */
	public static final String ACTION_CMD_STOP_GAME = "stopGame";
	
	/**
	 * Action command to exit the program.
	 */
	public static final String ACTION_CMD_EXIT = "exit";
	
	/**
	 * Action command to call the server settings dialogue.
	 */
	public static final String ACTION_CMD_SERVER_SETTINGS = "serverSettings";
	
	/**
	 * Action command to call the display settings dialogue.
	 */
	public static final String ACTION_CMD_DISPLAY_SETTINGS = "displaySettings";
	
	/**
	 * Action command to undo a turn.
	 */
	public static final String ACTION_CMD_UNDO_TURN = "undoTurn";
	
	/**
	 * Creates a menu bar with the given listener.
	 * @param listener the listener added to this menu bar.
	 */
	public MainMenuBar(ActionListener listener)
	{
		super();
		
		// main menu
		JMenu menuMain = new JMenu("Main");
		this.add(menuMain);
	
		// new game
		JMenuItem itemNewGame = new JMenuItem("New Game");
		itemNewGame.setActionCommand( ACTION_CMD_NEW_GAME );
		itemNewGame.addActionListener( listener );
		menuMain.add(itemNewGame);
		
		// end game
		JMenuItem itemStopGame = new JMenuItem("Stop Game");
		itemStopGame.setActionCommand( ACTION_CMD_STOP_GAME );
		itemStopGame.addActionListener( listener );
		menuMain.add(itemStopGame);
		
		menuMain.addSeparator();
		
		// exit program
		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.setActionCommand( ACTION_CMD_EXIT );
		itemExit.addActionListener( listener );
		menuMain.add(itemExit);
		
		// preferences
		JMenu menuSettings = new JMenu("Settings");
		this.add(menuSettings);
		
		JMenuItem itemDisplayOptions = new JMenuItem("Display Options");
		itemDisplayOptions.setActionCommand( ACTION_CMD_DISPLAY_SETTINGS );
		itemDisplayOptions.addActionListener( listener );
		menuSettings.add(itemDisplayOptions);
		
		// server settings
		JMenuItem itemLocalServerSettings = new JMenuItem("Server");
		itemLocalServerSettings.setActionCommand( ACTION_CMD_SERVER_SETTINGS );
		itemLocalServerSettings.addActionListener( listener );
		menuSettings.add(itemLocalServerSettings);
		
		// special
		JMenu menuSpecial = new JMenu("Special");
		this.add(menuSpecial);
		
		JMenuItem itemUndo = new JMenuItem("Undo Turn");
		itemUndo.setActionCommand( ACTION_CMD_UNDO_TURN );
		itemUndo.addActionListener( listener );
		menuSpecial.add(itemUndo);
	}
}

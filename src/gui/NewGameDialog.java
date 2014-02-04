/*
 * Created on 24.09.2006
 */
package gui;

import gameelements.GamePreferences;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Dialogue that opens everytime a user wants to start a new game. In this
 * dialogue all game options will be set by the user and saed on game start,
 * so that the last used game preferences will be available for the next game.
 */
@SuppressWarnings("serial")
public class NewGameDialog extends JDialog implements ActionListener
{
	/**
	 * Panel for game dimension setting.
	 */
	private DimensionsPanel pGameDim;
	
	/**
	 * Panel for setting first player.
	 */
	private PlayerPropertiesPanel pPlayerA;
	
	/**
	 * Panel for setting second player.
	 */
	private PlayerPropertiesPanel pPlayerB;
	
	/**
	 * Panel for setting artificial intelligence options.
	 */
	private AIPanel pAI;
	
	/**
	 * Panel for setting remote server options.
	 */
	private RemoteServerPanel pRemoteSrv;

	/**
	 * Constant used for action command to start a game.
	 */
	private static final String ACTION_CMD_START_GAME = "startGame"; 
	
	/**
	 * Creates a new game dialog window.
	 */
	NewGameDialog()
	{
		super(MainFrame.getInstance(), true);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane);
		
		// Tab1
		// game settings
		JPanel gamePane = new JPanel(new GridLayout(4, 1));
		tabbedPane.add("Game Settings", gamePane);
		
		// dimension settings
		pGameDim = new DimensionsPanel();
		gamePane.add( pGameDim );
		
		// first player settings
		pPlayerA = new PlayerPropertiesPanel(0);
		gamePane.add(pPlayerA);
		
		// second player settings
		pPlayerB = new PlayerPropertiesPanel(1);
		gamePane.add(pPlayerB);
		
		// buttons
		JPanel pButtons = new JPanel();
		gamePane.add(pButtons);
	
		JButton btnStart = new JButton("start game");
		btnStart.setActionCommand( ACTION_CMD_START_GAME );
		btnStart.addActionListener( this );
		pButtons.add(btnStart);
		
		JButton btnCancel = new JButton("cancel");
		btnCancel.addActionListener( this );
		pButtons.add(btnCancel);
		
		
		// Tab2
		// ai settings
		pAI = new AIPanel();
		tabbedPane.add("Artificial Intelligence", pAI);
		
		
		// Tab3
		// remote server settings
		pRemoteSrv = new RemoteServerPanel();
		tabbedPane.add("Remote Computer", pRemoteSrv);
		
		this.setLocation(MainFrame.getInstance().getStartLoc());
		
		pack();
	}
	
	/**
	 * Action to start the game and close the dialogue window.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(ACTION_CMD_START_GAME))
		{
			savePreferences();
			MainFrame.getInstance().createGame();
		}
		
		// the dialogue will be closed in either way.
		this.setVisible(false);
		this.dispose();
	}

	private void savePreferences()
	{
		GamePreferences prefs = GamePreferences.getInstance();
		
		// game settings
		prefs.setDimX( pGameDim.getSelectedDimX() );
		prefs.setDimY( pGameDim.getSelectedDimY() );
		prefs.setPlayerType(0, pPlayerA.getSelectedPlayerType() );
		prefs.setPlayerType(1, pPlayerB.getSelectedPlayerType() );
		prefs.setPlayerName(0, pPlayerA.getPlayerName() );
		prefs.setPlayerName(1, pPlayerB.getPlayerName() );
		
		// ai settings
		prefs.setStrategy( pAI.getSelectedStrategy() );
		prefs.setMaxThinkingTime( pAI.getMaxThinkingTime() );
		
		// remote server settings
		prefs.setRemoteServerHost(pRemoteSrv.getSelectedRemoteServerHost());
		prefs.setRemoteServerPort(pRemoteSrv.getSelectedRemoteServerPort());
	}
}

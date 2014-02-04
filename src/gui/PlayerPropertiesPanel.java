/*
 * Created on 01.10.2006
 */
package gui;

import gameelements.GamePreferences;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import player.Player;

/**
 * Panel for setting player properties.
 */
@SuppressWarnings("serial")
public class PlayerPropertiesPanel extends JPanel
{
	/**
	 * Radio button group for choosing the player type.
	 */
	private ButtonGroup bgPlayer;
	
	/**
	 * Textfield for the name of the player.
	 */
	private JTextField tfPlayerName;
	
	/**
	 * Identifier for first or second player.
	 */
	private int playerNr;
	
	/**
	 * The game preferences where previously chosen settings are read from and 
	 * newly adjusted settings will be saved into.
	 */
	private GamePreferences prefs = GamePreferences.getInstance();
	
	/**
	 * Creates a player property panel for a player.
	 * @param playerNr the identifying number of the player for which the panel 
	 * is created.
	 */
	public PlayerPropertiesPanel(int playerNr)
	{
		this.playerNr = playerNr;
		
		JLabel lblPlayer = new JLabel(playerNr == 0 ? "white player" : "black player");
		this.add(lblPlayer);
		
		tfPlayerName = new JTextField( 10 );
		tfPlayerName.setText( prefs.getPlayerName(playerNr) );
		this.add(tfPlayerName);
		
		bgPlayer = new ButtonGroup();
		JRadioButton human = new JRadioButton("human", isHumanSelected());
		human.setActionCommand( String.valueOf( Player.TYPE_HUMAN ) );
		JRadioButton localComp = new JRadioButton("local computer", isLocalCompSelected());
		localComp.setActionCommand( String.valueOf( Player.TYPE_COMPUTER ) );
		JRadioButton remoteComp = new JRadioButton("remote computer", isRemoteCompSelected());
		remoteComp.setActionCommand( String.valueOf( Player.TYPE_REMOTE ) );
		bgPlayer.add(human);
		bgPlayer.add(localComp);
		bgPlayer.add(remoteComp);
		this.add(human);
		this.add(localComp);
		this.add(remoteComp);
	}
	
	private boolean isRemoteCompSelected()
	{
		return prefs.getPlayerType(playerNr) ==  Player.TYPE_REMOTE;
	}

	private boolean isLocalCompSelected()
	{
		return prefs.getPlayerType(playerNr) == Player.TYPE_COMPUTER;
	}

	private boolean isHumanSelected()
	{
		return prefs.getPlayerType(playerNr) == Player.TYPE_HUMAN;
	}
	
	/**
	 * Returns the selected player type.
	 * @return the selected player type.
	 */
	public int getSelectedPlayerType()
	{
		return Integer.valueOf( bgPlayer.getSelection().getActionCommand() );
	}

	/**
	 * Returns the entered player name.
	 * @return the entered player name.
	 */
	public String getPlayerName()
	{
		return tfPlayerName.getText();
	}
	
}

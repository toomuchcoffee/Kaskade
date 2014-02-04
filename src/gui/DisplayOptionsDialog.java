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

@SuppressWarnings("serial")
public class DisplayOptionsDialog extends JDialog implements ActionListener
{
	/**
	 * Panel for setting display options.
	 */
	private DisplayOptionsPanel pDisplayOpt;
	
	/**
	 * Action command for changing settings.
	 */
	private static final String ACTION_CMD_CHANGE_SETTINGS = "changeSettings"; 
	
	/**
	 * Creates a new display options dialogue.
	 */
	DisplayOptionsDialog()
	{
		super(MainFrame.getInstance(), true);
		
		JPanel pBox = new JPanel(new GridLayout(2, 1));
		getContentPane().add(pBox);
		
		pDisplayOpt = new DisplayOptionsPanel();
		pBox.add(pDisplayOpt);
		
		// buttons
		JPanel pButtons = new JPanel();
		pBox.add(pButtons);
	
		JButton btnStart = new JButton("change settings");
		btnStart.setActionCommand( ACTION_CMD_CHANGE_SETTINGS );
		btnStart.addActionListener( this );
		pButtons.add(btnStart);
		
		JButton btnCancel = new JButton("cancel");
		btnCancel.addActionListener( this );
		pButtons.add(btnCancel);
		
		this.setLocation(MainFrame.getInstance().getStartLoc());
		
		pack();
	}
	
	/**
	 * Saves the game preferences and closes the window.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(ACTION_CMD_CHANGE_SETTINGS))
		{
			savePreferences();
		}
		
		// the dialogue will be closed either way.
		this.setVisible(false);
		this.dispose();
	}

	private void savePreferences()
	{
		GamePreferences prefs = GamePreferences.getInstance();
		
		prefs.setAnimatedSteps(pDisplayOpt.isDisplayIntermediateStepsSelection());
		prefs.setAnimationSpeed(pDisplayOpt.getAnimationSpeed());
	}
}

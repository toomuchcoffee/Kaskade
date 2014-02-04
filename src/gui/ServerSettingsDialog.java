/*
 * Created on 24.09.2006
 */
package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import network.Server;
import network.ServerPreferences;

/**
 * Panel for setting local server properties.
 */
@SuppressWarnings("serial")
public class ServerSettingsDialog extends JDialog implements ActionListener
{

	/**
	 * Panel for setting the server options.
	 */
	private ServerSettingsPanel pServerSettings;
	
	/**
	 * Action command for restarting server.
	 */
	private static final String ACTION_CMD_RESTART_SERVER = "restartServer";
	
	/**
	 * Creates a server setting dialog.
	 */
	ServerSettingsDialog()
	{
		super(MainFrame.getInstance(), true);
		
		JPanel pBox = new JPanel(new GridLayout(2, 1));
		getContentPane().add(pBox);
		
		pServerSettings = new ServerSettingsPanel();
		pBox.add(pServerSettings);
		
		// Buttons
		JPanel pButtons = new JPanel();
		pBox.add(pButtons);
	
		JButton btnStart = new JButton("restart server");
		btnStart.setActionCommand( ACTION_CMD_RESTART_SERVER );
		btnStart.addActionListener( this );
		pButtons.add(btnStart);
		
		JButton btnCancel = new JButton("cancel");
		btnCancel.addActionListener( this );
		pButtons.add(btnCancel);
		
		this.setLocation(MainFrame.getInstance().getStartLoc());
		
		pack();
	}
	
	/** 
	 * Saves the preferences and restarts the server.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(ACTION_CMD_RESTART_SERVER))
		{
			if ( !restartServer() )
				return;
		}
		// the dialogues will be closed anyway.
		this.setVisible(false);
		this.dispose();
	}

	private boolean restartServer()
	{
		int portNr = Integer.valueOf( pServerSettings.getServerPort() );
		
		ServerPreferences.getInstance().setThinkingTime( pServerSettings.getMaxThinkingTime() );
		ServerPreferences.getInstance().setServerPort( portNr ); 
		
		Server.getInstance().stopServer();
		try
		{
			Server.getInstance().startServer( portNr );
			
			return true;
		} 
		catch (IOException e)
		{
			MainFrame.getInstance().serverStartExceptionDialog(portNr);
			
			e.printStackTrace();
			
			return false;
		}
	}

}

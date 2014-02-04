/*
 * Created on 01.10.2006
 */
package gui;

import gameelements.GamePreferences;

import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel for setting remote server options.
 */
@SuppressWarnings("serial")
public class RemoteServerPanel extends JPanel implements ChangeListener
{
	/**
	 * Server host for skylab.
	 */
	private static final String SERVER_HOST_WEAK = "132.176.66.72";
	
	/**
	 * Server host for sirius.
	 */
	private static final String SERVER_HOST_STRONG = "132.176.66.13";
	
	/**
	 * Action command for choosing skylab via radio button.
	 */
	private static final String ACTION_CMD_WEAK_REMOTE_SERVER = "weakRemoteServer";
	
	/**
	 * Action command for choosing sirius via radio button.
	 */
	private static final String ACTION_CMD_STRONG_REMOTE_SERVER = "strongRemoteServer"; 
	
	/**
	 * Action command for choosing a custom server host via radio button.
	 */
	private static final String ACTION_CMD_OTHER_REMOTE_SERVER = "otherRemoteServer";
	
	/**
	 * The group of radio buttons to choose a server host.
	 */
	private ButtonGroup bgSrv;
	
	/**
	 * Textfield to enter custom server host.
	 */
	private JTextField tfSrvHost;
	
	/**
	 * Textfield to enter server port.
	 */
	private JTextField tfSrvPort;
	
	/**
	 * The game preferences where previously chosen settings are read from and 
	 * newly adjusted settings will be saved into.
	 */
	private GamePreferences prefs = GamePreferences.getInstance();
	
	/**
	 * Creates a panel for setting the remote server options.
	 */
	RemoteServerPanel()
	{
		this.setLayout(new GridLayout(2, 1));
		
		JPanel p1 = new JPanel();
		this.add(p1);
		
		bgSrv = new ButtonGroup();
		
		JRadioButton optWeakSrv = new JRadioButton("132.176.66.72 (weak)", isWeakRemoteServerSelected());
		optWeakSrv.setActionCommand(ACTION_CMD_WEAK_REMOTE_SERVER);
		
		JRadioButton optStrongSrv = new JRadioButton("132.176.66.13 (strong)", isStrongRemoteServerSelected());
		optStrongSrv.setActionCommand(ACTION_CMD_STRONG_REMOTE_SERVER);
		
		JRadioButton optOtherSrv = new JRadioButton("manual input", isOtherRemoteServerSelected());
		optOtherSrv.setActionCommand(ACTION_CMD_OTHER_REMOTE_SERVER);
		optOtherSrv.addChangeListener(this);
		
		bgSrv.add(optWeakSrv);
		bgSrv.add(optStrongSrv);
		bgSrv.add(optOtherSrv);
		p1.add(optWeakSrv);
		p1.add(optStrongSrv);
		p1.add(optOtherSrv);
		
		// textfields
		RemoteServerSettingsVerifier settingsVerifier = new RemoteServerSettingsVerifier();
		
		tfSrvHost = new JTextField(20);
		tfSrvHost.setText( String.valueOf( prefs.getRemoteServerHost() ) );
		tfSrvHost.setEditable(isOtherRemoteServerSelected());
		p1.add(tfSrvHost);
		
		JPanel p2 = new JPanel();
		this.add(p2);
		
		JLabel lblPort = new JLabel("port: ");
		p2.add(lblPort);
		tfSrvPort = new JTextField(4);
		tfSrvPort.setText( String.valueOf( prefs.getRemoteServerPort() ) );
		tfSrvPort.setInputVerifier(settingsVerifier);
		p2.add(tfSrvPort);
	}
	
	private class RemoteServerSettingsVerifier extends InputVerifier
	{
		public boolean verify(JComponent input)
		{
			return this.checkField(input);
		}
		
		public boolean shouldYieldFocus(JComponent input) 
		{
	        boolean inputOK = verify(input);
			if (!inputOK) 
	        {
				if (input.equals(tfSrvPort))
				{
					tfSrvPort.setText( String.valueOf( GamePreferences.getInstance().getRemoteServerPort() ) );
				}
	            JOptionPane.showMessageDialog(RemoteServerPanel.this, "Ungültige Portangabe. Wert wurde zurückgesetzt.");
	            return false;
	        }
			return true;
	    }
		
		private boolean checkField(JComponent input)
		{
			if (input.equals(tfSrvPort))
			{
				try
				{
					Integer.parseInt( tfSrvPort.getText() );
					return true; 
				}
				catch(NumberFormatException e)
				{
					return false;
				}
			}
			return false;
		}
	}

	private boolean isWeakRemoteServerSelected()
	{
		return prefs.getRemoteServerHost().equals(SERVER_HOST_WEAK);
	}
	
	private boolean isStrongRemoteServerSelected()
	{
		return prefs.getRemoteServerHost().equals(SERVER_HOST_STRONG);
	}
	
	private boolean isOtherRemoteServerSelected()
	{
		return !isWeakRemoteServerSelected() && !isStrongRemoteServerSelected();
	}
	
	/**
	 * Returns the selected remote server host.
	 * @return the selected remote server host.
	 */
	public String getSelectedRemoteServerHost()
	{
		if ( bgSrv.getSelection().getActionCommand().equals(ACTION_CMD_WEAK_REMOTE_SERVER) )
			return SERVER_HOST_WEAK;
		else if ( bgSrv.getSelection().getActionCommand().equals(ACTION_CMD_STRONG_REMOTE_SERVER) )
			return SERVER_HOST_STRONG;
		else
			return tfSrvHost.getText();
	}
	
	/**
	 * Returns the selected remote server port.
	 * @return the selected remote server port.
	 */
	public int getSelectedRemoteServerPort()
	{
		return Integer.valueOf( tfSrvPort.getText() );
	}
	
	/**
	 * Enables/disables other panels according to the chosen radio button.
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		tfSrvHost.setEditable( !tfSrvHost.isEditable() );
	}

}

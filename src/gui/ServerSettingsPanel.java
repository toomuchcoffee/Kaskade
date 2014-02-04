/*
 * Created on 15.10.2006
 */
package gui;

import java.awt.GridLayout;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import network.ServerPreferences;

/**
 * Panel for setting local server options.
 */
@SuppressWarnings("serial")
public class ServerSettingsPanel extends JPanel
{
	/**
	 * Panel for setting the maximal time per turn used by the server.
	 */
	private MaxTimePanel pMaxTime;
	
	/**
	 * Textfield for setting the port on which the server should run.
	 */
	private JTextField tfPort;
	
	/**
	 * Creates a panel for setting all necessary server settings.
	 */
	ServerSettingsPanel()
	{
		this.setLayout(new GridLayout(3,1));
		
		pMaxTime = new MaxTimePanel(ServerPreferences.getInstance().getThinkingTime());
		this.add(pMaxTime);
		
		JPanel p2 = new JPanel();
		this.add(p2);
		
		JLabel lblPort = new JLabel("local port: ");
		p2.add(lblPort);
		
		tfPort = new JTextField( String.valueOf( ServerPreferences.getInstance().getServerPort() ) );
		tfPort.setInputVerifier(new ServerSettingsVerifier());
		p2.add(tfPort);
	}
	
	private class ServerSettingsVerifier extends InputVerifier
	{
		public boolean verify(JComponent input)
		{
			return this.checkPortField(input);
		}
		
		public boolean shouldYieldFocus(JComponent input) 
		{
			boolean inputOK = verify(input);
			if (!inputOK) 
	        {
				if (input.equals(tfPort))
				{
					tfPort.setText( String.valueOf( ServerPreferences.getInstance().getServerPort() ) );
				}
				JOptionPane.showMessageDialog(ServerSettingsPanel.this, "Illegal port. Value has been reset.");
	            return false;
	        }
			return true;
	    }
		
		private boolean checkPortField(JComponent input)
		{
			if (input.equals(tfPort))
			{
				try
				{
					Integer.parseInt( tfPort.getText() );
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

	/**
	 * Returns the chosen maximal allowed thinking time of the server.
	 * @return the chosen maximal allowed thinking time of the server.
	 */
	public long getMaxThinkingTime()
	{
		return pMaxTime.getMaxThinkingTime();
	}


	/**
	 * Returns the chosen port used by the server.
	 * @return the chosen port used by the server.
	 */
	public int getServerPort()
	{
		return Integer.parseInt( tfPort.getText() );
	}

}

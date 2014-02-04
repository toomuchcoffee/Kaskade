/*
 * Created on 01.10.2006
 */
package gui;

import gameelements.GamePreferences;

import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class DimensionsPanel extends JPanel implements ChangeListener
{
	/**
	 * Action command for choosing a 6 x 6 dimension game board.
	 */
	private static final String ACTION_CMD_6_x_6 = "6";
	
	/**
	 * Action command for choosing a 8 x 8 dimension game board. 
	 */
	private static final String ACTION_CMD_8_x_8 = "8"; 
	
	/**
	 * Action command for choosing a 10 x 10 dimension game board.
	 */
	private static final String ACTION_CMD_10_x_10 = "10";
	
	/**
	 * Action command for choosing a custom dimension game board.
	 */
	private static final String ACTION_CMD_VAR = "var"; 
	
	/**
	 * Radio button group for choosing the dimension of the game board.
	 */
	private ButtonGroup bgDim;
	
	/**
	 * Textfield to enter a custom x-dimension.
	 */
	private JTextField tfDimX;
	
	/**
	 * Textfield to enter a custom y-dimension.
	 */
	private JTextField tfDimY;
	
	/**
	 * The game preferences where previously chosen settings are read from and 
	 * newly adjusted settings will be saved into.
	 */
	private GamePreferences prefs = GamePreferences.getInstance();
	
	/**
	 * Creates a dimension panel.
	 */
	public DimensionsPanel()
	{
		bgDim = new ButtonGroup();
		
		JRadioButton opt6x6 = new JRadioButton("6 x 6 fields", is6x6Selected());
		opt6x6.setActionCommand(ACTION_CMD_6_x_6);
		
		JRadioButton opt8x8 = new JRadioButton("8 x 8 fields", is8x8Selected());
		opt8x8.setActionCommand(ACTION_CMD_8_x_8);
		
		JRadioButton opt10x10 = new JRadioButton("10 x 10 fields", is10x10Selected());
		opt10x10.setActionCommand(ACTION_CMD_10_x_10);
		
		JRadioButton optOther = new JRadioButton("manual input", isOptSelected());
		optOther.setActionCommand(ACTION_CMD_VAR);
		optOther.addChangeListener(this);
		
		bgDim.add(opt6x6);
		bgDim.add(opt8x8);
		bgDim.add(opt10x10);
		bgDim.add(optOther);
		this.add(opt6x6);
		this.add(opt8x8);
		this.add(opt10x10);
		this.add(optOther);
		
		// Textfields
		InputVerifier dimVerifier = new DimensionVerifier();
		
		tfDimX = new JTextField(3);
		tfDimX.setText( String.valueOf( prefs.getDimX() ) );
		tfDimX.setEditable(isOptSelected());
		tfDimX.setInputVerifier(dimVerifier);
		
		tfDimY = new JTextField(3);
		tfDimY.setText( String.valueOf( prefs.getDimY() ) );
		tfDimY.setEditable(isOptSelected());
		tfDimY.setInputVerifier(dimVerifier);
		
		this.add(tfDimX);
		this.add(tfDimY);
	}

	private boolean is6x6Selected()
	{
		return prefs.getDimX() == 6 && prefs.getDimY() == 6;
	}
	
	private boolean is8x8Selected()
	{
		return prefs.getDimX() == 8 && prefs.getDimY() == 8;
	}
	
	private boolean is10x10Selected()
	{
		return prefs.getDimX() == 10 && prefs.getDimY() == 10;
	}
	
	private boolean isOptSelected()
	{
		return !is6x6Selected() && !is8x8Selected() && !is10x10Selected();
	}
	
	/**
	 * Returns the selected dimension on x-axis.
	 * @return the selected dimension on x-axis.
	 */
	public int getSelectedDimX()
	{
		return getSelectedDimValue("x");
	}
	
	/**
	 * Returns the selected dimension on y-axis.
	 * @return the selected dimension on y-axis.
	 */
	public int getSelectedDimY()
	{
		return getSelectedDimValue("y");
	}
	
	private int getSelectedDimValue(String value)
	{
		if ( bgDim.getSelection().getActionCommand().equals( ACTION_CMD_VAR ) )
		{
			return Integer.valueOf( value.equals("x") ? tfDimX.getText() : tfDimY.getText() );
		}
		else
		{
			return Integer.valueOf( bgDim.getSelection().getActionCommand() );
		}
	}
	
	private class DimensionVerifier extends InputVerifier
	{
		public boolean verify(JComponent input)
		{
			return this.checkDimField(input);
		}
		
		public boolean shouldYieldFocus(JComponent input) 
		{
			boolean inputOK = verify(input);
			if (!inputOK) 
	        {
				if (input.equals(tfDimX))
				{
					tfDimX.setText( String.valueOf( GamePreferences.getInstance().getDimX() ) );
				}
				else if (input.equals(tfDimY))
				{
					tfDimY.setText( String.valueOf( GamePreferences.getInstance().getDimY() ) );
				}
	            JOptionPane.showMessageDialog(DimensionsPanel.this, "Illegal board dimension. Board can have between 3 and 20 fields.");
	            return false;
	        }
			return true;
	    }
		
		private boolean checkDimField(JComponent input)
		{
			JTextField tf;
			if (input instanceof JTextField)
			{
				tf = (JTextField) input;
				try
				{
					int dim = Integer.parseInt( tf.getText() );
					return dim >= 3 && dim <= 20; 
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
	 * Enables/disables other panels according to the chosen radio button.
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		tfDimX.setEditable( !tfDimX.isEditable() );
		tfDimY.setEditable( !tfDimY.isEditable() );
	}

}

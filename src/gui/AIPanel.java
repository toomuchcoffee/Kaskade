/*
 * Created on 01.10.2006
 */
package gui;

import gameelements.GamePreferences;

import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel for setting the artificial intelligence of the local computer player.
 * Oanel is accessible within the new game dialogue. 
 */
@SuppressWarnings("serial")
public class AIPanel extends JPanel implements ChangeListener
{
	/**
	 * Constant for identifying random strategy.
	 */
	private static final String ACTION_CMD_RANDOM_STRATEGY = "1";
	
	/**
	 * Constant for identifying rule-based strategy.
	 */
	private static final String ACTION_CMD_RULEBASED_STRATEGY = "2";
	
	/**
	 * Constant for identifying game tree strategy.
	 */
	private static final String ACTION_CMD_GAMETREE_STRATEGY = "0";
	
	/**
	 * Button group combines radio buttons for choosing strategy type.
	 */
	private ButtonGroup bgAI;
	
	/**
	 * Panel for setting maximal allowed time for game tree intelligence.
	 */
	private MaxTimePanel pMaxTime;;
	
	/**
	 * The game preferences where previously chosen settings are read from and 
	 * newly adjusted settings will be saved into.
	 */
	private GamePreferences prefs = GamePreferences.getInstance();
	
	/**
	 * Initiates a panel for setting artificial intelligence options.
	 */
	public AIPanel()
	{
		this.setLayout(new GridLayout(2, 1));
		
		JPanel p1 = new JPanel();
		this.add(p1);
		
		bgAI = new ButtonGroup();
		
		JRadioButton optRandom = new JRadioButton("easy (random strategy)", isRandomSelected());
		optRandom.setActionCommand(ACTION_CMD_RANDOM_STRATEGY);
		
		JRadioButton optRulebased = new JRadioButton("intermediate (rule-based strategy)", isRulebasedSelected());
		optRulebased.setActionCommand(ACTION_CMD_RULEBASED_STRATEGY);
		
		JRadioButton optGametree = new JRadioButton("difficult (gametree strategy)", isGametreeSelected());
		optGametree.setActionCommand(ACTION_CMD_GAMETREE_STRATEGY);
		optGametree.addChangeListener(this);
		
		bgAI.add(optRandom);
		bgAI.add(optRulebased);
		bgAI.add(optGametree);
		p1.add(optRandom);
		p1.add(optRulebased);
		p1.add(optGametree);
		
		pMaxTime = new MaxTimePanel(prefs.getMaxThinkingTime());
		pMaxTime.setEnabled(isGametreeSelected());
		this.add(pMaxTime);
	}

	/**
	 * Returns true if random strategy is set in the preferences.
	 * @return true if random strategy is set in the preferences.
	 */
	private boolean isRandomSelected()
	{
		return prefs.getStrategy() == 1;
	}
	
	/**
	 * Returns true if rule-based strategy is set in the preferences.
	 * @return true if rule-based strategy is set in the preferences.
	 */
	private boolean isRulebasedSelected()
	{
		return prefs.getStrategy() == 2;
	}
	
	/**
	 * Returns true if game tree strategy is set in the preferences.
	 * @return true if game tree strategy is set in the preferences.
	 */
	private boolean isGametreeSelected()
	{
		return prefs.getStrategy() == 0;
	}
	
	/**
	 * Returns the identifier for the selected strategy.
	 * @return the identifier for the selected strategy.
	 */
	public int getSelectedStrategy()
	{
		return Integer.valueOf( bgAI.getSelection().getActionCommand() );
	}
	
	/**
	 * Returns the chosen maximal allowed thinking time for game tree strategy.
	 * @return the chosen maximal allowed thinking time for game tree strategy.
	 */
	public long getMaxThinkingTime()
	{
		return pMaxTime.getMaxThinkingTime();
	}
	
	/**
	 * Enables/disables other panels according to the chosen radio button.
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		pMaxTime.setEnabled( !pMaxTime.isEnabled() );
	}

}

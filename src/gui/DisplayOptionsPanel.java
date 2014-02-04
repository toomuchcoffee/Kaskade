/*
 * Created on 01.10.2006
 */
package gui;

import gameelements.GamePreferences;

import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class DisplayOptionsPanel extends JPanel
{
	/**
	 * Action command for option to show animation steps.
	 */
	private static final String ACTION_CMD_ANIMATE_STEPS_YES = "animateStepsYes";
	
	/**
	 * Action command for option not to show animation steps.
	 */
	private static final String ACTION_CMD_ANIMATE_STEPS_NO = "animateStepsNo";
	
	/**
	 * Radio button group for choosing whether to show or not to show animation 
	 * steps.
	 */
	private ButtonGroup bgDisplay;
	
	/**
	 * Panel for choosing the speed of the animation.
	 */
	private AnimationSpeedPanel pAnimSpd;
	
	/**
	 * The game preferences where previously chosen settings are read from and 
	 * newly adjusted settings will be saved into.
	 */
	private GamePreferences prefs = GamePreferences.getInstance();
	
	/**
	 * Creates a new display option panel.
	 */
	public DisplayOptionsPanel()
	{
		this.setLayout(new GridLayout(2, 1));
		
		JPanel p1 = new JPanel();
		this.add(p1);
		
		bgDisplay = new ButtonGroup();
		
		JLabel lbl = new JLabel("animate steps: ");
		p1.add(lbl);
		
		JRadioButton optShowIntermStates = new JRadioButton("yes", isAnimateSteps());
		optShowIntermStates.setActionCommand(ACTION_CMD_ANIMATE_STEPS_YES);
		
		JRadioButton optDontShowIntermStates = new JRadioButton("no", !isAnimateSteps());
		optDontShowIntermStates.setActionCommand(ACTION_CMD_ANIMATE_STEPS_NO);
		
		bgDisplay.add(optShowIntermStates);
		bgDisplay.add(optDontShowIntermStates);
		
		p1.add(optShowIntermStates);
		p1.add(optDontShowIntermStates);
		
		pAnimSpd = new AnimationSpeedPanel(prefs.getAnimationSpeed());
		this.add(pAnimSpd);
	}

	private boolean isAnimateSteps()
	{
		return prefs.isAnimatedSteps();
	}
	
	/**
	 * Returns true, if intermediate steps should be animated, else false.
	 * @return true, if intermediate steps should be animated, else false.
	 */
	public boolean isDisplayIntermediateStepsSelection()
	{
		String actionCmd = bgDisplay.getSelection().getActionCommand();
		if (actionCmd.equals(ACTION_CMD_ANIMATE_STEPS_YES))
		{
			return true;
		}
		else if (actionCmd.equals(ACTION_CMD_ANIMATE_STEPS_NO))
		{
			return false;
		}
		else
		{
			throw new RuntimeException("Animation mode undefined");
		}
	}
	
	/**
	 * Returns the time that passes between every animation step in 
	 * milliseconds.
	 * @return the time that passes between every animation step.
	 */
	public long getAnimationSpeed()
	{
		return pAnimSpd.getAnimationSpeed();
	}

}

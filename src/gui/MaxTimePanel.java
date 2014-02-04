/*
 * Created on 26.12.2006
 */
package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * Slider Panel for setting a maximal thinking time.
 */
@SuppressWarnings("serial")
public class MaxTimePanel extends JPanel
{
	/**
	 * The slider for adjusting the maximal time.
	 */
	private JSlider slMaxTime;
	
	/**
	 * Creates a slider panel with the given time as default set on the panel.
	 * @param defaultMaxThinkingTime
	 */
	public MaxTimePanel(long defaultMaxThinkingTime)
	{
		JLabel lblHost = new JLabel("max time of turn: ");
		this.add(lblHost);
		
		int defaultTime = (int) defaultMaxThinkingTime/1000;
		slMaxTime = new JSlider(JSlider.HORIZONTAL, 0, 10, defaultTime);
		slMaxTime.setMajorTickSpacing(5);
		slMaxTime.setMinorTickSpacing(1);
		slMaxTime.setPaintTicks(true);
		slMaxTime.setPaintLabels(true);
		slMaxTime.setSnapToTicks(true);
		this.add(slMaxTime);
	}
	
	/**
	 * Returns the selected maximal thinking time.
	 * @return the selected maximal thinking time.
	 */
	public long getMaxThinkingTime()
	{
		return slMaxTime.getValue() * 1000L;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled)
	{
		slMaxTime.setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
}

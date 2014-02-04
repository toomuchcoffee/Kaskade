/*
 * Created on 26.12.2006
 */
package gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * Panel for adjusting the animation speed.
 */
@SuppressWarnings("serial")
public class AnimationSpeedPanel extends JPanel
{
	/**
	 * Slider for adjusting the animation speed.
	 */
	private JSlider slAnimationSpd;
	
	/**
	 * Creates an animation speed panel with the given value set as default.
	 * @param defaultAnimationSpeed the speed value to set as default.
	 */
	public AnimationSpeedPanel(long defaultAnimationSpeed)
	{
		JLabel lbl = new JLabel("animation speed: ");
		this.add(lbl);
		
		slAnimationSpd = new JSlider(JSlider.HORIZONTAL, 0, 2000, (int) defaultAnimationSpeed);
		slAnimationSpd.setMajorTickSpacing(500);
		slAnimationSpd.setMinorTickSpacing(100);
		slAnimationSpd.setPaintTicks(true);
		slAnimationSpd.setPaintLabels(true);
		slAnimationSpd.setSnapToTicks(true);
		this.add(slAnimationSpd);
	}
	
	/**
	 * Returns the selected animation speed.
	 * @return the selected animation speed.
	 */
	public long getAnimationSpeed()
	{
		return slAnimationSpd.getValue() * 1L;
	}
	
}

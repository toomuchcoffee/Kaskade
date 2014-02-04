/*
 * Created on 05.10.2006
 */
package app;

import gui.MainFrame;

/**
 * Main class with main-method.
 */
public class Application
{
	
	/**
	 * Starts the main window.
	 * @param args no args used.
	 */
	public static void main (String[] args)
	{
		MainFrame.getInstance().setVisible(true);
	}

}

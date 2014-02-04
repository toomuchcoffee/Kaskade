/*
 * Created on 17.09.2006
 */
package gui;

import gameelements.Game;
import gameelements.GameColor;
import gameelements.GamePreferences;
import gameelements.Position;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import player.HumanPlayer;
import player.Player;
import animation.Animator;

/**
 * Panel which displays the game board and its game states and where all moves
 * can be set via mouse click.
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements MouseListener, Runnable
{
	/**
	 * The size of one rectangle which represents one field of the board.
	 */
	private int rectSize;
	
	/**
	 * The size of one circle which represents a game token.
	 */
	private int circleSize;
	
	/**
	 * Indicates if the panel's thread is running.
	 */
	private boolean running = false;
	
	/**
	 * The game that is being displayed on the panel.
	 */
	private Game game;
	
	/**
	 * The animator class that calculates animation steps from the model.
	 */
	private Animator animator;
	
	/**
	 * Creates a game panel for a game.
	 * @param game the game the panel is created for.
	 */
	GamePanel(Game game)
	{
		this.game = game;
		
		setBackground(Color.lightGray);
		
		addMouseListener( this );
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		// init der Feldgrößen
		int dimX = game.getSituation().getDimX();
		int dimY = game.getSituation().getDimY();
		int maxDim = dimX > dimY ? dimX : dimY;
		rectSize = this.prefPanelSize()/maxDim;
		circleSize = rectSize/5;
		
		animator = new Animator(game);
		animator.processStateQueue();
		
		setPreferredSize(new Dimension(dimX*rectSize+1, dimY*rectSize+1));
	}
	
	/**
	 * Returns the animator.
	 * @return the animator.
	 */
	public Animator getAnimator()
	{
		return this.animator;
	}
	
	private int prefPanelSize()
	{
		int frameHeight = MainFrame.getInstance().getHeight();
		int frameWidth = MainFrame.getInstance().getWidth();
		
		int minFrameDim = frameHeight < frameWidth ? frameHeight : frameWidth;
		
		return minFrameDim - 80;
	}

	private void start()
	{
		if (!running)
		{
			Thread t = new Thread(this);
			t.start();
		}
	}
	
	/**
	 * This ensures the panel's thread to be started just after the panel has 
	 * been added to the main frame. 
	 * @see javax.swing.JComponent#addNotify()
	 */
	public void addNotify()
	{
		super.addNotify();
		this.start();
	}
	
	/** 
	 * This stops the panel's thread just after the panel has been removed from
	 * the main frame.
	 * @see javax.swing.JComponent#removeNotify()
	 */
	public void removeNotify()
	{
		this.running = false;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
		        RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON); 
		
		int currentX = 0;
		int currentY = 0;
		
		if (animator.hasCurrentAnimationState())
		{
			MainFrame.getInstance().setStatus1( "turn " + animator.getCurrentTurn() ); 
			MainFrame.getInstance().setStatus2( animator.getCurrentPlayer().getPlayerName() );
			MainFrame.getInstance().setStatus3( 
					"white: " + animator.getTokensOfColor(GameColor.WHITE) );
			MainFrame.getInstance().setStatus4( 
					"black: " + animator.getTokensOfColor(GameColor.BLACK) );
			
			for (Position aPos : game.getSituation().getPositions())
			{
				currentX = aPos.getX() * rectSize;
				currentY = aPos.getY() * rectSize;
				
				if ( animator.getCurrentMove() != null )
				{
					if ( animator.getCurrentMove().equals(aPos) )
					{
						g2.setColor(Color.orange);
						g2.fillRect(currentX, currentY, rectSize, rectSize);
					}
					else if ( animator.isFlowingOver(aPos) )
					{
						g2.setColor(Color.darkGray);
						g2.fillRect(currentX, currentY, rectSize, rectSize);
					}
					else if (animator.getCurrentChangedPositions().contains(aPos) )
					{
						g2.setColor(Color.gray);
						g2.fillRect(currentX, currentY, rectSize, rectSize);
					}
					g2.setColor(Color.black);
				}
				
				g2.drawRect( currentX, currentY, rectSize, rectSize );
	
				this.drawTokens(g2, animator.getTokens(aPos), animator.getColor(aPos), currentX, currentY);	
			}
		}
	}

	private void drawTokens(
			Graphics2D g2, int tokens, GameColor color, int currentFieldPosX, int currentFieldPosY)
	{
		if (tokens > 0)
		{
			double pileWidth = Math.ceil( Math.sqrt(tokens) );
			double pileHeight = Math.ceil( tokens / pileWidth );
			int pileCapacity = (int) (pileHeight * pileWidth);
			
			// start left bottom
			int currentTokenPosX = 0;			// left
			int currentTokenPosY = rectSize;	// bottom
			
			// arrange circles ...
			for (int i=0; i<tokens; i++)
			{
				// ... in height
				if (i % pileWidth == 0) // wenn wir eine Reihe voll haben, gehen wir einen höher
				{
					currentTokenPosX = 0;
					currentTokenPosY -= rectSize / (pileHeight + 1);
				}
				// ... and width
				int row = (int) Math.ceil((i+1)/pileWidth);
				if (tokens % pileCapacity != 0 && row == pileHeight) // Rest in oberster Reihe
				{
					currentTokenPosX += rectSize / (pileWidth-(pileCapacity-tokens) + 1);
				}
				else // row is full
				{
					currentTokenPosX += rectSize / (pileWidth + 1);
				}
				
				this.drawPositionedToken(g2, color, currentFieldPosX, currentFieldPosY, currentTokenPosX, currentTokenPosY);
			}
		}	
	}

	private void drawPositionedToken(
			Graphics2D g2, GameColor color, int currentFieldPosX, int currentFieldPosY, int currentTokenPosX, int currentTokenPosY)
	{
		int x = currentFieldPosX - circleSize/2 + currentTokenPosX;
		int y = currentFieldPosY - circleSize/2 + currentTokenPosY;

		if (color != null)
		{
			if (color == GameColor.BLACK)
			{
				g2.drawOval(x, y, circleSize, circleSize);
				g2.fillOval(x, y, circleSize, circleSize);
			}
			else if (color == GameColor.WHITE)
			{
				g2.setColor(Color.white);
				g2.drawOval(x, y, circleSize, circleSize);
				g2.fillOval(x, y, circleSize, circleSize);
				g2.setColor(Color.black);
			}
		}
	}
	
	/**
	 * Ensures that the panel will always receive the most recent state from
	 * the model and triggers a repaint of the panel according to the selected 
	 * animation speed. Stops if game is over.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		running = true;
		
		while (running)
		{
			if ( !animator.hasNext() )
			{
				synchronized(animator)
				{
					try
					{
						animator.wait();
					} 
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			animator.processStateQueue();
			
			this.repaint();
			
			try
			{
				Thread.sleep( GamePreferences.getInstance().getAnimationSpeed() );
			} 
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			if ( animator.isUniColored() )
			{
				this.repaint();
				
				running = false;
				
				int retval = JOptionPane.showConfirmDialog(this, 
						"GAME OVER\nwinner: " 
					  + game.getWinner().getPlayerName() 
					  + "\nnew game?");
				
				if (retval == JOptionPane.YES_OPTION)
				{
					MainFrame.getInstance().newGameDialog();
				}
				else if (retval == JOptionPane.NO_OPTION)
				{
					MainFrame.getInstance().exit();
				}
			}
		}
		
	}
	
	/** 
	 * Delivers a move to the model.
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e)
	{
		Player activePlayer = game.getActivePlayer();
		if ( activePlayer != null && activePlayer instanceof HumanPlayer )
		{
			Position nextMove = new Position( (e.getX()/rectSize), (e.getY()/rectSize) );
			((HumanPlayer) activePlayer).setNextMove( nextMove );
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}
	
}

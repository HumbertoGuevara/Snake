
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * The {@code SidePanel} class is responsible for displaying statistics and
 * controls to the player.
 * @author Brendan Jones
 *
 */
public class SidePanel extends JPanel {
	
	/**
	 * Serial Version UID.
	 */
	private static final long lSerialVersionUID = -40557434900946408L;

	/**
	 * The large font to draw with.
	 */
	private static final Font fntLARGE_FONT = new Font("Tahoma", Font.BOLD, 20);
	
	/**
	 * The medium font to draw with.
	 */
	private static final Font fntMEDIUM_FONT = new Font("Tahoma", Font.BOLD, 16);

	/**
	 * The small font to draw with.
	 */
	private static final Font fntSMALL_FONT = new Font("Tahoma", Font.BOLD, 12);
	
	/**
	 * The SnakeGame instance.
	 */
	private SnakeGame snkGame;
	
	/**
	 * Creates a new SidePanel instance.
	 * @param game The SnakeGame instance.
	 */
	public SidePanel(SnakeGame snkGame) {
		this.snkGame = snkGame;
		
		setPreferredSize(new Dimension(300, BoardPanel.iROW_COUNT * BoardPanel.iTILE_SIZE));
		setBackground(Color.BLACK);
	}
	
	private static final int iSTATISTICS_OFFSET = 150;
	
	private static final int iCONTROLS_OFFSET = 320;
	
	private static final int iMESSAGE_STRIDE = 30;
	
	private static final int iSMALL_OFFSET = 30;
	
	private static final int iLARGE_OFFSET = 50;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		/*
		 * Set the color to draw the font in to white.
		 */
		g.setColor(Color.WHITE);
		
		/*
		 * Draw the game name onto the window.
		 */
		g.setFont(fntLARGE_FONT);
		g.drawString("Snake Game", getWidth() / 2 - g.getFontMetrics().stringWidth("Snake Game") / 2, 50);
		
		/*
		 * Draw the categories onto the window.
		 */
		g.setFont(fntMEDIUM_FONT);
		g.drawString("Statistics", iSMALL_OFFSET, iSTATISTICS_OFFSET);
		g.drawString("Controls", iSMALL_OFFSET, iCONTROLS_OFFSET);
				
		/*
		 * Draw the category content onto the window.
		 */
		g.setFont(fntSMALL_FONT);
		
		//Draw the content for the statistics category.
		int iDrawY = iSTATISTICS_OFFSET;
		g.drawString("Total Score: " + snkGame.getScore(), iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
		g.drawString("Fruit Eaten: " + snkGame.getFruitsEaten(), iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
		g.drawString("Fruit Score: " + snkGame.getNextFruitScore(), iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
		//Draw the content for the controls category.
		drawY = iCONTROLS_OFFSET;
		g.drawString("Move Up: W / Up Arrowkey", iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
		g.drawString("Move Down: S / Down Arrowkey", iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
		g.drawString("Move Left: A / Left Arrowkey", iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
		g.drawString("Move Right: D / Right Arrowkey", iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
		g.drawString("Pause Game: P", iLARGE_OFFSET, drawY += iMESSAGE_STRIDE);
	}

}
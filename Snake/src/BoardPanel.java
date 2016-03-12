
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.JPanel;

/**
 * The {@code BoardPanel} class is responsible for managing and displaying the
 * contents of the game board.
 *
 * @author Brendan Jones
 *
 */
public class BoardPanel extends JPanel {

    /**
     * Serial Version UID.
     */
    private static final long lSerialVersionUID = -1102632585936750607L;

    /**
     * The number of columns on the board. (Should be odd so we can start in the
     * center).
     */
    public static final int iCOL_COUNT = 25;

    /**
     * The number of rows on the board. (Should be odd so we can start in the
     * center).
     */
    public static final int iROW_COUNT = 25;

    /**
     * The size of each tile in pixels.
     */
    public static final int iTILE_SIZE = 20;

    /**
     * The number of pixels to offset the eyes from the sides.
     */
    private static final int iEYE_LARGE_INSET = iTILE_SIZE / 3;

    /**
     * The number of pixels to offset the eyes from the front.
     */
    private static final int iEYE_SMALL_INSET = iTILE_SIZE / 6;

    /**
     * The length of the eyes from the base (small inset).
     */
    private static final int iEYE_LENGTH = iTILE_SIZE / 5;

    /**
     * The font to draw the text with.
     */
    private static final Font fntFONT = new Font("Tahoma", Font.BOLD, 25);

    /**
     * The SnakeGame instance.
     */
    private SnakeGame snkGame;

    /**
     * The array of tiles that make up this board.
     */
    private TileType[] tltTiles;

    //imagen de los objetos malos
    private Image imaMalo;
    
    //imagen del objeto bueno1
    private Image imaBueno1;
    
    //imagen del objeto bueno2
    private Image imaBueno2;
    
    //imagen del objeto bueno3
    private Image imaBueno3;

    //arreglo de colores para saber que color modificar una tile
    private Color[] colColores;
    /**
     * Creates a new BoardPanel instance.
     *
     * @param snkGame The SnakeGame instance.
     */
    public BoardPanel(SnakeGame snkGame) {
        this.snkGame = snkGame;
        //inicializa las tiles
        this.tltTiles = new TileType[iROW_COUNT * iCOL_COUNT];
        //inicializa los colores
        this.colColores = new Color[tltTiles.length];
        //por default todas las tiles se deben de pintar en verde
        //si se ocupa en ellas una tile del cuerpo de la snake
        for(int iI=0;iI<colColores.length;iI++){
            colColores[iI] = Color.GREEN;
        }
        setPreferredSize(new Dimension(iCOL_COUNT * iTILE_SIZE, iROW_COUNT * iTILE_SIZE));
        setBackground(Color.BLACK);
        //inicializar la imagen de los malos
        URL urlMalo = this.getClass().getResource("skull.png");
        imaMalo = Toolkit.getDefaultToolkit().getImage(urlMalo);
        
        //inicializo las imagenes de los buenos
        URL urlBueno1 = this.getClass().getResource("strawberry.png");
        imaBueno1 = Toolkit.getDefaultToolkit().getImage(urlBueno1);
        URL urlBueno2 = this.getClass().getResource("peach.png");
        imaBueno2 = Toolkit.getDefaultToolkit().getImage(urlBueno2);
        URL urlBueno3 = this.getClass().getResource("banana.png");
        imaBueno3 = Toolkit.getDefaultToolkit().getImage(urlBueno3);

    }

    /**
     * Clears all of the tiles on the board and sets their values to null.
     */
    public void clearBoard() {
        for (int i = 0; i < tltTiles.length; i++) {
            tltTiles[i] = null;
        }
    }

    /**
     * Sets the tile at the desired coordinate.
     *
     * @param pntPoint The coordinate of the tile.
     * @param tltType The type to set the tile to.
     */
    public void setTile(Point pntPoint, TileType tltType) {
        setTile(pntPoint.x, pntPoint.y, tltType);
    }

    /**
     * Sets the tile at the desired coordinate.
     *
     * @param iX The x coordinate of the tile.
     * @param iY The y coordinate of the tile.
     * @param tltType The type to set the tile to.
     */
    public void setTile(int iX, int iY, TileType tltType) {
        tltTiles[iY * iROW_COUNT + iX] = tltType;
    }
    public void setColor(int iX, int iY, Color colColor){
        colColores[iY*iROW_COUNT+iX] = colColor;
    }
    /**
     * Gets the tile at the desired coordinate.
     *
     * @param iX The x coordinate of the tile.
     * @param iY The y coordinate of the tile.
     * @return
     */
    public TileType getTile(int iX, int iY) {
        return tltTiles[iY * iROW_COUNT + iX];
    }
    //regresa el color asociado a una tile en especifico
    //misma logica de getTile
    public Color getColor(int iX, int iY){
        return colColores[iY*iROW_COUNT+iX];
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*
		 * Loop through each tile on the board and draw it if it
		 * is not null.
         */
        for (int iX = 0; iX < iCOL_COUNT; iX++) {
            for (int iY = 0; iY < iROW_COUNT; iY++) {
                TileType tltType = getTile(iX, iY);
                Color colColor = getColor(iX,iY);
                if (tltType != null) {
                    drawTile(iX * iTILE_SIZE, iY * iTILE_SIZE, tltType
                            ,colColor, g);
                }
            }
        }

        /*
		 * Draw the grid on the board. This makes it easier to see exactly
		 * where we in relation to the fruit.
		 * 
		 * The panel is one pixel too small to draw the bottom and right
		 * outlines, so we outline the board with a rectangle separately.
         */
        g.setColor(Color.DARK_GRAY);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        for (int iX = 0; iX < iCOL_COUNT; iX++) {
            for (int iY = 0; iY < iROW_COUNT; iY++) {
                g.drawLine(iX * iTILE_SIZE, 0, iX * iTILE_SIZE, getHeight());
                g.drawLine(0, iY * iTILE_SIZE, getWidth(), iY * iTILE_SIZE);
            }
        }

        /*
		 * Show a message on the screen based on the current game state.
         */
        if (snkGame.isGameOver() || snkGame.isNewGame() || snkGame.isPaused()) {
            g.setColor(Color.WHITE);

            /*
			 * Get the center coordinates of the board.
             */
            int iCenterX = getWidth() / 2;
            int iCenterY = getHeight() / 2;

            /*
			 * Allocate the messages for and set their values based on the game
			 * state.
             */
            String sLargeMessage = null;
            String sSmallMessage = null;
            if (snkGame.isNewGame()) {
                sLargeMessage = "Snake Game!";
                sSmallMessage = "Press Enter to Start";
            } else if (snkGame.isGameOver()) {
                sLargeMessage = "Game Over!";
                sSmallMessage = "Press Enter to Restart";
            } else if (snkGame.isPaused()) {
                sLargeMessage = "Paused";
                sSmallMessage = "Press P to Resume";
            }

            /*
			 * Set the message font and draw the messages in the center of the board.
             */
            g.setFont(fntFONT);
            g.drawString(sLargeMessage, iCenterX - g.getFontMetrics().stringWidth(sLargeMessage) / 2, iCenterY - 50);
            g.drawString(sSmallMessage, iCenterX - g.getFontMetrics().stringWidth(sSmallMessage) / 2, iCenterY + 50);
        }
    }

    /**
     * Draws a tile onto the board.
     *
     * @param iX The x coordinate of the tile (in pixels).
     * @param iY The y coordinate of the tile (in pixels).
     * @param tltType The type of tile to draw.
     * @param g The graphics object to draw to.
     */
    private void drawTile(int iX, int iY, TileType tltType,Color colColor,
            Graphics g) {
        /*
		 * Because each type of tile is drawn differently, it's easiest
		 * to just run through a switch statement rather than come up with some
		 * overly complex code to handle everything.
         */
        switch (tltType) {

            /*
		 * A fruit is depicted as a small red circle that with a bit of padding
		 * on each side.
             */
            case Fruit:
                g.drawImage(imaBueno1, iX + 2, iY + 2, iTILE_SIZE, iTILE_SIZE,this);
                break;
                
            case Fruit2:
                g.drawImage(imaBueno2, iX + 2, iY + 2, iTILE_SIZE, iTILE_SIZE,this);
                break;
                
            case Fruit3:
                g.drawImage(imaBueno3, iX + 2, iY + 2, iTILE_SIZE, iTILE_SIZE,this);
                break;

            /*
		 * The snake body is depicted as a green square that takes up the
		 * entire tile.
             */
            case SnakeBody:
                g.setColor(colColor);
                g.fillRect(iX, iY, iTILE_SIZE, iTILE_SIZE);
                break;

            /*
		 * The snake head is depicted similarly to the body, but with two
		 * lines (representing eyes) that indicate it's direction.
             */
            case SnakeHead:
                //Fill the tile in with green.
                g.setColor(Color.GREEN);
                g.fillRect(iX, iY, iTILE_SIZE, iTILE_SIZE);

                //Set the color to black so that we can start drawing the eyes.
                g.setColor(Color.BLACK);

                /*
			 * The eyes will always 'face' the direction that the snake is
			 * moving.
			 * 
			 * Vertical lines indicate that it's facing North or South, and
			 * Horizontal lines indicate that it's facing East or West.
			 * 
			 * Additionally, the eyes will be closer to whichever edge it's
			 * facing.
			 * 
			 * Drawing the eyes is fairly simple, but is a bit difficult to
			 * explain. The basic process is this:
			 * 
			 * First, we add (or subtract) EYE_SMALL_INSET to or from the
			 * side of the tile representing the direction we're facing. This
			 * will be constant for both eyes, and is represented by the
			 * variable 'baseX' or 'baseY' (depending on orientation).
			 * 
			 * Next, we add (or subtract) EYE_LARGE_INSET to and from the two
			 * neighboring directions (Example; East and West if we're facing
			 * north).
			 * 
			 * Finally, we draw a line from the base offset that is EYE_LENGTH
			 * pixels in length at whatever the offset is from the neighboring
			 * directions.
			 * 
                 */
                switch (snkGame.getDirection()) {
                    case North: {
                        int baseY = iY + iEYE_SMALL_INSET;
                        g.drawLine(iX + iEYE_LARGE_INSET, baseY, iX + iEYE_LARGE_INSET, baseY + iEYE_LENGTH);
                        g.drawLine(iX + iTILE_SIZE - iEYE_LARGE_INSET, baseY, iX + iTILE_SIZE - iEYE_LARGE_INSET, baseY + iEYE_LENGTH);
                        break;
                    }

                    case South: {
                        int baseY = iY + iTILE_SIZE - iEYE_SMALL_INSET;
                        g.drawLine(iX + iEYE_LARGE_INSET, baseY, iX + iEYE_LARGE_INSET, baseY - iEYE_LENGTH);
                        g.drawLine(iX + iTILE_SIZE - iEYE_LARGE_INSET, baseY, iX + iTILE_SIZE - iEYE_LARGE_INSET, baseY - iEYE_LENGTH);
                        break;
                    }

                    case West: {
                        int baseX = iX + iEYE_SMALL_INSET;
                        g.drawLine(baseX, iY + iEYE_LARGE_INSET, baseX + iEYE_LENGTH, iY + iEYE_LARGE_INSET);
                        g.drawLine(baseX, iY + iTILE_SIZE - iEYE_LARGE_INSET, baseX + iEYE_LENGTH, iY + iTILE_SIZE - iEYE_LARGE_INSET);
                        break;
                    }

                    case East: {
                        int baseX = iX + iTILE_SIZE - iEYE_SMALL_INSET;
                        g.drawLine(baseX, iY + iEYE_LARGE_INSET, baseX - iEYE_LENGTH, iY + iEYE_LARGE_INSET);
                        g.drawLine(baseX, iY + iTILE_SIZE - iEYE_LARGE_INSET, baseX - iEYE_LENGTH, iY + iTILE_SIZE - iEYE_LARGE_INSET);
                        break;
                    }

                }
                break;
            case Venom:
                g.drawImage(imaMalo,iX - 2, iY - 2, 24, 24,this);
                break;

        }
    }
    public int[] getTablero(){
        int iarrSalida[] = new int[tltTiles.length];
        for(int iC=0;iC<tltTiles.length;iC++){
            if(tltTiles[iC]==null){
                iarrSalida[iC]=-1;
            }else{
            switch(tltTiles[iC]){
                case Fruit:
                    iarrSalida[iC] = 1;
                    break;
                case SnakeHead:
                    iarrSalida[iC] = 2;
                    break;
                case SnakeBody:
                    iarrSalida[iC] = 3;
                    break;
                case Venom:
                    iarrSalida[iC] = 4;
                    break;
                case Fruit2:
                    iarrSalida[iC] = 5;
                    break;
                case Fruit3:
                    iarrSalida[iC] = 6;
                    break;
                    
            }
            }
        }
        return iarrSalida;
    }
    public void setTablero(int[] iArrEntrada){
        tltTiles = new TileType[iArrEntrada.length];
        this.clearBoard();
        for(int iC=0;iC<iArrEntrada.length;iC++){
            switch(iArrEntrada[iC]){
                case -1:
                    tltTiles[iC] = null;
                    break;
                case 1:
                    tltTiles[iC] = TileType.Fruit;
                    break;
                case 2:
                    tltTiles[iC] = TileType.SnakeHead;
                    break;
                case 3:
                    tltTiles[iC] = TileType.SnakeBody;
                    break;
                case 4:
                    tltTiles[iC] = TileType.Venom;
                    break;
                case 5:
                    tltTiles[iC] = TileType.Fruit2;
                    break;
                case 6:
                    tltTiles[iC] = TileType.Fruit3;
                    break;
            }
        }
    }
}

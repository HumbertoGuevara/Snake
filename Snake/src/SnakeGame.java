
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Random;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
 * Segundo examen proyecto de desarrollo de videojuegos
 * 
 * Snake
 * 
 * Agregadas funcion de guardar y cargar, algunas imagenes
 * sonidos, efectos de colores
 * 
 * diferentes tipos de objetos buenos y malos
 * 
 * José Humberto Guevara Martínez A01280642
 * 
 * Juan José López Jaimez A01361534
 * 
 * 
 */

/**
 * The {@code SnakeGame} class is responsible for handling much of the game's
 * logic.
 *
 * @author Brendan Jones
 *
 */
public class SnakeGame extends JFrame {

    /**
     * The Serial Version UID.
     */
    private static final long lserialVersionUID = 6678292058307426314L;

    /**
     * The number of milliseconds that should pass between each frame.
     */
    private static final long lFRAME_TIME = 1000L / 50L;

    /**
     * The minimum length of the snake. This allows the snake to grow right when
     * the game starts, so that we're not just a head moving around on the
     * board.
     */
    private static final int iMIN_SNAKE_LENGTH = 5;

    /**
     * The maximum number of directions that we can have polled in the direction
     * list.
     */
    private static final int iMAX_DIRECTIONS = 3;

    /**
     * The BoardPanel instance.
     */
    private BoardPanel bpnBoard;

    /**
     * The SidePanel instance.
     */
    private SidePanel spnSide;

    /**
     * The random number generator (used for spawning fruits).
     */
    private Random ranRandom;

    /**
     * The Clock instance for handling the game logic.
     */
    private Clock clkLogicTimer;

    /**
     * Whether or not we're running a new game.
     */
    private boolean bIsNewGame;

    /**
     * Whether or not the game is over.
     */
    private boolean bIsGameOver;

    /**
     * Whether or not the game is paused.
     */
    private boolean bIsPaused;

    /**
     * The list that contains the points for the snake.
     */
    private LinkedList<Point> lklSnake;

    /**
     * The list that contains the queued directions.
     */
    private LinkedList<Direction> lklDirections;

    /**
     * The current score.
     */
    private int iScore;

    /**
     * The number of fruits that we've eaten.
     */
    private int iFruitsEaten;

    /**
     * The number of points that the next fruit will award us.
     */
    private int iNextFruitScore;
    
    /**
     * The amount the Snake will increase depending on the fruit.
     */
    private int contadorFruitSnake;
    
    /**
     * Variable que controla el sonido de fondo.
     */
    private SoundClip SClipFondo; 
    
    //variable del sonido de comer una frutita
    private SoundClip sClipEat;
    
    //variable de chocar con algo malo o una pared
    
    private SoundClip sClipDead;
    /**
     * Variable para saber si el juego esta pausado.
     */
    private boolean bPausado; 
    
    /**
     * Variable para saber si el juego esta muteado.
     */
    private boolean bMuteado; 
    
        /**
     * Variable para saber si el juego ya esta muteado.
     */
    private boolean bIsMuteado;

    //para controlar la animacion de los colores
    //se usa la posicion actual para dibujar los colores especiales
    //la cantidad para decir cuantas tiles especiales
    //y el color para cual color especial dibujar
    private int iPosColor;
    private int iCantidadColor;
    private Color colEspecial;
    /**
     * Creates a new SnakeGame instance. Creates a new window, and sets up the
     * controller input.
     */
    private SnakeGame() {
        super("Snake Remake");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        
        /*
		 * Initialize the game's panels and add them to the window.
         */
        this.bpnBoard = new BoardPanel(this);
        this.spnSide = new SidePanel(this);

        add(bpnBoard, BorderLayout.CENTER);
        add(spnSide, BorderLayout.EAST);

        /*
	 * Add the background sound. 
	*/
        SClipFondo = new SoundClip("Fondo.wav");
        SClipFondo.setLooping(true);
        SClipFondo.play();
        //inicializar los otros sonidos útiles
        this.sClipEat = new SoundClip("eat.wav");
        this.sClipDead = new SoundClip("dead.wav");
        //por default las variables de control de anmacion de color son 0 y verde
        iPosColor = 0;
        iCantidadColor = 0;
        colEspecial = Color.GREEN;
        /*
	 * Inicializo en false los boleanos que revisan el pausado y muteado. 
	*/
        bPausado = false;
        bMuteado = false;
        bIsMuteado = true;
        
        
        /*
		 * Adds a new key listener to the frame to process input. 
         */
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent keyE) {
                switch (keyE.getKeyCode()) {

                    /*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to North before adding the
				 * direction to the list.
                     */
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        if (!bIsPaused && !bIsGameOver) {
                            if (lklDirections.size() < iMAX_DIRECTIONS) {
                                Direction dirLast = lklDirections.peekLast();
                                if (dirLast != Direction.South && dirLast != Direction.North) {
                                    lklDirections.addLast(Direction.North);
                                }
                            }
                        }
                        break;

                    /*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to South before adding the
				 * direction to the list.
                     */
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        if (!bIsPaused && !bIsGameOver) {
                            if (lklDirections.size() < iMAX_DIRECTIONS) {
                                Direction dirLast = lklDirections.peekLast();
                                if (dirLast != Direction.North && dirLast != Direction.South) {
                                    lklDirections.addLast(Direction.South);
                                }
                            }
                        }
                        break;

                    /*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to West before adding the
				 * direction to the list.
                     */
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                        if (!bIsPaused && !bIsGameOver) {
                            if (lklDirections.size() < iMAX_DIRECTIONS) {
                                Direction dirLast = lklDirections.peekLast();
                                if (dirLast != Direction.East && dirLast != Direction.West) {
                                    lklDirections.addLast(Direction.West);
                                }
                            }
                        }
                        break;

                    /*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to East before adding the
				 * direction to the list.
                     */
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                        if (!bIsPaused && !bIsGameOver) {
                            if (lklDirections.size() < iMAX_DIRECTIONS) {
                                Direction dirLast = lklDirections.peekLast();
                                if (dirLast != Direction.West && dirLast != Direction.East) {
                                    lklDirections.addLast(Direction.East);
                                }
                            }
                        }
                        break;

                    /*
				 * If the game is not over, toggle the paused flag and update
				 * the logicTimer's pause flag accordingly.
                     */
                    case KeyEvent.VK_P:
                        if (!bIsGameOver) {
                            bIsPaused = !bIsPaused;
                            clkLogicTimer.setPaused(bIsPaused);
                            if(bIsMuteado)
                            {
                            if(bPausado)
                            {
                                SClipFondo.unpause();
                            }
                            else
                            {
                                SClipFondo.pause();
                            }
                            bPausado = !bPausado;
                            }
                        }
                        break;
                       
                    /*          
                       * Opcion que puede usar el usuario si quiere mutear el sonido.
                    */
                        
                    case KeyEvent.VK_M:
                        if (!bIsGameOver && !bIsPaused) {
                        if(bMuteado)
                        {
                        //mutea el sonido cuando el usuario presione la tecla m
                        SClipFondo.stop();
                        bIsMuteado = false;
                        }
                        else
                        {
                        //vuelve a correr la musica cuando el usuario vuelva a presionar m
                        SClipFondo.setLooping(true);
                        SClipFondo.play();
                        bIsMuteado = true;
                        }
                        bMuteado = !bMuteado;
                        }
                        break;

                    /*
				 * Reset the game if one is not currently in progress.
                     */
                    case KeyEvent.VK_ENTER:
                        if (bIsNewGame || bIsGameOver) {
                            resetGame();
                        }
                        break;
                    case KeyEvent.VK_C:
                        Cargar();
                        break;
                    case KeyEvent.VK_G:
                        Guardar();
                        break;
                }
            }

        });

        /*
		 * Resize the window to the appropriate size, center it on the
		 * screen and display it.
         */
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Starts the game running.
     */
    private void startGame() {
        /*
		 * Initialize everything we're going to be using.
         */
        this.ranRandom = new Random();
        this.lklSnake = new LinkedList<>();
        this.lklDirections = new LinkedList<>();
        this.clkLogicTimer = new Clock(9.0f);
        this.bIsNewGame = true;

        //Set the timer to paused initially.
        clkLogicTimer.setPaused(true);

        /*
		 * This is the game loop. It will update and render the game and will
		 * continue to run until the game window is closed.
         */
        while (true) {
            //Get the current frame's start time.
            long lStart = System.nanoTime();

            //Update the logic timer.
            clkLogicTimer.update();

            /*
			 * If a cycle has elapsed on the logic timer, then update the game.
             */
            if (clkLogicTimer.hasElapsedCycle()) {
                updateGame();
            }

            //Repaint the board and side panel with the new content.
            bpnBoard.repaint();
            spnSide.repaint();

            /*
			 * Calculate the delta time between since the start of the frame
			 * and sleep for the excess time to cap the frame rate. While not
			 * incredibly accurate, it is sufficient for our purposes.
             */
            long delta = (System.nanoTime() - lStart) / 1000000L;
            if (delta < lFRAME_TIME) {
                try {
                    Thread.sleep(lFRAME_TIME - delta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Updates the game's logic.
     */
    private void updateGame() {
        /*
		 * Gets the type of tile that the head of the snake collided with. If 
		 * the snake hit a wall, SnakeBody will be returned, as both conditions
		 * are handled identically.
         */
        TileType tltCollision = updateSnake();

        /*
		 * Here we handle the different possible collisions.
		 * 
		 * Fruit: If we collided with a fruit, we increment the number of
		 * fruits that we've eaten, update the score, and spawn a new fruit.
		 * 
		 * SnakeBody: If we collided with our tail (or a wall), we flag that
		 * the game is over and pause the game.
		 * 
		 * If no collision occurred, we simply decrement the number of points
		 * that the next fruit will give us if it's high enough. This adds a
		 * bit of skill to the game as collecting fruits more quickly will
		 * yield a higher score.
         */
        if (tltCollision == TileType.Fruit) {
            //actualiza la informacion de las coisiones
            iFruitsEaten++;
            iScore += iNextFruitScore;
            contadorFruitSnake = 2;
            
            //genera la animacion de los colores
            this.iPosColor = 1;
            this.iCantidadColor = 2;
            this.colEspecial = Color.RED;
           
            spawnFruit();
            sClipEat.play();
        } else if (tltCollision == TileType.Fruit2) {
            iFruitsEaten++;
            iScore += iNextFruitScore;
            contadorFruitSnake = 3;
            spawnFruit2();
            //genera la animacion de los colores
            this.iPosColor = 2;
            this.iCantidadColor = 3;
            this.colEspecial = Color.YELLOW;
            
            sClipEat.play();
        } else if (tltCollision == TileType.Fruit3) {
            iFruitsEaten++;
            iScore += iNextFruitScore;
            contadorFruitSnake = 1;
            spawnFruit3();
            //genera la animacion de los colores
            this.iPosColor = 0;
            this.iCantidadColor = 1;
            this.colEspecial = Color.ORANGE;
            
            sClipEat.play();
        } 
        
        
        else if (tltCollision == TileType.SnakeBody
                ||tltCollision==TileType.Venom) {
            bIsGameOver = true;
            sClipDead.play();
            clkLogicTimer.setPaused(true);
            bPausado = true;
            this.SClipFondo.stop();
        } else if (iNextFruitScore > 10) {
            iNextFruitScore--;
        }
    }

    /**
     * Updates the snake's position and size.
     *
     * @return Tile tile that the head moved into.
     */
    private TileType updateSnake() {

        /*
		 * Here we peek at the next direction rather than polling it. While
		 * not game breaking, polling the direction here causes a small bug
		 * where the snake's direction will change after a game over (though
		 * it will not move).
         */
        Direction dirDirection = lklDirections.peekFirst();

        /*
		 * Here we calculate the new point that the snake's head will be at
		 * after the update.
         */
        Point pntHead = new Point(lklSnake.peekFirst());
        switch (dirDirection) {
            case North:
                pntHead.y--;
                break;

            case South:
                pntHead.y++;
                break;

            case West:
                pntHead.x--;
                break;

            case East:
                pntHead.x++;
                break;
        }

        /*
		 * If the snake has moved out of bounds ('hit' a wall), we can just
		 * return that it's collided with itself, as both cases are handled
		 * identically.
         */
        if (pntHead.x < 0 || pntHead.x >= BoardPanel.iCOL_COUNT || pntHead.y < 0 || pntHead.y >= BoardPanel.iROW_COUNT) {
            return TileType.SnakeBody; //Pretend we collided with our body.
        }

        /*
		 * Here we get the tile that was located at the new head position and
		 * remove the tail from of the snake and the board if the snake is
		 * long enough, and the tile it moved onto is not a fruit.
		 * 
		 * If the tail was removed, we need to retrieve the old tile again
		 * incase the tile we hit was the tail piece that was just removed
		 * to prevent a false game over.
         */
        if(this.iCantidadColor!=0){
            for(int iI=0;iI<iCantidadColor;iI++){
                
                Point pntP = lklSnake.get(iPosColor-iI);
                bpnBoard.setColor(pntP.x,pntP.y,colEspecial);
            
            }
            for(int iI=iPosColor-iCantidadColor;iI>=0;iI--){
                Point pntP = lklSnake.get(iI);
                bpnBoard.setColor(pntP.x,pntP.y,Color.GREEN);
            }
            iPosColor++;
        }
        if(iPosColor>=lklSnake.size()){
            iCantidadColor = 0;
            for(Point pntP : lklSnake){
                bpnBoard.setColor(pntP.x,pntP.y,Color.GREEN);
            }
        }
        TileType tltOld = bpnBoard.getTile(pntHead.x, pntHead.y);
        if (tltOld != TileType.Fruit && tltOld != TileType.Fruit2 && tltOld != TileType.Fruit3
                && --contadorFruitSnake < 1 && tltOld != TileType.Venom && lklSnake.size() > iMIN_SNAKE_LENGTH) {
            Point tail = lklSnake.removeLast();
            bpnBoard.setTile(tail, null);
            tltOld = bpnBoard.getTile(pntHead.x, pntHead.y);
            
        }
        /*
		 * Update the snake's position on the board if we didn't collide with
		 * our tail:
		 * 
		 * 1. Set the old head position to a body tile.
		 * 2. Add the new head to the snake.
		 * 3. Set the new head position to a head tile.
		 * 
		 * If more than one direction is in the queue, poll it to read new
		 * input.
         */
        if (tltOld != TileType.SnakeBody) {
            bpnBoard.setTile(lklSnake.peekFirst(), TileType.SnakeBody);
            lklSnake.push(pntHead);
            bpnBoard.setTile(pntHead, TileType.SnakeHead);
            if (lklDirections.size() > 1) {
                lklDirections.poll();
            }
        }

        return tltOld;
    }

    /**
     * Resets the game's variables to their default states and starts a new
     * game.
     */
    private void resetGame() {
        /*
		 * Reset the score statistics. (Note that nextFruitPoints is reset in
		 * the spawnFruit function later on).
         */
        this.iScore = 0;
        this.iFruitsEaten = 0;
        
        /*
		 * Reset the amount of places the snake will move
         */
        this.contadorFruitSnake = 0;
        /*
		 * Reset both the new game and game over flags.
         */
        this.bIsNewGame = false;
        this.bIsGameOver = false;

        /*
		 * Create the head at the center of the board.
         */
        Point pntHead = new Point(BoardPanel.iCOL_COUNT / 2, BoardPanel.iROW_COUNT / 2);
        
        /*
		 * Clear the snake list and add the head.
         */
        lklSnake.clear();
        lklSnake.add(pntHead);
        
        /*
		 * Clear the board and add the head.
         */
        bpnBoard.clearBoard();
        bpnBoard.setTile(pntHead, TileType.SnakeHead);

        /*
		 * Clear the directions and add north as the
		 * default direction.
         */
        lklDirections.clear();
        lklDirections.add(Direction.North);

        /*
		 * Reset the logic timer.
         */
        clkLogicTimer.reset();

        /*
		 * Spawn a new fruit.
         */
        spawnFruit();
        spawnFruit2();
        spawnFruit3();
        spawnBad();
        
        //restart the sound
        this.bPausado = false;
        this.SClipFondo.setLooping(true);
        this.SClipFondo.play();
        
        //reiniciar variables de animacion de color
        this.iPosColor = 0;
        this.iCantidadColor = 0;
        this.colEspecial = Color.GREEN;
        }
    

    /**
     * Gets the flag that indicates whether or not we're playing a new game.
     *
     * @return The new game flag.
     */
    public boolean isNewGame() {
        return bIsNewGame;
    }

    /**
     * Gets the flag that indicates whether or not the game is over.
     *
     * @return The game over flag.
     */
    public boolean isGameOver() {
        return bIsGameOver;
    }

    /**
     * Gets the flag that indicates whether or not the game is paused.
     *
     * @return The paused flag.
     */
    public boolean isPaused() {
        return bIsPaused;
    }

    /**
     * Spawns a new fruit onto the board.
     */
    private void spawnFruit() {
        //Reset the score for this fruit to 100.
        this.iNextFruitScore = 100;

        /*
		 * Get a random index based on the number of free spaces left on the board.
         */
        int index = ranRandom.nextInt(BoardPanel.iCOL_COUNT
                * BoardPanel.iROW_COUNT - lklSnake.size());

        /*
		 * While we could just as easily choose a random index on the board
		 * and check it if it's free until we find an empty one, that method
		 * tends to hang if the snake becomes very large.
		 * 
		 * This method simply loops through until it finds the nth free index
		 * and selects uses that. This means that the game will be able to
		 * locate an index at a relatively constant rate regardless of the
		 * size of the snake.
         */
        int freeFound = -1;
        for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
            for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                TileType type = bpnBoard.getTile(x, y);
                if (type == null || type == TileType.Fruit) {
                    if (++freeFound == index) {
                        bpnBoard.setTile(x, y, TileType.Fruit);
                        break;
                    }
                }
            }
        }

    }
    
        private void spawnFruit2() {
        //Reset the score for this fruit to 100.
        this.iNextFruitScore = 100;

        /*
		 * Get a random index based on the number of free spaces left on the board.
         */
        int index = ranRandom.nextInt(BoardPanel.iCOL_COUNT
                * BoardPanel.iROW_COUNT - lklSnake.size());

        /*
		 * While we could just as easily choose a random index on the board
		 * and check it if it's free until we find an empty one, that method
		 * tends to hang if the snake becomes very large.
		 * 
		 * This method simply loops through until it finds the nth free index
		 * and selects uses that. This means that the game will be able to
		 * locate an index at a relatively constant rate regardless of the
		 * size of the snake.
         */
        int freeFound = -1;
        for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
            for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                TileType type = bpnBoard.getTile(x, y);
                if (type == null || type == TileType.Fruit2) {
                    if (++freeFound == index) {
                        bpnBoard.setTile(x, y, TileType.Fruit2);
                        break;
                    }
                }
            }
        }

    }
        
        private void spawnFruit3() {
        //Reset the score for this fruit to 100.
        this.iNextFruitScore = 100;
        /*
		 * Get a random index based on the number of free spaces left on the board.
         */
        int index = ranRandom.nextInt(BoardPanel.iCOL_COUNT
                * BoardPanel.iROW_COUNT - lklSnake.size());
        /*
		 * While we could just as easily choose a random index on the board
		 * and check it if it's free until we find an empty one, that method
		 * tends to hang if the snake becomes very large.
		 * 
		 * This method simply loops through until it finds the nth free index
		 * and selects uses that. This means that the game will be able to
		 * locate an index at a relatively constant rate regardless of the
		 * size of the snake.
         */
        int freeFound = -1;
        for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
            for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                TileType type = bpnBoard.getTile(x, y);
                if (type == null || type == TileType.Fruit3) {
                    if (++freeFound == index) {
                        bpnBoard.setTile(x, y, TileType.Fruit3);
                        break;
                    }
                }
            }
        }

    }

    public void spawnBad() {
        //siguiendo la misma logica de las "fruits"
        //pero ahora en lugar de agregar un tyle del tipo fruit
        //se agrega un tyle del tipo Venom
        //ademas se modifica para que se agreguen 3 de estos 
        //objetos malos
            /*
                * Get a random index based on the number of free spaces left on the board.
             */
            for(int iI=0;iI<3;iI++){
            int index = ranRandom.nextInt(BoardPanel.iCOL_COUNT
                    * BoardPanel.iROW_COUNT - lklSnake.size());
            int freeFound = -1;
            for (int x = 0; x < BoardPanel.iCOL_COUNT; x++) {
                for (int y = 0; y < BoardPanel.iROW_COUNT; y++) {
                    TileType type = bpnBoard.getTile(x, y);
                    if (type == null || type == TileType.Venom) {
                        if (++freeFound == index) {
                            bpnBoard.setTile(x, y, TileType.Venom);
                            break;
                        }
                    }
                }
            }
            }
    }

    /**
     * Gets the current score.
     *
     * @return The score.
     */
    public int getScore() {
        return iScore;
    }

    /**
     * Gets the number of fruits eaten.
     *
     * @return The fruits eaten.
     */
    public int getFruitsEaten() {
        return iFruitsEaten;
    }

    /**
     * Gets the next fruit score.
     *
     * @return The next fruit score.
     */
    public int getNextFruitScore() {
        return iNextFruitScore;
    }

    /**
     * Gets the current direction of the snake.
     *
     * @return The current direction.
     */
    public Direction getDirection() {
        return lklDirections.peek();
    }
    private String validar(String sEntrada){
        String sSalida = "";
        if(sEntrada.length()<20){
            for(int iI=0;iI<sEntrada.length();iI++){
                sSalida+=sEntrada.charAt(iI);
            }
            for(int iI=sEntrada.length();iI<20;iI++){
                sSalida+=" ";
            }
        }else if(sEntrada.length()>20){
            for(int iI=0;iI<20;iI++){
                sSalida += sEntrada.charAt(iI);
            }
        }
        return sSalida;
    }
    /**
     * Funcion que crea un dialogo de entrada para que el usuario introduzca
     * su usuario a guardar o cargar
     * 
     * el parametro que toma sirve para nombrar a la ventana y que diga
     * save o load dependiendo de donde se manda a llamar
     * @param sFunc
     * @return 
     */
    public String entradaUsuario(String sFunc){
        clkLogicTimer.setPaused(true);
        String sUser = (String)JOptionPane.showInputDialog(
                    this,
                    "Enter your username to " +sFunc,
                    sFunc,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "Username");
        clkLogicTimer.setPaused(false);
        return sUser;
    }
    /**
     * muestra un mensaje de error en caso de que el usuario que se introdujo
     * no se encontrara al momento de intentar cargarlo
     */
    public void userNotFound(){
        
            clkLogicTimer.setPaused(true);
            JOptionPane.showMessageDialog(this, "User Not Found");
            clkLogicTimer.setPaused(false);
    }
    /**
     * Funcion que toma un string de usuario pasada a un formato valido
     * y lo busca en el archivo de usuarios
     * 
     * si lo encuentra regresa un long que es el offset de donde está almacenado
     * el estdo del juego del usuario en el archivo de save's
     * 
     * 
     * @param sValida
     * @param rafEntrada
     * @return
     * @throws IOException 
     */
    public long buscarUsuario(String sValida,RandomAccessFile rafEntrada)
    throws IOException{
        while(rafEntrada.getFilePointer()<rafEntrada.length()){
                String sGuardado = "";
                for(int iI=0;iI<20;iI++){
                    //Se lee un usuario guardado
                    sGuardado+=rafEntrada.readChar(); 
                }
                if(sGuardado.equals(sValida)){ 
                    //se comprueba si el usuario leido es igual al que se busca
                    //de ser asi se regresa el offset
                    return rafEntrada.readLong();
                }else{
                    // si no, se lee el offset del usuario leido y se continua
                    //con la busqueda
                    long lDummy = rafEntrada.readLong();
                }
            }
        return -1; //si no se encuentra el usuario se regresa un -1
    }
    /**
     * Funcion general de cargar, toma las entradas de usuario para localizar
     * en el archivo de save's la posicion que tiene el estado del juego para
     * este usuario
     */
    public void Cargar(){
        if(bIsNewGame){
            clkLogicTimer.setPaused(true);
            JOptionPane.showMessageDialog(this, "The game must be running");
            
        }else{
        //obtener el nombre de usuario
        String sUser = entradaUsuario("Load");
        if(sUser != null){
        //pasar a una forma valida el string de usuario (20 caracteres exactos)
        String sValida = validar(sUser);
        try{
            RandomAccessFile rafEntrada;
            rafEntrada = new RandomAccessFile("users.dat","rw");
            //buscar en el archivo users.dat el usuario en cuestion
            long offset = buscarUsuario(sValida,rafEntrada);
            if(offset == -1){
             userNotFound();//si no se encuentra informarlo al jugador
            }else
            {
                Cargar(offset);//si lo encontró entonces cargar el juego
            }
            
            rafEntrada.close();
            }catch(Exception e){
            System.out.println(e);
        }
        }
        //para no tener problemas con el estado de pause del clock
        if(bIsPaused||bIsGameOver){
                clkLogicTimer.setPaused(true);
                SClipFondo.pause();
                this.bPausado=true;
        }
        }
    }
    /**
     * Guardar: 
     * La lógica es similar a la de cargar, se obtiene del jugador el 
     * nombre de usuario, se busca entre los usuarios almacenados, solo que
     * en este caso si no existe entonces crea un nuevo registro, tanto en 
     * el archivo de usuarios como en el archivo de save's del juego
     * 
     */
    public void Guardar(){
        if(bIsNewGame){
            clkLogicTimer.setPaused(true);
            JOptionPane.showMessageDialog(this, "The game must be running");
            
        }else{
        String sUser = entradaUsuario("Save"); //obtener el nombre de usuario
        if(sUser != null){
        String sValida = validar(sUser); //pasarlo a una forma válida
        RandomAccessFile rafSalida;
        try{
            rafSalida = new RandomAccessFile("users.dat","rw");
            if(rafSalida.length()==0){ //si el archivo está vacio insertar el usuario directamente
                for(int iI=0;iI<20;iI++){
                    rafSalida.writeChar(sValida.charAt(iI));
                }
                rafSalida.writeLong(0);
                Guardar(0);
            }else{//si el archivo no esta vacio buscar el usuario para ver si existe
                long offset = buscarUsuario(sValida,rafSalida);
                if(offset == -1){//si no existe se crea un nuevo registro
                    nuevoRegistro(rafSalida,sValida);
                }else{
                    //si ya existe entonces se pregunta si se quiere
                    //sobre escribir los datos
                    clkLogicTimer.setPaused(true);
                    int n = JOptionPane.showConfirmDialog(
                            this,
                            "Would you like to overwrite this user?",
                            "r u sure?",
                            JOptionPane.YES_NO_OPTION);
                     
                      clkLogicTimer.setPaused(false);
                      if(n==0){
                            Guardar(offset); //se guarda en el archivo de datos
                      }
                }
            }
            
            rafSalida.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
        if(bIsPaused||bIsGameOver){ //para evitar que la snake siga caminando
                clkLogicTimer.setPaused(true);
                SClipFondo.pause();
                this.bPausado=true;
            }
    }
    }
    /**
     * toma un usuario que aún no esta registrado en el archivo users.dat
     * y lo inserta, calcula el offset que este usuario tiene en el archivo
     * datos.dat para futuros save's
     * 
     * @param rafSalida
     * @param sValida
     * @throws IOException 
     */
    public void nuevoRegistro(RandomAccessFile rafSalida, String sValida)
            throws IOException{
        long offset;
        for(int iI=0;iI<20;iI++){
                        rafSalida.writeChar(sValida.charAt(iI));
                    }
                    //Se obtiene la cantidad de usuarios registrados hasta el momento
                    int cantidad = (int) rafSalida.length()/48; 
                    //el offset es la cantidad de usuarios por el tamaño de registro de cada save en datos.dat
                    offset = 10027*cantidad;
                    rafSalida.writeLong(offset);
                    Guardar(offset);
    }
    /**
     * Funcion de guardar
     * toma el estado actual del juego y lo guarda en un archivo de acceso
     * aelatorio para poder cargarlo después
     */
    public void Guardar(long offset){
        try{
        RandomAccessFile rafSalida;
        rafSalida = new RandomAccessFile("datos.dat","rw");
        rafSalida.seek(offset);
        //almacenar las variables del funcionamiento del juego
        rafSalida.writeInt(iScore);
        rafSalida.writeInt(iFruitsEaten);
        rafSalida.writeInt(iNextFruitScore);
        rafSalida.writeBoolean(bIsNewGame);
        rafSalida.writeBoolean(bIsPaused);
        rafSalida.writeBoolean(bIsGameOver);
        //se guarda la snake como tal en el archivo
        guardarSnake(rafSalida);
        //almacenar las direcciones de la snake
       guardarDirecciones(rafSalida);
        //almacenar el tablero
        guardarTablero(rafSalida);
        
        rafSalida.close();
        }catch(Exception e){
            System.out.println("guardar: "+e);
        }
    }
    /*
    Funcion que almacena la snake en el archivo de salida
    rellena tantos valores dummy como sea necesario para mantener un
    tamaño de registro fijo entre los usuarios
    */
    public void guardarSnake(RandomAccessFile rafSalida)throws IOException{
         //almacenar las coordenadas de la snake
        rafSalida.writeInt(lklSnake.size());
        for(int iC=0;iC<lklSnake.size();iC++){
            rafSalida.writeInt(lklSnake.get(iC).x);
            rafSalida.writeInt(lklSnake.get(iC).y);
        }
        /*
        se agregan valores dummy, esto tiene como objetivo que los "registros"
        entre todos los usuarios tengan exactamente la misma cantidad de bytes
        */
        for(int iC = lklSnake.size(); iC 
                < bpnBoard.iCOL_COUNT*bpnBoard.iROW_COUNT;iC++){
            rafSalida.writeInt(-1); 
            rafSalida.writeInt(-1);
        }
    }
    /*
    Funcion guardarDirecciones, funciona idem a guardar Snake
    */
    public void guardarDirecciones(RandomAccessFile rafSalida)throws IOException{
        //guardar direcciones de la snake
         rafSalida.writeInt(lklDirections.size());
        for(int iC=0;iC<lklDirections.size();iC++){
            Direction dirTemp = lklDirections.get(iC);
            switch(dirTemp){//se traduce cada direccion a un integer
                case North:
                    rafSalida.writeInt(1);
                    break;
                case South:
                    rafSalida.writeInt(2);
                    break;
                case East:
                    rafSalida.writeInt(3);
                    break;
                case West:
                    rafSalida.writeInt(4);
                    break;     
            }
        }
        for(int iC = lklDirections.size(); iC //llenamos de valores dummy
                < bpnBoard.iCOL_COUNT*bpnBoard.iROW_COUNT;iC++){
            rafSalida.writeInt(-1); //esto tiene como objetivo mantener una
        }//cantidad fija de bytes por guardado, para facilitar las cosas
    }
    /*
    Funcion tablero, toma una representacion de integers del tablero
    y lo guarda en el archivo de salida
    */
    public void guardarTablero(RandomAccessFile rafSalida)throws IOException{
        //se guarda el tablero actual traducido a un arreglo de enteros
        int iarrTablero[] = bpnBoard.getTablero();
        rafSalida.writeInt(iarrTablero.length);
        for(int iC=0;iC<iarrTablero.length;iC++){
            rafSalida.writeInt(iarrTablero[iC]);
        }
    }
    /**
     * Funcion de cargar:
     * Toma el archivo de guardado y lo carga en el estado actual del juego
     * 
     */
    public void Cargar(long offset){
        this.resetGame();//se reinicia el juego para evitar bugs raros
        RandomAccessFile rafEntrada;
        try{
            //abrir el archivo de entrada
            rafEntrada = new RandomAccessFile("datos.dat","rw");
            //moverse hasta el registro especificado
            rafEntrada.seek(offset);
        //primero se cargan las variables de estado
        this.iScore = rafEntrada.readInt();
        this.iFruitsEaten = rafEntrada.readInt();
        this.iNextFruitScore = rafEntrada.readInt();
        this.bIsNewGame = rafEntrada.readBoolean();
        this.bIsPaused = rafEntrada.readBoolean();
        this.bIsGameOver = rafEntrada.readBoolean();
        //cargar la snake del registro
        cargarSnake(rafEntrada);
        //se cargan las direcciones almacenadas hasta ahora
        cargarDirecciones(rafEntrada);
        //se carga el tablero del registro
        cargarTablero(rafEntrada);
        rafEntrada.close();//cerrar el archivo de entrada
        }catch(Exception e){
            System.out.println(e);
        }
        
    }
    /*
    Para modularizar se carga la snake en una funcion separada
    descarta los valores dummy almacenados en el registro
    */
    public void cargarSnake(RandomAccessFile rafEntrada)throws IOException{
          //cargar la serpiente
        lklSnake.clear();
        int iElementos = rafEntrada.readInt();
        for(int iC=0;iC<iElementos;iC++){
            int iX = rafEntrada.readInt();
            int iY = rafEntrada.readInt();
            Point pntTemp = new Point(iX,iY);
            lklSnake.add(pntTemp);
        }
        //se leen los valores dummy almacenados, estos no se usan para nada
        //son solo relleno
        for(int iC = iElementos;iC<bpnBoard.iCOL_COUNT*bpnBoard.iROW_COUNT;iC++){
            int iDummy = rafEntrada.readInt();
            iDummy = rafEntrada.readInt();
        }
    }
    /*
    la funcion de cargar direcciones funciona IDEM a cargar snake
    tambien se deshace de valores dummy
    */
    public void cargarDirecciones(RandomAccessFile rafEntrada)throws IOException{
        //cargar las direcciones
        int iElementos;
        iElementos = rafEntrada.readInt();
        lklDirections.clear();
        for(int iC=0;iC<iElementos;iC++){
            int iDireccion = rafEntrada.readInt();
            switch(iDireccion){
                case 1:
                    lklDirections.add(Direction.North);
                    break;
                case 2:
                    lklDirections.add(Direction.South);
                    break;
                case 3:
                    lklDirections.add(Direction.East);
                    break;
                case 4:
                    lklDirections.add(Direction.West);
                    break;
            }
        }
        //se leen los valores dummy, son solo relleno
        for(int iC = iElementos;iC<bpnBoard.iCOL_COUNT*bpnBoard.iROW_COUNT;iC++){
            int iDummy = rafEntrada.readInt();
        }
    }
    /*
    Se carga el tablero del registro, para esto se lee una serie de ints
    que representan al tablero y se guardan en un arreglo de ints
    */
    public void cargarTablero(RandomAccessFile rafEntrada)throws IOException{
        int iElementos;
        //cargar el tablero 
        iElementos = rafEntrada.readInt();
        int iArrTablero[] = new int[iElementos];
        for(int iC=0;iC<iElementos;iC++){
            iArrTablero[iC] = rafEntrada.readInt();
        }
        
        bpnBoard.setTablero(iArrTablero);
        //se reinicia el logic timer,
        //esto está aqui para no pasarse de las 25 lineas......
        clkLogicTimer.reset();
        ;
        if(bIsPaused||bIsGameOver){
            clkLogicTimer.setPaused(true);
            SClipFondo.pause();
            this.bPausado=true;
        }
    }
    /**
     * Entry point of the program.
     *
     * @param args Unused.
     */
    public static void main(String[] args) {
        SnakeGame sngSnake = new SnakeGame();
        sngSnake.startGame();
    }

}

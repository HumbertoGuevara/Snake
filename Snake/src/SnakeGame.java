
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

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
            iFruitsEaten++;
            iScore += iNextFruitScore;
            spawnFruit();
        } else if (tltCollision == TileType.SnakeBody
                ||tltCollision==TileType.Venom) {
            bIsGameOver = true;
            clkLogicTimer.setPaused(true);
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
        TileType tltOld = bpnBoard.getTile(pntHead.x, pntHead.y);
        if (tltOld != TileType.Fruit && tltOld != TileType.Venom && 
                lklSnake.size() > iMIN_SNAKE_LENGTH) {
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
        spawnBad();
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

    public void spawnBad() {
        //siguiendo la misma logica de las "fruits"
        //pero ahora en lugar de agregar un tyle del tipo fruit
        //se agrega un tyle del tipo Venom
        //ademas se modifica para que se agreguen 3 de estos 
        //objetos malos
        for (int iC = 0; iC < 3; iC++) {
            /*
	* Get a random index based on the number of free spaces left on the board.
             */
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

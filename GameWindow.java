import java.awt.event.*;
import javax.swing.*;

public class GameWindow{
    // Debug Mode
    private boolean DBG = false;
    private final String DBG_STR = "[DEBUG] ";

    // Keybinds
    private String K_MOVEUP = "W";
    private String K_MOVEUP2 = "UP";
    private String K_MOVELEFT = "A";
    private String K_MOVELEFT2 = "LEFT";
    private String K_MOVEDOWN = "S";
    private String K_MOVEDOWN2 = "DOWN";
    private String K_MOVERIGHT = "D";
    private String K_MOVERIGHT2 = "RIGHT";
    private String K_QUIT = "ESCAPE";
    private String K_INTERACT = "SPACE";

    // Display  + Update Variables
    private GameWindowFrame WINDOW;
    private int MAP_LIMIT_X, MAP_LIMIT_Y;
    private final int PLAYER_SPEED_MULTIPLIER = 4;
    private Timer moveTimer;
    boolean movingUp = false;
    boolean movingLeft = false;
    boolean movingRight = false;
    boolean movingDown = false;

    // Content Variables
    private int playerX;
    private int playerY;
    private int backgroundX;
    private int backgroundY;
    private int tileCountX;
    private int tileCountY;
    private short[] tileContents;
    /* Tile Contents:
     * 0 = Empty
     * 1 = Boundary
     */

    // Constructors
    public GameWindow(){
        init("content/img/region/Example.png", 1080, 720, 100, 100);
    }

    protected void toggleDebugMode(){
        DBG = !DBG;
        WINDOW.toggleDebugMode();
    }


    // Initializing Window Functions

    private class PlayerAction extends AbstractAction {
        private final Runnable action;

        public PlayerAction(Runnable action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            action.run();
        }
    }

    private void setupKeyBindings() {
        // InputMap and ActionMap for the JPanel
        InputMap inputMap = WINDOW.getInputMap(WINDOW.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = WINDOW.getActionMap();

        // Bind the keys
        inputMap.put(KeyStroke.getKeyStroke(K_MOVEUP), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(K_MOVEUP2), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(K_MOVELEFT), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(K_MOVELEFT2), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(K_MOVEDOWN), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(K_MOVEDOWN2), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(K_MOVERIGHT), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke(K_MOVERIGHT2), "moveRight");

        // Quit Game
        inputMap.put(KeyStroke.getKeyStroke(K_QUIT), "quit");

        // Bind key release to stop movement
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVEUP), "stopMoveUp");
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVEUP2), "stopMoveUp");
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVELEFT), "stopMoveLeft");
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVELEFT2), "stopMoveLeft");
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVEDOWN), "stopMoveDown");
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVEDOWN2), "stopMoveDown");
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVERIGHT), "stopMoveRight");
        inputMap.put(KeyStroke.getKeyStroke("released "+K_MOVERIGHT2), "stopMoveRight");

        // Actions for moving
        actionMap.put("moveUp", new PlayerAction(() -> movingUp = true));
        actionMap.put("moveLeft", new PlayerAction(() -> movingLeft = true));
        actionMap.put("moveDown", new PlayerAction(() -> movingDown = true));
        actionMap.put("moveRight", new PlayerAction(() -> movingRight = true));

        // Action to quit
        actionMap.put("quit", new PlayerAction(() -> System.exit(0)));

        // Actions for stopping movement
        actionMap.put("stopMoveUp", new PlayerAction(() -> movingUp = false));
        actionMap.put("stopMoveLeft", new PlayerAction(() -> movingLeft = false));
        actionMap.put("stopMoveDown", new PlayerAction(() -> movingDown = false));
        actionMap.put("stopMoveRight", new PlayerAction(() -> movingRight = false));
    }

    private void resizeWindow(int sizeX, int sizeY){
        // WINDOW_SIZE_X = sizeX;
        // WINDOW_SIZE_Y = sizeY;
        WINDOW.setSize(sizeX,sizeY);
    }

    private void populateBoundaryTiles(){ // Populate the edge of the map with boundary tiles
        int x = 0, y = 0;
        for(short i : tileContents){
            if (x == 0 || y == 0 || x == tileCountX-1 || y == tileCountY-1){
                // if(DBG) System.out.println(DBG_STR+"Setting tile ("+x+","+y+") to boundary tile.");
                tileContents[y*tileCountX+x] = 1;
            }
            x = (x+1)%tileCountX;
            y = (x == 0 ? y+1 : y);
        }
    }

    // print the grid as a 2d array of tile contents
    public void printTileContents(){
        if(DBG) System.out.println(DBG_STR+"Grid size: " + tileCountX + ", " + tileCountY);
        for(int i = 0; i < tileCountY; i++){
            for(int j = 0; j < tileCountX; j++){
                System.out.print(tileContents[tileCountX*i+j] + " ");
            }
            System.out.println();
        }
    }

    // Add tile detection system later
    private void updateMovement(){
        if (movingUp) moveUp(PLAYER_SPEED_MULTIPLIER);
        if (movingLeft) moveLeft(PLAYER_SPEED_MULTIPLIER);
        if (movingDown) moveDown(PLAYER_SPEED_MULTIPLIER);
        if (movingRight) moveRight(PLAYER_SPEED_MULTIPLIER);
    }

    private void moveLeft(int dX){
        if(DBG) System.out.println(DBG_STR+"Moving left!");
        int[] tile = WINDOW.getTile(playerX, playerY);
        int tileID = (tile[0]-1) + tile[1]*tileCountX; // get tile to the left
        if (tileContents[tileID] != 0) return; // block movement onto boundary
        playerX -= dX;
        backgroundX += dX;
        updateDisplay();
    }
    private void moveRight(int dX){
        if(DBG) System.out.println(DBG_STR+"Moving right!");
        int[] tile = WINDOW.getTile(playerX, playerY);
        int tileID = (tile[0]+1) + tile[1]*tileCountX; // get tile to the right
        if (tileContents[tileID] != 0) return; // block movement onto boundary
        playerX += dX;
        backgroundX -= dX;
        updateDisplay();
    }
    private void moveUp(int dY){ 
        if(DBG) System.out.println(DBG_STR+"Moving up!");
        int[] tile = WINDOW.getTile(playerX, playerY);
        int tileID = tile[0] + (tile[1]-1)*tileCountX; // get tile above player
        if (tileContents[tileID] != 0) return; // block movement on boundary
        playerY -= dY;
        backgroundY += dY;
        updateDisplay();
    }
    private void moveDown(int dY){
        if(DBG) System.out.println(DBG_STR+"Moving down!");
        int[] tile = WINDOW.getTile(playerX, playerY);
        int tileID = tile[0] + (tile[1]+1)*tileCountX; // get tile below player
        if (tileContents[tileID] != 0) return; // block movement on boundary
        playerY += dY;
        backgroundY -= dY;
        updateDisplay();
    }

    // Main display updater
    private void updateDisplay(){
        WINDOW.updateOffset(backgroundX, backgroundY);
    }

    public void updateGameWindowTitle(String newTitle){
        WINDOW.setTitle(newTitle);
    }

    // Start Game Instance
    public void init(String imagePath, int windowResolutionX, int windowResolutionY, int playerStartingX, int playerStartingY){
        WINDOW = new GameWindowFrame(imagePath, windowResolutionX, windowResolutionY);

        // init map
        int[] mapLimits = WINDOW.getImageSize();
        MAP_LIMIT_X = mapLimits[0];
        MAP_LIMIT_Y = mapLimits[1];

        // init player
        playerX = backgroundX = playerStartingX;
        playerY = backgroundY = playerStartingY;
        WINDOW.updateOffset(backgroundX, backgroundY);

        // Game Tile System
        int[] tileCounts = WINDOW.getTileMapSize();
        tileCountX = tileCounts[0];
        tileCountY = tileCounts[1];
        // if(DBG) System.out.println(DBG_STR+"Tile Counts on Ticker Side: " + tileCountX + ", " + tileCountY);
        tileContents = new short[tileCountY*tileCountX];
        for(int i = 0; i < tileContents.length; i++){
            tileContents[i] = 0; // initialize tileContents array to all empty tiles
        }
        populateBoundaryTiles(); // add boundary tiles
        printTileContents();


        // attach keylistener to display, make display visible
        if(DBG) WINDOW.getWindowSize();
        setupKeyBindings();
        moveTimer = new Timer(16, e -> updateMovement());
        moveTimer.start();
        WINDOW.setTitle("Game Window");
        WINDOW.show();
    }
}
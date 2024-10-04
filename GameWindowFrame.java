import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameWindowFrame extends JPanel{
    private final int TILE_SIZE = 70;
    private boolean DBG = false;
    private final String DBG_STR = "[DEBUG] ";
    private BufferedImage backgroundImage;
    private CharacterToken playerSprite;
    private int bgOffsetX, bgOffsetY;
    private int playerRenderX, playerRenderY; // where the player should be rendered on screen
    private Dimension windowSize;
    private JFrame displayWindow;

    public GameWindowFrame(String imagePath, int windowResolutionX, int windowResolutionY){
        super();

        // Setup JFrame window
        displayWindow = new JFrame();
        displayWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windowSize = new Dimension(windowResolutionX, windowResolutionY);
        displayWindow.setPreferredSize(windowSize);
        displayWindow.setSize(windowSize);
        displayWindow.setLocationRelativeTo(null); // Center the window

        // Load background image
        try{
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e){
            System.out.println("Background image file not found or corrupted.");
            System.exit(0);
        }
        bgOffsetX = 0;
        bgOffsetY = 0;

        // Load player sprite
        playerSprite = new CharacterToken();
        int[] playerSpriteSize = getPlayerSize();
        playerRenderX = displayWindow.getWidth()/2 - playerSpriteSize[0]/2;
        playerRenderY = displayWindow.getHeight()/2 - playerSpriteSize[1]/2;

        // Initial load
        repaint();

        // Add the JPanel to the JFrame
        displayWindow.add(this);
        displayWindow.pack(); // Ensure the frame is sized correctly
    }

    protected void toggleDebugMode(){
        DBG = !DBG;
        playerSprite.toggleDebugMode();
    }

    protected void setTitle(String newTitle){
        displayWindow.setTitle(newTitle);
    }
    
    // KEY MOVEMENT METHOD
    public void updateOffset(int newX, int newY){
        bgOffsetX = newX;
        bgOffsetY = newY;
        if(DBG) System.out.println(DBG_STR+"Player position updated to " + bgOffsetX + ", " + bgOffsetY);
        repaint();
    }

    // Update player sprite on speed or direction change
    public void updatePlayerSprite(char spd, char dir){
        playerSprite.setSprite(spd, dir);
        if(DBG) System.out.println(DBG_STR+"Player sprite updated.");
        repaint();
    }

    // get the size, in pixels (2d arr), of the game window
    public int[] getWindowSize(){ 
        Rectangle windowBounds = displayWindow.getBounds();
        int windowSizeX = windowBounds.width;
        int windowSizeY = windowBounds.height;
        if (DBG) System.out.println(DBG_STR + "WindowSize: " + windowSizeX+", "+windowSizeY);
        return new int[]{windowSizeX, windowSizeY};
    }

    // get the size, in pixels (2d arr), of the background image
    public int[] getImageSize(){ 
        int imgSizeX = backgroundImage.getWidth();
        int imgSizeY = backgroundImage.getHeight();
        if (DBG) System.out.println(DBG_STR+"ImageSize: " + imgSizeX+", "+imgSizeY);
        return new int[]{imgSizeX, imgSizeY};
    }

    // get the size, in pixels (2d arr), of the player sprite
    public int[] getPlayerSize(){
        int playerSizeX = playerSprite.getSprite().getWidth();
        int playerSizeY = playerSprite.getSprite().getHeight();
        if (DBG) System.out.println(DBG_STR+"PlayerSize: " + playerSizeX+", "+playerSizeY);
        return new int[]{playerSizeX,playerSizeY};
    }

    // get the size, in tiles (2d arr), of the area's bgimg
    public int[] getTileMapSize(){ 
        int[] imgSize = getImageSize();
        int tileSizeX = imgSize[0] / TILE_SIZE;
        int tileSizeY = imgSize[1] / TILE_SIZE;
        if (DBG) System.out.println(DBG_STR+"TileMapSize: " + tileSizeX+", "+tileSizeY);
        return new int[]{tileSizeX, tileSizeY};
    }

    private void dbg_drawGridLines(Graphics g){
        int[] gridTiles = getTileMapSize();
        for(int col = 0; col < gridTiles[0]; col++){
            g.drawLine(col*TILE_SIZE+bgOffsetX, 0+bgOffsetY, col*TILE_SIZE+bgOffsetX,backgroundImage.getHeight()+bgOffsetY);
        }
        for(int row = 0; row < gridTiles[1]; row++){
            g.drawLine(0+bgOffsetX, row*TILE_SIZE+bgOffsetY,backgroundImage.getWidth()+bgOffsetX, row*TILE_SIZE+bgOffsetY);
        }
    }

    // get the tiles the player is on
    public int[] getPlayerTiles(){ 
        int tileX = bgOffsetX / TILE_SIZE; // floor offset by tilesize
        int tileY = bgOffsetY / TILE_SIZE;
        return new int[]{tileX, tileY};
    }

    public int[] getTile(int playerX, int playerY){ // get the tile the player is standing on
        int tileX = playerX / TILE_SIZE;
        int tileY = playerY / TILE_SIZE;
        return new int[]{tileX, tileY};
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());
        if(backgroundImage!=null) g.drawImage(backgroundImage, bgOffsetX, bgOffsetY, this);
        if(playerSprite != null) g.drawImage(playerSprite.getSprite(), playerRenderX, playerRenderY, this);
        if(DBG) dbg_drawGridLines(g);
    }

    /*
    public void linkGameInputs(KeyListener input){ // link game inputs to this window
        displayWindow.addKeyListener(input);
    }
        */

    public void show(){ // show the display
        displayWindow.setVisible(true);
    }

    public void hide(){ // hide the display
        displayWindow.setVisible(false);
    }
}
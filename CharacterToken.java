import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

// Container class for player sprites
public class CharacterToken{
    private boolean DBG = true;
    private final String DBG_STR = "[DEBUG] ";
    private final String ERR_STR = "[ERROR] ";

    private String path;
    private String name;
    private String suffix;
    private String fileType;
    private HashMap<Character, Map<Character, WeakReference>> spriteMap = new HashMap<>();
    private BufferedImage currentSprite, baseSprite,
                        baseSpriteUp, baseSpriteDown, baseSpriteLeft, baseSpriteRight,
                        walkSpriteUp, walkSpriteDown, walkSpriteLeft, walkSpriteRight,
                        runSpriteUp, runSpriteDown, runSpriteLeft, runSpriteRight;
    
    public CharacterToken(String name, String suffix, String fileType) {
        try {
            this.path = (new java.io.File(".").getCanonicalPath()) + "\\content\\img\\player\\";
        } catch (Exception e) {
            System.out.println(ERR_STR+"Failed to get current working directory.");
        }
        this.name = name;
        this.suffix = suffix;
        this.fileType = fileType;
        loadSprites();
        initSpriteMap();
        currentSprite = baseSprite;
    }

    public CharacterToken(){
        try {
            this.path = (new java.io.File(".").getCanonicalPath()) + "\\content\\img\\player\\";
        } catch (Exception e) {
            System.out.println(ERR_STR+"Failed to get current working directory.");
        }
        this.name = "Placeholder";
        this.suffix = "";
        this.fileType = "png";
        loadSprites();
        initSpriteMap();
        currentSprite = baseSprite;
        if(DBG) System.out.println(DBG_STR+"Is our current sprite okay? " + (currentSprite instanceof BufferedImage));
    }

    // Constructor helper method to set up the mapping between keycodes and sprite references
    private void loadSprites(){

        // Check if Base Sprite can load
        String target = path + name + suffix + "." + fileType;
        File baseFile = new File(target);
        if (!baseFile.exists()){
            System.out.println(ERR_STR+"Image file NOT FOUND at: " + baseFile.getAbsolutePath());
            System.exit(0);
        } else if (!baseFile.canRead()){
            System.out.println(ERR_STR+"No READ perms for file at: " + baseFile.getAbsolutePath());
            System.exit(0);
        } else {
            if(DBG) System.out.println(DBG_STR+"Loading valid image file from: " + baseFile.getAbsolutePath());
        }

        // Check if ImageIO supports the file format
        Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(fileType);
        if (!readers.hasNext()) {
            System.out.println(ERR_STR + "No image readers found for the file format: " + fileType);
            System.exit(0);
        }

        // Try to parse base sprite as BufferedImage
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(baseFile);
            readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                System.out.println(ERR_STR + "No image readers found this file.");
                System.exit(0);
            }
            baseSprite = ImageIO.read(baseFile);
        } catch (IOException e) {
            System.out.println(ERR_STR+"Something went wrong. This sprite appears to be unparseable: " + target);
            System.exit(0);
        }
        if (baseSprite == null){
            System.out.println(ERR_STR+"Image could not be read. Is the format invalid?");
            if(DBG) System.out.println(DBG_STR+"Target sprite: "+target);
            System.exit(0);
        }
        if(DBG) System.out.println(DBG_STR+"Did base sprite load correctly? " + (baseSprite instanceof BufferedImage));

        // Up standing sprite
        target = path + name + suffix + "_up." + fileType;
        try{
            baseSpriteUp = ImageIO.read(new File(target));
        } catch (IOException e) { // if turned sprites are unavailable, use the base sprite
            if(DBG) System.out.println(DBG_STR+"UP Standing sprite is unavailable. Using base sprite instead.");
            // baseSpriteUp = ((BufferedImage)baseSprite).clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (baseSpriteUp instanceof BufferedImage));

        // Down standing sprite
        target = path + name + suffix + "_down." + fileType;
        try{
            baseSpriteDown = ImageIO.read(new File(target));
        } catch (IOException e) { // if turned sprites are unavailable, use the base sprite
            if(DBG) System.out.println(DBG_STR+"DOWN Standing sprite is unavailable. Using base sprite instead.");
            // baseSpriteDown = baseSprite.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (baseSpriteDown instanceof BufferedImage));

        // Left standing sprite
        target = path + name + suffix + "_left." + fileType;
        try{
            baseSpriteLeft = ImageIO.read(new File(target));
        } catch (IOException e) { // if turned sprites are unavailable, use the base sprite
            if(DBG) System.out.println(DBG_STR+"LEFT Standing sprite is unavailable. Using base sprite instead.");
            // baseSpriteLeft = baseSprite.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (baseSpriteLeft instanceof BufferedImage));

        // Right standing sprite
        target = path + name + suffix + "_right." + fileType;
        try{
            baseSpriteRight = ImageIO.read(new File(target));
        } catch (IOException e) { // if turned sprites are unavailable, use the base sprite
            if(DBG) System.out.println(DBG_STR+"RIGHT Standing sprite is unavailable. Using base sprite instead.");
            // baseSpriteRight = baseSprite.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (baseSpriteRight instanceof BufferedImage));

        // Up walk sprite
        target = path + name + suffix + "_up_walk." + fileType;
        try{
            walkSpriteUp = ImageIO.read(new File(target));
        } catch (IOException e) { // if walk sprites are unavailable, use the standing sprite
            if(DBG) System.out.println(DBG_STR+"UP Walk sprite is unavailable. Using standing sprite instead.");
            // walkSpriteUp = baseSpriteUp.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (walkSpriteUp instanceof BufferedImage));

        // Down walk sprite
        target = path + name + suffix + "_down_walk." + fileType;
        try{
            walkSpriteDown = ImageIO.read(new File(target));
        } catch (IOException e) { // if walk sprites are unavailable, use the standing sprite
            if(DBG) System.out.println(DBG_STR+"DOWN Walk sprite is unavailable. Using standing sprite instead.");
            // walkSpriteDown = baseSpriteDown.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (walkSpriteDown instanceof BufferedImage));

        // Left walk sprite
        target = path + name + suffix + "_left_walk." + fileType;
        try{
            walkSpriteLeft = ImageIO.read(new File(target));
        } catch (IOException e) { // if walk sprites are unavailable, use the standing sprite
            if(DBG) System.out.println(DBG_STR+"LEFT Walk sprite is unavailable. Using standing sprite instead.");
            // walkSpriteLeft = baseSpriteLeft.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (walkSpriteLeft instanceof BufferedImage));

        // Right walk sprite
        target = path + name + suffix + "_right_walk." + fileType;
        try{
            walkSpriteRight = ImageIO.read(new File(target));
        } catch (IOException e) { // if walk sprites are unavailable, use the standing sprite
            if(DBG) System.out.println(DBG_STR+"RIGHT Walk sprite is unavailable. Using standing sprite instead.");
            // walkSpriteRight = baseSpriteRight.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (walkSpriteRight instanceof BufferedImage));

        // Up run sprite
        target = path + name + suffix + "_up_run." + fileType;
        try{
            runSpriteUp = ImageIO.read(new File(target));
        } catch (IOException e) { // if run sprites are unavailable, use the walk sprite
            if(DBG) System.out.println(DBG_STR+"UP run sprite is unavailable. Using walk sprite instead.");
            // runSpriteUp = walkSpriteUp.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (runSpriteUp instanceof BufferedImage));

        // Down run sprite
        target = path + name + suffix + "_down_run." + fileType;
        try{
            runSpriteDown = ImageIO.read(new File(target));
        } catch (IOException e) { // if run sprites are unavailable, use the walk sprite
            if(DBG) System.out.println(DBG_STR+"DOWN run sprite is unavailable. Using walk sprite instead.");
            // runSpriteDown = walkSpriteDown.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (runSpriteDown instanceof BufferedImage));

        // Left run sprite
        target = path + name + suffix + "_left_run." + fileType;
        try{
            runSpriteLeft = ImageIO.read(new File(target));
        } catch (IOException e) { // if run sprites are unavailable, use the walk sprite
            if(DBG) System.out.println(DBG_STR+"LEFT run sprite is unavailable. Using walk sprite instead.");
            // runSpriteLeft = walkSpriteLeft.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (runSpriteLeft instanceof BufferedImage));

        // Right run sprite
        target = path + name + suffix + "_right_run." + fileType;
        try{
            runSpriteRight = ImageIO.read(new File(target));
        } catch (IOException e) { // if run sprites are unavailable, use the walk sprite
            if(DBG) System.out.println(DBG_STR+"RIGHT run sprite is unavailable. Using walk sprite instead.");
            // runSpriteRight = walkSpriteRight.clone();
        }
        if(DBG) System.out.println(DBG_STR+"Did this sprite load correctly? " + (runSpriteRight instanceof BufferedImage));
    }

    // Constructor helper method to build the mappings of spd/dir to sprites
    private void initSpriteMap(){
        if(DBG) System.out.println(DBG_STR+"Initializing sprite maps!");

        // pointers to standing sprites
        WeakReference upPtr = new WeakReference<>(baseSpriteUp);
        WeakReference downPtr = new WeakReference<>(baseSpriteDown);
        WeakReference leftPtr = new WeakReference<>(baseSpriteLeft);
        WeakReference rightPtr = new WeakReference<>(baseSpriteRight);

        // pointers to walk sprites
        WeakReference upWPtr = new WeakReference<>(walkSpriteUp);
        WeakReference downWPtr = new WeakReference<>(walkSpriteDown);
        WeakReference leftWPtr = new WeakReference<>(walkSpriteLeft);
        WeakReference rightWPtr = new WeakReference<>(walkSpriteRight);

        // pointers to run sprites
        WeakReference upRPtr = new WeakReference<>(runSpriteUp);
        WeakReference downRPtr = new WeakReference<>(runSpriteDown);
        WeakReference leftRPtr = new WeakReference<>(runSpriteLeft);
        WeakReference rightRPtr = new WeakReference<>(runSpriteRight);

        // initialize submaps
        HashMap<Character, WeakReference> spriteMapStanding = new HashMap<>();
        HashMap<Character, WeakReference> spriteMapWalking = new HashMap<>();
        HashMap<Character, WeakReference> spriteMapRunning = new HashMap<>();

        // fill standing map
        spriteMapStanding.put('u', upPtr);
        spriteMapStanding.put('d', downPtr);
        spriteMapStanding.put('l', leftPtr);
        spriteMapStanding.put('r', rightPtr);

        // fill walking map
        spriteMapWalking.put('u', upWPtr);
        spriteMapWalking.put('d', downWPtr);
        spriteMapWalking.put('l', leftWPtr);
        spriteMapWalking.put('r', rightWPtr);

        // fill running map
        spriteMapRunning.put('u', upRPtr);
        spriteMapRunning.put('d', downRPtr);
        spriteMapRunning.put('l', leftRPtr);
        spriteMapRunning.put('r', rightRPtr);

        // fill general sprite map
        spriteMap.put('s',spriteMapStanding);
        spriteMap.put('w',spriteMapWalking);
        spriteMap.put('r',spriteMapRunning);
    }

    // debug toggle
    protected void toggleDebugMode(){
        DBG = !DBG;
    }

    // Sprite Getters
    public BufferedImage getSprite(){ return currentSprite; } // get the CURRENT sprite (not the base sprite!)
    public BufferedImage getSpriteBase(){ return baseSprite; }
    public BufferedImage getSpriteUp(){ return baseSpriteUp; }
    public BufferedImage getSpriteDown(){ return baseSpriteDown; }
    public BufferedImage getSpriteLeft(){ return baseSpriteLeft; }
    public BufferedImage getSpriteRight(){ return baseSpriteRight; }
    public BufferedImage getSpriteUpWalk(){ return walkSpriteUp; }
    public BufferedImage getSpriteDownWalk(){ return walkSpriteDown; }
    public BufferedImage getSpriteLeftWalk(){ return walkSpriteLeft; }
    public BufferedImage getSpriteRightWalk(){ return walkSpriteRight; }
    public BufferedImage getSpriteUpRun(){ return runSpriteUp; }
    public BufferedImage getSpriteDownRun(){ return runSpriteDown; }
    public BufferedImage getSpriteLeftRun(){ return runSpriteLeft; }
    public BufferedImage getSpriteRightRun(){ return runSpriteRight; }

    // Sprite Setter
    // @preconditions: spd is 's' (standing), 'w' (walk), or 'r' (run); dir is 'u' (up), 'd' (down), 'l' (left), or 'r' (right)
    public void setSprite(char spd, char dir){
        if (DBG) System.out.println(DBG_STR+"Updating player sprite.");
        Object target = spriteMap.get(spd).get(dir).get();
        if (target instanceof BufferedImage){
            BufferedImage targetSprite = (BufferedImage)target;
            currentSprite = targetSprite;
        } else {
            System.out.println(ERR_STR+"Object in sprite mappings is not a bufferedimage. Check your hashmaps.");
            System.exit(0);
        }
    }
}
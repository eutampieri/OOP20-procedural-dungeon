package eu.eutampieri.catacombs.ui.gamefx;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import eu.eutampieri.catacombs.ui.utils.ImageLoader;
import eu.eutampieri.catacombs.ui.utils.ImageRotator;
import eu.eutampieri.catacombs.ui.utils.ImageTransformerFactory;
import eu.eutampieri.catacombs.ui.utils.ImageTransformerFactoryImpl;

/**
 * This class is used to provide the images for the animation of the entities and for the assets of the map.
 */

public final class AssetManager {

    private static final String PLAYER_SHEET = "res/playersheet.png";
    private static final String SLIME_SHEET = "res/slimesheet.png";
    private static final String BAT_SHEET = "res/batsheet.png";
    private static final String PROJECTILE_1 = "res/proj1.png";
    private static final String PROJECTILE_2 = "res/proj2.png";
    private static final String EXTENSION = ".png";
    private static final ImageRotator IMAGE_ROTATOR = new ImageRotator();

    private static final AssetManager SINGLETON_MANAGER = new AssetManager();

    private final Map<String, ArrayList<Optional<BufferedImage>>> allAnimations = new HashMap<>();
    private final Map<String, BufferedImage> allImages = new HashMap<>();

    /**
     * private constructor for the Singleton of the AssetManager.
     */
    private AssetManager() {
        load();
    }

    /**
     * private constructor for the Singleton of the AssetManager.
     * @return AssetManager
     */
    public static AssetManager getAssetManager() {
        return SINGLETON_MANAGER;
    }

    /**
     * This method select the frame specified by the parameter.
     *
     * @param key the string that identifies an action
     * @return the list of image corresponding to the action specified in the parameter
     */
    public List<Optional<BufferedImage>> getFrames(final String key) {
        return allAnimations.get(key);
    }

    /**
     * This method return a single image specified by the parameter.
     *
     * @param key a string that specify the action
     * @return the image specified by parameter
     */
    public BufferedImage getImage(final String key) {
        return this.allImages.get(key);
    }

    /**
     * This method loads all the images in two separates List of images and animations.
     */
    private void load() {
        // Check:OFF: MagicNumber
        loadAnimations("Walk_up", PLAYER_SHEET, 8, 3, 32, false);
        loadAnimations("Walk_down", PLAYER_SHEET, 8, 2, 32, false);
        loadAnimations("Walk_left", PLAYER_SHEET, 8, 1, 32, false);
        loadAnimations("Walk_right", PLAYER_SHEET, 8, 0, 32, false);

        loadAnimations("Die", PLAYER_SHEET, 8, 4, 32, false);

        loadAnimations("Attack_up", PLAYER_SHEET, 8, 5, 32, false);
        loadAnimations("Attack_down", PLAYER_SHEET, 8, 6, 32, false);
        loadAnimations("Attack_left", PLAYER_SHEET, 8, 8, 32, false);
        loadAnimations("Attack_right", PLAYER_SHEET, 8, 7, 32, false);

        loadAnimations("Slime_up", SLIME_SHEET, 4, 2, 32, false);
        loadAnimations("Slime_down", SLIME_SHEET, 4, 0, 32, false);
        loadAnimations("Slime_left", SLIME_SHEET, 4, 3, 32, false);
        loadAnimations("Slime_right", SLIME_SHEET, 4, 1, 32, false);

        loadAnimations("Bat_right", BAT_SHEET, 3, 0, 16, false);
        loadAnimations("Bat_left", BAT_SHEET, 3, 0, 16, true);

        loadBossAnimations("Boss_Idle_right", 6, false, true);
        loadBossAnimations("Boss_Idle_left", 6, true, true);
        loadBossAnimations("Boss_Move_right", 6, false, false);
        loadBossAnimations("Boss_Move_left", 6, true, false);

        loadGunAnimations("Projectile_1", PROJECTILE_1, 1, 0, 28, 0);
        loadGunAnimations("Projectile_2", PROJECTILE_2, 1, 0, 28, 0);

        loadImages();
    }

    /**
     * This method flip the image.
     *
     * @param image the image to flip
     * @return the flipped image
     */
    public Optional<BufferedImage> horizontalFlip(final Optional<BufferedImage> image) {
        if (image.isPresent()) {
            final ImageTransformerFactory itf = new ImageTransformerFactoryImpl();
            return Optional.of(itf.flip(false, true).transform(image.get()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * This method load the assets for the map.
     */
    public void loadImages() {
        // Check:OFF: MagicNumber
        // Tiles
        final GameSheet tileSheet = new GameSheet("res/tilesheet.png");
        int count = 1;
        final Optional<BufferedImage> image = Optional.of(tileSheet.cutImage(112, 0, 16, 16));

        allImages.put(String.valueOf(count++), image.get());
        allImages.put(String.valueOf(count++), IMAGE_ROTATOR.rotate(image.get(), 90));
        allImages.put(String.valueOf(count++), IMAGE_ROTATOR.rotate(image.get(), 180));
        allImages.put(String.valueOf(count++), tileSheet.cutImage(112 + 16, 0, 16, 16));
        allImages.put(String.valueOf(count++), tileSheet.cutImage(112 + 16, 0, 16, 16));
        allImages.put(String.valueOf(count++), tileSheet.cutImage(112, 16, 16, 16));
        allImages.put(String.valueOf(count++), tileSheet.cutImage(112 + 16, 16, 16, 16));
        allImages.put(String.valueOf(count++), tileSheet.cutImage(112 + 16, 16, 16, 16));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j < 3) {
                    Optional<BufferedImage> img;
                    img = Optional.of(tileSheet.cutImage(j * 16, i * 16, 16, 16));
                    this.allImages.put(String.valueOf(count++), img.get());
                    this.allImages.put(String.valueOf(count++), IMAGE_ROTATOR.rotate(image.get(), 90));
                    this.allImages.put(String.valueOf(count++), IMAGE_ROTATOR.rotate(image.get(), 180));
                    this.allImages.put(String.valueOf(count++), IMAGE_ROTATOR.rotate(image.get(), 270));
                } else {
                    this.allImages.put(String.valueOf(count++), tileSheet.cutImage(j * 16, i * 16, 16, 16));
                }
            }
        }

        // background
        this.allImages.put("background", tileSheet.cutImage(64, 96, 16, 16));

        // world objects
        allImages.put("coin", tileSheet.cutImage(0, 144, 16, 16));
        allImages.put("skull", tileSheet.cutImage(16, 144, 16, 16));
        allImages.put("fire", tileSheet.cutImage(0, 160, 8, 21));
        allImages.put("potion", tileSheet.cutImage(0, 181, 8, 11));
        allImages.put("gun", tileSheet.cutImage(0, 193, 21, 17));
        allImages.put("rifle", tileSheet.cutImage(24, 193, 45, 17));
        allImages.put("sniper", tileSheet.cutImage(71, 193, 57, 17));

        // Check:ON: MagicNumber
    }

    /**
     * this method loads the images for the animations.
     *
     * @param name         the name of the action
     * @param image        the sheet of the entity
     * @param numFrames    the frames necessary for that action
     * @param offset       the y offset in the image
     * @param dimension    the dimension of the single image
     * @param flip         the flipped image
     */
    public void loadAnimations(final String name, final String image, final int numFrames, final int offset,
            final int dimension, final boolean flip) {
        final GameSheet sheet = new GameSheet(image);
        final ArrayList<Optional<BufferedImage>> res = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            if (!flip) {
                res.add(Optional.of(sheet.cutImage(dimension * i, offset * dimension, dimension, dimension)));
            } else {
                res.add(horizontalFlip(
                        Optional.of(sheet.cutImage(dimension * i, offset * dimension, dimension, dimension))));
            }

        }
        allAnimations.put(name, res);
    }

    /**
     * This method load the boss images for each action.
     *
     * @param name           the name of the action
     * @param numFrames      the number of the frames for an action
     * @param flip           the flipped image
     * @param idle           the image can be idle or walk
     */
    public void loadBossAnimations(final String name, final int numFrames, final boolean flip, final boolean idle) {
        final ArrayList<Optional<BufferedImage>> res = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            if (idle) {
                if (!flip) {
                    res.add(ImageLoader.loadImage("res/boss/Golem_Idle_" + (i + 1) + EXTENSION));
                } else {
                    res.add(horizontalFlip(
                            ImageLoader.loadImage("res/boss/Golem_Idle_" + (i + 1) + EXTENSION)));
                }
            } else {
                if (!flip) {
                    res.add(ImageLoader.loadImage("res/boss/Golem_Walk_" + (i + 1) + EXTENSION));
                } else {
                    res.add(horizontalFlip(
                            ImageLoader.loadImage("res/boss/Golem_Walk_" + (i + 1) + EXTENSION)));
                }
            }

        }
        allAnimations.put(name, res);
    }

    /**
     * This method loads the images for the shooting animation.
     *
     * @param name          the name of the projectile
     * @param image         the image to load
     * @param numFrames     the number of frames for that animation
     * @param y             the y offest
     * @param dimension     the dimension of the image
     * @param offset        the x offset
     */
    public void loadGunAnimations(final String name, final String image, final int numFrames, final int y,
            final int dimension, final int offset) {
        final GameSheet sheet = new GameSheet(image);
        final ArrayList<Optional<BufferedImage>> res = new ArrayList<>();
        for (int i = 0; i < numFrames; i++) {
            res.add(Optional.of(sheet.cutImage(offset + (dimension * i), y, dimension, dimension)));
        }
        allAnimations.put(name, res);
    }

}

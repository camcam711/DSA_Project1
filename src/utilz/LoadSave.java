package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;

import javax.imageio.ImageIO;

import entities.Crabby;
import main.Game;
import main.GamePanel;

import static utilz.Constants.EnemyConstants.CRABBY;

public class LoadSave {
    public static final String PLAYER_ATLAS = "src/images/player_sprites.png";
    public static final String LEVEL_ATLAS = "src/images/outside_sprites.png";
    public static final String MENU_BUTTONS = "src/images/button_atlas.png";
    public static final String MENU_BACKGROUND = "src/images/menu_background.png";
    public static final String PAUSED_BACKGROUND = "src/images/pause_menu.png";
    public static final String SOUND_BUTTONS = "src/images/sound_button.png";
    public static final String URM_BUTTONS = "src/images/urm_buttons.png";
    public static final String VOLUME_BUTTONS = "src/images/volume_buttons.png";
    public static final String MENU_BACKGROUND_IMG = "src/images/background_menu.png";
    public static final String PLAYING_BG_IMG = "src/images/playing_bg_img.png";
    public static final String BIG_CLOUDS = "src/images/big_clouds.png";
    public static final String SMALL_CLOUDS = "src/images/small_clouds.png";
    public static final String CRABBY_SPRITE = "src/images/crabby_sprite.png";
    public static final String STATUS_BAR = "src/images/health_power_bar.png";
    public static final String COMPLETED_IMG = "src/images/completed_sprite.png";

    /* */
    public static BufferedImage GetSpritesAtlas(String filename) {
        BufferedImage image = null; // Initialize the image variable with null.
        try {
            // image = ImageIO.read(new File("src/images/player_sprites.png"));
            image = ImageIO.read(new File("" + filename));

        } catch (IOException ex) {
            Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    /*
     * this is the original method that now in the HelpMethod class, it is used to
     * spawn crabs in 1 level, but now the game has multiple levels so it is not
     * suitable anymore
     * 
     * public static ArrayList<Crabby> GetCrabs() {
     * BufferedImage img = GetSpritesAtlas(LEVEL_ONE_DATA);
     * ArrayList<Crabby> list = new ArrayList<>();
     * for (int j = 0; j < img.getHeight(); j++)
     * for (int i = 0; i < img.getWidth(); i++) {
     * Color color = new Color(img.getRGB(i, j));
     * int value = color.getGreen();
     * if (value == CRABBY)
     * list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
     * }
     * return list;
     * 
     * }
     */
    /*
     * this is the original method that now in the HelpMethod class, it is used to
     * deal with only 1 level, but now the game has multiple levels so it is not
     * suitable anymore
     * 
     * public static int[][] GetLevelData() {
     * BufferedImage img = GetSpritesAtlas(LEVEL_ONE_DATA);
     * int[][] lvlData = new int[img.getHeight()][img.getWidth()];
     * 
     * for (int j = 0; j < img.getHeight(); j++)
     * for (int i = 0; i < img.getWidth(); i++) {
     * Color color = new Color(img.getRGB(i, j));
     * int value = color.getRed();
     * if (value >= 48)
     * value = 0;
     * lvlData[j][i] = value;
     * }
     * return lvlData;
     * 
     * }
     */
    /*
     * this method gets all the levels in the images/lvls folder and returns them as
     * a BufferedImage array that contains all the levels
     */
    public static BufferedImage[] GetAllLevels() {
        URL url = LoadSave.class.getResource("/images/lvls");
        File file = null;

        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        File[] files = file.listFiles();
        File[] filesSorted = new File[files.length];

        for (int i = 0; i < filesSorted.length; i++)
            for (int j = 0; j < files.length; j++) {
                if (files[j].getName().equals((i + 1) + ".png"))
                    filesSorted[i] = files[j];

            }

        BufferedImage[] imgs = new BufferedImage[filesSorted.length];

        for (int i = 0; i < imgs.length; i++)
            try {
                imgs[i] = ImageIO.read(filesSorted[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return imgs;
    }

}

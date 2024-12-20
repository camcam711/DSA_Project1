package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.awt.image.BufferedImage;

import UI.GameOverOverlay;
import UI.LevelCompletedOverLay;
import UI.PauseOverLay;
import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.Environment.*;

public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private PauseOverLay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverLay levelCompletedOverlay;
    private boolean paused = false;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH);
    /*
     * this is the old code, which had been moved to level class and been replaced
     * by the following code
     * 
     * // calculating how many tiles the entire level is in width
     * private int lvlTilesWide = LoadSave.GetLevelData()[0].length;
     * // game width = 20 tiles, lvl width = 30 tiles, so maxTilesOffset is 10 ( the
     * // part we cant see due to the game width)
     * private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
     * // turning the maxTilesOffset into pixels
     * private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE;
     */
    private int maxLvlOffsetX;

    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private int[] smallCloudsPos;
    private Random rnd = new Random();

    private boolean gameOver;
    private boolean lvlCompleted;

    public Playing(Game game) {
        super(game);
        initClasses();

        backgroundImg = LoadSave.GetSpritesAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpritesAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpritesAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for (int i = 0; i < smallCloudsPos.length; i++)
            smallCloudsPos[i] = (int) (90 * Game.SCALE) + rnd.nextInt((int) (100 * Game.SCALE));

        calcLvlOffset();
        loadStartLevel();
    }

    /*
     * loadNextLevel of levelManager is called to load the next level. The playing's
     * is used to reset all the entities and variables in the playing state. The
     * player's spawn is set to the current level's player spawn.
     * 
     * giống tên nhưng khác chức năng. loadNextLevel của levelManager được gọi để
     * tải level tiếp theo. playing's được sử dụng để reset tất cả các thực thể và
     * biến trong trạng thái playing.
     */
    public void loadNextLevel() {
        resetAll();
        levelManager.loadNextLevel();
        // player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        /*
         * 200 200 là vị trí spawn của player
         */
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        pauseOverlay = new PauseOverLay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverLay(this);
    }

    @Override
    public void update() {
        if (paused) {
            pauseOverlay.update();
        } else if (lvlCompleted) {
            levelCompletedOverlay.update();
        } else if (!gameOver) {
            levelManager.update();
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            checkCloseToBorder();
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;

    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        drawClouds(g);

        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);

        if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        } else if (gameOver)
            gameOverOverlay.draw(g);
        else if (lvlCompleted)
            levelCompletedOverlay.draw(g);
    }

    private void drawClouds(Graphics g) {
        for (int i = 0; i < 3; i++)
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int) (xLvlOffset * 0.3), (int) (204 * Game.SCALE),
                    BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null);

        for (int i = 0; i < smallCloudsPos.length; i++)
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i],
                    SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);

    }

    /*
     * tác dụng của hàm này là reset tất cả các biến và đối tượng trong trạng thái
     * resetAll() của player là để reset tất cả các biến của người chơi, resetAll()
     * của playing là để reset cả player và enemyManager, giống tên nhưng khác chức
     * năng.
     */
    public void resetAll() {
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        player.resetAll();
        enemyManager.resetAllEnemies();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /*
     * ban đầu tên của method là checkEnemyHit, nhưng sau đó đã được đổi thành
     * handleEnemyHit để phù hợp với chức năng của nó và tránh nhầm lẫn với
     * checkEnemyHit của EnemyManager.
     * 
     * Purpose: Acts as a bridge between the Player and the EnemyManager.
     * tác dụng làm cầu nối giữa Player và EnemyManager. Nó nhận hitbox tấn công của
     * người chơi (attackBox) và gọi enemyManager.checkEnemyHit(attackBox) để chuyển
     * tiếp hitbox và cho phép EnemyManager xử lý phát hiện va chạm chi tiết với kẻ
     * thù.
     * 
     * Flow:
     * Receives the player's attack hitbox (attackBox).
     * Calls enemyManager.checkEnemyHit(attackBox) to forward the hitbox and let the
     * EnemyManager handle the detailed collision detection with enemies.
     */
    public void handleEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            gameOverOverlay.keyPressed(e);
        switch (e.getKeyCode()) {
            // case KeyEvent.VK_W:
            // gamePanel.getGame().getPlayer().setUp(true);
            // break;
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            // case KeyEvent.VK_S:
            // gamePanel.getGame().getPlayer().setDown(true);
            // break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_ESCAPE:
                paused = !paused;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver)
            switch (e.getKeyCode()) {

                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;

                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;

            }
    }

    public void mouseDragged(MouseEvent e) {
        if (paused)
            pauseOverlay.mouseDragged(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver)
            if (e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mousePressed(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mouseReleased(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseReleased(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pauseOverlay.mouseMoved(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseMoved(e);
        }
    }

    public void setLevelCompleted(boolean levelCompleted) {
        this.lvlCompleted = levelCompleted;
    }

    public void setMaxLvlOffset(int lvlOffset) {
        this.maxLvlOffsetX = lvlOffset;
    }

    public void unpauseGame() {
        paused = false;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }
}

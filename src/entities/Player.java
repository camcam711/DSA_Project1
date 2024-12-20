package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethod.CanMoveHere;
import static utilz.HelpMethod.GetEntityXPosNextToWall;
import static utilz.HelpMethod.GetEntityYPosUnderRoofOrAboveFloor;
import static utilz.HelpMethod.IsEntityOnFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import main.Game;
import utilz.LoadSave;

public class Player extends Entity {
    private BufferedImage[][] animation;
    private int animationTick, animationIndex, animationSpeed = 20;
    private int playerAction = IDLE;
    private boolean moving = false, attacking = false;
    private boolean left, up, right, down, jump;
    private float playerSpeed = 2.0f;
    private int[][] lvlData;
    // X Y DRAWOFFSET is used for minimize the hitbox orbit around the character
    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    // for jumping/ gravity
    private float airSpeed = 0f;
    // gravity is a constant, the lower the gravity value is, the higher the
    // character can jump
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -3.2f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    // StatusBarUI
    private BufferedImage statusBarImg;
    // the size of the status bar, which is 192x58 pixels, is multiplied by the
    // scale
    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    // the x and y coordinates of the status bar are set to 10 pixels from the top
    // left
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    // the size of the health bar is 150x4 pixels, and its x and y coordinates are
    // set to 34 and 14 pixels from the top left corner of the status bar,
    // respectively
    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);

    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private int healthWidth = healthBarWidth;

    // AttackBox

    // if the enemy is in this attackbox, the enemy will take damage
    private Rectangle2D.Float attackBox;

    // flipX and flipW are used to flip the player sprite horizontally.( quay ngược
    // lại)
    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private Playing playing;

    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        loadAnimation();
        // The hitbox is indeed attached to the player because its x and y coordinates
        // are based on the player's x and y coordinates.
        inithitbox(x, y, (int) (20 * Game.SCALE), (int) (27 * Game.SCALE));
        initAttackBox();

    }

    public void setSpawn(Point spawn) {
        this.x = spawn.x;
        this.y = spawn.y;
        hitbox.x = x;
        hitbox.y = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
    }

    public void update() {
        updateHealthBar();
        if (currentHealth <= 0) {
            playing.setGameOver(true);
            return;
        }
        updateAttackBox();
        updatePosition();
        if (attacking)
            checkAttack();

        updateAnimationTick();
        setAnimation();

    }

    /*
     * Purpose: To determine when the player’s attack should actually check for
     * collisions with enemies.
     * When It Runs:
     * Only during specific frames of the attack animation.
     * Specifically, when the player’s attack animation reaches the correct frame
     * (animationIndex == 1 in this case).
     * 
     */
    private void checkAttack() {
        /*
         * This if condition determines whether the method should skip the collision
         * detection for the attack.
         * 
         * If the attack has already been checked equal false ( it is false by default)
         * or the animation index is not 1, no damage will be dealt.
         */
        if (attackChecked || animationIndex != 1)
            return;
        attackChecked = true;
        playing.handleEnemyHit(attackBox);

    }

    private void updateAttackBox() {
        if (right)
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
        else if (left)
            attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);

        attackBox.y = hitbox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar() {
        // máu hiện tại = máu tối đa chia cho máu tối đa nhân với chiều rộng của thanh
        healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
    }

    public void render(Graphics g, int lvlOffset) {
        // xDrawOffset và yDrawOffset được sử dụng để giảm thiểu hitbox quay quanh nhân
        // vật, - lvlOffset là để vẽ nhân vật ở vị trí cố định, + flipX là để quay ngược
        // lại, width * flipW là để quay ngược lại
        g.drawImage(animation[playerAction][animationIndex], (int) (hitbox.x - xDrawOffset) - lvlOffset + flipX,
                (int) (hitbox.y - yDrawOffset), width * flipW, height, null);
        // drawHitbox(g);
        // drawAttackBox(g, lvlOffset);
        drawUI(g);
    }

    private void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);

    }

    private void drawUI(Graphics g) {
        /*
         * (int) (hitbox.x - xDrawOffset) - lvlOffset + flipX:
         * hitbox.x: Là tọa độ x của hộp va chạm của nhân vật.
         * xDrawOffset: Là một giá trị điều chỉnh (có thể để căn giữa hoặc căn chỉnh
         * hình ảnh, kiểu như 1 pixel chiều rộng là 22, nhưng vật thì chỉ dài 10, thì
         * phải trừ đi cái khoảng cách từ viền pixel đến vật, để hitbox sát vật hơn.
         * lvlOffset: Là độ dịch chuyển của cấp độ, có thể dùng để điều chỉnh vị trí vẽ
         * theo môi trường.
         * flipX: Biến này được sử dụng để điều chỉnh vị trí vẽ dựa trên hướng của nhân
         * vật. Nếu nhân vật đang di chuyển sang trái, flipX có thể được đặt bằng chiều
         * rộng của nhân vật, giúp hình ảnh được vẽ từ vị trí bên phải.
         */

        /*
         * width * flipW:
         * 
         * Đây là chiều rộng của hình ảnh được vẽ. Nếu flipW là 1 (khi nhân vật đang
         * hướng sang phải), chiều rộng sẽ giữ nguyên. Nếu flipW là -1 (khi nhân vật
         * đang hướng sang trái), điều này sẽ làm cho chiều rộng trở thành âm, gây ra
         * hiệu ứng lật hình ảnh theo chiều ngang.
         */
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.red);
        // statusBar là 1 ô trạng thái chứa trạng thái của player bao gồm cả thanh máu
        // là healthBar, và thanh máu được vẽ bên trong statusBar, mà vị trí khởi đầu
        // của status bar là 10, 10, và thanh máu bắt đầu từ 34, 14, nên khi vẽ thanh
        // máu, phải cộng thêm với vị trí khởi đầu của status bar.
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    // This method is responsible for updating the animation tick and the animation
    // index. The animation tick is a counter that increments every time this method
    // is called. When it reaches or exceeds the animation speed (which is 25, in
    // this case), it resets to 0 and increments the animation index. The animation
    // index is used to determine which frame of the animation to display. When it
    // reaches or exceeds the length of the animation array, it resets to 0.
    // --
    // it will keep drawing the same image of til the animationtick reach 25, then
    // animationIndex is incremented to 1
    // After this, animationTick is reset to 0, and the process repeats.
    // animationIndex continues to increment every 25 iterations of
    // updateAnimationTick(), allowing for smooth animation in the game.
    // --
    // at first animationtick and index are 0.
    // this method get invoked rapidly since the paintComponent get invoked
    // rapidly as well due to the repaint() method from the gameloop (fps, run()
    // method from Game class)

    // the comments above is for the old version of the game with 1D array,
    // the new version of the game is using 2D array.
    // the reason why we have to set animationIndex >= GetSpriteAmount(playerAction)
    // but not 6 ( the max column value of the array ), is because of the
    // GetSpriteAmount method is used to get the amount of sprite in each action, so
    // it is more dynamic and flexible, and some action does not have enough 6
    // sprites.
    private void updateAnimationTick() {
        animationTick++;
        if (animationTick >= animationSpeed) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= GetSpriteAmount(playerAction)) {
                animationIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }
    }

    // At the moment the setAnimation() method is called, the value of the moving
    // variable is true (or any other non-zero value). This is because the moving
    // variable is set to true in the keyPressed() method when the VK_RIGHT or
    // VK_LEFT keys are pressed.
    private void setAnimation() {
        int startAnimation = playerAction;
        if (moving) {
            playerAction = RUNNING;
        } else {
            playerAction = IDLE;
        }
        if (inAir) {
            if (airSpeed < 0)
                playerAction = FALLING;
            else
                playerAction = JUMP;
        }

        if (attacking) {
            playerAction = ATTACK;
            if (startAnimation != ATTACK) {
                animationIndex = 1;
                animationTick = 0;
                return;
            }
        }
        if (startAnimation != playerAction) {
            resetAnimationTick();
        }
    }

    // the reason why this method exist is due to the animationtick and
    // animationindex, for example, the default animation is idle, when it is in
    // index 3, and I click mouse for attacking, it will continue at index 3 and
    // start the attack animation, but the attack animation ends at index 3, so
    // that's why sometimes clicking the mouse cant attack, and this method fix that
    // issue by reseting animationtick and animationindex
    private void resetAnimationTick() {
        animationIndex = 0;
        animationTick = 0;
    }

    private void updatePosition() {

        moving = false;

        if (jump)
            jump();

        if (!inAir)
            if ((!left && !right) || (right && left))
                return;

        float xSpeed = 0;

        if (left) {
            xSpeed -= playerSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right) {
            xSpeed += playerSpeed;
            flipX = 0;
            flipW = 1;
        }

        if (!inAir)
            if (!IsEntityOnFloor(hitbox, lvlData))
                inAir = true;

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXposition(xSpeed);
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXposition(xSpeed);
            }

        } else
            updateXposition(xSpeed);
        moving = true;
        // if (CanMoveHere(x + xSpeed, y + ySpeed, width, height, lvlData)) {
        // this.x += xSpeed;
        // this.y += ySpeed;
        // moving = true;
        // }
        // if (CanMoveHere(hitbox.x + xSpeed, hitbox.y + ySpeed, hitbox.width,
        // hitbox.height, lvlData)) {
        // hitbox.x += xSpeed;
        // hitbox.y += ySpeed;
        // moving = true;
        // }

    }

    private void jump() {
        if (inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;

    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;

    }

    private void updateXposition(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }

    public void changeHealth(int value) {
        currentHealth += value;

        if (currentHealth <= 0)
            currentHealth = 0;
        else if (currentHealth >= maxHealth)
            currentHealth = maxHealth;
    }

    private void loadAnimation() {
        BufferedImage image = LoadSave.GetSpritesAtlas(LoadSave.PLAYER_ATLAS);
        // the line below is used to create an array of BufferedImage with the size of
        // 7x8 (7 rows and 8 columns). Each element in this array represent a frame of
        // the animation.
        animation = new BufferedImage[7][8];
        for (int i = 0; i < animation.length; i++) {
            for (int j = 0; j < animation[i].length; j++) {
                // the image.getSubimage() is used to cut the image into pieces
                // The extracted subimage is then assigned to the i and jth position of the
                // animation array.
                // subimage(x, y, width, height)
                animation[i][j] = image.getSubimage(j * 64, i * 40, 64, 40);

            }
        }
        statusBarImg = LoadSave.GetSpritesAtlas(LoadSave.STATUS_BAR);
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public void resetAll() {
        // the reason why we have to reset the dir booleans is because if we dont reset
        // it when we press the key, the key will be stuck and the player will keep on
        // moving.
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        playerAction = IDLE;
        currentHealth = maxHealth;

        hitbox.x = x;
        hitbox.y = y;

        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }
}

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Player {
    public int x;
    public int y;
    public int width;
    public int height;
    public int velocityY = 0;
    public boolean onGround = true;
    public int lives = 3;
    public boolean invulnerable = false;
    public int invulnerabilityTimer = 0;
    public boolean hasSpeedBoost = false;
    public boolean hasJumpBoost = false;
    public int speedBoostTimer = 0;
    public int jumpBoostTimer = 0;
    private static final int BASE_SPEED = 5;
    private static final int BASE_JUMP_POWER = -15;
    private static final int GRAVITY = 1;
    private static final int MAX_LIVES = 5;
    private int animationFrame = 0;
    private int animationTimer = 0;
    private boolean facingRight = true;
    private BufferedImage playerImage;
    private boolean imageLoaded = false;

    // NEW: Offset tambahan untuk menyeimbangkan visual pemain dengan ground.
    // Sesuaikan nilai ini jika setelah perubahan lain, pemain masih melayang atau terlalu tenggelam.
    // Nilai positif akan membuat pemain "turun" (lebih dekat ke ground).
    // Nilai negatif akan membuat pemain "naik" (lebih jauh dari ground).
    private static final int FINE_TUNE_Y_OFFSET = 0; // Mulai dari 0, sesuaikan jika perlu.

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.loadImage();
        // Pastikan width dan height sudah diatur setelah loadImage()
        // Jika loadImage() gagal, width dan height akan menggunakan nilai fallback.
    }

    private void loadImage() {
        try {
            this.playerImage = ImageIO.read(new File("images/idle.png"));
            this.imageLoaded = true;
            // AMBIL DIMENSI DARI GAMBAR YANG DIMUAT
            this.width = this.playerImage.getWidth();
            this.height = this.playerImage.getHeight();
            System.out.println("Player image loaded successfully. Dimensions: " + this.width + "x" + this.height);
        } catch (IOException var2) {
            System.out.println("Could not load player image, using default graphics.");
            this.imageLoaded = false;
            // Fallback: Jika gambar tidak bisa dimuat, gunakan ukuran default
            this.width = 75;
            this.height = 130;
        }
    }

    public void update(boolean[] keys, int groundY) {
        int currentSpeed = this.hasSpeedBoost ? 10 : 5;
        int currentJumpPower = this.hasJumpBoost ? -20 : -15;
        boolean moving = false;
        if (keys[37]) {
            this.x -= currentSpeed;
            this.facingRight = false;
            moving = true;
        }

        if (keys[39]) {
            this.x += currentSpeed;
            this.facingRight = true;
            moving = true;
        }

        if (keys[38] && this.onGround) {
            this.velocityY = currentJumpPower;
            this.onGround = false;
        }

        if (moving && this.onGround) {
            ++this.animationTimer;
            if (this.animationTimer > 8) {
                this.animationFrame = (this.animationFrame + 1) % 4;
                this.animationTimer = 0;
            }
        } else {
            this.animationFrame = 0;
        }

        ++this.velocityY;
        this.y += this.velocityY;

        // PENYESUAIAN PENTING DI SINI:
        // Gunakan FINE_TUNE_Y_OFFSET untuk menyeimbangkan posisi y pemain
        if (this.y >= groundY - this.height + FINE_TUNE_Y_OFFSET) {
            this.y = groundY - this.height + FINE_TUNE_Y_OFFSET;
            this.velocityY = 0;
            this.onGround = true;
        }

        if (this.invulnerable) {
            --this.invulnerabilityTimer;
            if (this.invulnerabilityTimer <= 0) {
                this.invulnerable = false;
            }
        }

        if (this.hasSpeedBoost) {
            --this.speedBoostTimer;
            if (this.speedBoostTimer <= 0) {
                this.hasSpeedBoost = false;
            }
        }

        if (this.hasJumpBoost) {
            --this.jumpBoostTimer;
            if (this.jumpBoostTimer <= 0) {
                this.hasJumpBoost = false;
            }
        }

        if (this.x < 0) {
            this.x = 0;
        }

    }

    public void activatePowerUp(int type) {
        switch (type) {
            case 0:
                this.hasSpeedBoost = true;
                this.speedBoostTimer = 600;
                break;
            case 1:
                this.hasJumpBoost = true;
                this.jumpBoostTimer = 600;
                break;
            case 2:
                if (this.lives < 5) {
                    ++this.lives;
                }
                break;
            case 3:
                this.invulnerable = true;
                this.invulnerabilityTimer = 300;
        }

    }

    public void takeDamage() {
        if (!this.invulnerable) {
            --this.lives;
            this.invulnerable = true;
            this.invulnerabilityTimer = 120;
        }

    }

    public void draw(Graphics g, int cameraX) {
        int drawX = this.x - cameraX;
        if (!this.invulnerable || this.invulnerabilityTimer / 10 % 2 == 0) {
            if (this.imageLoaded && this.playerImage != null) {
                if (this.facingRight) {
                    g.drawImage(this.playerImage, drawX, this.y, this.width, this.height, (ImageObserver)null);
                } else {
                    g.drawImage(this.playerImage, drawX + this.width, this.y, -this.width, this.height, (ImageObserver)null);
                }
            } else {
                this.drawFallbackPlayer(g, drawX);
            }

            this.drawPowerUpEffects(g, drawX);
        }

    }

    private void drawFallbackPlayer(Graphics g, int drawX) {
        Color bodyColor = Color.BLUE;
        if (this.hasSpeedBoost) {
            bodyColor = new Color(0, 255, 255);
        } else if (this.hasJumpBoost) {
            bodyColor = new Color(0, 255, 0);
        }

        g.setColor(bodyColor);
        // Pastikan fallback player juga menggunakan width/height yang benar
        g.fillRect(drawX, this.y, this.width, this.height);
        g.setColor(Color.WHITE);
        int eyeOffset = this.facingRight ? 10 : 22;
        g.fillOval(drawX + eyeOffset, this.y + 10, 8, 8);
        g.fillOval(drawX + eyeOffset + 12, this.y + 10, 8, 8);
        g.setColor(Color.BLACK);
        g.fillOval(drawX + eyeOffset + 2, this.y + 12, 4, 4);
        g.fillOval(drawX + eyeOffset + 14, this.y + 12, 4, 4);
        g.setColor(bodyColor);
        int legOffset1 = this.animationFrame % 2 == 0 ? 0 : 2;
        int legOffset2 = this.animationFrame % 2 == 0 ? 2 : 0;
        g.fillRect(drawX + 8 + legOffset1, this.y + this.height - 10, 8, 10);
        g.fillRect(drawX + 24 + legOffset2, this.y + this.height - 10, 8, 10);
        g.fillRect(drawX - 5, this.y + 15, 10, 6);
        g.fillRect(drawX + this.width - 5, this.y + 15, 10, 6);
    }

    private void drawPowerUpEffects(Graphics g, int drawX) {
        if (this.hasSpeedBoost) {
            g.setColor(new Color(0, 255, 255, 100));

            for(int i = 1; i <= 5; ++i) {
                g.drawLine(drawX - i * 10, this.y + this.height / 2, drawX - i * 5, this.y + this.height / 2);
            }
        }

        if (this.hasJumpBoost && !this.onGround) {
            g.setColor(new Color(0, 255, 0, 150));
            int[] xPoints = new int[]{drawX + this.width / 2, drawX + this.width / 2 - 5, drawX + this.width / 2 + 5};
            int[] yPoints = new int[]{this.y - 10, this.y - 5, this.y - 5};
            g.fillPolygon(xPoints, yPoints, 3);
        }

        if (this.invulnerable && this.invulnerabilityTimer > 120) {
            g.setColor(new Color(255, 255, 0, 100));
            g.drawOval(drawX - 5, this.y - 5, this.width + 10, this.height + 10);
            g.setColor(new Color(255, 255, 0, 50));
            g.fillOval(drawX - 5, this.y - 5, this.width + 10, this.height + 10);
        }

    }
}
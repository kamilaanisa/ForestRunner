import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

class PowerUp {
    int x, y, width = 25, height = 25;
    int type; // 0 = speed, 1 = jump, 2 = extra life, 3 = invulnerability
    private int animationOffset = 0;
    private int animationDirection = 1;
    private Random random = new Random();

    // Image variables
    private BufferedImage powerUpImage;
    private boolean imageLoaded = false;

    // Power-up colors
    private Color[] powerUpColors = {
            new Color(0, 255, 255),    // Cyan for speed
            new Color(0, 255, 0),      // Green for jump
            new Color(255, 0, 255),    // Magenta for extra life
            new Color(255, 255, 0)     // Yellow for invulnerability
    };

    public PowerUp(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = random.nextInt(4); // Random power-up type
        loadImage();
    }

    private void loadImage() {
        try {
            powerUpImage = ImageIO.read(new File("images/powerup.png"));
            imageLoaded = true;
        } catch (IOException e) {
            imageLoaded = false;
        }
    }

    public void update() {
        // Floating animation
        animationOffset += animationDirection;
        if (animationOffset > 10) {
            animationDirection = -1;
        } else if (animationOffset < -10) {
            animationDirection = 1;
        }
    }

    public void draw(Graphics g, int cameraX) {
        int screenX = x - cameraX;
        int drawY = y + animationOffset;

        if (screenX > -width && screenX < 800 + width) {
            if (imageLoaded && powerUpImage != null) {
                // Draw image with type-based tint
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
                g.drawImage(powerUpImage, screenX, drawY, width, height, null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } else {
                // Enhanced fallback drawing
                drawFallbackPowerUp(g, screenX, drawY);
            }

            // Draw power-up icon/symbol
            drawPowerUpSymbol(g, screenX, drawY);
        }
    }

    private void drawFallbackPowerUp(Graphics g, int screenX, int drawY) {
        // Draw glowing effect
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Outer glow
        g.setColor(new Color(powerUpColors[type].getRed(), powerUpColors[type].getGreen(),
                powerUpColors[type].getBlue(), 50));
        g.fillOval(screenX - 5, drawY - 5, width + 10, height + 10);

        // Main power-up body
        g.setColor(powerUpColors[type]);
        g.fillOval(screenX, drawY, width, height);

        // Inner highlight
        g.setColor(Color.WHITE);
        g.fillOval(screenX + 5, drawY + 5, width - 10, height - 10);

        // Core
        g.setColor(powerUpColors[type]);
        g.fillOval(screenX + 8, drawY + 8, width - 16, height - 16);
    }

    private void drawPowerUpSymbol(Graphics g, int screenX, int drawY) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();

        String symbol = "";
        switch (type) {
            case 0: // Speed
                symbol = "S";
                break;
            case 1: // Jump
                symbol = "J";
                break;
            case 2: // Extra life
                symbol = "+";
                break;
            case 3: // Invulnerability
                symbol = "I";
                break;
        }

        int symbolX = screenX + (width - fm.stringWidth(symbol)) / 2;
        int symbolY = drawY + (height + fm.getAscent()) / 2;
        g.drawString(symbol, symbolX, symbolY);
    }
}
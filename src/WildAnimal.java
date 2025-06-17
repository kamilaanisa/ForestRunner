import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

class WildAnimal {
    int x, y, width = 38, height = 42;
    private int direction = 1;
    private int baseSpeed = 2;
    private int speed;
    private int moveTimer = 0;
    private int patrolDistance = 100;
    private int startX;
    private Random random = new Random();

    // Animation variables
    private int animationFrame = 0;
    private int animationTimer = 0;

    // Image variables
    private BufferedImage animalImage;
    private boolean imageLoaded = false;

    // Animal type (affects appearance and behavior)
    private int animalType;
    private Color[] animalColors = {
            Color.ORANGE,           // Fox
            new Color(139, 69, 19), // Bear (brown)
            Color.GRAY,             // Wolf
            new Color(255, 192, 203) // Pig (pink)
    };

    public WildAnimal(int x, int y, int gameSpeed) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.speed = baseSpeed + gameSpeed;
        this.animalType = random.nextInt(4);
        loadImage();
    }

    private void loadImage() {
        try {
            animalImage = ImageIO.read(new File("images/animals.png"));
            imageLoaded = true;
        } catch (IOException e) {
            imageLoaded = false;
        }
    }

    public void update() {
        // Update animation
        animationTimer++;
        if (animationTimer > 15) {
            animationFrame = (animationFrame + 1) % 4;
            animationTimer = 0;
        }

        // Move back and forth within patrol area
        moveTimer++;
        x += direction * speed;

        // Check if reached patrol boundaries
        if (x > startX + patrolDistance || x < startX - patrolDistance) {
            direction *= -1;
            moveTimer = 0;
        }

        // Occasionally change direction randomly
        if (moveTimer > 120 && random.nextInt(100) < 5) {
            direction *= -1;
            moveTimer = 0;
        }
    }

    public void draw(Graphics g, int cameraX) {
        int screenX = x - cameraX;
        if (screenX > -width && screenX < 800 + width) {
            if (imageLoaded && animalImage != null) {
                // Draw image with flip effect and animation offset
                int animOffset = (animationFrame % 2) * 2;
                if (direction == -1) {
                    g.drawImage(animalImage, screenX + width, y - animOffset, -width, height, null);
                } else {
                    g.drawImage(animalImage, screenX, y - animOffset, width, height, null);
                }
            } else {
                // Enhanced fallback drawing
                drawFallbackAnimal(g, screenX);
            }
        }
    }

    private void drawFallbackAnimal(Graphics g, int screenX) {
        // Animation offset for walking effect
        int animOffset = (animationFrame % 2) * 2;
        int drawY = y - animOffset;

        // Draw animal body based on type
        g.setColor(animalColors[animalType]);

        switch (animalType) {
            case 0: // Fox
                drawFox(g, screenX, drawY);
                break;
            case 1: // Bear
                drawBear(g, screenX, drawY);
                break;
            case 2: // Wolf
                drawWolf(g, screenX, drawY);
                break;
            case 3: // Pig
                drawPig(g, screenX, drawY);
                break;
        }
    }

    private void drawFox(Graphics g, int screenX, int drawY) {
        // Body
        g.setColor(Color.ORANGE);
        g.fillOval(screenX, drawY, width, height);

        // Tail
        g.fillOval(screenX + width - 5, drawY + 5, 15, 8);

        // Ears
        int earX = direction == 1 ? screenX + width - 10 : screenX + 5;
        g.fillOval(earX, drawY - 5, 8, 10);

        // Eyes
        g.setColor(Color.BLACK);
        int eyeX = direction == 1 ? screenX + width - 15 : screenX + 8;
        g.fillOval(eyeX, drawY + 5, 3, 3);
        g.fillOval(eyeX + 5, drawY + 5, 3, 3);
    }

    private void drawBear(Graphics g, int screenX, int drawY) {
        // Body (larger)
        g.setColor(new Color(139, 69, 19));
        g.fillOval(screenX - 2, drawY - 2, width + 4, height + 4);

        // Ears
        g.fillOval(screenX + 5, drawY - 5, 6, 6);
        g.fillOval(screenX + width - 11, drawY - 5, 6, 6);

        // Eyes
        g.setColor(Color.BLACK);
        g.fillOval(screenX + 8, drawY + 5, 4, 4);
        g.fillOval(screenX + width - 12, drawY + 5, 4, 4);

        // Nose
        g.fillOval(screenX + width/2 - 1, drawY + 12, 3, 2);
    }

    private void drawWolf(Graphics g, int screenX, int drawY) {
        // Body
        g.setColor(Color.GRAY);
        g.fillRect(screenX, drawY, width, height);

        // Snout
        int snoutX = direction == 1 ? screenX + width : screenX - 8;
        g.fillRect(snoutX, drawY + 8, 8, 8);

        // Ears
        int earX = direction == 1 ? screenX + width - 8 : screenX + 3;
        int[] xPoints = {earX, earX + 5, earX + 2};
        int[] yPoints = {drawY, drawY - 8, drawY};
        g.fillPolygon(xPoints, yPoints, 3);

        // Eyes
        g.setColor(Color.RED);
        int eyeX = direction == 1 ? screenX + width - 15 : screenX + 8;
        g.fillOval(eyeX, drawY + 5, 3, 3);
        g.fillOval(eyeX + 6, drawY + 5, 3, 3);
    }

    private void drawPig(Graphics g, int screenX, int drawY) {
        // Body
        g.setColor(new Color(255, 192, 203));
        g.fillOval(screenX, drawY, width, height);

        // Snout
        g.setColor(new Color(255, 160, 180));
        int snoutX = direction == 1 ? screenX + width - 5 : screenX;
        g.fillOval(snoutX, drawY + 10, 8, 6);

        // Ears
        g.setColor(new Color(255, 192, 203));
        g.fillOval(screenX + 5, drawY - 3, 6, 8);
        g.fillOval(screenX + width - 11, drawY - 3, 6, 8);

        // Eyes
        g.setColor(Color.BLACK);
        g.fillOval(screenX + 8, drawY + 5, 3, 3);
        g.fillOval(screenX + width - 11, drawY + 5, 3, 3);

        // Nostrils
        g.fillOval(snoutX + 2, drawY + 12, 1, 1);
        g.fillOval(snoutX + 4, drawY + 12, 1, 1);

        // Tail (curly)
        g.setColor(new Color(255, 192, 203));
        g.drawArc(screenX + width - 10, drawY + 2, 8, 8, 0, 270);
    }
}
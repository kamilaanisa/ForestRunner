import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

class Fruit {
    int x, y, width = 20, height = 20;
    Color color;
    private Random random = new Random();

    // Image variables
    private BufferedImage fruitImage;
    private boolean imageLoaded = false;

    public Fruit(int x, int y) {
        this.x = x;
        this.y = y;
        // Random fruit colors
        Color[] colors = {Color.RED, Color.ORANGE, new Color(255, 20, 147), Color.YELLOW};
        color = colors[random.nextInt(colors.length)];
        loadImage();
    }

    private void loadImage() {
        try {
            // Coba load gambar buah
            fruitImage = ImageIO.read(new File("images/apple.png"));
            imageLoaded = true;
        } catch (IOException e) {
            imageLoaded = false;
        }
    }

    public void draw(Graphics g, int cameraX) {
        int screenX = x - cameraX;
        if (screenX > -width && screenX < 800 + width) {
            if (imageLoaded && fruitImage != null) {
                // Draw image
                g.drawImage(fruitImage, screenX, y, width, height, null);
            } else {
                // Fallback: draw simple oval
                g.setColor(color);
                g.fillOval(screenX, y, width, height);
                // Simple highlight
                g.setColor(Color.WHITE);
                g.fillOval(screenX + 4, y + 4, 6, 6);
            }
        }
    }
}
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

class Obstacle {
    int x, y, width = 30, height = 40;

    // Image variables
    private BufferedImage obstacleImage;
    private boolean imageLoaded = false;

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        try {
            obstacleImage = ImageIO.read(new File("images/obstacle.png"));
            imageLoaded = true;
        } catch (IOException e) {
            imageLoaded = false;
        }
    }

    public void draw(Graphics g, int cameraX) {
        int screenX = x - cameraX;
        if (screenX > -width && screenX < 800 + width) {
            if (imageLoaded && obstacleImage != null) {
                // Draw image
                g.drawImage(obstacleImage, screenX, y, width, height, null);
            } else {
                // Fallback: draw rock-like obstacle
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rock shape
                g.setColor(new Color(100, 100, 100));
                int[] xPoints = {screenX, screenX + 8, screenX + width, screenX + width - 5, screenX + 15, screenX + 5};
                int[] yPoints = {y + height, y + 10, y + 5, y + height, y + height, y + height};
                g.fillPolygon(xPoints, yPoints, 6);

                // Add some texture
                g.setColor(new Color(80, 80, 80));
                g.fillOval(screenX + 5, y + 15, 8, 6);
                g.fillOval(screenX + 15, y + 25, 6, 4);

                // Highlight
                g.setColor(new Color(120, 120, 120));
                g.drawLine(screenX + 3, y + 20, screenX + 8, y + 15);
                g.drawLine(screenX + 12, y + 10, screenX + 18, y + 8);
            }
        }
    }
}
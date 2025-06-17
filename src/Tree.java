import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

class Tree {
    int x, y, width = 40, height = 80;

    // Image variables
    private BufferedImage treeImage;
    private boolean imageLoaded = false;

    public Tree(int x, int y) {
        this.x = x;
        this.y = y;
        loadImage();
    }

    private void loadImage() {
        try {
            treeImage = ImageIO.read(new File("images/tree.png"));
            imageLoaded = true;
        } catch (IOException e) {
            imageLoaded = false;
        }
    }

    public void draw(Graphics g, int cameraX) {
        int screenX = x - cameraX;
        if (screenX > -width && screenX < 800 + width) {
            if (imageLoaded && treeImage != null) {
                // Draw image
                g.drawImage(treeImage, screenX, y, width, height, null);
            } else {
                // Fallback: draw simple tree
                // Draw trunk
                g.setColor(new Color(101, 67, 33));
                g.fillRect(screenX + 15, y + 40, 10, 40);

                // Draw leaves
                g.setColor(new Color(0, 128, 0));
                g.fillOval(screenX, y, width, 50);
            }
        }
    }
}
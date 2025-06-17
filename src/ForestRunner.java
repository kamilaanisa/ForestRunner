import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

// Main Game Class
public class ForestRunner extends JPanel implements ActionListener, KeyListener {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int GROUND_Y = 500;

    // Game variables
    private Timer gameTimer;
    private boolean gameRunning = false;
    private boolean gameOver = false;
    private int score = 0;
    private int level = 1;
    private int cameraX = 0;
    private Random random = new Random();
    private int gameSpeed = 1; // Increases with level

    // Sound variables
    private SoundManager soundManager;

    // Player
    private Player player;

    // Game objects
    private ArrayList<Fruit> fruits;
    private ArrayList<WildAnimal> animals;
    private ArrayList< PowerUp> powerUps;
    private ArrayList<Obstacle> obstacles;

    // Input
    private boolean[] keys = new boolean[256];

    // Background scrolling
    private int[] cloudX = {100, 300, 500, 700};
    private int[] cloudY = {50, 80, 120, 90};

    // Background image variable
    private BufferedImage backgroundImage;
    private boolean backgroundLoaded = false;

    // New: Ground image variable
    private BufferedImage groundImage;
    private boolean groundImageLoaded = false;


    public ForestRunner() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this);

        // Initialize sound manager
        soundManager = new SoundManager();

        try {
            backgroundImage = ImageIO.read(new File("images/background.png"));
            backgroundLoaded = true;
            System.out.println("Background image loaded successfully.");
        } catch (IOException e) {
            System.out.println("Could not load background image: " + e.getMessage());
            backgroundLoaded = false;
        }

        try {
            groundImage = ImageIO.read(new File("images/ground1.jpg"));
            groundImageLoaded = true;
            System.out.println("Ground image loaded successfully.");
        } catch (IOException e) {
            System.out.println("Could not load ground image: " + e.getMessage());
            groundImageLoaded = false;
        }

        initializeGame();

        gameTimer = new Timer(16, this); // ~60 FPS
        gameTimer.start();
        gameRunning = false; // Start with start screen

        soundManager.playBackgroundMusic();
    }

    private void initializeGame() {
        // PERUBAHAN DI SINI: Gunakan player.height untuk posisi Y
        // player = new Player(50, GROUND_Y - 50); // Baris asli
        // Kita inisialisasi pemain dulu agar player.height terisi dari gambar.
        player = new Player(50, 0); // Posisi Y awal tidak terlalu penting di sini karena akan di-adjust

        // Setelah player diinisialisasi, baru kita bisa menggunakan player.height-nya
        // Posisi Y akan diatur agar bagian bawah pemain tepat di GROUND_Y
        player.y = GROUND_Y - player.height;

        fruits = new ArrayList<>();
        animals = new ArrayList<>();
        powerUps = new ArrayList<>();
        obstacles = new ArrayList<>();

        generateLevel();
    }

    private void generateLevel() {
        fruits.clear();
        animals.clear();
        powerUps.clear();
        obstacles.clear();

        gameSpeed = 1 + (level - 1) / 3;

        // Sesuaikan offset y di sini jika buah masih melayang/tenggelam
        for (int i = 0; i < 10 + level * 3; i++) {
            int x = 200 + i * 120 + random.nextInt(80);
            // Angka 30 ini adalah offset. Kurangi untuk 'menurunkan' buah, tambah untuk 'menaikkan' buah.
            int fruitYOffset = 30; // Sesuaikan jika perlu
            int y = GROUND_Y - fruitYOffset - random.nextInt(50);
            fruits.add(new Fruit(x, y));
        }

        // Sesuaikan offset y di sini jika hewan masih melayang/tenggelam
        for (int i = 0; i < 3 + level * 2; i++) {
            int x = 300 + i * 250 + random.nextInt(100);
            // Angka 40 ini adalah offset. Kurangi untuk 'menurunkan' hewan, tambah untuk 'menaikkan' hewan.
            int animalYOffset = 40; // Sesuaikan jika perlu
            animals.add(new WildAnimal(x, GROUND_Y - animalYOffset, gameSpeed));
        }

        if (level % 3 == 0) {
            // Sesuaikan offset y di sini jika power-up masih melayang/tenggelam
            for (int i = 0; i < 2; i++) {
                int x = 400 + i * 300 + random.nextInt(100);
                // Angka 30 ini adalah offset. Kurangi untuk 'menurunkan' power-up, tambah untuk 'menaikkan' power-up.
                int powerUpYOffset = 30; // Sesuaikan jika perlu
                int y = GROUND_Y - powerUpYOffset - random.nextInt(30);
                powerUps.add(new PowerUp(x, y));
            }
        }

        if (level >= 2) {
            // Sesuaikan offset y di sini jika rintangan masih melayang/tenggelam
            for (int i = 0; i < level; i++) {
                int x = 500 + i * 200 + random.nextInt(100);
                // Angka 40 ini adalah offset. Kurangi untuk 'menurunkan' rintangan, tambah untuk 'menaikkan' rintangan.
                int obstacleYOffset = 40; // Sesuaikan jika perlu
                obstacles.add(new Obstacle(x, GROUND_Y - obstacleYOffset));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!gameRunning && !gameOver) {
            drawStartScreen(g);
            return;
        }

        if (gameOver) {
            drawGameOverScreen(g);
            return;
        }

        drawBackground(g);

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g, cameraX);
        }

        for (Fruit fruit : fruits) {
            fruit.draw(g, cameraX);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g, cameraX);
        }

        for (WildAnimal animal : animals) {
            animal.draw(g, cameraX);
        }

        player.draw(g, cameraX);

        drawUI(g);
    }

    private void drawBackground(Graphics g) {
        if (backgroundLoaded && backgroundImage != null) {
            int bgX = -(cameraX / 4);
            bgX = (int) (bgX % SCREEN_WIDTH);
            if (bgX > 0) {
                bgX -= SCREEN_WIDTH;
            }
            g.drawImage(backgroundImage, bgX, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
            g.drawImage(backgroundImage, bgX + SCREEN_WIDTH, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint skyGradient = new GradientPaint(0, 0, new Color(135, 206, 235),
                    0, SCREEN_HEIGHT, new Color(173, 216, 230));
            g2d.setPaint(skyGradient);
            g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        g.setColor(Color.WHITE);
        for (int i = 0; i < cloudX.length; i++) {
            drawCloud(g, cloudX[i] - cameraX / 3, cloudY[i]);
            if (cloudX[i] - cameraX / 3 < -100) {
                cloudX[i] += SCREEN_WIDTH + 200;
            }
        }

        if (groundImageLoaded && groundImage != null) {
            int groundX = -(cameraX / 2);
            groundX = (int) (groundX % SCREEN_WIDTH);
            if (groundX > 0) {
                groundX -= SCREEN_WIDTH;
            }
            for (int i = 0; i < SCREEN_WIDTH / groundImage.getWidth() + 2; i++) {
                g.drawImage(groundImage, groundX + i * SCREEN_WIDTH, GROUND_Y, SCREEN_WIDTH, SCREEN_HEIGHT - GROUND_Y, null);
            }
        } else {
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, GROUND_Y, SCREEN_WIDTH, SCREEN_HEIGHT - GROUND_Y);

            g.setColor(new Color(0, 100, 0));
            for (int i = 0; i < SCREEN_WIDTH; i += 10) {
                g.drawLine(i, GROUND_Y, i, GROUND_Y + 5);
            }

            g.setColor(Color.YELLOW);
            for (int i = 0; i < SCREEN_WIDTH; i += 50) {
                int flowerX = i - (cameraX / 2) % 50;
                if (flowerX > -10 && flowerX < SCREEN_WIDTH + 10) {
                    g.fillOval(flowerX, GROUND_Y + 10, 6, 6);
                }
            }
        }
    }

    private void drawCloud(Graphics g, int x, int y) {
        g.fillOval(x, y, 60, 30);
        g.fillOval(x + 20, y - 10, 80, 40);
        g.fillOval(x + 60, y, 60, 30);
    }

    private void drawUI(Graphics g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(5, 5, 200, 100);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);
        g.drawString("Level: " + level, 10, 55);
        g.drawString("Lives: " + player.lives, 10, 80);

        if (player.hasSpeedBoost) {
            g.setColor(Color.CYAN);
            g.drawString("SPEED BOOST!", 10, 105);
        }
        if (player.hasJumpBoost) {
            g.setColor(Color.GREEN);
            g.drawString("JUMP BOOST!", 10, 105);
        }

        int totalFruits = 10 + level * 3;
        int fruitsCollected = totalFruits - fruits.size();
        int progressWidth = (int) (150 * ((double) fruitsCollected / totalFruits));

        g.setColor(Color.GRAY);
        g.fillRect(SCREEN_WIDTH - 160, 10, 150, 15);
        g.setColor(Color.GREEN);
        g.fillRect(SCREEN_WIDTH - 160, 10, progressWidth, 15);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Level Progress", SCREEN_WIDTH - 160, 40);
    }

    private void drawStartScreen(Graphics g) {
        g.setColor(new Color(0, 100, 0));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "FOREST RUNNER";
        int x = (SCREEN_WIDTH - fm.stringWidth(title)) / 2;
        g.drawString(title, x, 200);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g.getFontMetrics();
        String subtitle = "Collect fruits and avoid wild animals!";
        x = (SCREEN_WIDTH - fm.stringWidth(subtitle)) / 2;
        g.drawString(subtitle, x, 280);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        String[] instructions = {
                "Press SPACE to start",
                "LEFT/RIGHT arrows to move",
                "UP arrow to jump",
                "Collect power-ups for special abilities!"
        };

        for (int i = 0; i < instructions.length; i++) {
            x = (SCREEN_WIDTH - fm.stringWidth(instructions[i])) / 2;
            g.drawString(instructions[i], x, 320 + i * 25);
        }
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String gameOverText = "GAME OVER";
        int x = (SCREEN_WIDTH - fm.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, x, 250);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g.getFontMetrics();
        String finalScore = "Final Score: " + score;
        x = (SCREEN_WIDTH - fm.stringWidth(finalScore)) / 2;
        g.drawString(finalScore, x, 300);

        String levelReached = "Level Reached: " + level;
        x = (SCREEN_WIDTH - fm.stringWidth(levelReached)) / 2;
        g.drawString(levelReached, x, 330);

        String restart = "Press SPACE to restart";
        x = (SCREEN_WIDTH - fm.stringWidth(restart)) / 2;
        g.drawString(restart, x, 380);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning && !gameOver) {
            update();
        }
        repaint();
    }

    private void update() {
        player.update(keys, GROUND_Y);

        int targetCameraX = player.x - SCREEN_WIDTH / 3;
        cameraX += (targetCameraX - cameraX) * 0.1;

        for (WildAnimal animal : animals) {
            animal.update();
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.update();
        }

        checkCollisions();
        if (fruits.isEmpty()) {
            nextLevel();
        }
        if (player.lives <= 0) {
            gameOver = true;
            soundManager.stopBackgroundMusic();
            soundManager.playGameOverSound();
        }
    }

    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(player.x, player.y, player.width, player.height);

        for (int i = fruits.size() - 1; i >= 0; i--) {
            Fruit fruit = fruits.get(i);
            Rectangle fruitRect = new Rectangle(fruit.x, fruit.y, fruit.width, fruit.height);

            if (playerRect.intersects(fruitRect)) {
                fruits.remove(i);
                score += 10 + (level * 2);
                soundManager.playCollectSound();
            }
        }

        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            Rectangle powerUpRect = new Rectangle(powerUp.x, powerUp.y, powerUp.width, powerUp.height);

            if (playerRect.intersects(powerUpRect)) {
                powerUps.remove(i);
                player.activatePowerUp(powerUp.type);
                score += 25;
                soundManager.playCollectSound();
            }
        }

        for (Obstacle obstacle : obstacles) {
            Rectangle obstacleRect = new Rectangle(obstacle.x, obstacle.y, obstacle.width, obstacle.height);

            if (playerRect.intersects(obstacleRect) && !player.invulnerable) {
                player.takeDamage();
                soundManager.playHitSound();
                break;
            }
        }

        for (WildAnimal animal : animals) {
            Rectangle animalRect = new Rectangle(animal.x, animal.y, animal.width, animal.height);

            if (playerRect.intersects(animalRect) && !player.invulnerable) {
                player.takeDamage();
                soundManager.playHitSound();
                break;
            }
        }
    }

    private void nextLevel() {
        level++;
        score += 50 + (level * 10);
        generateLevel();
        player.x = 50;
        // Posisi Y pemain akan diatur ulang agar bagian bawahnya menjejak di GROUND_Y
        player.y = GROUND_Y - player.height;
        cameraX = 0;
        soundManager.playLevelUpSound();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (!gameRunning && !gameOver && keyCode == KeyEvent.VK_SPACE) {
            gameRunning = true;
            return;
        }
        if (gameOver && keyCode == KeyEvent.VK_SPACE) {
            score = 0;
            level = 1;
            gameOver = false;
            gameRunning = true;
            initializeGame(); // Memanggil ulang initializeGame akan mengatur ulang player.y
            soundManager.playBackgroundMusic();
            return;
        }

        if (keyCode < keys.length) {
            keys[keyCode] = true;
        }

        if (keyCode == KeyEvent.VK_UP && player.onGround) {
            soundManager.playJumpSound();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keys.length) {
            keys[keyCode] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Forest Runner");
        ForestRunner game = new ForestRunner();

        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
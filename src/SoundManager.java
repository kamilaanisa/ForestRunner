import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private Clip backgroundMusic;
    private Clip collectSound;
    private Clip jumpSound;
    private Clip hitSound;
    private Clip gameOverSound;
    private Clip levelUpSound;

    private boolean soundEnabled = true;
    private float masterVolume = 0.5f;

    public SoundManager() {
        try {
            loadSound();
        } catch (Exception e) {
            System.out.println("Could not load sound files: " + e.getMessage());
            System.out.println("Game will run without sound.");
            soundEnabled = false;
        }
    }

    private void loadSound() {
        try {
            // Create sounds directory if it doesn't exist
            File soundsDir = new File("sounds");
            if (!soundsDir.exists()) {
                soundsDir.mkdirs();
                System.out.println("Created sounds directory. Please add sound files:");
                System.out.println("- background.wav (background music)");
                System.out.println("- collect.wav (fruit collection sound)");
                System.out.println("- jump.wav (jump sound)");
                System.out.println("- hit.wav (damage sound)");
                System.out.println("- gameover.wav (game over sound)");
                System.out.println("- levelup.wav (level up sound)");
            }

            // Load sound files with fallback to silence
            backgroundMusic = loadClipWithFallback("sounds/background.wav");
            collectSound = loadClipWithFallback("sounds/collect.wav");
            jumpSound = loadClipWithFallback("sounds/jump.wav");
            hitSound = loadClipWithFallback("sounds/hit.wav");
            gameOverSound = loadClipWithFallback("sounds/gameover.wav");
            levelUpSound = loadClipWithFallback("sounds/levelup.wav");

            System.out.println("Sound system initialized!");

        } catch (Exception e) {
            System.out.println("Sound files not found, running without audio");
            soundEnabled = false;
        }
    }

    private Clip loadClipWithFallback(String filepath) {
        try {
            File soundFile = new File(filepath);
            if (soundFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);

                // Set volume
                setClipVolume(clip, masterVolume);

                System.out.println("Loaded: " + filepath);
                return clip;
            } else {
                System.out.println("Sound file not found: " + filepath + " (will run silently)");
                return createSilentClip();
            }
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio format: " + filepath);
            return createSilentClip();
        } catch (IOException e) {
            System.out.println("Error reading sound file: " + filepath);
            return createSilentClip();
        } catch (LineUnavailableException e) {
            System.out.println("Audio line unavailable for: " + filepath);
            return createSilentClip();
        }
    }

    private Clip createSilentClip() {
        try {
            // Create a silent audio clip as fallback
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            byte[] silentAudio = new byte[4410]; // 0.1 second of silence
            AudioInputStream silentStream = new AudioInputStream(
                    new java.io.ByteArrayInputStream(silentAudio), format, silentAudio.length / format.getFrameSize());

            Clip clip = AudioSystem.getClip();
            clip.open(silentStream);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        try {
            if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        } catch (Exception e) {
            // Volume control not supported, continue without it
        }
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));

        // Update all clips
        setClipVolume(backgroundMusic, masterVolume * 0.3f); // Background music quieter
        setClipVolume(collectSound, masterVolume);
        setClipVolume(jumpSound, masterVolume);
        setClipVolume(hitSound, masterVolume);
        setClipVolume(gameOverSound, masterVolume);
        setClipVolume(levelUpSound, masterVolume);
    }

    public void playBackgroundMusic() {
        if (!soundEnabled) return;

        try {
            if (backgroundMusic != null) {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                System.out.println("Background music started");
            }
        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (!soundEnabled) return;

        try {
            if (backgroundMusic != null && backgroundMusic.isRunning()) {
                backgroundMusic.stop();
                System.out.println("Background music stopped");
            }
        } catch (Exception e) {
            System.out.println("Error stopping background music: " + e.getMessage());
        }
    }

    public void playCollectSound() {
        playSound(collectSound, "collect");
    }

    public void playJumpSound() {
        playSound(jumpSound, "jump");
    }

    public void playHitSound() {
        playSound(hitSound, "hit");
    }

    public void playGameOverSound() {
        stopBackgroundMusic();
        playSound(gameOverSound, "game over");
    }

    public void playLevelUpSound() {
        playSound(levelUpSound, "level up");
    }

    private void playSound(Clip clip, String soundName) {
        if (!soundEnabled) return;

        try {
            if (clip != null) {
                // Stop the clip if it's already playing
                if (clip.isRunning()) {
                    clip.stop();
                }
                // Reset to beginning
                clip.setFramePosition(0);
                // Play the sound
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("Error playing " + soundName + " sound: " + e.getMessage());
        }
    }

    public void toggleSound() {
        soundEnabled = !soundEnabled;
        if (!soundEnabled) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
        System.out.println("Sound " + (soundEnabled ? "enabled" : "disabled"));
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    // Method untuk cleanup saat game ditutup
    public void cleanup() {
        try {
            if (backgroundMusic != null) {
                backgroundMusic.close();
            }
            if (collectSound != null) {
                collectSound.close();
            }
            if (jumpSound != null) {
                jumpSound.close();
            }
            if (hitSound != null) {
                hitSound.close();
            }
            if (gameOverSound != null) {
                gameOverSound.close();
            }
            if (levelUpSound != null) {
                levelUpSound.close();
            }
            System.out.println("Sound system cleaned up");
        } catch (Exception e) {
            System.out.println("Error cleaning up sound system: " + e.getMessage());
        }
    }
}
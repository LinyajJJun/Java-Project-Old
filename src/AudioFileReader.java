//package audio.basic;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
public class AudioFileReader {
    private Clip audioClip;
    private boolean isPlaying = false;

    public void readFile() {
        AudioFileReader audioPlayer = new AudioFileReader();
        audioPlayer.playAudio("C:\\Users\\林亞駿\\Downloads\\M500002p7tUL3zZaOG.wav");
        audioPlayer.handleUserInput();
    }

    public void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            isPlaying = true;
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            System.out.println("Error playing audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleUserInput() {
        Scanner scanner = new Scanner(System.in);
        String input;
        while (true) {
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("pause")) {
                pauseAudio();
            } else if (input.equalsIgnoreCase("continue")) {
                resumeAudio();
            } else if (input.equalsIgnoreCase("exit")) {
                stopAudio();
                break;
            } else {
                System.out.println("Invalid input");
            }
        }
    }

    public void pauseAudio() {
        if (isPlaying) {
            audioClip.stop();
            isPlaying = false;
        }
    }
    public void resumeAudio() {
        if (!isPlaying) {
            audioClip.start();
            isPlaying = true;
        }
    }
    public void stopAudio() {
        if (isPlaying) {
            audioClip.stop();
            audioClip.close();
            isPlaying = false;
        }
    }
}

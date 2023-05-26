package audio.basic;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class AudioMixer extends JFrame {
    private JFileChooser fileChooser;
    private JButton selectFilesButton;
    private JButton selectOutputButton;

    private File[] selectedFiles;
    private File outputFile;

    public AudioMixer() {
        setTitle("Audio Mixer");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout(new FlowLayout());

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        selectFilesButton = new JButton("Select Files");
        selectFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(AudioMixer.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedFiles = fileChooser.getSelectedFiles();
                    StringBuilder message = new StringBuilder("Selected Files:\n");
                    for (File file : selectedFiles) {
                        message.append(file.getAbsolutePath()).append("\n");
                    }
                    JOptionPane.showMessageDialog(AudioMixer.this, message.toString());
                }
            }
        });

        selectOutputButton = new JButton("Select Output File");
        selectOutputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showSaveDialog(AudioMixer.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    outputFile = fileChooser.getSelectedFile();
                    JOptionPane.showMessageDialog(AudioMixer.this, "Output file selected: " + outputFile.getAbsolutePath());
                }
            }
        });

        JButton mixButton = new JButton("Mix Audio");
        mixButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mixAudio();
            }
        });

        add(selectFilesButton);
        add(selectOutputButton);
        add(mixButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void mixAudio() {
        if (selectedFiles == null || selectedFiles.length < 2 || outputFile == null) {
            JOptionPane.showMessageDialog(this, "Please select at least 2 files and an output file.");
            return;
        }

        try {
            AudioInputStream[] audioStreams = new AudioInputStream[selectedFiles.length];
            AudioFormat[] formats = new AudioFormat[selectedFiles.length];

            // Get audio streams and check format compatibility
            for (int i = 0; i < selectedFiles.length; i++) {
                audioStreams[i] = AudioSystem.getAudioInputStream(selectedFiles[i]);
                formats[i] = audioStreams[i].getFormat();
            }

            // Check if all formats match
            for (int i = 1; i < formats.length; i++) {
                if (!formats[0].matches(formats[i])) {
                    throw new IllegalArgumentException("Audio formats do not match!");
                }
            }

            AudioInputStream mixedAudioStream = mixAudioStreams(audioStreams);
            AudioSystem.write(mixedAudioStream, AudioFileFormat.Type.WAVE, outputFile);
            JOptionPane.showMessageDialog(this, "Audio mixing complete. Saved to: " + outputFile.getAbsolutePath());

        } catch (UnsupportedAudioFileException | IOException | IllegalArgumentException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while mixing audio.");
        }
    }

    private static AudioInputStream mixAudioStreams(AudioInputStream[] audioStreams) {
        try {
            AudioFormat format = audioStreams[0].getFormat();
            long length = getMinimumLength(audioStreams);

            byte[] mixedAudioData = new byte[(int) (length * format.getFrameSize())];

            byte[][] audioData = new byte[audioStreams.length][format.getFrameSize()];

            // Mix audio data frame by frame
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < audioStreams.length; j++) {
                    audioStreams[j].read(audioData[j]);
                }
                for (int j = 0; j < audioData[0].length; j++) {
                    int mixedValue = 0;
                    for (int k = 0; k < audioData.length; k++) {
                        mixedValue += audioData[k][j];
                    }
                    mixedValue /= audioData.length;
                    mixedAudioData[i * format.getFrameSize() + j] = (byte) mixedValue;
                }
            }

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mixedAudioData);
            return new AudioInputStream(byteArrayInputStream, format, length);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long getMinimumLength(AudioInputStream[] audioStreams) {
        long minLength = Long.MAX_VALUE;
        for (AudioInputStream audioStream : audioStreams) {
            long length = audioStream.getFrameLength();
            if (length < minLength) {
                minLength = length;
            }
        }
        return minLength;
    }

    public static void main(String[] args) {
        new AudioMixer();
    }
}

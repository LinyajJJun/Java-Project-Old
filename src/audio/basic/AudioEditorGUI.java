package audio.basic;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.Iterator;

public class AudioEditorGUI extends JFrame {

    private JButton mergeButton, segmentButton;
    private JLabel statusLabel;

    public AudioEditorGUI() {
        setTitle("音频编辑器");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // 创建UI组件
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        mergeButton = new JButton("MERGE MUSIC");
        segmentButton = new JButton("CUT MUSIC");
        JLabel startTimeLabel = new JLabel("START TIME (sec)");
        JLabel endTimeLabel = new JLabel("END TIME (sec)");
        JTextField startTimeField = new JTextField();
        JTextField endTimeField = new JTextField();
        statusLabel = new JLabel("JUKE");

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        startTimeLabel.setFont(labelFont);
        endTimeLabel.setFont(labelFont);
        startTimeField.setFont(labelFont);
        endTimeField.setFont(labelFont);
        statusLabel.setFont(labelFont);

        mergeButton.setPreferredSize(new Dimension(150, 30));
        segmentButton.setPreferredSize(new Dimension(150, 30));

        panel.add(mergeButton);
        panel.add(segmentButton);
        panel.add(startTimeLabel);
        panel.add(startTimeField);
        panel.add(endTimeLabel);
        panel.add(endTimeField);
        panel.add(statusLabel);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(panel);

        mergeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                int result = fileChooser.showOpenDialog(AudioEditorGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    JFileChooser saveFileChooser = new JFileChooser();
                    saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int saveResult = saveFileChooser.showSaveDialog(AudioEditorGUI.this);
                    if (saveResult == JFileChooser.APPROVE_OPTION) {
                        File outputFile = saveFileChooser.getSelectedFile();
                        mergeAudioFiles(files, outputFile);
                    }
                }
            }
        });

        segmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(AudioEditorGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File inputFile = fileChooser.getSelectedFile();
                    double startTime = Double.parseDouble(startTimeField.getText());
                    double endTime = Double.parseDouble(endTimeField.getText());
                    JFileChooser saveFileChooser = new JFileChooser();
                    saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int saveResult = saveFileChooser.showSaveDialog(AudioEditorGUI.this);
                    if (saveResult == JFileChooser.APPROVE_OPTION) {
                        File outputFile = saveFileChooser.getSelectedFile();
                        segmentAudioFile(inputFile, startTime, endTime, outputFile);
                    }
                }
            }
        });
    }

    private void mergeAudioFiles(File[] files, File outputFile) {
        if (files.length < 2) {
            statusLabel.setText("AT LEAST CHOOSE 2");
            return;
        }
        List<AudioInputStream> audioStreams = new ArrayList<>();

        try {
            for (File file : files) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                audioStreams.add(audioStream);
            }

            AudioFormat format = audioStreams.get(0).getFormat();

            Enumeration<AudioInputStream> audioEnum = new Enumeration<AudioInputStream>() {
                private final Iterator<AudioInputStream> iterator = audioStreams.iterator();

                @Override
                public boolean hasMoreElements() {
                    return iterator.hasNext();
                }

                @Override
                public AudioInputStream nextElement() {
                    return iterator.next();
                }
            };
            SequenceInputStream mergedStream = new SequenceInputStream(audioEnum);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = mergedStream.read(buffer, 0, buffer.length)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            AudioInputStream mergedAudioStream = new AudioInputStream(byteArrayInputStream, format,
                    byteArrayOutputStream.size() / format.getFrameSize());

            AudioSystem.write(mergedAudioStream, AudioFileFormat.Type.WAVE, outputFile);
            statusLabel.setText("合併成功");

            mergedAudioStream.close();
            byteArrayOutputStream.close();
            byteArrayInputStream.close();

            mergedStream.close();
            for (AudioInputStream audioStream : audioStreams) {
                audioStream.close();
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed");
        }
    }

    private void segmentAudioFile(File inputFile, double startTime, double endTime, File outputFile) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputFile);
            AudioFormat format = audioStream.getFormat();

            long startFrame = Math.round(startTime * format.getFrameRate());
            long endFrame = Math.round(endTime * format.getFrameRate());

            long frameCount = endFrame - startFrame;
            audioStream.skip(startFrame * format.getFrameSize());

            AudioInputStream segmentedStream = new AudioInputStream(
                    new AudioInputStream(audioStream, format, frameCount),
                    format, frameCount);

            AudioSystem.write(segmentedStream, AudioFileFormat.Type.WAVE, outputFile);

            segmentedStream.close();
            audioStream.close();

            statusLabel.setText("裁剪成功");
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed");

        }
    }

    public void run() {
        new AudioEditorGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AudioEditorGUI().setVisible(true);
            }
        });
    }
}

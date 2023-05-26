package advanced;

import advanced.io.*;
import advanced.pedal.Compressor;
import advanced.pedal.Delay;
import advanced.pedal.Overdrive;
import advanced.pedal.Pedal;

import javax.swing.*;
import javax.sound.sampled.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MultiEffectsProcessorUI {
    private final static int BUFFER_SIZE = 1024;

    enum IOInDevice {
        LINE_IN,
        FILE_IN
    }
    enum IOOutDevice {
        LINE_OUT,
        FILE_OUT
    }
    private String[] inDeviceName= {"Line In", "File In"};
    private String[] outDeviceName = {"Line Out", "File Out"};
    private JFrame frame;
    private JPanel pedalFrame;
    private JToggleButton overdriveToggle;
    private JToggleButton compressorToggle;
    private JToggleButton delayToggle;
    private JSlider gainSlider;
    private JSlider overdriveLevelSlider;
    private JSlider levelSlider;
    private JSlider delaySlider;
    private JSlider feedbackSlider;
    private JComboBox inDeviceComboBox;
    private JComboBox outDeviceComboBox;
    private JButton chooseFileButton;
    private JButton saveFileButton;
    private JButton pauseButton;
    private JButton startButton;
    private JButton closeButton;
    private JButton continueButton;
    private JButton recodeButton;
    private JButton chooseFloderButton;
    private JLabel statusLabel;


    private JLabel statusMessageLabel;

    private Pedal overdrive;
    private Pedal compressor;
    private Pedal delay;
    private IOInDevice inDevice;
    private IOOutDevice outDevice;
    private boolean isOverdrive;
    private boolean isCompressor;
    private boolean isDelay;
    private boolean isPaused;
    private boolean isClosed;
    private boolean isRecode;
    private float gain;
    private float overdriveLevel;
    private float level;
    private int delayMills;
    private float feedback;
    private IOIn lineIn;
    private IOIn lineInForSave;
    private IOOut lineOut;
    private IOOut fileOut;
    private AudioFormat format;
    private GridBagConstraints c;

    private File file;

    private File folder;
    public MultiEffectsProcessorUI() {
        initialize();
    }

    private void initialize() {

        format = new AudioFormat(44100, 16, 1, true, true);
        frame = new JFrame();
        //固定視窗大小
        frame.setResizable(false);
        frame.setSize(400, 300);
        pedalFrame = new JPanel();
        pedalFrame.setLayout(new GridBagLayout());

        inDevice = IOInDevice.LINE_IN;
        outDevice = IOOutDevice.LINE_OUT;


        inDeviceComboBox = new JComboBox(inDeviceName);
        outDeviceComboBox = new JComboBox(outDeviceName);

        overdrive = new Overdrive();
        compressor = new Compressor();
        delay = new Delay(format);

        isPaused = false;
        isClosed = true;
        isRecode = false;

        frame.setBounds(100, 100, 400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        overdriveToggle = new JToggleButton("Overdrive");
        compressorToggle = new JToggleButton("Compressor");
        delayToggle = new JToggleButton("Delay");

        gain = 2;
        overdriveLevel = 100;
        level = 100;
        delayMills = 500;
        feedback = 25f;


        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        initObject();
        initActionListener();
        initPedalFrame();
    }
    private void initPedalFrame(){
        c.weightx = 1;

        c.gridx = 0;
        c.gridy = 0;
        pedalFrame.add(new JLabel("Gain"), c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        pedalFrame.add(gainSlider, c);
        c.gridx = 3;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        pedalFrame.add(overdriveToggle, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        pedalFrame.add(new JLabel("Level"), c);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        pedalFrame.add(overdriveLevelSlider, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        pedalFrame.add(new JLabel("Level"), c);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        pedalFrame.add(levelSlider, c);
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        pedalFrame.add(compressorToggle, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        pedalFrame.add(new JLabel("Delay"), c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        pedalFrame.add(delaySlider, c);
        c.gridx = 3;
        c.gridy = 3;
        c.gridheight = 2;
        c.gridwidth = 1;
        pedalFrame.add(delayToggle, c);
        c.gridx = 0;
        c.gridy = 4;
        c.gridheight = 1;
        pedalFrame.add(new JLabel("Feedback"), c);
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 2;
        pedalFrame.add(feedbackSlider,c);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 5;
        pedalFrame.add(startButton, c);

        c.gridx = 1;
        c.gridy = 5;
        pedalFrame.add(pauseButton, c);
        c.gridx = 2;
        c.gridy = 5;
        pedalFrame.add(closeButton, c);
        frame.getContentPane().add(pedalFrame);
        c.gridx = 3;
        c.gridy = 5;
        c.gridwidth = 1;
        pedalFrame.add(recodeButton, c);


        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        pedalFrame.add(new JLabel("Input"), c);
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 2;
        pedalFrame.add(inDeviceComboBox, c);
        c.gridx = 3;
        c.gridy = 6;
        c.gridwidth = 1;
        pedalFrame.add(chooseFloderButton, c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        pedalFrame.add(statusLabel, c);
        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 3;
        pedalFrame.add(statusMessageLabel, c);

        frame.setVisible(true);
    }
    private void initObject(){
        gainSlider = new JSlider(1, 10, 2);
        overdriveLevelSlider = new JSlider(1, 128, 100);
        levelSlider = new JSlider(10, 128, 100);
        delaySlider = new JSlider(10, 1000, 500);
        feedbackSlider = new JSlider(1, 99, 25);

        statusLabel = new JLabel("Status:");
        statusMessageLabel = new JLabel("");
        statusMessageLabel.setName("statusMessageLabel");

        startButton = new JButton("Start");
        startButton.setName("startButton");
        closeButton = new JButton("Close");
        closeButton.setEnabled(false);
        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);
        continueButton = new JButton("Continue");
        continueButton.setEnabled(false);
        continueButton.setName("continueButton");
        chooseFileButton = new JButton("Choose File");
        chooseFileButton.setName("chooseFileButton");
        saveFileButton = new JButton("Save File");
        saveFileButton.setName("saveFileButton");
        recodeButton = new JButton("Recode");
        recodeButton.setName("recodeButton");
        recodeButton.setEnabled(false);
        chooseFloderButton = new JButton("Choose Folder");
        chooseFloderButton.setName("chooseFolderButton");
    }

    private void initActionListener(){
        overdriveToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = overdriveToggle.isSelected();
                isOverdrive = selected;
            }
        });
        compressorToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = compressorToggle.isSelected();
                isCompressor = selected;
            }
        });
        delayToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = delayToggle.isSelected();
                isDelay = selected;
            }
        });
        gainSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = gainSlider.getValue();
                gain = (float) value;
                ((Overdrive)overdrive).setGain(gain, overdriveLevel);

            }
        });
        overdriveLevelSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = overdriveLevelSlider.getValue();
                overdriveLevel = (float) value;
                ((Overdrive)overdrive).setGain(gain, overdriveLevel);
            }
        });
        levelSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = levelSlider.getValue();
                level = (float) value;
                ((Compressor)compressor).setLevel(level);

            }
        });
        delaySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = delaySlider.getValue();
                delayMills = value;
                ((Delay)delay).setDelay(delayMills, feedback);
            }
        });
        feedbackSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = feedbackSlider.getValue();
                feedback = (float) value / 100;
                ((Delay)delay).setDelay(delayMills, feedback);
            }
        });
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isPaused){
                    isPaused = false;
                    startButton.setEnabled(false);
                    pauseButton.setEnabled(true);
                    saveFileButton.setEnabled(false);
                    startAudioProcessing();
                    return;
                }

                int index = inDeviceComboBox.getSelectedIndex();
                if (index == 0) {
                    try {
                        lineIn = new LineIn(format);
                    } catch (LineUnavailableException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    try {
                        lineIn = new FileIn(file, format);
                    } catch (Exception ex) {
                        //跳出警告視窗 檔案錯誤
                        JOptionPane.showMessageDialog(frame, "File Error", "Error", JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException(ex);
                    }
                }
                try {
                    lineOut = new LineOut(lineIn.getFormat());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "File Format Error", "Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }
                updateStatusMessage("Playing");
                inDeviceComboBox.setEnabled(false);
                startButton.setEnabled(false);
                closeButton.setEnabled(true);
                pauseButton.setEnabled(true);
                if(folder!=null)
                    recodeButton.setEnabled(true);
                saveFileButton.setEnabled(false);
                isPaused = false;
                isClosed = false;
                startAudioProcessing();
            }
        });
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inDeviceComboBox.setEnabled(true);
                pauseButton.setEnabled(false);
                closeButton.setEnabled(false);
                startButton.setEnabled(true);
                recodeButton.setEnabled(false);
                saveFileButton.setEnabled(false);
                isPaused = false;
                isClosed = true;
                closeAudioProcessing();
                updateStatusMessage("Closed File and Line");
            }
        });
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseButton.setEnabled(false);
                startButton.setEnabled(true);
                saveFileButton.setEnabled(true);
                isPaused = true;
                updateStatusMessage("Paused");
            }
        });
        chooseFileButton.addActionListener(new ActionListener() {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV File", "wav");
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(pedalFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                }
                updateStatusMessage("File Selected " + file.getName());
                saveFileButton.setEnabled(true);
                try {
                    lineIn = new FileIn(file, format);
                } catch (Exception ex) {
                    //跳出警告視窗 檔案錯誤
                    JOptionPane.showMessageDialog(frame, "File Error", "Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }
            }
        });
        recodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isRecode == false) {

                    if(folder == null){
                        JOptionPane.showMessageDialog(frame, "Folder Error", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    try {
                        fileOut = new FileOut(folder + "\\recode.wav", format);
                    } catch (Exception ex) {
                        //跳出警告視窗 檔案錯誤
                        JOptionPane.showMessageDialog(frame, "Folder Error", "Error", JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException(ex);
                    }
                    closeButton.setEnabled(false);
                    updateStatusMessage("Recode Start");
                    isRecode = true;
                } else {
                    isRecode = false;
                    closeButton.setEnabled(true);
                    updateStatusMessage("Recode Stop");
                    fileOut.close();
                }

            }
        });
        chooseFloderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(pedalFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    folder = fileChooser.getSelectedFile();
                }
                updateStatusMessage("Folder Selected " + folder.getName());
                recodeButton.setEnabled(true);            }
        });
        inDeviceComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = inDeviceComboBox.getSelectedIndex();
                if (index == 0) {
                    inDevice = IOInDevice.LINE_IN;
                    try {
                        Component[] components = pedalFrame.getComponents();
                        for (Component component : components) {
                            if (component.getName() != null && (component.getName().equals("chooseFileButton") || component.getName().equals("saveFileButton"))){
                                System.out.println(component.getName());
                                pedalFrame.remove(component);
                            }
                        }
                        try {
                            c.gridx = 3;
                            c.gridy = 6;
                            c.gridwidth = 1;
                            pedalFrame.add(chooseFloderButton, c);
                            c.gridx = 3;
                            c.gridy = 5;
                            c.gridwidth = 1;
                            pedalFrame.add(recodeButton, c);
                            pedalFrame.revalidate();
                            pedalFrame.repaint();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return;
                        }
                        pedalFrame.revalidate();
                        pedalFrame.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                } else if (index == 1) {
                    inDevice = IOInDevice.FILE_IN;
                    Component[] components = pedalFrame.getComponents();
                    for (Component component : components) {
                        if (component.getName() != null && ((component.getName().equals("chooseFolderButton"))||component.getName().equals("recodeButton"))){
                            System.out.println(component.getName());
                            pedalFrame.remove(component);
                        }
                    }
                    try {
                        c.gridx = 3;
                        c.gridy = 6;
                        c.gridwidth = 1;
                        pedalFrame.add(chooseFileButton, c);
                        c.gridx = 3;
                        c.gridy = 5;
                        c.gridwidth = 1;
                        pedalFrame.add(saveFileButton, c);
                        pedalFrame.revalidate();
                        pedalFrame.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                }
            }
        });
        saveFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(file == null){
                    JOptionPane.showMessageDialog(frame, "No File Selected", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String filePath = file.getPath();
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_processed.wav";
                try {
                    fileOut = new FileOut(filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" + fileName, format);
                    lineInForSave = new FileIn(file, format);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                try {
                    lineInForSave.start();
                    fileOut.start();
                    byte[] buffer = new byte[1024];

                    while (true) {
                        int bytesRead = lineIn.read(buffer, 0, buffer.length);
                        if (bytesRead == -1) break;
                        byte[] overdriveBuffer;
                        byte[] compressorBuffer;
                        byte[] delayBuffer;

                        if (isOverdrive) overdriveBuffer = overdrive.process(buffer);
                        else overdriveBuffer = buffer;

                        if (isCompressor) compressorBuffer = compressor.process(overdriveBuffer);
                        else compressorBuffer = overdriveBuffer;

                        if (isDelay) delayBuffer = delay.process(compressorBuffer);
                        else delayBuffer = compressorBuffer;


                        fileOut.write(delayBuffer, 0, bytesRead);
                    }
                    fileOut.close();
                    lineInForSave.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                updateStatusMessage("File Saved " + fileName);
            }
        });
    }
    private void startAudioProcessing() {
        SwingWorker<Void, Void> audioProcessingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    lineIn.start();
                    lineOut.start();
                    if(isRecode){
                        fileOut.start();
                    }
                    byte[] buffer = new byte[1024];

                    while (!isCancelled()) {
                        if (isPaused || isClosed) {
                            try {
                                Thread.sleep(100); // 暫停執行緒 100 毫秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        int bytesRead = lineIn.read(buffer, 0, buffer.length);
                        if(bytesRead == -1) {
                            updateStatusMessage("Audio Processing Finished");
                            startButton.setEnabled(false);
                            closeButton.setEnabled(true);
                            pauseButton.setEnabled(false);
                            saveFileButton.setEnabled(false);
                            break;
                        };
                        byte[] overdriveBuffer;
                        byte[] compressorBuffer;
                        byte[] delayBuffer;

                        if (isOverdrive) overdriveBuffer = overdrive.process(buffer);
                        else overdriveBuffer = buffer;

                        if (isCompressor) compressorBuffer = compressor.process(overdriveBuffer);
                        else compressorBuffer = overdriveBuffer;

                        if (isDelay) delayBuffer = delay.process(compressorBuffer);
                        else delayBuffer = compressorBuffer;


                        lineOut.write(delayBuffer, 0, bytesRead);
                        if(isRecode){
                            fileOut.write(delayBuffer, 0, bytesRead);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    lineIn.close();
                    lineOut.close();
                }
                return null;
            }
        };

        audioProcessingWorker.execute();
    }
    private void closeAudioProcessing() {
        if (lineIn != null) {
            lineIn.close();
        }

        if (lineOut != null) {
            lineOut.close();
        }
    }

    private void updateStatusMessage(String message) {
        statusMessageLabel.setText(message);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MultiEffectsProcessorUI();
            }
        });
    }
}

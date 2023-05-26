package audio.basic;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class Main extends JFrame implements ActionListener {

    JButton fileReaderButton,recordButton,mixerButton,editButton;
    private AudioFileReader fileReader;
    private MyRecord record;
    private AudioMixer mixer;
    private AudioEditorGUI edit;
    public Main() {
        setTitle("音頻基礎實現");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon backgroundIcon = new ImageIcon("C:\\Users\\林亞駿\\Downloads\\螢幕擷取畫面 2023-05-19 012701.png");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());

        setLayout(new BorderLayout());
        ((JPanel) getContentPane()).setOpaque(false);
        getLayeredPane().add(backgroundLabel, new Integer(Integer.MIN_VALUE));

        fileReaderButton = new JButton("讀取");
        fileReaderButton.addActionListener(this);
        fileReaderButton.setBackground(Color.CYAN);
        fileReaderButton.setPreferredSize(new Dimension(80, 80));
        recordButton = new JButton("錄音");
        recordButton.addActionListener(this);
        recordButton.setBackground(Color.CYAN);
        recordButton.setPreferredSize(new Dimension(80, 80));
        mixerButton = new JButton("混音");
        mixerButton.addActionListener(this);
        mixerButton.setBackground(Color.CYAN);
        mixerButton.setPreferredSize(new Dimension(80, 80));
        editButton = new JButton("音導裁減/合併");
        editButton.addActionListener(this);
        editButton.setBackground(Color.CYAN);
        editButton.setPreferredSize(new Dimension(120, 80));

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(fileReaderButton);
        panel.add(recordButton);
        panel.add(mixerButton);
        panel.add(editButton);

        getContentPane().add(panel, BorderLayout.SOUTH);
        setSize(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fileReaderButton) {
            fileReader = new AudioFileReader();
            fileReader.readFile();
        } else if (e.getSource() == recordButton) {
            record = new MyRecord();
            record.showgui();
        } else if (e.getSource() == mixerButton) {
            mixer = new AudioMixer();
            //mixer.mixAudio();
        }
        else if(e.getSource()==editButton){
            edit=new AudioEditorGUI();
            edit.run();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}


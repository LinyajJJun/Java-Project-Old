//package audio.basic;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;

public class MyRecord extends JFrame implements ActionListener{
    AudioFormat af = null;
    TargetDataLine td = null;
    SourceDataLine sd = null;
    ByteArrayInputStream bais = null;
    ByteArrayOutputStream baos = null;
    AudioInputStream ais = null;
    Boolean stopflag = false;

    JPanel jp1,jp2,jp3;
    JLabel jl1=null;
    JButton captureBtn,stopBtn,playBtn,saveBtn;
    //建構函式
    public MyRecord() {
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();

        Font myFont = new Font("華文新魏",Font.BOLD,30);
        jl1 = new JLabel("錄音機功能的實現");
        jl1.setFont(myFont);
        jp1.add(jl1);

        captureBtn = new JButton("開始錄音");
        //對開始錄音按鈕進行註冊監聽
        captureBtn.addActionListener(this);
        captureBtn.setActionCommand("captureBtn");
        //對停止錄音進行註冊監聽
        stopBtn = new JButton("停止錄音");
        stopBtn.addActionListener(this);
        stopBtn.setActionCommand("stopBtn");
        //對播放錄音進行註冊監聽
        playBtn = new JButton("播放錄音");
        playBtn.addActionListener(this);
        playBtn.setActionCommand("playBtn");
        //對儲存錄音進行註冊監聽
        saveBtn = new JButton("儲存錄音");
        saveBtn.addActionListener(this);
        saveBtn.setActionCommand("saveBtn");


        this.add(jp1,BorderLayout.NORTH);
        this.add(jp2,BorderLayout.CENTER);
        this.add(jp3,BorderLayout.SOUTH);
        jp3.setLayout(null);
        jp3.setLayout(new GridLayout(1, 4,10,10));
        jp3.add(captureBtn);
        jp3.add(stopBtn);
        jp3.add(playBtn);
        jp3.add(saveBtn);
        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        this.setSize(400,300);
        this.setTitle("錄音機");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
    public void showgui(){
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("captureBtn")) {
            captureBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            playBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            capture();
        }
        else if (e.getActionCommand().equals("stopBtn")) {
            captureBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            saveBtn.setEnabled(true);
            stop();
        }
        else if(e.getActionCommand().equals("playBtn")){
            play();
        }
        else if(e.getActionCommand().equals("saveBtn")) {
            save();
        }
    }
    public void capture()
    {
        try {
            af = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);
            td = (TargetDataLine)(AudioSystem.getLine(info));
            td.open(af);
            td.start();

            //建立播放錄音的執行緒
            Record record = new Record();
            Thread t1 = new Thread(record);
            t1.start();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            return;
        }
    }
    public void stop()
    {
        stopflag = true;
    }
    public void play()
    {
        byte audioData[] = baos.toByteArray();
        bais = new ByteArrayInputStream(audioData);
        af = getAudioFormat();
        ais = new AudioInputStream(bais, af, audioData.length/af.getFrameSize());
        try {
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af);
            sd = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sd.open(af);
            sd.start();
            //建立播放程序
            Play py = new Play();
            Thread t2 = new Thread(py);
            t2.start();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                //關閉流
                if(ais != null)
                {
                    ais.close();
                }
                if(bais != null)
                {
                    bais.close();
                }
                if(baos != null)
                {
                    baos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //儲存錄音
    public void save() {
        af = getAudioFormat();
        byte audioData[] = baos.toByteArray();
        bais = new ByteArrayInputStream(audioData);
        ais = new AudioInputStream(bais,af, audioData.length / af.getFrameSize());
        File file = null;
        try {
            File filePath = new File("C:\\Users\\林亞駿\\Desktop\\audio");
            if(!filePath.exists()) {//如果檔案不存在，則建立該目錄
                filePath.mkdir();
            }
            file = new File(filePath.getPath()+"/"+System.currentTimeMillis()+".mp3");
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //關閉流
            try {

                if(bais != null)
                {
                    bais.close();
                }
                if(ais != null)
                {
                    ais.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public AudioFormat getAudioFormat() {
        float sampleRate = 44100.0f;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat audioFormat = new AudioFormat(
                sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        return audioFormat;
    }

    class Record implements Runnable {
        byte bts[] = new byte[10000];
        public void run() {
            baos = new ByteArrayOutputStream();
            try {
                System.out.println("ok3");
                stopflag = false;
                while(stopflag != true) {
                    int cnt = td.read(bts, 0, bts.length);
                    if(cnt > 0){
                        baos.write(bts, 0, cnt);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    if(baos != null) {
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    td.drain();
                    td.close();
                }
            }
        }

    }

    class Play implements Runnable {
        public void run() {
            byte bts[] = new byte[10000];
            try {
                int cnt;
                while ((cnt = ais.read(bts, 0, bts.length)) != -1) {
                    if (cnt > 0){
                        sd.write(bts, 0, cnt);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                sd.drain();
                sd.close();
            }
        }
    }
}
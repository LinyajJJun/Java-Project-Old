//package audio.basic;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class AudioMixer {
    public void mixAudio(){
        String filePath1 = "C:\\Users\\林亞駿\\Downloads\\2442946163.wav";
        String filePath2 = "C:\\Users\\林亞駿\\Downloads\\157838373.wav";
        String outputFilePath = "C:\\Users\\林亞駿\\Desktop\\audio\\output.wav";
        try {
            AudioInputStream audioStream1 = AudioSystem.getAudioInputStream(new File(filePath1));
            AudioInputStream audioStream2 = AudioSystem.getAudioInputStream(new File(filePath2));

            AudioFormat format1 = audioStream1.getFormat();
            AudioFormat format2 = audioStream2.getFormat();
            if (!format1.matches(format2)) {
                throw new IllegalArgumentException("音频格式不匹配！");
            }

            AudioInputStream mixedAudioStream = mixAudioStreams(audioStream1, audioStream2);
            AudioSystem.write(mixedAudioStream, AudioFileFormat.Type.WAVE, new File(outputFilePath));
            System.out.println("音频混合完成，保存到：" + outputFilePath);

        } catch (UnsupportedAudioFileException | IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static AudioInputStream mixAudioStreams(AudioInputStream audioStream1, AudioInputStream audioStream2) {
        try {
            AudioFormat format = audioStream1.getFormat();
            long length = Math.min(audioStream1.getFrameLength(), audioStream2.getFrameLength());

            byte[] mixedAudioData = new byte[(int) (length * format.getFrameSize())];

            byte[] audioData1 = new byte[format.getFrameSize()];
            byte[] audioData2 = new byte[format.getFrameSize()];

            // 逐帧混合音频数据
            for (int i = 0; i < length; i++) {
                audioStream1.read(audioData1);
                audioStream2.read(audioData2);
                for (int j = 0; j < audioData1.length; j++) {
                    int mixedValue = (audioData1[j] + audioData2[j]) / 2;
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
}

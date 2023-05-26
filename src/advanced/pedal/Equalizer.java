package advanced.pedal;

import javax.sound.sampled.AudioFormat;
import java.util.HashMap;

public class Equalizer extends Pedal{
    private HashMap<Double, Double> eq = new HashMap<>();
    private HashMap<Double, Double> prevSample = new HashMap<>();
    private double smoothingFactor = 1;

    private AudioFormat audioFormat;
    public Equalizer(AudioFormat audioFormat){
        this.audioFormat = audioFormat;
    }
    public void setEqualizer(double frequency, double gain) {
        this.eq.put(frequency, gain);
    }
    @Override
    public byte[] process(byte[] input) {
        for(Double frequency : eq.keySet()) {
            double gain = eq.get(frequency);
            input = applyEqualizer(input, frequency, gain);
        }
        return input;
    }
    private byte[] applyEqualizer(byte[] audioData, double frequency, double gain) {
        int sampleSizeInBytes = audioFormat.getSampleSizeInBits() / 8; // 采样数据的字节数（假设是16位PCM）
        int channels = audioFormat.getChannels(); // 声道数量（假设是立体声）

        int bytesPerSample = sampleSizeInBytes * channels;
        double omega = 2.0 * Math.PI * frequency / 44100.0; // 假设采样率为44.1kHz

        double prevGain = prevSample.getOrDefault(frequency, 0.0);
        double smoothedGain = prevGain + (gain - prevGain) * smoothingFactor;

        for (int i = 0; i < audioData.length; i += bytesPerSample) {
            short sampleValue = (short) ((audioData[i + 1] << 8) | (audioData[i] & 0xFF));
            double amplifiedSampleValue = sampleValue * Math.pow(10.0, smoothedGain / 20.0);
            short processedSampleValue = (short) Math.round(amplifiedSampleValue);
            audioData[i] = (byte) processedSampleValue;
            audioData[i + 1] = (byte) (processedSampleValue >> 8);
        }

        prevSample.put(frequency, smoothedGain);

        return audioData;
    }
}

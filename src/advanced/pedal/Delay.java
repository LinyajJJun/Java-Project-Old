package advanced.pedal;

import javax.sound.sampled.AudioFormat;

//import javax.sound.sampled.AudioFormat;
//public class advanced.pedal.Delay extends advanced.pedal.Pedal{
//    private byte[] delayBuffer;
//    private int delayBufferSize;
//    private int delayPosition;
//    private float feedback;
//    private int delaySamples;
//    private int delayOffset;
//
//    private AudioFormat format;
//    public advanced.pedal.Delay(AudioFormat format) {
//        this.format = format;
//    }
//    public void setDelay(int delayMillis, float feedback){
//        delaySamples = (int) (delayMillis * format.getSampleRate() / 1000);
//        // 初始化延遲緩衝區
//        delayBufferSize = delaySamples * format.getFrameSize();
//        delayBuffer = new byte[delayBufferSize];
//        delayPosition = 0;
//        this.feedback = feedback;
//    }
//    public byte[] process(byte[] input) {
//        System.out.println("advanced.pedal.Delay");
//        byte[] output = new byte[input.length];
//
//        for (int i = 0; i < input.length; i++) {
//            // 將輸入數據複製到輸出
//            output[i] = input[i];
//
//            // 計算延遲緩衝區中的位置
//            int delayIndex = (delayPosition + i) % delayBufferSize;
//
//            // 將輸入數據與延遲緩衝區中的數據相加，並乘以反饋係數
//            output[i] += (byte) (delayBuffer[delayIndex] * feedback);
//
//            // 將輸入數據存儲到延遲緩衝區
//            delayBuffer[delayIndex] = output[i];
//        }
//
//        // 更新延遲緩衝區的位置
//        delayPosition = (delayPosition + input.length) % delayBufferSize;
//
//        return output;
//    }
//}
public class Delay extends Pedal {
    private byte[] delayBuffer;
    private int delayBufferSize;
    private int delayPosition;
    private float feedback;
    private int delaySamples;
    private AudioFormat format;
    public Delay(AudioFormat format) {
        this.format = format;
    }

    public void setDelay(int delayMillis, float feedback){
        // 計算延遲的樣本數
        delaySamples = (int) (delayMillis * format.getSampleRate() / 1000);
        // 初始化延遲緩衝區
        delayBufferSize = delaySamples * format.getFrameSize();
        delayBuffer = new byte[delayBufferSize];
        System.out.println("delayBufferSize: " + delayBufferSize + " delaySamples: " + delaySamples);
        delayPosition = 0;
        this.feedback = feedback;
    }

    public byte[] process(byte[] input) {
        if(delayBufferSize == 0) return input;
        byte[] output = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            // 將輸入數據複製到輸出
            output[i] = input[i];

            // 計算延遲緩衝區中的位置
            int delayIndex = (delayPosition + i) % delayBufferSize;

            // 將輸入數據與延遲緩衝區中的數據相加，並乘以反饋係數
            output[i] += (byte) (delayBuffer[delayIndex] * feedback);

            // 將輸入數據存儲到延遲緩衝區
            delayBuffer[delayIndex] = output[i];
        }

        // 更新延遲緩衝區的位置
        delayPosition = (delayPosition + input.length) % delayBufferSize;

        return output;
    }
}

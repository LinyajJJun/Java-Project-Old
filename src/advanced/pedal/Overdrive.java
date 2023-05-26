package advanced.pedal;

public class Overdrive extends Pedal{
    private float gain;
    private float threshold;
    public void setGain(float gain, float threshold) {
        this.gain = gain;
        this.threshold = threshold;
    }
    public byte[] process(byte[] input) {
        // 執行聲音效果處理，例如過載效果
        // 在這裡實現您所需的效果處理邏輯
        // 以下僅為示例，將增益值乘以輸入數據
        for (int i = 0; i < input.length; i++) {
            input[i] *= gain;
            // 裁切波形为方波
            if (input[i] >= threshold) {
                input[i] = (byte)threshold;
            } else if (input[i] <= -threshold) {
                input[i] = (byte)-threshold;
            }
        }
        return input;
    }
}

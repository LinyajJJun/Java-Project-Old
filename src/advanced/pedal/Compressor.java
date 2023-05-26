package advanced.pedal;

public class Compressor extends Pedal{
    private float level;
    public void setLevel(float level){
        this.level = level;
    }
    public byte[] process(byte[] input){
        for (int i = 0; i < input.length; i++) {
            if(input[i] >= level){
                input[i] = (byte)level;
            }else if(input[i] <= -level){
                input[i] = (byte)-level;
            }
        }
        return input;
    }
}

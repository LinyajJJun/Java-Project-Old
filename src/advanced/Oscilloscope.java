package advanced;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.devices.AudioDeviceManager;
import com.jsyn.scope.AudioScope;
import com.jsyn.swing.JAppletFrame;
import com.jsyn.unitgen.LineIn;
import com.jsyn.unitgen.LineOut;
import java.awt.BorderLayout;
import javax.swing.JApplet;
import javax.swing.JLabel;

public class Oscilloscope extends JApplet{
    Synthesizer synth;
    LineIn lineIn;
    LineOut lineOut;
    private AudioScope scope;
    private void gui(){
        System.out.println("run GUI");
        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, new JLabel("ShowWaves in an AudioScope Mod001"));
        scope = new AudioScope(synth);
        scope.setViewMode(AudioScope.ViewMode.WAVEFORM);
        scope.addProbe(lineIn.output);
        scope.start();
        add(BorderLayout.CENTER, scope.getView());
        validate();
    }
    @Override
    public void start() {
        synth = JSyn.createSynthesizer();
        synth.add(lineIn = new LineIn());
        synth.add(lineOut = new LineOut());
        AudioScope audioScope = new AudioScope(synth);
        lineIn.output.connect(0, lineOut.input, 0);
        lineIn.output.connect(1, lineOut.input, 1);
        audioScope.addProbe(lineIn.output);
        int numInputChannels = 2;
        int numOutputChannels = 2;
        synth.start(48000, AudioDeviceManager.USE_DEFAULT_DEVICE, numInputChannels,
                AudioDeviceManager.USE_DEFAULT_DEVICE, numOutputChannels);
        lineOut.start();
        System.out.println("Audio passthrough started.");
        gui();
        scope.setTriggerMode(AudioScope.TriggerMode.NORMAL);
        audioScope.start();
        lineOut.start();
        scope.getView().setControlsVisible(true);
        add(BorderLayout.CENTER, scope.getView());
        try {
            double time = synth.getCurrentTime();
            synth.sleepUntil(time + 400.0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synth.stop();
        System.out.println("All done.");
    }
    public static void main(String[] args) {
        Oscilloscope applet = new Oscilloscope();
        JAppletFrame frame = new JAppletFrame("ShowWaves", applet);
        frame.setSize(640, 300);
        frame.setVisible(true);
        frame.test();
    }
}

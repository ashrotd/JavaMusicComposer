import javax.sound.midi.*;
import javax.swing.JPanel;
import java.util.Random;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import static javax.sound.midi.ShortMessage.*;

public class MusicAnimation{
    public static void main(String[] args){
        MusicAnimation anime = new MusicAnimation();
        anime.go();
    }

    public void go(){
        try{
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();

            int[] eventsIwant = {127};
            sequencer.addControllerEventListener(event->System.out.println("la"), eventsIwant);

            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            for (int i = 5; i < 61; i+=4) {
                track.add(makeEvent(NOTE_ON, 1, i, 100, i));
                track.add(makeEvent(CONTROL_CHANGE, 1, 127, 0, i));
                track.add(makeEvent(NOTE_OFF, 1, i, 100, i+2));
            }
            sequencer.setSequence(seq);
            sequencer.setTempoInBPM(220);
            sequencer.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static MidiEvent makeEvent(int cmd, int chnl, int one, int two, int tick){
        MidiEvent event = null;
        try{
            ShortMessage msg = new ShortMessage();
            msg.setMessage(cmd, chnl, one, two);
            event = new MidiEvent(msg, tick);
        }catch (Exception e){
            e.printStackTrace();
        }
        return event;
    }

    class myDrawPanel extends JPanel implements ControllerEventListener{
        private boolean msg = false;
        public void controlChange(ShortMessage event){
            msg = true;
            repaint();
        }
        public void paintComponent(Graphics g){
            if(msg){
                Random random = new Random();
                int r = random.nextInt(250);
                int gr = random.nextInt(250);
                int b = random.nextInt(250);

                g.setColor(new Color(r, gr, b));

                int height = random.nextInt(120)+10;
                int width = random.nextInt(120)+10;

                int x_pos = random.nextInt(40)+10;
                int y_pos = random.nextInt(40)+10;

                g.fillRect(x_pos, y_pos, width, height);
                msg = false;
            }
        }
    }
}
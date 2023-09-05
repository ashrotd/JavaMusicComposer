import javax.sound.midi.*;
import static javax.sound.midi.ShortMessage.*;
import java.util.Random;

public class MusicApp{
    public static void main(String[] args){
        MusicApp mini = new MusicApp();
        if (args.length<2){
            System.out.println("Please Donot forget to pass the instrument and Notes");
        }
        else{
            int instrument = Integer.parseInt(args[0]);
            int notes = Integer.parseInt(args[1]);
            mini.play(instrument, notes);
        }
        
    }
    public void play(int instrument,int note){
        try{
            Sequencer player = MidiSystem.getSequencer();
            player.open();

            Sequence seq  = new Sequence(Sequence.PPQ,4);

            Track track = seq.createTrack();

            ShortMessage msg1 = new ShortMessage();
            msg1.setMessage(PROGRAM_CHANGE,1, instrument,0);
            MidiEvent changInstrument = new MidiEvent(msg1, 1);
            track.add(changInstrument);

            ShortMessage msg2 = new ShortMessage();
            msg2.setMessage(NOTE_ON,1,note,100);
            MidiEvent noteOn = new MidiEvent(msg2, 2);
            track.add(noteOn);

            ShortMessage msg3 = new ShortMessage();
            msg3.setMessage(NOTE_OFF,1, note,100);
            MidiEvent noteOff = new MidiEvent(msg3, 16);
            track.add(noteOff);

            player.setSequence(seq);

            player.start();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}


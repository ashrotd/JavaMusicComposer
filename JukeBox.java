import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import static javax.sound.midi.ShortMessage.*;

public class JukeBox {
    private ArrayList<JCheckBox> checkboxes;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

    String[] instrumentNames = {"Bass Drum",
    "Closed Hi-Hat","Open Hi-Hat",
    "Acoustic Snare","Crash Cymbal","Hand Clap","High Tom",
    "High Bongo","Marcas","Whistle","Low Conga",
    "Cowbell","Vibraslap","Low-mid Tom","High Agogo","Open Hi Conga"
    };
    int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
    public static void main(String[] args){
        new JukeBox().buildGUI();
    }
    public void buildGUI(){
        JFrame frame = new JFrame("Juke Box Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(e->buildAndStartTrack());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(e->sequencer.stop());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo UP");
        upTempo.addActionListener(e->changeTempo(1.03f));
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(e->changeTempo(0.97f));
        buttonBox.add(downTempo);

        // Creating Labels of the Instruments on the left side of the screen
        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (String instruments:instrumentNames){
            JLabel instrumentLabel = new JLabel(instruments);
            instrumentLabel.setBorder(BorderFactory.createEmptyBorder(4, 1, 4, 1));
            nameBox.add(instrumentLabel);
        }

        background.add(BorderLayout.EAST,buttonBox);
        background.add(BorderLayout.WEST, nameBox);
        frame.getContentPane().add(background);

        // Now make Grid of Checkboxes for the choices
        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);

        JPanel mainPanel = new JPanel(grid);
        
        checkboxes = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxes.add(c);
            mainPanel.add(c);
        }
        background.add(BorderLayout.CENTER,mainPanel);
        
        // Setup MIDI Sequencer by calling the function
        setupMidi();

        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }
    private void setupMidi(){
        try{
            sequencer =  MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ,4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static MidiEvent makEvents(int cmd,int chnl,int one,int two,int tick){
        MidiEvent event = null;
        try{
            ShortMessage msg = new ShortMessage();
            msg.setMessage(cmd,chnl,one,two);
            event = new MidiEvent(msg,tick);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Shit!");
        }
        return event;
    }
    private void makeTracks(int[] list){
        for (int i=0;i<16;i++){
            int key = list[i];
            
            if(key!=0){
                track.add(makEvents(NOTE_ON,9,key,100,i));
                track.add(makEvents(NOTE_OFF, 9,key, 100, i+1));
            }
        }
    }
    public void buildAndStartTrack(){
        int[] tracklist;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0;i<16;i++){
            tracklist = new int[16];
            int key = instruments[i];
            for(int j=0;j<16;j++){
                JCheckBox jc = checkboxes.get(j+16*i);
                if(jc.isSelected()){
                    tracklist[j] = key;
                }
                else{
                    tracklist[j] = 0;
                }
            }
            makeTracks(tracklist);
            track.add(makEvents(CONTROL_CHANGE, 1, 127, 0, 16));
        }
        track.add(makEvents(PROGRAM_CHANGE, 9, 1, 0, 15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.setTempoInBPM(120);
            sequencer.start();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Shitty!");
        }
    }
    private void changeTempo(float tempoMultiplier){
        float tempoFactor = sequencer.getTempoFactor();
        sequencer.setTempoFactor(tempoFactor*tempoMultiplier);
    }
}

package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static data.Options.*;

public class Options extends JDialog implements ItemListener, ActionListener, 
    ChangeListener{
 
    private static final long serialVersionUID = 1L;
    private final JCheckBox symbAsCharBox = new JCheckBox();
    private final JSlider bufferSize;
    private final JTextArea bufferDisplay;
    private final JComboBox<Byte>  simbSizeList;
    private final JPanel panel = new JPanel();
    private final JSlider steps;
    private final JTextArea stepsDisplay;
    
    public Options(JFrame parentFrame){
        super(parentFrame, "Options");
        setSize( 310, 200 );
        this.setResizable(false);
        setVisible( false ); 
        super.setLocationRelativeTo(parentFrame);
        bufferDisplay = new JTextArea(1, 1);
        bufferSize = new JSlider(JSlider.HORIZONTAL,
                1, 1024, 16);
        stepsDisplay = new JTextArea(1, 1);
        steps = new JSlider(JSlider.HORIZONTAL,
                1, 1000, optRecreateTreeSteps);
        
        bufferSize.addChangeListener(this); 
        bufferSize.setMajorTickSpacing(512);
        bufferSize.setMinorTickSpacing(2);
        bufferDisplay.setText(optBufferSize/1024 + " KB");
        bufferDisplay.setEditable(false);
        
        steps.addChangeListener(this);  
        stepsDisplay.setText(optRecreateTreeSteps + "");
        stepsDisplay.setEditable(false);
        
        Byte[] sizeOptions = new Byte[15];
        for (byte i = 0; i < 15; i++){
            sizeOptions[i] = (byte)(i + 2);
        }
        simbSizeList = new JComboBox<>(sizeOptions);
        simbSizeList.setSelectedIndex(6);
        simbSizeList.addActionListener(this);
        symbAsCharBox.addItemListener(this);
        panel.add(new JLabel("Buffer:"));
        panel.add(bufferSize);
        panel.add(bufferDisplay);
        panel.add(new JLabel("Symbol size in bits"));
        panel.add(simbSizeList);
        panel.add(new JLabel("Display symbol bits as ascii chars"));
        panel.add(symbAsCharBox);
        panel.add(new JLabel("Huffman tree reconstruction interval:"));
        panel.add(steps);
        panel.add(stepsDisplay);

        this.addWindowFocusListener(new WindowFocusListener() 
        {

            @Override
            public void windowGainedFocus(WindowEvent e) {
                //do nothing
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                setVisible(false);
            }

        });
        add(panel);
        setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
         
        if (source == symbAsCharBox) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                optSymbAsChar = false;
            }
            else if (e.getStateChange() == ItemEvent.SELECTED){ 
                optSymbAsChar = true;
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (source == bufferSize){
            if (!source.getValueIsAdjusting()) {
                int bufSize = (int)source.getValue();
                optBufferSize = bufSize*1024;
                bufferDisplay.setText(optBufferSize / 1024 + " KB");
            }
        }
        else if (source == steps){
            optRecreateTreeSteps = (int)source.getValue();
            stepsDisplay.setText(optRecreateTreeSteps + "");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (simbSizeList == e.getSource()){
            Byte selected = (Byte) simbSizeList.getSelectedItem();
            symbSize = selected;
        }
    }
}

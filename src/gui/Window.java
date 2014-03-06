package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea; 
import javax.swing.BorderFactory;

import compressor.Fan;
import compressor.Huffman;
import compressor.actions.BinaryAction; 
import compressor.actions.CounterAction;
import decompressor.Decompressor;
import file.FileManager;
import static data.Data.*;

public class Window extends JFrame implements ActionListener{

    private static final long serialVersionUID = 1L;
    public static final String newline = "\n";
    //Panels
    JPanel panel = new JPanel(new BorderLayout());
    JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    public static JTextArea viewer = new JTextArea(0, 5);
    JFileChooser fc = new JFileChooser();
    public Fan fanCompressor; 
    Decompressor decompressor; 
    long originalFileLength = 0;
    private boolean algorithm;
    //Buttons
    JButton browseButton = new JButton("Browse...");
    JButton binaryButton = new JButton("Generate binary");
    JButton treeButton = new JButton("Show Code Tree");
    JButton frequenciesButton = new JButton("Show frequencies");
    JButton fanButton = new JButton("Fan compress");
    JButton decompressButton = new JButton("Decompress");
    JButton huffmanButton = new JButton("Huffman compress");
    JButton optionButton;
    List<JButton> buttons = new ArrayList<>();
    //Objects
    Options options = new Options(this);
    FileManager fileManager;
    
    public Window(){
        super( "Compressor" );
        //Initialize buttons
        ImageIcon opt = new ImageIcon(getClass().getResource("/resources/options-icon.png"));
        optionButton = new JButton(opt);
        frequenciesButton.setEnabled(false);
        binaryButton.setEnabled(false);
        fanButton.setEnabled(false);
        decompressButton.setEnabled(false);
        treeButton.setEnabled(false);
        huffmanButton.setEnabled(false);
        //Add buttons
        buttons.add(optionButton);
        buttons.add(binaryButton);
        buttons.add(frequenciesButton);
        buttons.add(browseButton);
        buttons.add(fanButton);
        buttons.add(huffmanButton);
        buttons.add(decompressButton);
        treeButton.addActionListener(this);
        for (JButton b : buttons){
            b.addActionListener(this);
        }
        //Initialize panels
        viewer.setEditable(false); 
        viewer.setLineWrap(true); 
        viewer.setWrapStyleWord(true);
        JScrollPane scrollArea = new JScrollPane (viewer);
        scrollArea.setBorder(BorderFactory.createLineBorder(Color.black));
        scrollArea.setWheelScrollingEnabled(true); 
        scrollArea.setPreferredSize(new Dimension(250, 145));
        scrollArea.setMinimumSize(new Dimension(100, 100));
        panel.add(scrollArea, BorderLayout.NORTH);  
        innerPanel.add(browseButton);
        innerPanel.add(frequenciesButton);
        innerPanel.add(binaryButton);
        innerPanel.add(treeButton);
        innerPanel.add(fanButton);
        innerPanel.add(decompressButton);
        innerPanel.add(huffmanButton);
        lowerPanel.add(optionButton);
        lowerPanel.setBorder(BorderFactory.createMatteBorder(
                1, 5, 1, 1, Color.darkGray));
        panel.add(innerPanel, BorderLayout.CENTER);
        panel.add(lowerPanel, BorderLayout.SOUTH);
    } // end Window constructor 
    
    public  void display(){
        add(panel, BorderLayout.CENTER);
        setSize( 400, 500 ); // set size of window
        this.setLocation(350, 50);
        setVisible( true ); // show window
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    private void swichButtons(boolean state){
        for (JButton b : buttons){
            b.setEnabled(state);
        }
    }
    
    private void countFrequencies(File file){ 
        symbols.clear();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        swichButtons(false);
        fileManager = new FileManager(file);
        fileManager.fileActionBuffered(new CounterAction(file.length()));
        this.setCursor(Cursor.getDefaultCursor());
        swichButtons(true);
        decompressButton.setEnabled(false); 
    }
    
    private void compressFan(){ 
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        fanCompressor = new Fan(); 
        swichButtons(false);
        System.out.println("Original file length:" + fileManager.file.length());
        originalFileLength = fileManager.file.length();
        fanCompressor.compress(fileManager);
        this.setCursor(Cursor.getDefaultCursor());
        swichButtons(true);
        decompressButton.setEnabled(false);
        treeButton.setEnabled(true);
        viewer.setCaretPosition(0);
    }
    
    private void decompress(){ 
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); 
        swichButtons(false);
        try{
            decompressor = new Decompressor();
            if (algorithm){
                decompressor.fanDecompress(fileManager);
            }
            else{
                decompressor.huffmanDecompress(fileManager);
            }
        }
        catch(NullPointerException e){
            viewer.append("Fano medis nerastas!" + newline);
            System.err.println("Fano medis nerastas!");
            throw e;
        } 
        this.setCursor(Cursor.getDefaultCursor());
        swichButtons(true);
        frequenciesButton.setEnabled(false);
        fanButton.setEnabled(false);
        huffmanButton.setEnabled(false);
        viewer.setCaretPosition(0);
    }
    
    private void compressHuffman(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        fanCompressor = new Huffman(); 
        swichButtons(false);
        System.out.println("Original file length:" + fileManager.file.length());
        originalFileLength = fileManager.file.length();
        fanCompressor.compress(fileManager);
        this.setCursor(Cursor.getDefaultCursor());
        swichButtons(true);
        decompressButton.setEnabled(false);
        treeButton.setEnabled(true); 
        viewer.setCaretPosition(0);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //Handle open button action.
        if (e.getSource() == browseButton) {
            int returnVal = fc.showOpenDialog(Window.this); 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = fc.getSelectedFile();
                fileManager = new FileManager(file);
                viewer.setText("");
                if ((!file.getName().contains(".fan")) && (!file.getName().contains(".huf"))){  
                    viewer.append("Opened file: " + file.getName() + newline + "Size: " + file.length() + "." + newline);   
                    fanButton.setEnabled(true);
                    huffmanButton.setEnabled(true);
                    frequenciesButton.setEnabled(true); 
                }
                else{ 
                    viewer.append("Opened compressed file: " + file.getName() + newline + "Size: " + file.length() + "." + newline);
                    frequenciesButton.setEnabled(false); 
                    decompressButton.setEnabled(true);
                    fanButton.setEnabled(false);
                    huffmanButton.setEnabled(false);
                    algorithm = file.getName().contains(".fan");
                }
                binaryButton.setEnabled(true);
                treeButton.setEnabled(false);
            } else {
                viewer.append("Open command cancelled by user." + newline);
            } 
            viewer.setCaretPosition(viewer.getDocument().getLength());
        
        }
        else if(e.getSource() == binaryButton){
            int n = 0;
            long fSize = fileManager.file.length();
            if (fSize > 16384){
                Object[] choice = {"Ok, proceed",
                        "Cancel"};
                n = JOptionPane.showOptionDialog(this,
                    "Are you sure you would like to start this process?" + newline
                    + "It may take very long time" + newline
                    + "(File size is " + fSize + " bytes).", "Confirm action",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    choice,
                    choice[1]);
            }
            if(n == 0){ 
                Runnable readFileProcess = new Runnable() {
                    @Override
                    public void run() {
                        swichButtons(false);
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        fileManager.fileActionBuffered(new BinaryAction(fileManager.file.length())); 
                        setCursor(Cursor.getDefaultCursor());
                        swichButtons(true);
                        if (!fileManager.file.getName().contains(".fan")){
                            decompressButton.setEnabled(false);
                        }
                        else{
                            frequenciesButton.setEnabled(false);
                            fanButton.setEnabled(false);
                            huffmanButton.setEnabled(false);
                        }
                    }
                };
                new Thread(readFileProcess).start(); 
            }
        }
        else if(e.getSource() == frequenciesButton){
            countFrequencies(fileManager.file);
            Window.viewer.setText("Frequences are:" + Window.newline);
            viewer.append(frequencies());
            viewer.setCaretPosition(0);
        }
        else if(e.getSource() == optionButton){
            options.setVisible(true);
        }
        else if(e.getSource() == treeButton){
            viewer.setText("Fan Tree:" + newline);
            viewer.append(fanCompressor.getTree());
        }
        else if(e.getSource() == fanButton){  
            algorithm = true;
            Runnable compressFileProcess = new Runnable() {
                @Override
                public void run() {
                    countFrequencies(fileManager.file);
                    compressFan();
                }
            };
            new Thread(compressFileProcess).start(); 
        }
        else if(e.getSource() == decompressButton){ 
            
            Runnable compressFileProcess = new Runnable() {
                @Override
                public void run() {
                    decompress();
                } 
            };
            new Thread(compressFileProcess).start(); 
        }
        else if(e.getSource() == huffmanButton){
            algorithm = false;
            Runnable compressFileProcess = new Runnable() {
                @Override
                public void run() {
                    compressHuffman();
                }
            };
            new Thread(compressFileProcess).start(); 
        }
    }
}

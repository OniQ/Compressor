package compressor.actions;
 
import static data.Options.symbSize;

import java.util.BitSet;

import file.FileManager;
import gui.Window;

public class BinaryAction extends CounterAction{ 
     
    FileManager fileM = new FileManager();
    BitSet binary = new BitSet();
    int processedBitsNumber = 0;
    
    public BinaryAction(long fileLength) {
        super(fileLength);
    }
    
    @Override
    public void perform(byte[] bytes){
        if (bytes != null){
            BitSet bits = BitSet.valueOf(bytes);
            int bitsNumber = bytes.length*8;
            for (int j = 0; j < bitsNumber; j++){
                binary.set(processedBitsNumber, bits.get(j));
                processedBitsNumber++;
            } 
        }
        else{
            String byteString = "";
            for (int j = 0; j < length*8; j ++){
                if (((j % symbSize)==0) && (j != 0))
                    byteString = byteString.concat(" ");
                if (binary.get(j)){
                    byteString = byteString.concat("1");
                }
                else{
                    byteString = byteString.concat("0");
                }
            }
            fileM.writeToTempFile(byteString);
            Window.viewer.setText("Done! Result is in binary.txt located in the same directory" + Window.newline);
        }
    }
}

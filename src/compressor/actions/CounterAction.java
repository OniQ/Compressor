package compressor.actions;

import gui.Window;
import static data.Data.*;
import static data.Options.*;

import java.util.BitSet;

public class CounterAction implements Action {
    protected int count;
    protected long length;
    protected int gotBytes;
    protected long gotBits;
    protected int gotSymbols;
    protected int progress;
    protected BitSet lastBits;
    protected int remainBits = 0;
    protected int setBits = 0;
    protected boolean header = false;
    
    public CounterAction(long fileLength){
        count = 0;
        gotBytes = 0;
        progress = 0;
        length = fileLength;
        gotBits = 0;
    } 
    
    protected void showProgress(){
        double proc = ((double)gotSymbols / (length*8/symbSize)) * 100;
        if (proc > progress){
            progress++;
            Window.viewer.setText("Progress: " + progress + "% (" + gotSymbols + " of " + length*8/symbSize + " symbols)\n");
        }
    } 
    
    protected BitSet processHeader(BitSet bits){
        return null;
    }
    
    protected void processData(BitSet b){
        if (b != null){
            if (symbols.containsKey(b)){
                count = symbols.get(b) + 1;
            }
            else{
                count = 1;
            }
            symbols.put(b, count);
            gotSymbols++;
        }
    }
    
    protected void processLast(BitSet lastBits, int length){
        
    }
    
    @Override
    public void perform(byte[] bytes) { 
        if ((bytes != null) && (bytes.length != 0)){
            BitSet bits = BitSet.valueOf(bytes); 
            if (header){
                processHeader(bits);
            } 
            int bitsNumber = bytes.length*8;
            gotBits += bitsNumber; 
            BitSet simbol = new BitSet(symbSize);
            
            int firstBit = 0;
            if (remainBits != 0){
                if ((setBits + remainBits) != symbSize){
                    System.err.println("Sth wrong with algorithm");
                }
                for (firstBit = 0; firstBit < remainBits; firstBit++){
                    lastBits.set(firstBit + setBits, bits.get(firstBit));
                }
                processData(lastBits);
                remainBits = 0;
                setBits = 0;
            }
            for (int i = firstBit; i < bitsNumber; i += symbSize){
                if ((i+symbSize) >= bitsNumber){
                    lastBits = bits.get(i, i+symbSize);
                    setBits = (bitsNumber - i);
                    remainBits = symbSize - setBits;
                    if (remainBits == 0){
                        processData(lastBits);
                        lastBits = null;
                        showProgress();
                    }
                    else{
                        lastBits = bits.get(i, i+setBits);
                    }
                }
                else{
                    simbol = bits.get(i, i+symbSize);
                    processData(simbol);
                    showProgress();
                }
            }
        }
        else{ 
            long symbNumber = length*8/symbSize;
            if (lastBits != null){
                processLast(lastBits, setBits);//last symbol(part)
                symbNumber++;
            }
            Window.viewer.setText("Progress: " + 100 + "% (" + gotSymbols + " of " + symbNumber + " symbols)\n");
            Window.viewer.append("File size: " + length + Window.newline);
            processData(null);
        }
    }

}

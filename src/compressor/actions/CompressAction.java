package compressor.actions;
 
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import compressor.CodeStruct;
import data.Data;
import static data.Options.*; 
import file.FileManager;
import gui.Window;
import static file.FileManager.toBitSetLen;
import static data.Data.*;

public class CompressAction extends CounterAction {
    protected FileManager fileM;
    protected BitSet compressedData = new BitSet(optBufferSize);
    protected int processedBitsNumber = 0; 
    protected int writtenBytes = 0;
    public LinkedHashMap<BitSet, CodeStruct> tree;
    
    public CompressAction(long fileLength){
        super(fileLength);
        header = true;
    }
    
    public CompressAction(long fileLength, String fileName, LinkedHashMap<BitSet, CodeStruct> tree) {
        this(fileLength);
        this.tree = tree;
        if (fileName.contains(".")){
            int pos = fileName.indexOf(".");
            fileName = fileName.substring(0, pos);
            fileName = fileName.concat(".fan");
        }
        else{
            fileName = fileName + ".fan";
        } 
        fileM = new FileManager(fileName); 
    }
    

    protected void setLenght(long len){
        length = len;
    }
    
    protected void flushBuffer(){
        if (processedBitsNumber >= 8*optBufferSize){
            BitSet bytes = compressedData.get(0, 8*optBufferSize);
            compressedData = compressedData.get(8*optBufferSize, processedBitsNumber);
            byte byteArray[] = new byte[optBufferSize]; 
            byteArray = Arrays.copyOf(bytes.toByteArray(), optBufferSize);
            writtenBytes += optBufferSize;
            fileM.fileOutputBuffered(byteArray);
            processedBitsNumber = processedBitsNumber - 8*optBufferSize;
        }  
    }
    
    @Override
    protected BitSet processHeader(BitSet bits){
        BitSet headerBits = new BitSet();
        int offset = 0;
        if (!symbols_sorted.isEmpty()){
             Iterator<Entry<BitSet, Integer>> it = symbols_sorted.entrySet().iterator(); 
             long maxSize = it.next().getValue();
             long maxValue = 1;
             int bitCount;
             for (bitCount = 1; bitCount <= 32; bitCount++){
                 maxValue *= 2;
                 if (maxSize < maxValue){
                     break;
                 }
             } 
             toBitSetLen(headerBits, bitCount, 0, 6);
             offset += 6; 
             
             toBitSetLen(headerBits, symbSize, offset, 5);
             offset += 5; 
             it = symbols_sorted.entrySet().iterator();
             int freq;
             BitSet symb;
             Entry<BitSet, Integer> pairs;
             while (it.hasNext()) {
                   pairs = it.next();
                   freq = pairs.getValue();
                   symb = pairs.getKey();
                   
                toBitSetLen(headerBits, freq, offset, bitCount);
                offset += bitCount;
                
                
                for (int i = 0; i < symbSize; i++){
                     headerBits.set(i + offset, symb.get(i));  
                }
                offset += symbSize;
              }
             freq = 0;//end of header
             toBitSetLen(headerBits, freq, offset, bitCount);
             offset += bitCount;
        }
        
        int size;
        if ((offset % 8) != 0){
            size = offset/8 + 1;
        }
        else{
            size = offset/8;
        }
        byte byteArray[] = new byte[size];
        byteArray = Arrays.copyOf(headerBits.toByteArray(), size);
        fileM.fileOutputBuffered(byteArray);
        if (size < 1024*5)
            System.out.println("Header Size is " + size + " bytes");
        else
            System.out.println("Header Size is " + size/1024 + " KB");
        header = false;
        return null;
    }
    
    @Override
    protected void processLast(BitSet lastBits, int length){
        for (int i = 0; i < length; i++){
            compressedData.set(processedBitsNumber, lastBits.get(i));
            processedBitsNumber++;
        }
    }
    
    @Override
    protected void processData(BitSet b){
        if (b != null){
            CodeStruct code = tree.get(b);
            int codesize = code.length;
            for (int i = 0; i < codesize; i++){
                compressedData.set(processedBitsNumber, code.code.get(i));
                processedBitsNumber++;
            }
            gotSymbols++;
            flushBuffer(); 
        }
        else{
            int size;
            if (((processedBitsNumber % 8) != 0) && (processedBitsNumber/8*8 < processedBitsNumber)){
                size = processedBitsNumber/8 + 1;
            }
            else{
                size = processedBitsNumber/8;
            }
            writtenBytes = size;
            byte byteArray[] = new byte[size];
            byteArray = Arrays.copyOf(compressedData.toByteArray(), size);
            fileM.fileOutputBuffered(byteArray);
            Data.symbolCount = gotSymbols;
            Window.viewer.append("Compressed file size:");
            fileM.fileOutputBuffered(null);
        }
    }
}

package compressor.actions;

import static data.Data.*; 
import static data.Options.optSymbAsChar;
import static data.Options.symbSize; 
import static data.Options.optRecreateTreeSteps;
import static file.FileManager.bitSetToChar;
import static file.FileManager.bitSetToString;
import static file.FileManager.toBitSetLen;

import java.util.Arrays;
import java.util.BitSet; 
import java.util.Iterator;
import java.util.LinkedHashMap; 
import java.util.Map.Entry;

import compressor.CodeStruct;  
import file.FileManager;
import static compressor.HuffmanTreeCreator.*;

public class HuffmanCompressAction extends CompressAction {
    
    private int counter;
    private long len;

    public HuffmanCompressAction( LinkedHashMap<BitSet, CodeStruct> tree) {
        super(0);
        initTree(); 
        this.tree = createTree();
        counter = 0;
    }
    
    public HuffmanCompressAction(long fileLength, String fileName,
            LinkedHashMap<BitSet, CodeStruct> tree) {
        super(fileLength);
        len = fileLength;
        header = true;
        this.tree = tree;
        if (fileName.contains(".")){
            int pos = fileName.indexOf(".");
            fileName = fileName.substring(0, pos);
            fileName = fileName.concat(".huf");
        }
        else{
            fileName = fileName + ".huf";
        } 
        fileM = new FileManager(fileName); 
    }
    
    @Override
    protected BitSet processHeader(BitSet bits){
        BitSet headerBits = new BitSet();
        int offset = 0;
        toBitSetLen(headerBits, symbSize, offset, 5);
        offset += 5;
        toBitSetLen(headerBits, optRecreateTreeSteps, offset, 10);
        offset += 10;
        symbolCount = len*8 / symbSize;
        toBitSetLen(headerBits, symbolCount, offset, 32);
        offset += 32;
        int size = 6;
        byte byteArray[] = new byte[size];
        byteArray = Arrays.copyOf(headerBits.toByteArray(), size);
        fileM.fileOutputBuffered(byteArray); 
        header = false;
        return null;
    }
    
    @Override
    protected void processData(BitSet b){
        counter++;
        super.processData(b);
        if (b != null){
            symbols.put(b, symbols.get(b).intValue() + 1);
            if (counter % optRecreateTreeSteps == 0){
                symbols_sorted.clear();
                symbols_sorted.putAll(symbols);
                tree = createTree(); 
            }
        }
    }
    
    public String getTree(){
         String result = ""; 
         Iterator<Entry<BitSet, CodeStruct>> it = tree.entrySet().iterator();
         while (it.hasNext()) {
            Entry<BitSet, CodeStruct> pairs = it.next();
            String symb;
            if (optSymbAsChar){
                symb = String.valueOf(bitSetToChar(pairs.getKey(), symbSize));
            }
            else{
                symb = bitSetToString(pairs.getKey(), symbSize);
            } 
            String s = String.format("%s ---> %s\n", symb, bitSetToString(pairs.getValue().code, pairs.getValue().length));
            result += s;
         }
         return result + "\n";
    }

}

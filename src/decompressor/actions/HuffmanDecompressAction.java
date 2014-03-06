package decompressor.actions;

import static data.Data.symbolCount;
import static data.Data.symbols;
import static data.Data.symbols_sorted;
import static data.Options.symbSize;
import static data.Options.optRecreateTreeSteps;
import static file.FileManager.fromBitSetLen;
import static compressor.HuffmanTreeCreator.*;

import java.io.File;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import compressor.CodeStruct;
import compressor.HuffmanTreeCreator;

public class HuffmanDecompressAction extends DecompressAction {

    private LinkedHashMap<BitSet, CodeStruct> hTree;
    private int counter;
    
    public HuffmanDecompressAction(long compressedFileLength, File file,
            boolean algorithm) {
        super(compressedFileLength, file, algorithm); 
        counter = 0;
    }
    
    private void createTree(LinkedHashMap<BitSet, CodeStruct> tree){
        LinkedHashMap<BitSet, CodeStruct> defanTree = new LinkedHashMap<>();
        Iterator<Entry<BitSet, CodeStruct>> it = tree.entrySet().iterator();
        while(it.hasNext()){
            Entry<BitSet, CodeStruct> pairs = it.next();
            defanTree.put(pairs.getValue().code, new CodeStruct(pairs.getKey(), pairs.getValue().length)); 
        }
        this.tree = defanTree;
    } 
    
    @Override
    protected BitSet processHeader(BitSet bits){
        int simbolSize = fromBitSetLen(bits, 0, 5);
        symbSize = (byte) simbolSize;
        int steps = fromBitSetLen(bits, 5, 10);
        optRecreateTreeSteps = steps;
        long symbols = fromBitSetLen(bits, 15, 32); 
        symbolCount = symbols;
        bits = bits.get(6*8, bitsNumber);
        bitsNumber -= 6*8;
        header = false;
        initTree(); 
        hTree = HuffmanTreeCreator.createTree();
        createTree(hTree); 
        setLenght(symbolCount);
        return bits;
    }
    
    @Override
    protected void processData(BitSet b){ 
        counter++;
        if (b != null){
            symbols.put(b, symbols.get(b).intValue() + 1);
            if (counter % optRecreateTreeSteps == 0){
                symbols_sorted.clear();
                symbols_sorted.putAll(symbols);
                hTree = HuffmanTreeCreator.createTree();
                createTree(hTree);
            } 
        }
        super.processData(b);
    }
}

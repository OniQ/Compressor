package compressor;

import static data.Data.*;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashMap; 
import java.util.Map.Entry;

import static data.Options.optSymbAsChar;
import static data.Options.symbSize;
import static file.FileManager.bitSetToChar;
import static file.FileManager.bitSetToString;


import compressor.actions.CompressAction;
import file.FileManager;
  
public class Fan {
    
    public LinkedHashMap<BitSet, CodeStruct> tree = new LinkedHashMap<>();
    private short iteration = 0; 
    
    public Fan(){ 
        symbols_sorted.clear();
        symbols_sorted.putAll(symbols);
        if (symbols_sorted.size() != 1){
            Iterator<Entry<BitSet, Integer>> it = symbols_sorted.entrySet().iterator();
            while (it.hasNext()) {
                BitSet bs = it.next().getKey();
                tree.put(bs, new CodeStruct(new BitSet(),  0));
            }
            createTree(new LinkedHashMap<>(symbols_sorted));  
        }
        else{
            BitSet cb = new BitSet();
            cb.set(0);
            tree.put(symbols_sorted.firstKey(), new CodeStruct(cb,  1));
        }
    }
    
    public void compress(FileManager fileManager){ 
        fileManager.fileActionBuffered(new CompressAction( fileManager.file.length(), fileManager.file.getPath(), tree));
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
         return result+"\n";
    }
    
    private int getSum(Iterator<Entry<BitSet, Integer>> it){
        int sum = 0;
        while (it.hasNext()) {
            sum += it.next().getValue();
        }
        return sum;
    }
    
    protected void createTree(LinkedHashMap<BitSet, Integer> set1){
        if (set1.size() <= 1){
            return;
        }
        LinkedHashMap<BitSet, Integer> part1 = new LinkedHashMap<>();
        LinkedHashMap<BitSet, Integer> part2 = new LinkedHashMap<>();
        Iterator<Entry<BitSet, Integer>> it = set1.entrySet().iterator();
        int sum = getSum(it);
        int partSum = 0;
        it = set1.entrySet().iterator();
        
        while (it.hasNext()) {
            Entry<BitSet, Integer> pairs = it.next(); 
            BitSet bsDivide = pairs.getKey();
            partSum += pairs.getValue();
            
            if (partSum >= sum-partSum){
                Iterator<Entry<BitSet, Integer>> it1 = set1.entrySet().iterator();
                BitSet bs; 
                if (it1.hasNext()){
                    do{ 
                        bs = it1.next().getKey(); 
                        Integer value = set1.get(bs); 
                        part1.put(bs, value);
                        CodeStruct codeStruct = tree.get(bs);
                        BitSet code = codeStruct.code;
                        code.set(iteration, false);
                        tree.get(bs).length = (short) (iteration + 1);
                        tree.get(bs).code = code;
                        if (!it.hasNext())
                            break;
                    }while(bs != bsDivide);
                    
                    do{ 
                        if (it1.hasNext()){
                            bs = it1.next().getKey();
                            Integer value = set1.get(bs);
                            part2.put(bs, value);
                            CodeStruct codeStruct = tree.get(bs);
                            BitSet code = codeStruct.code;
                            code.set(iteration, true);
                            tree.get(bs).length = (short) (iteration + 1);
                            tree.get(bs).code = code;
                        }
                    }while(it1.hasNext());
                    break;
                }
            }
        }
        iteration++;
        short iter = iteration;
        createTree(part1);
        iteration = iter;
        createTree(part2);
    }
}

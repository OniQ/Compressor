package data;

import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator; 
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry; 

import static data.Options.optSymbAsChar;
import static data.Options.symbSize;
import static file.FileManager.bitSetToChar;
import static file.FileManager.bitSetToString;

public class Data {
    public static final Map<BitSet, Integer> symbols = new HashMap<>(); 
    private static final ValueComparator bvc =  new ValueComparator(symbols); 
    public static SortedMap<BitSet, Integer> symbols_sorted = new TreeMap<>(bvc);
    public static long symbolCount = 0; 
    
    public static String frequencies(){
        String result = "";
        symbols_sorted.clear();
        symbols_sorted.putAll(symbols);
        Iterator<Entry<BitSet, Integer>> it = symbols_sorted.entrySet().iterator();
        while (it.hasNext()) {
            Entry<BitSet, Integer> pairs = it.next();
            BitSet b = pairs.getKey();
            int val = pairs.getValue();
            String symb;
            if (optSymbAsChar){
                symb = String.valueOf(bitSetToChar(b, symbSize));
            }
            else{
                symb = bitSetToString(b, symbSize);
            } 
            String s = String.format("symbol: %s\nfrequency: %s\n\n", symb, val);
            result += s;
            it.remove();
        }
        return result;
    }
    
} 

class ValueComparator implements Comparator<BitSet> {

    Map<BitSet, Integer> base;
    public ValueComparator(Map<BitSet, Integer> base) {
        this.base = base;
    }
  
    @Override
    public int compare(BitSet a, BitSet b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
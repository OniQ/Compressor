package compressor;

import java.util.BitSet;
import static file.FileManager.bitSetToString;
import static data.Options.symbSize;

public class HuffmanNode {
    
    public int weight;
    public HuffmanNode parent;
    public HuffmanNode child1;
    public HuffmanNode child2;
    public BitSet symb;
    public int index;
    
    public HuffmanNode(BitSet symb, int weight) {
         this.symb = symb;
         this.weight = weight;
    }
    
    public HuffmanNode(HuffmanNode ch1, HuffmanNode ch2) {
         child1 = ch1;
         child2 = ch2;
         weight = child1.weight + child2.weight;
    }
    
    @Override
    public String toString(){
        String str;
            if (symb != null){
                str = String.format("[%s ; %d ; %s]", bitSetToString(symb, symbSize), weight, parent != null);
            } 
            else{
                str = String.format("[%d; %s ]", weight, parent != null);
            }
        return str;
    }
}

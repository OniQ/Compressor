package compressor;

import static data.Data.symbols;
import static data.Data.symbols_sorted; 
import static data.Options.symbSize; 

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
 
class NodeCompare implements Comparator<HuffmanNode> {

    @Override
    public int compare(HuffmanNode n1, HuffmanNode n2) {
        
         if (n1.weight < n2.weight) {
             return -1;
         } else {
             return 1;
         }
    }
}

public class HuffmanTreeCreator {
    public static ArrayList<HuffmanNode> huffmanTree;  
    
    public HuffmanTreeCreator() {
         
    }
    
    public static void initTree(){
        symbols_sorted.clear();
        symbols.clear();
        int k = (int) Math.pow(2, symbSize);
        for (int i = 0; i < k; i++){
            long[] array= { i };
            symbols.put(BitSet.valueOf(array), 0);
        }
        symbols_sorted.putAll(symbols);
    }
    
    static public final LinkedHashMap<BitSet, CodeStruct> createTree(){ 
        huffmanTree = new ArrayList<>();
        Iterator<Entry<BitSet, Integer>> it = symbols_sorted.entrySet().iterator();
        while (it.hasNext()) {
            Entry<BitSet, Integer> entry = it.next();
            BitSet bs = entry.getKey();
            int len = entry.getValue();
            huffmanTree.add(new HuffmanNode(bs, len));
        } 
        Collections.reverse(huffmanTree);
        HuffmanNode child1;
        HuffmanNode child2;
        do{
            child1 = null;
            child2 = null;
            for (HuffmanNode hn : huffmanTree){
                if (hn.parent == null){
                    if (child1 == null){
                        child1 = hn;
                    }
                    else{
                        child2 = hn;
                        break;
                    }
                }
            }
            if (child2 != null){
                HuffmanNode parent = new HuffmanNode(child1, child2);
                child1.parent = parent;
                child2.parent = parent;
                //assign code
                huffmanTree.add(parent); 
                Collections.sort(huffmanTree, new NodeCompare());
            }
        }while(child2 != null);
        numTree();
        return createCodeTree();
    } 
    
    static public final LinkedHashMap<BitSet, CodeStruct> createCodeTree(){
        HuffmanNode leaf;
        LinkedHashMap<BitSet, CodeStruct> tree = new LinkedHashMap<>();
            for (HuffmanNode hn : huffmanTree){
                if (hn.symb != null){
                    BitSet symbol = hn.symb;
                    BitSet code = new BitSet();
                    leaf = hn;
                    int i = 0;
                    do{
                        if ((leaf.index % 2) == 0){
                            code.set(i, true);
                        }
                        else{
                            code.set(i, false);
                        }
                        i++;
                        leaf = leaf.parent;
                    }while(leaf.parent != null);
                    BitSet temp = new BitSet();
                    for(int j = 0; j < i; j++){
                        temp.set(j, code.get(i - j - 1));
                    }
                    code = temp;
                    tree.put(symbol, new CodeStruct(code, i));
                } 
            }  
            return tree;
    } 
    
    public static void numTree(){
         Iterator<HuffmanNode> it = huffmanTree.iterator();
         int index = 1;
         while (it.hasNext()){
                 HuffmanNode hn = it.next();
                 hn.index = index;
                index++;
         }
    }  

}

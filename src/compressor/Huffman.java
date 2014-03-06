package compressor; 
 

import java.util.BitSet; 
import java.util.LinkedHashMap; 

import compressor.actions.HuffmanCompressAction;
import file.FileManager;

public class Huffman extends Fan {
    
    HuffmanCompressAction action;
    
    public Huffman() {
        super();
    }
    
    @Override
    public void compress(FileManager fileManager){
        action = new HuffmanCompressAction( fileManager.file.length(), fileManager.file.getPath(), action.tree);
        fileManager.fileActionBuffered(action);
    }
    
    @Override
    protected void createTree(LinkedHashMap<BitSet, Integer> set1){
        if (action == null){
            action = new HuffmanCompressAction( tree);
        }
        tree = action.tree;
    }
    
    @Override
    public String getTree(){
        return action.getTree();
    }
}

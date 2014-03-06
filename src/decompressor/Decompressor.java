package decompressor;
  
import decompressor.actions.DecompressAction;
import decompressor.actions.HuffmanDecompressAction;
import file.FileManager;
import gui.Window;

public class Decompressor {
        
    public void fanDecompress(FileManager fileManager){
        try{
            fileManager.fileActionBuffered(new DecompressAction( fileManager.file.length(), fileManager.file, true ));
        }
        catch(IndexOutOfBoundsException e){
            Window.viewer.append("Failed! Buffer not long enough for header to fit! You can increase buffer size in options." + Window.newline); 
        }
    }
    
    public void huffmanDecompress(FileManager fileManager){
        try{
            fileManager.fileActionBuffered(new HuffmanDecompressAction( fileManager.file.length(), fileManager.file, false ));
        }
        catch(IndexOutOfBoundsException e){
            Window.viewer.append("Failed! Buffer not long enough for header to fit! You can increase buffer size in options." + Window.newline); 
        }
    }
}

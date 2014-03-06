package file; 

import java.io.BufferedInputStream; 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;

import compressor.actions.Action;
import gui.Window;
import static data.Options.*;

public class FileManager {
    
    public File file; 
    public FileOutputStream byteOutput;
    private PrintWriter outputStream;
    private Scanner fileScan; 
    private int writtenBytes = 0;
    
    public FileManager(File file){
        this.file = file; 
    }
    
    public FileManager(String file){
        try {
            byteOutput = new FileOutputStream(file, false);
        } catch (FileNotFoundException e) {
            System.err.println("File creating error");
        }
    }
    
    public FileManager(){
        file = new File("binary.txt"); 
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) { 
                Window.viewer.append("Error creating file: " + e);
            }
        }
        try{
        outputStream = new PrintWriter(
                new BufferedWriter (new FileWriter(file, false)), true);
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
        try {
            fileScan = new Scanner(new BufferedReader(
                    new FileReader(file)));
        } 
        catch (FileNotFoundException e) {
        }
    }
    
    public void fileOutputBuffered(byte[] bytes){
        if (bytes == null){
            try {
                byteOutput.flush();
                byteOutput.close();
                Window.viewer.append(" " + writtenBytes);
            } catch (IOException e) {
                System.err.println("File closing error: " + e);
            }
        }
        else{
            try {
                writtenBytes += bytes.length;
                byteOutput.write(bytes); 
            } catch ( IOException e) {
                System.err.println("File writing error: " + e);
            }
        }
    }
    
    public static void toBitSetLen(BitSet bs, long number, int fromIndex, int len){
         long longArray[] =  { number };
         BitSet temp = BitSet.valueOf(longArray);  
         for (int i = 0; i < len; i++){
             bs.set(i + fromIndex, temp.get(i)); 
         }    
    }
    
    public static int fromBitSetLen(BitSet bs, int fromIndex, int len){ 
         BitSet temp = new BitSet();
         for (int i = 0; i < len; i++){
             temp.set(i, bs.get(i + fromIndex)); 
         }    
         long longArray[] = new long[len]; 
         longArray = Arrays.copyOf(temp.toLongArray(), len); 
         return (int)longArray[0];
    }
    
    public String readFromTempFile(int number){
        if (fileScan.hasNext()){
            String text = "";
            for (int i = 0; i < number; i++){
                if (fileScan.hasNext()){
                    text += fileScan.next() + " ";
                }
                else
                    break;
            }
            return text;
        }
        else{
            return null;
        }
    }
    
    public void writeToTempFile(String str){   
        outputStream.print( str );
        outputStream.flush();
        outputStream.close();
    }
    
    public static String bitSetToString(BitSet binary, int bitNumber){
        String byteString = "";
        for (int j = 0; j < bitNumber; j ++){
            if (binary.get(j)){
                byteString = byteString.concat("1");
            }
            else{
                byteString = byteString.concat("0");
            }
        }
        return byteString;
    }
    
    public static char bitSetToChar(BitSet binary, int bitNumber){
        char c;
        if (binary.isEmpty()){
            c = ' ';
        }
        else{
            c = (char) binary.toByteArray()[0];
        }
        return c;
    }
     
    public void fileActionBuffered(Action action){
        try{
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            byte bytes[] = new byte[optBufferSize];
            int ret;
            do{
                if (inputStream.available() < optBufferSize){
                    byte lastBytes[] = new byte[inputStream.available()];
                    inputStream.read( lastBytes );
                    action.perform(lastBytes);
                    ret = inputStream.read();
                }
                else{
                    ret = inputStream.read( bytes );
                    action.perform(bytes);
                }
                
            }while(ret != -1);
            action.perform(null);
            try{
                inputStream.close();
            }
            catch (IOException e){
                Window.viewer.append("Closing " + file.getName() + "has failed." + Window.newline);
            }
        }
        catch (FileNotFoundException e) {
            Window.viewer.append("File: " + file.getName() + "can not be found." + Window.newline);
        } catch (IOException e) {
            Window.viewer.append(e.getMessage() + Window.newline);
        } 
    }
}

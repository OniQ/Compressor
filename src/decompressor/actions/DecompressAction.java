package decompressor.actions;

import static data.Data.symbolCount; 
import static data.Data.symbols;
import static data.Options.symbSize;
import static file.FileManager.bitSetToString;
import static file.FileManager.fromBitSetLen;
import file.FileManager;
import gui.Window;

import java.io.File;
import java.util.Arrays;
import java.util.BitSet; 
import java.util.Iterator;
import java.util.LinkedHashMap; 
import java.util.Map.Entry;

import compressor.Fan;
import compressor.CodeStruct;
import compressor.Huffman;
import compressor.actions.CompressAction; 

public class DecompressAction extends CompressAction {
 
	BitSet codePart = new BitSet();
	int partBits = 0;  
	long compressedFileLength;
	int bitsNumber = 0;
	LinkedHashMap<BitSet, CodeStruct> tree;
	Fan treeCreator;
	boolean algorithm;
	
	public DecompressAction(long compressedFileLength, File file, boolean algorithm) {
		super(file.length());
		header = true; 
		String fileName = file.getPath();
		this.compressedFileLength = compressedFileLength; 
		if (fileName.contains(".")){
			int pos = fileName.indexOf(".");
			fileName = fileName.substring(0, pos);
			fileName = fileName.concat(".dec");
		}
		else{
			fileName = fileName + ".dec";
		} 
		fileM = new FileManager(fileName);
		this.algorithm = algorithm;
	}
	
	private final void createTree(LinkedHashMap<BitSet, CodeStruct> tree){
		LinkedHashMap<BitSet, CodeStruct> defanTree = new LinkedHashMap<BitSet, CodeStruct>();
		Iterator<Entry<BitSet, CodeStruct>> it = tree.entrySet().iterator();
		while(it.hasNext()){
			Entry<BitSet, CodeStruct> pairs = it.next();
			defanTree.put(pairs.getValue().code, new CodeStruct(pairs.getKey(), pairs.getValue().length)); 
		}
		this.tree = defanTree;
		//length = tree.size()*simbSize;
	} 
	
	public String getTree(){
		 String result = ""; 
		 Iterator<Entry<BitSet, CodeStruct>> it = tree.entrySet().iterator();
		 while (it.hasNext()) {
		    	Entry<BitSet, CodeStruct> pairs = it.next();
		    	String s = String.format("%s ---> %s\n", bitSetToString(pairs.getKey(), pairs.getValue().length), bitSetToString(pairs.getValue().code, symbSize));
		        result += s;
		        //it.remove(); // avoids a ConcurrentModificationException
		 }
		 return result+"\n";
	} 
	
	@Override
	protected BitSet processHeader(BitSet bits){
		int offset = 0;
		symbolCount = 0;
		int freqSize = fromBitSetLen(bits, offset, 6);
		offset += 6;
		int simbolSize = fromBitSetLen(bits, offset, 5);
		offset += 5;
		symbSize = (byte) simbolSize;
		Integer freq = -1;
		BitSet symb = new BitSet();
		symbols.clear();
		while(freq != 0){
			symb = new BitSet(); 
			freq = fromBitSetLen(bits, offset, freqSize);
			symbolCount += freq;
			offset += freqSize;
			if (freq == 0){
				while ((offset % 8) != 0){
					offset++;
				}
				break;
			} 
			for (int i = 0; i < symbSize; i++){
				symb.set(i, bits.get(i + offset));  
			}
			offset += symbSize;
			symbols.put(symb, freq);
		}  
		bits = bits.get(offset, bitsNumber);
		bitsNumber -= offset;
		if (algorithm){
			treeCreator = new Fan();
		}
		else{
			treeCreator = new Huffman();
		}
		createTree(treeCreator.tree); 
		header = false;
		setLenght(symbolCount);
		return bits;
	}
	
	protected void processLast(BitSet lastBits, int length){
		//if (Data.lastBits != null){
		//int len = fromBitSetLen(compressedData, processedBitsNumber, 5);
		for (int i = 0; i < length; i++){
			compressedData.set(processedBitsNumber, lastBits.get(i));
			processedBitsNumber++; 
			//gotBits++;
		//}
		}
	}
	
	@Override
	protected void processData(BitSet b){
		if (b != null){  
			for (int i = 0; i < symbSize; i++){
				compressedData.set(processedBitsNumber, b.get(i));
				processedBitsNumber++; 
				gotBits++;
			} 
			gotSymbols++; 
			flushBuffer(); 
		}
		else{
			//Output Compressed File 
			//int size = processedBitsNumber/8;
			int size;
			size = processedBitsNumber/8;
			byte byteArray[] = new byte[size]; 
			if (!compressedData.isEmpty()){  
				//byteArray = decompressedData.toByteArray();
				byteArray = Arrays.copyOf(compressedData.toByteArray(), size);
				fileM.fileOutputBuffered(byteArray);
			} 
			Window.viewer.setText("Progress: " + 100 + "% (" + gotSymbols + " of " + length*8/symbSize + " symbols)\n");
			Window.viewer.append("Compressed file size: " + compressedFileLength + Window.newline);
			Window.viewer.append("Decompressed file size:");
			fileM.fileOutputBuffered(null);
		} 
	}
	
	@Override
	public void perform(byte[] bytes) { 
		if ((bytes != null) && (bytes.length != 0)){
			BitSet bits = BitSet.valueOf(bytes);
			bitsNumber = bytes.length*8;
			if (header){
				bits = processHeader(bits);
			}
			gotBits += bitsNumber;  
			
			for (int i = 0; i < bitsNumber; i++){
				codePart.set(partBits, bits.get(i));
				if ( gotSymbols < symbolCount ){ 
					partBits++;
					if ((tree.containsKey(codePart))){
						if (tree.get(codePart).length == partBits){
							processData(tree.get(codePart).code);
							showProgress();
							codePart.clear();
							partBits = 0;
						}
					}
				}
				else{
					partBits++;
				}
			}
		}
		else{ 
			processLast(codePart, partBits);
			processData(null);
		}
	}

}

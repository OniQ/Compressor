package compressor;

import java.util.BitSet;

public class CodeStruct {
    public BitSet code;
    public short length;
    public CodeStruct(BitSet bs, int l){
        code = bs;
        length = (short)l;
    }
}

package com.datapro.dibs.encrypin;

import java.util.Hashtable;
import java.util.Random;


public class Block
    implements Cloneable
{

    public Block()
    {
        data = new byte[8];
        for(int i = 0; i < 8; i++)
            data[i] = 0;

    }

    public Block(byte abyte0[])
    {
        data = new byte[8];
        if(abyte0.length >= 8)
        {
            for(int i = 0; i < 8; i++)
                data[i] = abyte0[i];

        }
    }

    public Block(Block block, byte abyte0[])
    {
        data = new byte[8];
        for(int i = 0; i < abyte0.length; i++)
            putBit(i, block.getBit(abyte0[i] - 1));

    }

    public Block(String s)
    {
        this(Convert.getData(Convert.fromHexToBin(s)));
    }

    public void forceParity(boolean flag)
    {
        for(int i = 0; i < 8; i++)
        {
            boolean flag1 = getBit(i * 8);
            for(int j = 1; j < 7; j++)
                flag1 ^= getBit(i * 8 + j);

            putBit(i * 8 + 7, flag1 ^ flag);
        }

    }

    public void generateRandom()
    {
        for(int i = 0; i < 64; i++)
            putBit(i, hat.nextBoolean());

    }

    public void rotateLeft()
    {
        boolean flag = getBit(0);
        boolean flag1 = getBit(28);
        for(int i = 0; i < 55; i++)
            putBit(i, getBit(i + 1));

        putBit(27, flag);
        putBit(55, flag1);
    }

    public void rotateRight()
    {
        boolean flag = getBit(27);
        boolean flag1 = getBit(55);
        for(int i = 55; i > 0; i--)
            putBit(i, getBit(i - 1));

        putBit(0, flag);
        putBit(28, flag1);
    }

    public void transpose(byte abyte0[])
    {
        Block block = (Block)clone();
        for(int i = 0; i < abyte0.length; i++)
            putBit(i, block.getBit(abyte0[i] - 1));

    }

    public void xor(Block block)
    {
        for(int i = 0; i < 64; i++)
            putBit(i, getBit(i) ^ block.getBit(i));

    }

    protected Object clone()
    {
        Block block = new Block(data);
        return block;
    }

    public boolean getBit(int i)
    {
        byte byte0 = data[i / 8];
        byte byte1 = ((Integer)power_2.get(new Integer(i % 8))).byteValue();
        return (byte0 & byte1) == byte1;
    }

    public void putBit(int i, boolean flag)
    {
        byte byte0 = ((Integer)power_2.get(new Integer(i % 8))).byteValue();
        data[i / 8] = flag ? (byte)(data[i / 8] | byte0) : (byte)(data[i / 8] & 255 - byte0);
    }

    public String toString()
    {
        return Convert.fromBinToHex(Convert.getString(data));
    }

    public String toBinaryString()
    {
        String s = Convert.fromBinToHex(Convert.getString(data));
        String s1 = "";
        String s2 = "";
        for(int i = 0; i < s.length() / 2; i++)
            s1 = s1 + hex_to_bit.get(s.substring(i, i + 1));

        for(int j = s.length() / 2; j < s.length(); j++)
            s2 = s2 + hex_to_bit.get(s.substring(j, j + 1));

        return "\nL: " + s1 + "\nR: " + s2;
    }

    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(obj instanceof Block)
            return obj.toString().equals(toString());
        if(obj instanceof String)
            return obj.equals(toString());
        else
            return false;
    }

    private byte data[];
    private static final Hashtable power_2;
    private static final Hashtable hex_to_bit;
    private static Random hat = new Random();

    static 
    {
        power_2 = new Hashtable();
        power_2.put(new Integer(0), new Integer(128));
        power_2.put(new Integer(1), new Integer(64));
        power_2.put(new Integer(2), new Integer(32));
        power_2.put(new Integer(3), new Integer(16));
        power_2.put(new Integer(4), new Integer(8));
        power_2.put(new Integer(5), new Integer(4));
        power_2.put(new Integer(6), new Integer(2));
        power_2.put(new Integer(7), new Integer(1));
        hex_to_bit = new Hashtable();
        hex_to_bit.put("0", "0000");
        hex_to_bit.put("1", "0001");
        hex_to_bit.put("2", "0010");
        hex_to_bit.put("3", "0011");
        hex_to_bit.put("4", "0100");
        hex_to_bit.put("5", "0101");
        hex_to_bit.put("6", "0110");
        hex_to_bit.put("7", "0111");
        hex_to_bit.put("8", "1000");
        hex_to_bit.put("9", "1001");
        hex_to_bit.put("A", "1010");
        hex_to_bit.put("B", "1011");
        hex_to_bit.put("C", "1100");
        hex_to_bit.put("D", "1101");
        hex_to_bit.put("E", "1110");
        hex_to_bit.put("F", "1111");
    }
}

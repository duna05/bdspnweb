package com.datapro.dibs.encrypin;


public class Utils
{

    public Utils()
    {
    }

    private static Block f(int i, Block block, Block block1, boolean flag)
    {
        Block block2 = new Block();
        Block block3 = new Block(block1, e_transpose);
        if(flag)
        {
            for(int j = 0; j < rots[i]; j++)
                block.rotateLeft();

        }
        Block block4 = new Block(block, key_transpose_2);
        Block block5 = new Block();
        for(int k = 0; k < 48; k++)
            block5.putBit(k, block3.getBit(k) ^ block4.getBit(k));

        for(int l = 0; l < 8; l++)
        {
            int j1 = 32 * (block5.getBit(6 * l) ? 1 : 0) + 16 * (block5.getBit(6 * l + 5) ? 1 : 0) + 8 * (block5.getBit(6 * l + 1) ? 1 : 0) + 4 * (block5.getBit(6 * l + 2) ? 1 : 0) + 2 * (block5.getBit(6 * l + 3) ? 1 : 0) + (block5.getBit(6 * l + 4) ? 1 : 0);
            block2.putBit(4 * l, (s[l][j1] & 8) == 8);
            block2.putBit(4 * l + 1, (s[l][j1] & 4) == 4);
            block2.putBit(4 * l + 2, (s[l][j1] & 2) == 2);
            block2.putBit(4 * l + 3, (s[l][j1] & 1) == 1);
        }

        block2.transpose(p_transpose);
        if(!flag)
        {
            for(int i1 = 0; i1 < rots[i]; i1++)
                block.rotateRight();

        }
        return block2;
    }

    public static Block desEncrypt(Block block, Block block1)
    {
        Block block2 = new Block(block, initial_transpose);
        Block block3 = new Block(block1, key_transpose_1);
        for(int i = 0; i < 16; i++)
        {
            Block block4 = (Block)block2.clone();
            for(int j = 0; j < 32; j++)
                block2.putBit(j, block4.getBit(j + 32));

            Block block5 = f(i, block3, block2, true);
            for(int k = 0; k < 32; k++)
                block2.putBit(k + 32, block4.getBit(k) ^ block5.getBit(k));

        }

        block2.transpose(swap);
        block2.transpose(final_transpose);
        return block2;
    }

    public static Block desDecrypt(Block block, Block block1)
    {
        Block block2 = new Block(block, initial_transpose);
        Block block3 = new Block(block1, key_transpose_1);
        block2.transpose(swap);
        for(int i = 15; i >= 0; i--)
        {
            Block block4 = (Block)block2.clone();
            for(int j = 0; j < 32; j++)
                block2.putBit(j + 32, block4.getBit(j));

            Block block5 = f(i, block3, block2, false);
            for(int k = 0; k < 32; k++)
                block2.putBit(k, block4.getBit(k + 32) ^ block5.getBit(k));

        }

        block2.transpose(final_transpose);
        return block2;
    }

    public static Block tripledesEncrypt(Block block, Block block1, Block block2, Block block3)
    {
        return desEncrypt(desDecrypt(desEncrypt(block, block1), block2), block3);
    }

    public static Block tripledesDecrypt(Block block, Block block1, Block block2, Block block3)
    {
        return desDecrypt(desEncrypt(desDecrypt(block, block3), block2), block1);
    }

    public static Block tripledesEncrypt(Block block, Block ablock[])
    {
        switch(ablock.length)
        {
        case 2: // '\002'
            return tripledesEncrypt(block, ablock[0], ablock[1], ablock[0]);

        case 3: // '\003'
            return tripledesEncrypt(block, ablock[0], ablock[1], ablock[2]);
        }
        return null;
    }

    public static Block tripledesDecrypt(Block block, Block ablock[])
    {
        switch(ablock.length)
        {
        case 2: // '\002'
            return tripledesDecrypt(block, ablock[0], ablock[1], ablock[0]);

        case 3: // '\003'
            return tripledesDecrypt(block, ablock[0], ablock[1], ablock[2]);
        }
        return null;
    }

    public static String decimalise(String s1, String s2)
    {
        String s3 = "";
        for(int i = 0; i < s1.length(); i++)
        {
            char c = s1.charAt(i);
            if(c >= '0' && c <= '9')
            {
                s3 = s3 + c;
            } else
            {
                int j = (c - 65) + 10;
                s3 = s3 + s2.charAt(j);
            }
        }

        return s3;
    }

    public static String addNoCarry(String s1, String s2)
    {
        String s3 = "";
        for(int i = 0; i < s1.length(); i++)
            s3 = s3 + (((s1.charAt(i) - 48) + s2.charAt(i)) - 48) % 10;

        return s3;
    }

    public static String scanDigits(int i, String s1)
    {
        String s2 = "";
        for(int j = 0; j < s1.length(); j++)
        {
            char c = s1.charAt(j);
            if(c >= '0' && c <= '9')
                s2 = s2 + c;
            if(s2.length() == i)
                return s2;
        }

        return s2;
    }

    public static String scanAlpha(int i, String s1)
    {
        String s2 = "";
        for(int j = 0; j < s1.length(); j++)
        {
            char c = s1.charAt(j);
            if(c >= 'A' && c <= 'F')
                s2 = s2 + c;
            if(s2.length() == i)
                return s2;
        }

        return s2;
    }

    private static final byte initial_transpose[] = {
        58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 
        44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 
        30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 
        16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 
        59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 
        45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 
        31, 23, 15, 7
    };
    private static final byte final_transpose[] = {
        40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 
        47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 
        54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 
        61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 
        35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 
        42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 
        49, 17, 57, 25
    };
    private static final byte swap[] = {
        33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 
        43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 
        53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 
        63, 64, 1, 2, 3, 4, 5, 6, 7, 8, 
        9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 
        19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 
        29, 30, 31, 32
    };
    private static final byte key_transpose_1[] = {
        57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 
        42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 
        27, 19, 11, 3, 60, 52, 44, 36, 63, 55, 
        47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 
        30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 
        13, 5, 28, 20, 12, 4
    };
    private static final byte key_transpose_2[] = {
        14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 
        21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 
        27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 
        30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 
        34, 53, 46, 42, 50, 36, 29, 32
    };
    private static final byte e_transpose[] = {
        32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 
        8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 
        14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 
        20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 
        28, 29, 28, 29, 30, 31, 32, 1
    };
    private static final byte p_transpose[] = {
        16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 
        23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 
        32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 
        4, 25
    };
    private static final byte s[][] = {
        {
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 
            6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 
            14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 
            3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 
            15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 
            8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 
            10, 0, 6, 13
        }, {
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 
            2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 
            15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 
            11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 
            5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 
            10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 
            0, 5, 14, 9
        }, {
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 
            12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 
            3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 
            15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 
            11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 
            13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 
            11, 5, 2, 12
        }, {
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 
            8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 
            6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 
            14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 
            15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 
            0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 
            12, 7, 2, 14
        }, {
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 
            3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 
            4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 
            8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 
            15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 
            12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 
            10, 4, 5, 3
        }, {
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 
            3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 
            7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 
            3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 
            7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 
            2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 
            6, 0, 8, 13
        }, {
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 
            9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 
            4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 
            8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 
            10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 
            13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 
            14, 2, 3, 12
        }, {
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 
            3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 
            10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 
            9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 
            0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 
            14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 
            3, 5, 6, 11
        }
    };
    private static final byte rots[] = {
        1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 
        2, 2, 2, 2, 2, 1
    };

}

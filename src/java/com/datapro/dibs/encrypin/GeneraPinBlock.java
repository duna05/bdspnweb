package com.datapro.dibs.encrypin;

public class GeneraPinBlock {

    public GeneraPinBlock(){
    }

    public String calculapinblock(String PAN, String PIN, String Cripto){
        Block block = new Block(Cripto);
        Block block1 = new Block();
        String s2 = "04" + PIN + Convert.resize("", 8, 'F', false) + "FF";
        String s3 = null;
        s3 = "0000" + PAN.substring(PAN.length() - 13, PAN.length() - 1);
        String s4 = "";
        byte abyte0[] = Convert.fromHexDataToBinData(s2.getBytes());
        byte abyte1[] = Convert.fromHexDataToBinData(s3.getBytes());
        byte abyte2[] = new byte[8];
        for(int i = 0; i < 8; i++)
            abyte2[i] = (byte)(abyte0[i] ^ abyte1[i]);

        s4 = new String(Convert.fromBinDataToHexData(abyte2));
        Block block2 = new Block(s4);
        Block block3 = Utils.desEncrypt(block2, block);
        String PINBLOCK = block3.toString();
        return PINBLOCK;
    }
     
	public String calculapinblockDouble(String PAN, String PIN, String Cripto)	{
		Block Cblock1 = new Block(Cripto.substring(0, 16));
		Block Cblock2 = new Block(Cripto.substring(16, 32));
		Block block2 = new Block();
		String s3 = "04" + PIN + Convert.resize("", 8, 'F', false) + "FF";
		String s4 = null;
		s4 = "0000" + PAN.substring(PAN.length() - 13, PAN.length() - 1);
		String s5 = "";
		byte abyte0[] = Convert.fromHexDataToBinData(s3.getBytes());
		byte abyte1[] = Convert.fromHexDataToBinData(s4.getBytes());
		byte abyte2[] = new byte[8];
		for(int i = 0; i < 8; i++)
			abyte2[i] = (byte)(abyte0[i] ^ abyte1[i]);

		s5 = new String(Convert.fromBinDataToHexData(abyte2));
		Block block3 = new Block(s5);
		Block block4 = Utils.desEncrypt(block3, Cblock1);
		Block block5 = Utils.desDecrypt(block4, Cblock2);
		Block block6 = Utils.desEncrypt(block5, Cblock1);
		String s6 = block6.toString();
		return s6;
	}
	 
}

package com.datapro.dibs.encrypin;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.sql.SQLException;

import java.util.Stack;
import java.util.Vector;


public final class Convert
{

    public static final int fromMsgTypeToInt(String s)
    {
        return fromMsgTypeDataToInt(getData(s));
    }

    /**
     * @deprecated Method convertEbcdicToAscii is deprecated
     */

    public static final void convertEbcdicToAscii(byte abyte0[], int i, int j)
    {
        int k = i + j;
        for(int l = i; l < k; l++)
            if(abyte0[l] < 0)
                abyte0[l] = (byte)ebcdic_to_ascii[256 + abyte0[l]];
            else
                abyte0[l] = (byte)ebcdic_to_ascii[abyte0[l]];

    }

    /**
     * @deprecated Method getBinFromHexInAscii is deprecated
     */

    public static void getBinFromHexInAscii(String s, byte abyte0[], int i)
    {
        int j = s.length();
        char ac[] = new char[j];
        s.getChars(0, j, ac, 0);
        for(int k = 0; k < j; k++)
        {
            byte byte0;
            if(ac[k] <= '9')
                byte0 = (byte)((byte)ac[k] - 48 & 0xf);
            else
                byte0 = (byte)(((byte)ac[k] - 65) + 10 & 0xf);
            if(k % 2 == 0)
                abyte0[i + k / 2] = (byte)(byte0 << 4);
            else
                abyte0[i + k / 2] += byte0;
        }

    }

    /**
     * @deprecated Method toInt is deprecated
     */

    public static final int toInt(byte abyte0[], int i, int j)
    {
        int k = 0;
        for(; j > 0; j--)
            k = k * 10 + (abyte0[i++] & 0xf);

        return k;
    }

    /**
     * @deprecated Method toInt is deprecated
     */

    public static final int toInt(byte abyte0[])
    {
        return toInt(abyte0, 0, abyte0.length);
    }

    public static long fromStringToLong(String s)
    {
        return Long.parseLong(s);
    }

    /**
     * @deprecated Method fromMsgTypeToLong is deprecated
     */

    public static final long fromMsgTypeToLong(String s)
    {
        return fromMsgTypeDataToLong(getData(s));
    }

    /**
     * @deprecated Method convertAsciiToEbcdic is deprecated
     */

    public static final void convertAsciiToEbcdic(byte abyte0[], int i, int j)
    {
        int k = i + j;
        for(int l = i; l < k; l++)
            if(abyte0[l] < 0)
                abyte0[l] = (byte)ascii_to_ebcdic[256 + abyte0[l]];
            else
                abyte0[l] = (byte)ascii_to_ebcdic[abyte0[l]];

    }

    public static void main(String args[])
    {
        System.out.println("--------- Convert --------");
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+;'\\,.:\"|<>";
        String s1 = "09AF";
        System.out.println(fromDoubleToString(100D, 5));
        System.out.println(fromDoubleToString(-100D, 5));
        if(!s.equals(fromAsciiToEbcdic(fromEbcdicToAscii(s))) || !s.equals(fromHexToBin(fromBinToHex(s))) || !s.equals(fromPexToBin(fromBinToPex(s))) || !s1.equals(fromLongToMsgType(fromMsgTypeToLong(s1))) || !"".equals(strip("", '0', false, 0)) || !"".equals(strip("00", '0', false, 0)) || !"12".equals(strip("12", '0', false, 0)) || !"12".equals(strip("0012", '0', false, 0)) || !"00".equals(strip("", '0', false, 2)) || !"00".equals(strip("00", '0', false, 2)) || !"00".equals(strip("000", '0', false, 2)) || !"01".equals(strip("1", '0', false, 2)) || !"01".equals(strip("01", '0', false, 2)) || !"01".equals(strip("001", '0', false, 2)) || !"123".equals(strip("123", '0', false, 2)) || !"123".equals(strip("00123", '0', false, 2)) || !"0000".equals(resize("", 4, '0', false)) || !"0000".equals(resize("00", 4, '0', false)) || !"0000".equals(resize("0000", 4, '0', false)) || !"0012".equals(resize("12", 4, '0', false)) || !"0012".equals(resize("012", 4, '0', false)) || !"0012".equals(resize("0012", 4, '0', false)) || !"1234".equals(resize("1234", 4, '0', false)) || !"1234".equals(resize("0001234", 4, '0', false)) || !"00100".equals(fromDoubleToString(100D, 5)) || !"00100".equals(fromDoubleToString(-100D, 5)))
            throw new RuntimeException();
        try
        {
            System.in.read();
        }
        catch(Throwable throwable)
        {
            System.out.println(throwable.toString());
        }
        try
        {
            System.in.read();
        }
        catch(Throwable throwable1)
        {
            System.out.println(throwable1.toString());
        }
        System.out.println(fromDoubleToUnsignedLongString(0.0D, 0));
        System.out.println(fromDoubleToUnsignedLongString(-1D, 0));
        System.out.println(fromDoubleToUnsignedLongString(1234567890123D, 0));
        System.out.println(fromDoubleToUnsignedLongString(-1234567890123.1235D, 0));
    }

    /**
     * @deprecated Method fromMsgTypeDataToLong is deprecated
     */

    public static final long fromMsgTypeDataToLong(byte abyte0[])
    {
        long l = 0L;
        int i = abyte0.length;
        for(int j = 0; j < i; j++)
            l = l << 4 | (long)getHexNibble(abyte0[j]);

        return l;
    }

    /**
     * @deprecated Method toDataHex is deprecated
     */

    public static final byte[] toDataHex(byte abyte0[], int i, int j)
    {
        j += j;
        byte abyte1[] = new byte[j];
        for(int k = 0; k < j; k++)
        {
            if((abyte0[i] & 0x80) == 0)
                abyte1[k] = (byte)((abyte0[i] & 0x70) >> 4);
            else
                abyte1[k] = (byte)((abyte0[i] & 0x70) >> 4 | 8);
            if(abyte1[k] < 10)
                abyte1[k] += 48;
            else
                abyte1[k] += 55;
            k++;
            abyte1[k] = (byte)(abyte0[i] & 0xf);
            if(abyte1[k] < 10)
                abyte1[k] += 48;
            else
                abyte1[k] += 55;
            i++;
        }

        return abyte1;
    }

    /**
     * @deprecated Method toDataHex is deprecated
     */

    public static final void toDataHex(long l, byte abyte0[], int i, int j)
    {
        i += j;
        for(; j > 0; j--)
        {
            abyte0[--i] = (byte)(int)(l & 15L | 48L);
            if(abyte0[i] > 57)
                abyte0[i] += 7;
            l >>= 4;
        }

    }

    /**
     * @deprecated Method toStringHex is deprecated
     */

    public static final String toStringHex(long l, int i)
    {
        byte abyte0[] = new byte[i];
        while(i > 0) 
        {
            i--;
            long l1 = l & 15L;
            if(l1 < 10L)
                abyte0[i] = (byte)(int)(l1 + 48L);
            else
                abyte0[i] = (byte)(int)(l1 + 55L);
            l >>= 4;
        }
        return new String(abyte0, 0, 0, abyte0.length);
    }

    public static double fromStringToDouble(String s)
    {
        return (double)fromStringToLong(s);
    }

    private static final byte getPexNibble(byte byte0)
    {
        if(byte0 >= 48 && byte0 <= 63)
            return (byte)(byte0 & 0xf);
        else
            return 0;
    }

    /**
     * @deprecated Method toStringHex is deprecated
     */

    public static final String toStringHex(long l)
    {
        int i = (int)Math.round(Math.floor(Math.log(l) / Math.log(16D))) + 1;
        return toStringHex(l, i);
    }

    /**
     * @deprecated Method toStringHex is deprecated
     */

    public static final String toStringHex(String s)
    {
        byte abyte0[] = toData(s);
        return toStringHex(abyte0, 0, abyte0.length);
    }

    /**
     * @deprecated Method toStringHex is deprecated
     */

    public static final String toStringHex(byte abyte0[], int i, int j)
    {
        byte abyte1[] = toDataHex(abyte0, i, j);
        return new String(abyte1, 0, 0, abyte1.length);
    }

    /**
     * @deprecated Method toStringHex is deprecated
     */

    public static final String toStringHex(byte abyte0[])
    {
        return toStringHex(abyte0, 0, abyte0.length);
    }

    private static final int getPexChar(int i)
    {
        return i + 48;
    }

    /**
     * @deprecated Method setLength is deprecated
     */

    public static final String setLength(String s, int i, boolean flag)
    {
        return setLength(s, i, (byte)32, flag);
    }

    /**
     * @deprecated Method setLength is deprecated
     */

    public static final String setLength(String s, int i, byte byte0, boolean flag)
    {
        int j = s.length();
        if(j == i)
            return s;
        if(j < i)
        {
            byte abyte0[] = new byte[j];
            s.getBytes(0, j, abyte0, 0);
            abyte0 = toDataPadded(abyte0, i, byte0, flag);
            return new String(abyte0, 0, 0, i);
        }
        if(flag)
            return s.substring(0, i);
        else
            return s.substring(j - i);
    }

    /**
     * @deprecated Method splitString is deprecated
     */

    public static Vector splitString(String s, int i)
    {
        Vector vector = new Vector();
        boolean flag = false;
        int j = flag ? 1 : 0;
        do
        {
            int k = s.indexOf(i, j);
            if(k == -1)
            {
                if(s.length() > 0)
                    vector.addElement(s.substring(j));
                break;
            }
            vector.addElement(s.substring(j, k));
            j = k + 1;
        } while(true);
        return vector;
    }

    /**
     * @deprecated Method isEqual is deprecated
     */

    public static final boolean isEqual(byte abyte0[], byte abyte1[])
    {
        int i = abyte0.length;
        if(i != abyte1.length)
            return false;
        for(int j = 0; j < i; j++)
            if(abyte0[j] != abyte1[j])
                return false;

        return true;
    }

    /**
     * @deprecated Method fromDoubleToString is deprecated
     */

    public static String fromDoubleToString(double d, int i)
    {
        return fromDoubleToUnsignedLongString(d, i);
    }

    public static final int fromMsgTypeDataToInt(byte abyte0[])
    {
        int i = 0;
        int j = abyte0.length;
        for(int k = 0; k < j; k++)
            i = i << 4 | getHexNibble(abyte0[k]);

        return i;
    }

    public static final boolean isEqual(Object obj, Object obj1)
    {
        if((obj == null) ^ (obj1 == null))
            return false;
        if(obj == null)
            return true;
        else
            return obj.equals(obj1);
    }

    /**
     * @deprecated Method toString is deprecated
     */

    public static final String toString(byte abyte0[])
    {
        return new String(abyte0, 0, 0, abyte0.length);
    }

    /**
     * @deprecated Method toString is deprecated
     */

    public static final String toString(long l, int i)
    {
        byte abyte0[] = new byte[i];
        while(i > 0) 
        {
            i--;
            abyte0[i] = (byte)(int)(l % 10L + 48L);
            l /= 10L;
        }
        return toString(abyte0);
    }

    public static final boolean isEqualData(byte abyte0[], byte abyte1[])
    {
        if((abyte0 == null) ^ (abyte1 == null))
            return false;
        if(abyte0 == null)
            return true;
        int i = abyte0.length;
        if(i != abyte1.length)
            return false;
        for(int j = 0; j < i; j++)
            if(abyte0[j] != abyte1[j])
                return false;

        return true;
    }

    public static final byte[] fromPexDataToBinData(byte abyte0[])
    {
        int i = abyte0.length / 2;
        byte abyte1[] = new byte[i];
        int j = 0;
        for(int k = 0; k < i; k++)
            abyte1[k] = (byte)(getPexNibble(abyte0[j++]) << 4 | getPexNibble(abyte0[j++]));

        return abyte1;
    }

    public static final byte[] fromBinDataToPexData(byte abyte0[])
    {
        int i = abyte0.length;
        byte abyte1[] = new byte[i * 2];
        int j = 0;
        for(int k = 0; k < i; k++)
        {
            int l = abyte0[k] & 0xff;
            abyte1[j++] = (byte)getPexChar(l >> 4);
            abyte1[j++] = (byte)getPexChar(l & 0xf);
        }

        return abyte1;
    }

    public static final byte[] stripData(byte abyte0[], byte byte0, boolean flag, int i)
    {
        int j = abyte0.length;
        if(flag)
        {
            for(j--; j >= i && abyte0[j] == byte0; j--);
            if(j < i)
            {
                j++;
                byte abyte1[] = new byte[i];
                System.arraycopy(abyte0, 0, abyte1, 0, j);
                for(; j < i; j++)
                    abyte1[j] = byte0;

                return abyte1;
            } else
            {
                byte abyte2[] = new byte[++j];
                System.arraycopy(abyte0, 0, abyte2, 0, j);
                return abyte2;
            }
        }
        int k = 0;
        int l;
        for(l = j - i; k < l && abyte0[k] == byte0; k++);
        if(k > l)
        {
            j -= k;
            byte abyte3[] = new byte[i];
            System.arraycopy(abyte0, k, abyte3, i - j, j);
            i -= j;
            for(i--; i >= 0; i--)
                abyte3[i] = byte0;

            return abyte3;
        } else
        {
            j -= k;
            byte abyte4[] = new byte[j];
            System.arraycopy(abyte0, k, abyte4, 0, j);
            return abyte4;
        }
    }

    public static final String strip(String s, char c, boolean flag, int i)
    {
        return new String(stripData(s.getBytes(), (byte)c, flag, i));
    }

    /**
     * @deprecated Method toData is deprecated
     */

    public static final byte[] toData(String s)
    {
        int i = s.length();
        byte abyte0[] = new byte[i];
        s.getBytes(0, i, abyte0, 0);
        return abyte0;
    }

    public static final String fromPexToBin(String s)
    {
        return getString(fromPexDataToBinData(getData(s)));
    }

    /**
     * @deprecated Method toData is deprecated
     */

    public static final void toData(long l, byte abyte0[], int i, int j)
    {
        i += j;
        for(; j > 0; j--)
        {
            abyte0[--i] = (byte)(int)(l % 10L | 48L);
            l /= 10L;
        }

    }

    /**
     * @deprecated Method toData is deprecated
     */

    public static final byte[] toData(long l)
    {
        int i = (int)Math.round(Math.floor(Math.log(l) / Math.log(10D))) + 1;
        byte abyte0[] = new byte[i];
        toData(l, abyte0, 0, i);
        return abyte0;
    }

    public static String[] splitParams(String s)
    {
        if(s == null)
            return null;
        int i = s.length();
        char ac[] = {
            ' ', '\t'
        };
        char ac1[] = {
            '"', "'".charAt(0)
        };
        Vector vector = new Vector();
        for(int j = 0; j < i;)
        {
            int k = i;
            for(int i1 = 0; i1 < ac.length; i1++)
            {
                int k1 = s.indexOf(ac[i1], j);
                if(k1 != -1 && k1 < k)
                    k = k1;
            }

            int j1 = i;
            for(int l1 = 0; l1 < ac1.length; l1++)
            {
                int i2 = s.indexOf(ac1[l1], j);
                if(i2 != -1 && i2 < j1)
                    j1 = i2;
            }

            if(j1 == j)
            {
                char c = s.charAt(j);
                int j2 = s.indexOf(c, j + 1);
                if(j2 == -1)
                    j2 = i;
                vector.addElement(s.substring(j + 1, j2));
                j = j2 + 1;
            } else
            if(k == j)
            {
                j++;
            } else
            {
                vector.addElement(s.substring(j, k));
                j = k;
            }
        }

        String as[] = new String[vector.size()];
        for(int l = 0; l < vector.size(); l++)
            as[l] = (String)vector.elementAt(l);

        return as;
    }

    public static final String fromBinToPex(String s)
    {
        return getString(fromBinDataToPexData(getData(s)));
    }

    /**
     * @deprecated Method fromDataPadded is deprecated
     */

    public static final byte[] fromDataPadded(byte abyte0[], byte byte0, boolean flag, int i)
    {
        int j = abyte0.length;
        if(flag)
        {
            for(j--; j >= i && abyte0[j] == byte0; j--);
            if(j < i)
            {
                j++;
                byte abyte1[] = new byte[i];
                System.arraycopy(abyte0, 0, abyte1, 0, j);
                for(; j < i; j++)
                    abyte1[j] = byte0;

                return abyte1;
            } else
            {
                byte abyte2[] = new byte[++j];
                System.arraycopy(abyte0, 0, abyte2, 0, j);
                return abyte2;
            }
        }
        int k = 0;
        int l;
        for(l = j - i; k < l && abyte0[k] == byte0; k++);
        if(k > l)
        {
            j -= k;
            byte abyte3[] = new byte[i];
            System.arraycopy(abyte0, k, abyte3, i - j, j);
            i -= k;
            for(i--; i >= 0; i--)
                abyte3[i] = byte0;

            return abyte3;
        } else
        {
            j -= k;
            byte abyte4[] = new byte[j];
            System.arraycopy(abyte0, k, abyte4, 0, j);
            return abyte4;
        }
    }

    /**
     * @deprecated Method fromStringPadded is deprecated
     */

    public static final String fromStringPadded(String s, byte byte0, boolean flag, int i)
    {
        byte abyte0[] = toData(s);
        return toString(fromDataPadded(abyte0, byte0, flag, i));
    }

    public static final String fromStringToHTMLSpecialChars(String s)
    {
        byte abyte0[] = getData(s);
        StringBuffer stringbuffer = new StringBuffer(abyte0.length);
        for(int i = 0; i < abyte0.length; i++)
        {
            byte byte0 = abyte0[i];
            if(byte0 < 0)
                stringbuffer.append((char)byte0);
            else
                stringbuffer.append(html_entities[byte0]);
        }

        return new String(stringbuffer);
    }

    /**
     * @deprecated Method toDataPadded is deprecated
     */

    public static final byte[] toDataPadded(String s, int i, byte byte0)
    {
        byte abyte0[] = new byte[i];
        int j = s.length();
        if(j >= i)
        {
            s.getBytes(0, i, abyte0, 0);
        } else
        {
            s.getBytes(0, j, abyte0, 0);
            for(; j < i; j++)
                abyte0[j] = byte0;

        }
        return abyte0;
    }

    /**
     * @deprecated Method toDataPadded is deprecated
     */

    public static final byte[] toDataPadded(byte abyte0[], int i, byte byte0, boolean flag)
    {
        byte abyte1[] = new byte[i];
        int j = abyte0.length;
        if(flag)
        {
            if(j >= i)
            {
                System.arraycopy(abyte0, 0, abyte1, 0, i);
            } else
            {
                System.arraycopy(abyte0, 0, abyte1, 0, j);
                for(; j < i; j++)
                    abyte1[j] = byte0;

            }
        } else
        if(j >= i)
        {
            System.arraycopy(abyte0, j - i, abyte1, 0, i);
        } else
        {
            j = i - j;
            System.arraycopy(abyte0, 0, abyte1, j, abyte0.length);
            for(j--; j >= 0; j--)
                abyte1[j] = byte0;

        }
        return abyte1;
    }

    /**
     * @deprecated Method toStringPadded is deprecated
     */

    public static final String toStringPadded(String s, int i, byte byte0, boolean flag)
    {
        return toString(toDataPadded(toData(s), i, byte0, flag));
    }

    public static final String getString(byte abyte0[])
    {
        return new String(abyte0, 0, 0, abyte0.length);
    }

    public static String fromLongToString(long l, int i)
    {
        if(l < 0L)
            l = -l;
        String s = String.valueOf(l);
        return resize(s, i, '0', false);
    }

    public static final String fromDataToStringHexDump(byte abyte0[], String s)
    {
        int i = 0;
        int j = abyte0.length;
        StringBuffer stringbuffer = new StringBuffer(j * 4);
        String s1 = "   ";
        boolean flag = false;
        for(int k = i + j; i < k;)
        {
            stringbuffer.append(s + toString(i, 4) + "(" + toStringHex(i, 4) + ")  ");
            int l = i;
            for(int i1 = 0; i1 < 16;)
            {
                if(i < k)
                {
                    if(abyte0[i] > 0)
                        stringbuffer.append(toStringHex(abyte0[i], 2) + " ");
                    else
                        stringbuffer.append(toStringHex(256 + abyte0[i], 2) + " ");
                } else
                {
                    stringbuffer.append(s1);
                }
                if(i1 == 7)
                    stringbuffer.append(" ");
                i1++;
                i++;
            }

            i = l;
            stringbuffer.append("  ");
            for(int j1 = 0; j1 < 16 && i < k; i++)
            {
                if(abyte0[i] < 32)
                    stringbuffer.append(".");
                else
                    stringbuffer.append(new String(abyte0, i, 1));
                j1++;
            }

            stringbuffer.append("\n");
        }

        return new String(stringbuffer);
    }

    public static final String fromStringToXmlDump(String s, String s1)
    {
        byte byte0 = 2;
        StringBuffer stringbuffer = new StringBuffer(1000);
        Stack stack = new Stack();
        String s2 = s;
        int i = 0;
        String s3;
        for(String s4 = " "; s2.length() > 0; s4 = s3)
        {
            s2 = s2.trim();
            int j = s2.indexOf('<');
            if(j != 0)
                break;
            int k = s2.indexOf('>') + 1;
            if(k == 0)
                break;
            s3 = s2.substring(j, k);
            String s5 = s2.substring(k);
            int l = s5.indexOf('<');
            if(l == -1)
                break;
            String s6 = s5.substring(0, l);
            if(s3.charAt(1) == '/')
            {
                if(stack.isEmpty())
                    break;
                stack.pop();
                if(!s6.trim().equals(""))
                    break;
                if(s4.charAt(1) == '/' || s4.charAt(1) == '!' || s4.charAt(1) == '?' || s4.charAt(s4.length() - 2) == '/')
                    stringbuffer.append("\n" + s1 + resize("", i, ' ', true) + s3);
                else
                    stringbuffer.append(s3);
                i -= byte0;
            } else
            if(s3.charAt(1) == '?' || s3.charAt(1) == '!')
            {
                stringbuffer.append("\n" + s1 + resize("", i, ' ', true) + s3);
            } else
            {
                boolean flag = s3.charAt(s3.length() - 2) == '/';
                i += byte0;
                if(!flag)
                    stack.push(s3);
                stringbuffer.append("\n" + s1 + resize("", i, ' ', true) + s3 + s6);
                if(flag)
                    i -= byte0;
            }
            s2 = s2.substring(k + l);
        }

        if(s2.length() <= 0)
            if(stack.isEmpty());
        stringbuffer.append("\n" + s1 + resize("", i, ' ', true) + s2);
        return stringbuffer.toString();
    }

    /**
     * @deprecated Method toStringHexDump is deprecated
     */

    public static final String toStringHexDump(byte abyte0[], int i, int j, String s)
    {
        StringBuffer stringbuffer = new StringBuffer(abyte0.length * 4);
        String s1 = "   ";
        boolean flag = false;
        for(int k = i + j; i < k;)
        {
            stringbuffer.append(s + toString(i, 4) + "(" + toStringHex(i, 4) + ")  ");
            int l = i;
            for(int i1 = 0; i1 < 16;)
            {
                if(i < k)
                {
                    if(abyte0[i] > 0)
                        stringbuffer.append(toStringHex(abyte0[i], 2) + " ");
                    else
                        stringbuffer.append(toStringHex(256 + abyte0[i], 2) + " ");
                } else
                {
                    stringbuffer.append(s1);
                }
                if(i1 == 7)
                    stringbuffer.append(" ");
                i1++;
                i++;
            }

            i = l;
            stringbuffer.append("  ");
            for(int j1 = 0; j1 < 16 && i < k; i++)
            {
                if(abyte0[i] < 32)
                    stringbuffer.append(".");
                else
                    stringbuffer.append(new String(abyte0, i, 1));
                j1++;
            }

            stringbuffer.append("\n");
        }

        return new String(stringbuffer);
    }

    /**
     * @deprecated Method toStringHexDump is deprecated
     */

    public static final String toStringHexDump(byte abyte0[], int i, int j)
    {
        return toStringHexDump(abyte0, i, j, "");
    }

    /**
     * @deprecated Method toStringHexDump is deprecated
     */

    public static final String toStringHexDump(byte abyte0[])
    {
        return toStringHexDump(abyte0, 0, abyte0.length, "");
    }

    public static final String fromIntToMsgType(int i)
    {
        return getString(fromIntToMsgTypeData(i));
    }

    /**
     * @deprecated Method toStringPadded is deprecated
     */

    public static final String toStringPadded(String s, int i, char c, boolean flag)
    {
        char ac[] = new char[i];
        boolean flag1 = false;
        if(s != null && s.length() > i)
            if(flag)
                return s.substring(0, i);
            else
                return s.substring(s.length() - i, s.length());
        if(s != null)
        {
            int j = s.length();
            if(flag)
            {
                s.getChars(0, j, ac, 0);
                for(int l = j; l < i; l++)
                    ac[l] = c;

            } else
            {
                s.getChars(0, j, ac, i - j);
                for(int i1 = 0; i1 < i - j; i1++)
                    ac[i1] = c;

            }
        } else
        {
            for(int k = 0; k < i; k++)
                ac[k] = c;

        }
        return new String(ac);
    }

    public static String toStackBackTrace(Throwable throwable)
    {
        if(throwable instanceof SQLException)
        {
            return toSQLStackBackTrace((SQLException)throwable);
        } else
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(512);
            throwable.printStackTrace(new PrintWriter(bytearrayoutputstream, true));
            return bytearrayoutputstream.toString();
        }
    }

    private static String toSQLStackBackTrace(SQLException sqlexception)
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(1024);
        PrintWriter printwriter = new PrintWriter(bytearrayoutputstream, true);
        SQLException sqlexception1 = sqlexception;
        int i = 0;
        for(; sqlexception1 != null; sqlexception1 = sqlexception1.getNextException())
        {
            i++;
            printwriter.println("Chained SQLException: " + i);
            sqlexception1.printStackTrace(printwriter);
            printwriter.println();
            printwriter.println();
        }

        ByteArrayOutputStream bytearrayoutputstream1 = new ByteArrayOutputStream(200);
        PrintWriter printwriter1 = new PrintWriter(bytearrayoutputstream1, true);
        printwriter1.println("A SQLException was generated consisting of " + i + " chained exceptions.  The exceptions are" + " listed in chronological order.");
        printwriter1.println();
        return bytearrayoutputstream1.toString() + bytearrayoutputstream.toString();
    }

    public static final void putData(byte abyte0[], byte abyte1[], int i)
    {
        System.arraycopy(abyte0, 0, abyte1, i, abyte0.length);
    }

    public static final byte[] getData(byte abyte0[], int i, int j)
    {
        byte abyte1[] = new byte[j];
        System.arraycopy(abyte0, i, abyte1, 0, j);
        return abyte1;
    }

    public static final byte[] getData(String s)
    {
        int i = s.length();
        byte abyte0[] = new byte[i];
        s.getBytes(0, i, abyte0, 0);
        return abyte0;
    }

    public static final byte[] fromAsciiDataToEbcdicData(byte abyte0[])
    {
        int i = abyte0.length;
        byte abyte1[] = new byte[i];
        for(int j = 0; j < i; j++)
            abyte1[j] = (byte)ascii_to_ebcdic[abyte0[j] & 0xff];

        return abyte1;
    }

    public static final byte[] fromEbcdicDataToAsciiData(byte abyte0[])
    {
        int i = abyte0.length;
        byte abyte1[] = new byte[i];
        for(int j = 0; j < i; j++)
            abyte1[j] = (byte)ebcdic_to_ascii[abyte0[j] & 0xff];

        return abyte1;
    }

    public static final byte[] resizeData(byte abyte0[], int i, byte byte0, boolean flag)
    {
        byte abyte1[] = new byte[i];
        int j = abyte0.length;
        if(flag)
        {
            if(j >= i)
            {
                System.arraycopy(abyte0, 0, abyte1, 0, i);
            } else
            {
                System.arraycopy(abyte0, 0, abyte1, 0, j);
                for(; j < i; j++)
                    abyte1[j] = byte0;

            }
        } else
        if(j >= i)
        {
            System.arraycopy(abyte0, j - i, abyte1, 0, i);
        } else
        {
            j = i - j;
            System.arraycopy(abyte0, 0, abyte1, j, abyte0.length);
            for(j--; j >= 0; j--)
                abyte1[j] = byte0;

        }
        return abyte1;
    }

    public static Vector split(String s, char c)
    {
        Vector vector = new Vector();
        boolean flag = false;
        int i = flag ? 1 : 0;
        do
        {
            int j = s.indexOf(c, i);
            if(j == -1)
            {
                if(s.length() > 0)
                    vector.addElement(s.substring(i));
                break;
            }
            vector.addElement(s.substring(i, j));
            i = j + 1;
        } while(true);
        return vector;
    }

    public static final byte[] fromIntToMsgTypeData(int i)
    {
        byte abyte0[] = new byte[4];
        for(int j = 3; j >= 0; j--)
        {
            abyte0[j] = (byte)getHexChar(i & 0xf);
            i >>= 4;
        }

        return abyte0;
    }

    /**
     * @deprecated Method fromLongToMsgType is deprecated
     */

    public static final String fromLongToMsgType(long l)
    {
        return getString(fromLongToMsgTypeData(l));
    }

    public static final String fromEbcdicToAscii(String s)
    {
        return getString(fromEbcdicDataToAsciiData(getData(s)));
    }

    private static final byte getHexNibble(byte byte0)
    {
        if(byte0 >= 48 && byte0 <= 57)
            return (byte)(byte0 & 0xf);
        if(byte0 >= 65 && byte0 <= 70)
            return (byte)(byte0 - 55);
        if(byte0 >= 97 && byte0 <= 102)
            return (byte)(byte0 - 87);
        else
            return 0;
    }

    public static final String fromHexToBin(String s)
    {
        return getString(fromHexDataToBinData(getData(s)));
    }

    /**
     * @deprecated Method getHexInAsciiFromBin is deprecated
     */

    public static String getHexInAsciiFromBin(byte abyte0[], int i, int j)
    {
        StringBuffer stringbuffer = new StringBuffer(j * 2);
        for(int k = i + j; i < k; i++)
        {
            byte byte0 = (byte)(abyte0[i] >> 4 & 0xf);
            if(byte0 <= 9)
                stringbuffer.append((char)(byte0 + 48));
            else
                stringbuffer.append((char)((byte0 - 10) + 65));
            byte0 = (byte)(abyte0[i] & 0xf);
            if(byte0 <= 9)
                stringbuffer.append((char)(byte0 + 48));
            else
                stringbuffer.append((char)((byte0 - 10) + 65));
        }

        return stringbuffer.toString();
    }

    public static final String fromAsciiToEbcdic(String s)
    {
        return getString(fromAsciiDataToEbcdicData(getData(s)));
    }

    private Convert()
    {
    }

    public static final String fromBinToHex(String s)
    {
        return getString(fromBinDataToHexData(getData(s)));
    }

    /**
     * @deprecated Method fromDataHex is deprecated
     */

    public static final long fromDataHex(byte abyte0[], int i, int j)
    {
        long l = 0L;
        for(; j > 0; j--)
        {
            if(abyte0[i] >= 48 && abyte0[i] <= 57)
                l = l << 4 | (long)(abyte0[i] & 0xf);
            else
            if(abyte0[i] >= 65 && abyte0[i] <= 70)
                l = l << 4 | (long)(abyte0[i] - 55);
            else
            if(abyte0[i] >= 97 && abyte0[i] <= 102)
                l = l << 4 | (long)(abyte0[i] - 87);
            else
                return -1L;
            i++;
        }

        return l;
    }

    /**
     * @deprecated Method fromStringHex is deprecated
     */

    public static final String fromStringHex(String s)
    {
        byte abyte0[] = toData(s);
        int i = s.length() / 2;
        byte abyte1[] = new byte[i];
        int j = 0;
        int k = 0;
        for(; j < i; j++)
        {
            if(abyte0[k] >= 48 && abyte0[k] <= 57)
                abyte1[j] = (byte)((abyte0[k] & 0xf) << 4);
            else
            if(abyte0[k] >= 65 && abyte0[k] <= 70)
                abyte1[j] = (byte)(abyte0[k] - 55 << 4);
            else
            if(abyte0[k] >= 97 && abyte0[k] <= 102)
                abyte1[j] = (byte)(abyte0[k] - 87 << 4);
            else
                return null;
            k++;
            if(abyte0[k] >= 48 && abyte0[k] <= 57)
                abyte1[j] |= abyte0[k] & 0xf;
            else
            if(abyte0[k] >= 65 && abyte0[k] <= 70)
                abyte1[j] |= abyte0[k] - 55;
            else
            if(abyte0[k] >= 97 && abyte0[k] <= 102)
                abyte1[j] |= abyte0[k] - 87;
            else
                return null;
            k++;
        }

        return new String(abyte1, 0, 0, i);
    }

    private static final int getHexChar(int i)
    {
        if(i < 10)
            return i + 48;
        else
            return i + 55;
    }

    public static final String resize(String s, int i, char c, boolean flag)
    {
        return getString(resizeData(getData(s), i, (byte)c, flag));
    }

    public static String fromDoubleToUnsignedLongString(double d, int i)
    {
        d = Math.abs(d);
        String s = String.valueOf((long)d);
        return s;
    }

    public static final byte[] fromHexDataToBinData(byte abyte0[])
    {
        int i = abyte0.length / 2;
        byte abyte1[] = new byte[i];
        int j = 0;
        for(int k = 0; k < i; k++)
            abyte1[k] = (byte)(getHexNibble(abyte0[j++]) << 4 | getHexNibble(abyte0[j++]));

        return abyte1;
    }

    public static final byte[] fromBinDataToHexData(byte abyte0[])
    {
        int i = abyte0.length;
        byte abyte1[] = new byte[i * 2];
        int j = 0;
        for(int k = 0; k < i; k++)
        {
            int l = abyte0[k] & 0xff;
            abyte1[j++] = (byte)getHexChar(l >> 4);
            abyte1[j++] = (byte)getHexChar(l & 0xf);
        }

        return abyte1;
    }

    public static byte[] fromHexStringToHexData(String s)
    {
        if(s.length() % 2 == 1)
            s = '0' + s;
        byte abyte0[] = new byte[s.length() / 2];
        for(int i = 0; i < abyte0.length; i++)
        {
            String s1 = s.substring(i * 2, i * 2 + 2);
            abyte0[i] = (byte)Integer.parseInt(s1, 16);
        }

        return abyte0;
    }

    /**
     * @deprecated Method fromLongToMsgTypeData is deprecated
     */

    public static final byte[] fromLongToMsgTypeData(long l)
    {
        byte abyte0[] = new byte[4];
        for(int i = 3; i >= 0; i--)
        {
            abyte0[i] = (byte)getHexChar((int)l & 0xf);
            l >>= 4;
        }

        return abyte0;
    }

    private static final short ebcdic_to_ascii[];
    private static final short ascii_to_ebcdic[];
    private static String html_entities[];
    private static final int positive_byte_mask = 255;
    private static final long positive_int_mask = -1L;

    static 
    {
        ebcdic_to_ascii = new short[256];
        ascii_to_ebcdic = new short[256];
        html_entities = new String[256];
        ascii_to_ebcdic[0] = 0;
        ascii_to_ebcdic[1] = 1;
        ascii_to_ebcdic[2] = 2;
        ascii_to_ebcdic[3] = 3;
        ascii_to_ebcdic[4] = 55;
        ascii_to_ebcdic[5] = 45;
        ascii_to_ebcdic[6] = 46;
        ascii_to_ebcdic[7] = 47;
        ascii_to_ebcdic[8] = 22;
        ascii_to_ebcdic[9] = 5;
        ascii_to_ebcdic[10] = 37;
        ascii_to_ebcdic[11] = 11;
        ascii_to_ebcdic[12] = 12;
        ascii_to_ebcdic[13] = 13;
        ascii_to_ebcdic[14] = 14;
        ascii_to_ebcdic[15] = 15;
        ascii_to_ebcdic[16] = 16;
        ascii_to_ebcdic[17] = 17;
        ascii_to_ebcdic[18] = 18;
        ascii_to_ebcdic[19] = 19;
        ascii_to_ebcdic[20] = 60;
        ascii_to_ebcdic[21] = 61;
        ascii_to_ebcdic[22] = 50;
        ascii_to_ebcdic[23] = 38;
        ascii_to_ebcdic[24] = 24;
        ascii_to_ebcdic[25] = 25;
        ascii_to_ebcdic[26] = 63;
        ascii_to_ebcdic[27] = 39;
        ascii_to_ebcdic[28] = 28;
        ascii_to_ebcdic[29] = 29;
        ascii_to_ebcdic[30] = 30;
        ascii_to_ebcdic[31] = 31;
        ascii_to_ebcdic[32] = 64;
        ascii_to_ebcdic[33] = 90;
        ascii_to_ebcdic[34] = 127;
        ascii_to_ebcdic[35] = 123;
        ascii_to_ebcdic[36] = 91;
        ascii_to_ebcdic[37] = 108;
        ascii_to_ebcdic[38] = 80;
        ascii_to_ebcdic[39] = 125;
        ascii_to_ebcdic[40] = 77;
        ascii_to_ebcdic[41] = 93;
        ascii_to_ebcdic[42] = 92;
        ascii_to_ebcdic[43] = 78;
        ascii_to_ebcdic[44] = 107;
        ascii_to_ebcdic[45] = 96;
        ascii_to_ebcdic[46] = 75;
        ascii_to_ebcdic[47] = 97;
        ascii_to_ebcdic[48] = 240;
        ascii_to_ebcdic[49] = 241;
        ascii_to_ebcdic[50] = 242;
        ascii_to_ebcdic[51] = 243;
        ascii_to_ebcdic[52] = 244;
        ascii_to_ebcdic[53] = 245;
        ascii_to_ebcdic[54] = 246;
        ascii_to_ebcdic[55] = 247;
        ascii_to_ebcdic[56] = 248;
        ascii_to_ebcdic[57] = 249;
        ascii_to_ebcdic[58] = 122;
        ascii_to_ebcdic[59] = 94;
        ascii_to_ebcdic[60] = 76;
        ascii_to_ebcdic[61] = 126;
        ascii_to_ebcdic[62] = 110;
        ascii_to_ebcdic[63] = 111;
        ascii_to_ebcdic[64] = 124;
        ascii_to_ebcdic[65] = 193;
        ascii_to_ebcdic[66] = 194;
        ascii_to_ebcdic[67] = 195;
        ascii_to_ebcdic[68] = 196;
        ascii_to_ebcdic[69] = 197;
        ascii_to_ebcdic[70] = 198;
        ascii_to_ebcdic[71] = 199;
        ascii_to_ebcdic[72] = 200;
        ascii_to_ebcdic[73] = 201;
        ascii_to_ebcdic[74] = 209;
        ascii_to_ebcdic[75] = 210;
        ascii_to_ebcdic[76] = 211;
        ascii_to_ebcdic[77] = 212;
        ascii_to_ebcdic[78] = 213;
        ascii_to_ebcdic[79] = 214;
        ascii_to_ebcdic[80] = 215;
        ascii_to_ebcdic[81] = 216;
        ascii_to_ebcdic[82] = 217;
        ascii_to_ebcdic[83] = 226;
        ascii_to_ebcdic[84] = 227;
        ascii_to_ebcdic[85] = 228;
        ascii_to_ebcdic[86] = 229;
        ascii_to_ebcdic[87] = 230;
        ascii_to_ebcdic[88] = 231;
        ascii_to_ebcdic[89] = 232;
        ascii_to_ebcdic[90] = 233;
        ascii_to_ebcdic[91] = 173;
        ascii_to_ebcdic[92] = 224;
        ascii_to_ebcdic[93] = 189;
        ascii_to_ebcdic[94] = 95;
        ascii_to_ebcdic[95] = 109;
        ascii_to_ebcdic[96] = 121;
        ascii_to_ebcdic[97] = 129;
        ascii_to_ebcdic[98] = 130;
        ascii_to_ebcdic[99] = 131;
        ascii_to_ebcdic[100] = 132;
        ascii_to_ebcdic[101] = 133;
        ascii_to_ebcdic[102] = 134;
        ascii_to_ebcdic[103] = 135;
        ascii_to_ebcdic[104] = 136;
        ascii_to_ebcdic[105] = 137;
        ascii_to_ebcdic[106] = 145;
        ascii_to_ebcdic[107] = 146;
        ascii_to_ebcdic[108] = 147;
        ascii_to_ebcdic[109] = 148;
        ascii_to_ebcdic[110] = 149;
        ascii_to_ebcdic[111] = 150;
        ascii_to_ebcdic[112] = 151;
        ascii_to_ebcdic[113] = 152;
        ascii_to_ebcdic[114] = 153;
        ascii_to_ebcdic[115] = 162;
        ascii_to_ebcdic[116] = 163;
        ascii_to_ebcdic[117] = 164;
        ascii_to_ebcdic[118] = 165;
        ascii_to_ebcdic[119] = 166;
        ascii_to_ebcdic[120] = 167;
        ascii_to_ebcdic[121] = 168;
        ascii_to_ebcdic[122] = 169;
        ascii_to_ebcdic[123] = 192;
        ascii_to_ebcdic[124] = 79;
        ascii_to_ebcdic[125] = 208;
        ascii_to_ebcdic[126] = 161;
        ascii_to_ebcdic[127] = 7;
        ascii_to_ebcdic[128] = 32;
        ascii_to_ebcdic[129] = 33;
        ascii_to_ebcdic[130] = 34;
        ascii_to_ebcdic[131] = 35;
        ascii_to_ebcdic[132] = 36;
        ascii_to_ebcdic[133] = 21;
        ascii_to_ebcdic[134] = 6;
        ascii_to_ebcdic[135] = 23;
        ascii_to_ebcdic[136] = 40;
        ascii_to_ebcdic[137] = 41;
        ascii_to_ebcdic[138] = 42;
        ascii_to_ebcdic[139] = 43;
        ascii_to_ebcdic[140] = 44;
        ascii_to_ebcdic[141] = 9;
        ascii_to_ebcdic[142] = 10;
        ascii_to_ebcdic[143] = 27;
        ascii_to_ebcdic[144] = 48;
        ascii_to_ebcdic[145] = 49;
        ascii_to_ebcdic[146] = 26;
        ascii_to_ebcdic[147] = 51;
        ascii_to_ebcdic[148] = 52;
        ascii_to_ebcdic[149] = 53;
        ascii_to_ebcdic[150] = 54;
        ascii_to_ebcdic[151] = 8;
        ascii_to_ebcdic[152] = 56;
        ascii_to_ebcdic[153] = 57;
        ascii_to_ebcdic[154] = 58;
        ascii_to_ebcdic[155] = 59;
        ascii_to_ebcdic[156] = 4;
        ascii_to_ebcdic[157] = 20;
        ascii_to_ebcdic[158] = 62;
        ascii_to_ebcdic[159] = 255;
        ascii_to_ebcdic[160] = 65;
        ascii_to_ebcdic[161] = 170;
        ascii_to_ebcdic[162] = 74;
        ascii_to_ebcdic[163] = 177;
        ascii_to_ebcdic[164] = 159;
        ascii_to_ebcdic[165] = 178;
        ascii_to_ebcdic[166] = 106;
        ascii_to_ebcdic[167] = 181;
        ascii_to_ebcdic[168] = 187;
        ascii_to_ebcdic[169] = 180;
        ascii_to_ebcdic[170] = 154;
        ascii_to_ebcdic[171] = 138;
        ascii_to_ebcdic[172] = 176;
        ascii_to_ebcdic[173] = 202;
        ascii_to_ebcdic[174] = 175;
        ascii_to_ebcdic[175] = 188;
        ascii_to_ebcdic[176] = 144;
        ascii_to_ebcdic[177] = 143;
        ascii_to_ebcdic[178] = 234;
        ascii_to_ebcdic[179] = 250;
        ascii_to_ebcdic[180] = 190;
        ascii_to_ebcdic[181] = 160;
        ascii_to_ebcdic[182] = 182;
        ascii_to_ebcdic[183] = 179;
        ascii_to_ebcdic[184] = 157;
        ascii_to_ebcdic[185] = 218;
        ascii_to_ebcdic[186] = 155;
        ascii_to_ebcdic[187] = 139;
        ascii_to_ebcdic[188] = 183;
        ascii_to_ebcdic[189] = 184;
        ascii_to_ebcdic[190] = 185;
        ascii_to_ebcdic[191] = 171;
        ascii_to_ebcdic[192] = 100;
        ascii_to_ebcdic[193] = 101;
        ascii_to_ebcdic[194] = 98;
        ascii_to_ebcdic[195] = 102;
        ascii_to_ebcdic[196] = 99;
        ascii_to_ebcdic[197] = 103;
        ascii_to_ebcdic[198] = 158;
        ascii_to_ebcdic[199] = 104;
        ascii_to_ebcdic[200] = 116;
        ascii_to_ebcdic[201] = 113;
        ascii_to_ebcdic[202] = 114;
        ascii_to_ebcdic[203] = 115;
        ascii_to_ebcdic[204] = 120;
        ascii_to_ebcdic[205] = 117;
        ascii_to_ebcdic[206] = 118;
        ascii_to_ebcdic[207] = 119;
        ascii_to_ebcdic[208] = 172;
        ascii_to_ebcdic[209] = 105;
        ascii_to_ebcdic[210] = 237;
        ascii_to_ebcdic[211] = 238;
        ascii_to_ebcdic[212] = 235;
        ascii_to_ebcdic[213] = 239;
        ascii_to_ebcdic[214] = 236;
        ascii_to_ebcdic[215] = 191;
        ascii_to_ebcdic[216] = 128;
        ascii_to_ebcdic[217] = 253;
        ascii_to_ebcdic[218] = 254;
        ascii_to_ebcdic[219] = 251;
        ascii_to_ebcdic[220] = 252;
        ascii_to_ebcdic[221] = 186;
        ascii_to_ebcdic[222] = 174;
        ascii_to_ebcdic[223] = 89;
        ascii_to_ebcdic[224] = 68;
        ascii_to_ebcdic[225] = 69;
        ascii_to_ebcdic[226] = 66;
        ascii_to_ebcdic[227] = 70;
        ascii_to_ebcdic[228] = 67;
        ascii_to_ebcdic[229] = 71;
        ascii_to_ebcdic[230] = 156;
        ascii_to_ebcdic[231] = 72;
        ascii_to_ebcdic[232] = 84;
        ascii_to_ebcdic[233] = 81;
        ascii_to_ebcdic[234] = 82;
        ascii_to_ebcdic[235] = 83;
        ascii_to_ebcdic[236] = 88;
        ascii_to_ebcdic[237] = 85;
        ascii_to_ebcdic[238] = 86;
        ascii_to_ebcdic[239] = 87;
        ascii_to_ebcdic[240] = 140;
        ascii_to_ebcdic[241] = 73;
        ascii_to_ebcdic[242] = 205;
        ascii_to_ebcdic[243] = 206;
        ascii_to_ebcdic[244] = 203;
        ascii_to_ebcdic[245] = 207;
        ascii_to_ebcdic[246] = 204;
        ascii_to_ebcdic[247] = 225;
        ascii_to_ebcdic[248] = 112;
        ascii_to_ebcdic[249] = 221;
        ascii_to_ebcdic[250] = 222;
        ascii_to_ebcdic[251] = 219;
        ascii_to_ebcdic[252] = 220;
        ascii_to_ebcdic[253] = 141;
        ascii_to_ebcdic[254] = 142;
        ascii_to_ebcdic[255] = 223;
        ebcdic_to_ascii[0] = 0;
        ebcdic_to_ascii[1] = 1;
        ebcdic_to_ascii[2] = 2;
        ebcdic_to_ascii[3] = 3;
        ebcdic_to_ascii[4] = 156;
        ebcdic_to_ascii[5] = 9;
        ebcdic_to_ascii[6] = 134;
        ebcdic_to_ascii[7] = 127;
        ebcdic_to_ascii[8] = 151;
        ebcdic_to_ascii[9] = 141;
        ebcdic_to_ascii[10] = 142;
        ebcdic_to_ascii[11] = 11;
        ebcdic_to_ascii[12] = 12;
        ebcdic_to_ascii[13] = 13;
        ebcdic_to_ascii[14] = 14;
        ebcdic_to_ascii[15] = 15;
        ebcdic_to_ascii[16] = 16;
        ebcdic_to_ascii[17] = 17;
        ebcdic_to_ascii[18] = 18;
        ebcdic_to_ascii[19] = 19;
        ebcdic_to_ascii[20] = 157;
        ebcdic_to_ascii[21] = 133;
        ebcdic_to_ascii[22] = 8;
        ebcdic_to_ascii[23] = 135;
        ebcdic_to_ascii[24] = 24;
        ebcdic_to_ascii[25] = 25;
        ebcdic_to_ascii[26] = 146;
        ebcdic_to_ascii[27] = 143;
        ebcdic_to_ascii[28] = 28;
        ebcdic_to_ascii[29] = 29;
        ebcdic_to_ascii[30] = 30;
        ebcdic_to_ascii[31] = 31;
        ebcdic_to_ascii[32] = 128;
        ebcdic_to_ascii[33] = 129;
        ebcdic_to_ascii[34] = 130;
        ebcdic_to_ascii[35] = 131;
        ebcdic_to_ascii[36] = 132;
        ebcdic_to_ascii[37] = 10;
        ebcdic_to_ascii[38] = 23;
        ebcdic_to_ascii[39] = 27;
        ebcdic_to_ascii[40] = 136;
        ebcdic_to_ascii[41] = 137;
        ebcdic_to_ascii[42] = 138;
        ebcdic_to_ascii[43] = 139;
        ebcdic_to_ascii[44] = 140;
        ebcdic_to_ascii[45] = 5;
        ebcdic_to_ascii[46] = 6;
        ebcdic_to_ascii[47] = 7;
        ebcdic_to_ascii[48] = 144;
        ebcdic_to_ascii[49] = 145;
        ebcdic_to_ascii[50] = 22;
        ebcdic_to_ascii[51] = 147;
        ebcdic_to_ascii[52] = 148;
        ebcdic_to_ascii[53] = 149;
        ebcdic_to_ascii[54] = 150;
        ebcdic_to_ascii[55] = 4;
        ebcdic_to_ascii[56] = 152;
        ebcdic_to_ascii[57] = 153;
        ebcdic_to_ascii[58] = 154;
        ebcdic_to_ascii[59] = 155;
        ebcdic_to_ascii[60] = 20;
        ebcdic_to_ascii[61] = 21;
        ebcdic_to_ascii[62] = 158;
        ebcdic_to_ascii[63] = 26;
        ebcdic_to_ascii[64] = 32;
        ebcdic_to_ascii[65] = 160;
        ebcdic_to_ascii[66] = 226;
        ebcdic_to_ascii[67] = 228;
        ebcdic_to_ascii[68] = 224;
        ebcdic_to_ascii[69] = 225;
        ebcdic_to_ascii[70] = 227;
        ebcdic_to_ascii[71] = 229;
        ebcdic_to_ascii[72] = 231;
        ebcdic_to_ascii[73] = 241;
        ebcdic_to_ascii[74] = 162;
        ebcdic_to_ascii[75] = 46;
        ebcdic_to_ascii[76] = 60;
        ebcdic_to_ascii[77] = 40;
        ebcdic_to_ascii[78] = 43;
        ebcdic_to_ascii[79] = 124;
        ebcdic_to_ascii[80] = 38;
        ebcdic_to_ascii[81] = 233;
        ebcdic_to_ascii[82] = 234;
        ebcdic_to_ascii[83] = 235;
        ebcdic_to_ascii[84] = 232;
        ebcdic_to_ascii[85] = 237;
        ebcdic_to_ascii[86] = 238;
        ebcdic_to_ascii[87] = 239;
        ebcdic_to_ascii[88] = 236;
        ebcdic_to_ascii[89] = 223;
        ebcdic_to_ascii[90] = 33;
        ebcdic_to_ascii[91] = 36;
        ebcdic_to_ascii[92] = 42;
        ebcdic_to_ascii[93] = 41;
        ebcdic_to_ascii[94] = 59;
        ebcdic_to_ascii[95] = 94;
        ebcdic_to_ascii[96] = 45;
        ebcdic_to_ascii[97] = 47;
        ebcdic_to_ascii[98] = 194;
        ebcdic_to_ascii[99] = 196;
        ebcdic_to_ascii[100] = 192;
        ebcdic_to_ascii[101] = 193;
        ebcdic_to_ascii[102] = 195;
        ebcdic_to_ascii[103] = 197;
        ebcdic_to_ascii[104] = 199;
        ebcdic_to_ascii[105] = 209;
        ebcdic_to_ascii[106] = 166;
        ebcdic_to_ascii[107] = 44;
        ebcdic_to_ascii[108] = 37;
        ebcdic_to_ascii[109] = 95;
        ebcdic_to_ascii[110] = 62;
        ebcdic_to_ascii[111] = 63;
        ebcdic_to_ascii[112] = 248;
        ebcdic_to_ascii[113] = 201;
        ebcdic_to_ascii[114] = 202;
        ebcdic_to_ascii[115] = 203;
        ebcdic_to_ascii[116] = 200;
        ebcdic_to_ascii[117] = 205;
        ebcdic_to_ascii[118] = 206;
        ebcdic_to_ascii[119] = 207;
        ebcdic_to_ascii[120] = 204;
        ebcdic_to_ascii[121] = 96;
        ebcdic_to_ascii[122] = 58;
        ebcdic_to_ascii[123] = 35;
        ebcdic_to_ascii[124] = 64;
        ebcdic_to_ascii[125] = 39;
        ebcdic_to_ascii[126] = 61;
        ebcdic_to_ascii[127] = 34;
        ebcdic_to_ascii[128] = 216;
        ebcdic_to_ascii[129] = 97;
        ebcdic_to_ascii[130] = 98;
        ebcdic_to_ascii[131] = 99;
        ebcdic_to_ascii[132] = 100;
        ebcdic_to_ascii[133] = 101;
        ebcdic_to_ascii[134] = 102;
        ebcdic_to_ascii[135] = 103;
        ebcdic_to_ascii[136] = 104;
        ebcdic_to_ascii[137] = 105;
        ebcdic_to_ascii[138] = 171;
        ebcdic_to_ascii[139] = 187;
        ebcdic_to_ascii[140] = 240;
        ebcdic_to_ascii[141] = 253;
        ebcdic_to_ascii[142] = 254;
        ebcdic_to_ascii[143] = 177;
        ebcdic_to_ascii[144] = 176;
        ebcdic_to_ascii[145] = 106;
        ebcdic_to_ascii[146] = 107;
        ebcdic_to_ascii[147] = 108;
        ebcdic_to_ascii[148] = 109;
        ebcdic_to_ascii[149] = 110;
        ebcdic_to_ascii[150] = 111;
        ebcdic_to_ascii[151] = 112;
        ebcdic_to_ascii[152] = 113;
        ebcdic_to_ascii[153] = 114;
        ebcdic_to_ascii[154] = 170;
        ebcdic_to_ascii[155] = 186;
        ebcdic_to_ascii[156] = 230;
        ebcdic_to_ascii[157] = 184;
        ebcdic_to_ascii[158] = 198;
        ebcdic_to_ascii[159] = 164;
        ebcdic_to_ascii[160] = 181;
        ebcdic_to_ascii[161] = 126;
        ebcdic_to_ascii[162] = 115;
        ebcdic_to_ascii[163] = 116;
        ebcdic_to_ascii[164] = 117;
        ebcdic_to_ascii[165] = 118;
        ebcdic_to_ascii[166] = 119;
        ebcdic_to_ascii[167] = 120;
        ebcdic_to_ascii[168] = 121;
        ebcdic_to_ascii[169] = 122;
        ebcdic_to_ascii[170] = 161;
        ebcdic_to_ascii[171] = 191;
        ebcdic_to_ascii[172] = 208;
        ebcdic_to_ascii[173] = 91;
        ebcdic_to_ascii[174] = 222;
        ebcdic_to_ascii[175] = 174;
        ebcdic_to_ascii[176] = 172;
        ebcdic_to_ascii[177] = 163;
        ebcdic_to_ascii[178] = 165;
        ebcdic_to_ascii[179] = 183;
        ebcdic_to_ascii[180] = 169;
        ebcdic_to_ascii[181] = 167;
        ebcdic_to_ascii[182] = 182;
        ebcdic_to_ascii[183] = 188;
        ebcdic_to_ascii[184] = 189;
        ebcdic_to_ascii[185] = 190;
        ebcdic_to_ascii[186] = 221;
        ebcdic_to_ascii[187] = 168;
        ebcdic_to_ascii[188] = 175;
        ebcdic_to_ascii[189] = 93;
        ebcdic_to_ascii[190] = 180;
        ebcdic_to_ascii[191] = 215;
        ebcdic_to_ascii[192] = 123;
        ebcdic_to_ascii[193] = 65;
        ebcdic_to_ascii[194] = 66;
        ebcdic_to_ascii[195] = 67;
        ebcdic_to_ascii[196] = 68;
        ebcdic_to_ascii[197] = 69;
        ebcdic_to_ascii[198] = 70;
        ebcdic_to_ascii[199] = 71;
        ebcdic_to_ascii[200] = 72;
        ebcdic_to_ascii[201] = 73;
        ebcdic_to_ascii[202] = 173;
        ebcdic_to_ascii[203] = 244;
        ebcdic_to_ascii[204] = 246;
        ebcdic_to_ascii[205] = 242;
        ebcdic_to_ascii[206] = 243;
        ebcdic_to_ascii[207] = 245;
        ebcdic_to_ascii[208] = 125;
        ebcdic_to_ascii[209] = 74;
        ebcdic_to_ascii[210] = 75;
        ebcdic_to_ascii[211] = 76;
        ebcdic_to_ascii[212] = 77;
        ebcdic_to_ascii[213] = 78;
        ebcdic_to_ascii[214] = 79;
        ebcdic_to_ascii[215] = 80;
        ebcdic_to_ascii[216] = 81;
        ebcdic_to_ascii[217] = 82;
        ebcdic_to_ascii[218] = 185;
        ebcdic_to_ascii[219] = 251;
        ebcdic_to_ascii[220] = 252;
        ebcdic_to_ascii[221] = 249;
        ebcdic_to_ascii[222] = 250;
        ebcdic_to_ascii[223] = 255;
        ebcdic_to_ascii[224] = 92;
        ebcdic_to_ascii[225] = 247;
        ebcdic_to_ascii[226] = 83;
        ebcdic_to_ascii[227] = 84;
        ebcdic_to_ascii[228] = 85;
        ebcdic_to_ascii[229] = 86;
        ebcdic_to_ascii[230] = 87;
        ebcdic_to_ascii[231] = 88;
        ebcdic_to_ascii[232] = 89;
        ebcdic_to_ascii[233] = 90;
        ebcdic_to_ascii[234] = 178;
        ebcdic_to_ascii[235] = 212;
        ebcdic_to_ascii[236] = 214;
        ebcdic_to_ascii[237] = 210;
        ebcdic_to_ascii[238] = 211;
        ebcdic_to_ascii[239] = 213;
        ebcdic_to_ascii[240] = 48;
        ebcdic_to_ascii[241] = 49;
        ebcdic_to_ascii[242] = 50;
        ebcdic_to_ascii[243] = 51;
        ebcdic_to_ascii[244] = 52;
        ebcdic_to_ascii[245] = 53;
        ebcdic_to_ascii[246] = 54;
        ebcdic_to_ascii[247] = 55;
        ebcdic_to_ascii[248] = 56;
        ebcdic_to_ascii[249] = 57;
        ebcdic_to_ascii[250] = 179;
        ebcdic_to_ascii[251] = 219;
        ebcdic_to_ascii[252] = 220;
        ebcdic_to_ascii[253] = 217;
        ebcdic_to_ascii[254] = 218;
        ebcdic_to_ascii[255] = 0;
        html_entities = new String[128];
        for(int i = 0; i < 128; i++)
            html_entities[i] = new String(new byte[] {
                (byte)i
            });

        html_entities[34] = new String("&quot;");
        html_entities[38] = new String("&amp;");
        html_entities[39] = new String("&#039;");
        html_entities[60] = new String("&lt;");
        html_entities[62] = new String("&gt;");
    }
}

package com.datapro.dibs.encrypin;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Conversion
{

    public Conversion()
    {
    }

    public static void main(String args[])
    {
        InputStreamReader inputstreamreader = new InputStreamReader(System.in);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        try
        {
            System.out.println("Indique el n\243mero de metros y pulse RETURN");
            String s = bufferedreader.readLine();
            Double double1 = new Double(s);
            double d = double1.doubleValue();
            double d1 = d * 39.270000000000003D;
            double d2 = d1 / 12D;
            d1 %= 12D;
            d2 -= d1 / 12D;
            System.out.println("Conversi\242n de metros en un complejo de pies y pulgadas");
            System.out.println(d + " metros son " + d2 + " pies y " + d1 + " pulgadas");
        }
        catch(Exception exception)
        {
            System.out.println("Cualquier tipo de error");
        }
    }
}

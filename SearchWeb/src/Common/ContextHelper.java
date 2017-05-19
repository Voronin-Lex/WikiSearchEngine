package Common;
import Search.QuerryProcessors.Resources;

import java.io.*;
import java.util.*;

import static java.lang.Math.log;

public class ContextHelper {
    public static int decodeNumber(ArrayList<Byte> bytes)
    {
        int number = 0;
        for (byte currentByte : bytes) {
            if ((currentByte & 0xff) < 128) {
                number = 128 * number + currentByte;
            } else {
                number  = (128 * number + ((currentByte - 128) & 0xff));
                break;
            }
        }
        return number;
    }

    public static byte[] encodeNumber(int number) {
        if (number == 0) {
            return new byte[]{-128};
        }
        int numberOfBytes = (int) (log(number) / log(128)) + 1;
        byte[] resultValue = new byte[numberOfBytes];
        int positionOfByte = numberOfBytes - 1;
        do {
            resultValue[positionOfByte--] = (byte) (number % 128);
            number /= 128;
        } while (positionOfByte >= 0);
        resultValue[numberOfBytes - 1] += 128;
        return resultValue;
    }

    public static String getNormalForm(String querry) throws IOException {
        String normalForm;
        Process stemmer = Runtime.getRuntime().exec(Resources.stemPath + " -c");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stemmer.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stemmer.getOutputStream()));
        writer.write(querry);
        writer.flush();
        writer.close();

        normalForm = reader.readLine().replaceAll("\\S+\\{|}|\\?+", "").replaceAll("\\|", " ");
        return normalForm;
    }

    public static String getFirstNormalForm(String querry) throws IOException
    {
        return getNormalForm(querry).split("\\s+")[0];
    }

}
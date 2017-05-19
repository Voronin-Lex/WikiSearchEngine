package Search.QuerryProcessors;

import Common.ContextHelper;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by lex on 5/16/17.
 */
public abstract class DataProcessor {

    public  long VbOffset = 0;

    public  int getNextInt(FileChannel fileChannel) throws IOException {
        ArrayList<Byte> bytes = new ArrayList<>();
        while (true) {
            MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, VbOffset, 1);
            byte currentByte = byteBuffer.get();
            bytes.add(currentByte);
            VbOffset += 1;
            if ((currentByte & 0xff) >= 128) {
                return ContextHelper.decodeNumber(bytes);
            }
        }
    }

    public  TreeMap<Integer, Integer> GetTermDocs(long termOffset) throws Exception {
        VbOffset = termOffset;
        FileChannel fileChannel = new RandomAccessFile(Resources.indexPath, "r").getChannel();
        int idTerm = getNextInt(fileChannel);
        int numberOfDocuments = getNextInt(fileChannel);
        int jump = (int) (Math.sqrt(numberOfDocuments));
        int previousDocId = 0;
        TreeMap<Integer, Integer> docs = new TreeMap<>();
        for (int i=0; i<numberOfDocuments; i++)
        {
            int docId;
            if (i%jump==0)
            {
                docId = getNextInt(fileChannel);
                previousDocId = docId;
                if ((i + jump) < numberOfDocuments)
                    getNextInt(fileChannel);
            }
            else
            {
                int gap = getNextInt(fileChannel);
                docId = previousDocId + gap;
                previousDocId = docId;
            }
            int termFrequency = getNextInt(fileChannel);
            docs.put(docId, termFrequency);
            for (int j = 0; j < termFrequency; j++)
                getNextInt(fileChannel);
        }
        fileChannel.close();
        return docs;
    }
}

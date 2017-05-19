package Search.Utils;

import Common.ContextHelper;
import Search.DocInformation.MultiTermDocument;
import Search.QuerryProcessors.Resources;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.Math.log;

/**
 * Created by lex on 4/13/17.
 */
public class DocIntersector {

    private static int[] positions;
    private static int[] numberOfDocuments;
    private static int[] jumps;
    private static int[] currentDocuments;
    private static long[] termsOffsets;
    private static FileChannel fileChannel;

    public  int getNumberSize(int number) {
        return (int) (log(number) / log(128)) + 1;
    }

    public int getNextInt(int collectionNumber) throws IOException {
        ArrayList<Byte> bytes = new ArrayList<>();
        while (true) {
            MappedByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, termsOffsets[collectionNumber], 1);
            byte currentByte = byteBuffer.get();
            bytes.add(currentByte);
            termsOffsets[collectionNumber] += 1;
            if ((currentByte & 0xff) >= 128) {
                return ContextHelper.decodeNumber(bytes);
            }
        }
    }

    public long[] getTermOffsets(String[] terms) throws Exception {
        long[] termOffsets = new long[terms.length];
        for (int i = 0; i < termOffsets.length; i++) {
            Integer termId = DataGetter.GetTermKey(terms[i]);
            termOffsets[i] = DataGetter.GetTermOffset(termId);
        }
        return termOffsets;
    }

    public  FileChannel getFileChannel() throws IOException {
        File indexFile = new File(Resources.indexPath);
        FileChannel fileChannel = new RandomAccessFile(indexFile, "r").getChannel();
        return fileChannel;
    }

    public  int[] getJumps() {
        int[] jumps = new int[numberOfDocuments.length];
        for (int i = 0; i < jumps.length; i++)
            jumps[i] = (int) Math.sqrt(numberOfDocuments[i]);
        return jumps;
    }

    public  int[] getNumberOfDocs() throws IOException {
        int[] numberOfDocs = new int[termsOffsets.length];
        for (int i = 0; i < numberOfDocs.length; i++) {
            int termId = getNextInt(i);
            int numberOfDocuments = getNextInt(i);
            numberOfDocs[i] = numberOfDocuments;
        }
        return numberOfDocs;
    }

    public  boolean moveAllPostings() throws IOException {
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] + 1 >= numberOfDocuments[i])
                return false;
            positions[i]++;
        }
        return true;
    }

    public  boolean movePostingForward(int currentList, int maxDocId) throws IOException {
        if (positions[currentList] + 1 >= numberOfDocuments[currentList])
            return false;
        if (positions[currentList] % jumps[currentList] == 0 && (positions[currentList] + jumps[currentList]) < numberOfDocuments[currentList]) {
            int jump = getNextInt(currentList);
            termsOffsets[currentList] += jump; //Jump
            positions[currentList] += jumps[currentList];
            int currentDocId = getNextInt(currentList);
            int currentDocIdLength = getNumberSize(currentDocId);
            if (currentDocId <= maxDocId) {
                termsOffsets[currentList]-=currentDocIdLength;
                return true;
            } else {
                termsOffsets[currentList] -= currentDocIdLength;
                termsOffsets[currentList]-=jump;
                positions[currentList] -= jumps[currentList];
            }
        }
        int tf = getNextInt(currentList);
        for (int i = 0; i < tf; i++)
            getNextInt(currentList);
        positions[currentList]++;
        return true;
    }

    public int[] getCurrentDocuments() throws IOException {
        int[] currentDocuments = new int[termsOffsets.length];
        for (int i = 0; i < currentDocuments.length; i++)
            currentDocuments[i] = getNextInt(i);
        return currentDocuments;
    }

    public int getNextDocument(int currentList) throws IOException {
        if (positions[currentList] % jumps[currentList] == 0)
            return getNextInt(currentList);
        else
            return currentDocuments[currentList] + getNextInt(currentList);
    }

    public int[] getNextDocuments() throws IOException {
        for (int i = 0; i < currentDocuments.length; i++) {
            if (positions[i] % jumps[i] == 0)
                currentDocuments[i] = getNextInt(i);
            else
                currentDocuments[i] = currentDocuments[i] + getNextInt(i);
        }
        return currentDocuments;
    }

    public ArrayList<ArrayList<Integer>> getPositionsOfTerms() throws IOException {
        ArrayList<ArrayList<Integer>> positionsOfTerms = new ArrayList<>();
        for (int i = 0; i < positions.length; i++) {
            ArrayList<Integer> positionsOfTerm = new ArrayList<>();
            if (positions[i] % jumps[i] == 0 && (positions[i] + jumps[i]) < numberOfDocuments[i])
                getNextInt(i);
            int tf = getNextInt(i);
            boolean firstPosition = true;
            int previousPosition = 0;
            for (int j = 0; j < tf; j++) {
                if (firstPosition) {
                    previousPosition = getNextInt(i);
                    positionsOfTerm.add(previousPosition);
                    firstPosition = false;
                } else {
                    previousPosition += getNextInt(i);
                    positionsOfTerm.add(previousPosition);
                }
            }
            positionsOfTerms.add(positionsOfTerm);
        }
        return positionsOfTerms;
    }


    public Set<MultiTermDocument> intersectPostings() throws IOException {
        Set<MultiTermDocument> resultDocs = new TreeSet<>();
        positions = new int[termsOffsets.length];
        numberOfDocuments = getNumberOfDocs();
        jumps = getJumps();
        currentDocuments = getCurrentDocuments();
        while (true) {
            int maxDocId = Arrays.stream(currentDocuments).max().getAsInt();
            int equalValueCount = 0;
            for (int i = 0; i < currentDocuments.length; i++)
                if (currentDocuments[i] == maxDocId)
                    equalValueCount++;
            if (equalValueCount == currentDocuments.length) {
                MultiTermDocument currentDocument = new MultiTermDocument();
                currentDocument.setDocId(maxDocId);
                ArrayList<ArrayList<Integer>> positionsOfTerms = getPositionsOfTerms();
                currentDocument.setPositionsOfTerms(positionsOfTerms);
                resultDocs.add(currentDocument);
                if (!moveAllPostings())
                    return resultDocs;
                currentDocuments = getNextDocuments();
            } else {
                for (int i = 0; i < currentDocuments.length; i++) {
                    if (currentDocuments[i] < maxDocId) {
                        if (!movePostingForward(i, maxDocId))
                            return resultDocs;
                        currentDocuments[i] = getNextDocument(i);
                    }
                }
            }
        }
    }

    public  Set<MultiTermDocument> evaluateIntersection(String querry) throws Exception {
        String[] tokens = querry.split("\\s+");
        termsOffsets = getTermOffsets(tokens);
        fileChannel = getFileChannel();
        Set<MultiTermDocument> docs = intersectPostings();
        return docs;
    }
}

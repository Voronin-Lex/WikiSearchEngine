package Index.Utils;

import Index.DocInformation.Document;
import javafx.util.Pair;
import Common.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.lang.Math.log;

/**
 * Created by lex on 5/5/17.
 */
public class IndexWriter implements AutoCloseable {

    BufferedOutputStream binaryWriter;
    ArrayList<Pair<Integer, Long>> tokensOffset;
    long offset;


    public IndexWriter(String path) throws Exception {
        binaryWriter = new BufferedOutputStream(new FileOutputStream(path)); //
        tokensOffset = new ArrayList<>();
    }

    private static int getNumberSize(int number) {
        return (int) (log(number) / log(128)) + 1;
    }

    private int getJumpOverPositions(SortedSet<Integer> positions) {
        int jumpDistance = 0;
        boolean firstPosition = true;
        int previousPosition = 0;
        for (int position : positions) {
            if (firstPosition) {
                jumpDistance += getNumberSize(position);
                previousPosition = position;
                firstPosition = false;
            } else {
                jumpDistance += getNumberSize(position - previousPosition);
                previousPosition = previousPosition + (position - previousPosition);
            }
        }

        return jumpDistance;
    }

    private int getJumpOverCurrentDocument(Document document) {
        int jumpDistance = getNumberSize(document.getTermFrequency());
        jumpDistance += getJumpOverPositions(document.getPositions());
        return jumpDistance;
    }

    private int getJumpOverNextDocuments(NavigableSet<Document> docSet, int jump, int previousDocId) {
        int jumpDistance = 0;
        int currentDocument = 1;
        for (Document document : docSet) {
            if (currentDocument >= jump)
                break;
            jumpDistance += getNumberSize(document.getDocId() - previousDocId);
            previousDocId = previousDocId + (document.getDocId() - previousDocId);
            jumpDistance += getNumberSize(document.getTermFrequency());
            jumpDistance += getJumpOverPositions(document.getPositions());
            currentDocument++;
        }
        return jumpDistance;
    }

    public void WritePostingsBinary(Integer termId, TreeSet<Document> finalSet) throws Exception {

        Pair<Integer, Long> termOffset = new Pair<>(termId.intValue(), new Long(offset));
        tokensOffset.add(termOffset);
        int numberOfDocuments = finalSet.size();
        int jump = (int) (Math.sqrt(numberOfDocuments));
        int currentDocument = 0;
        byte[] currentNumber = ContextHelper.encodeNumber(termId.intValue());
        binaryWriter.write(currentNumber, 0, currentNumber.length); // writing termID
        offset += currentNumber.length;
        currentNumber = ContextHelper.encodeNumber(numberOfDocuments);
        binaryWriter.write(currentNumber, 0, currentNumber.length); //writing numberOfDocs
        offset += currentNumber.length;
        int previousDocId = 0;
        for (Document document : finalSet) {
            if (currentDocument % jump == 0) {
                currentNumber = ContextHelper.encodeNumber(document.getDocId().intValue());
                binaryWriter.write(currentNumber, 0, currentNumber.length); //writing DocID
                offset += currentNumber.length;
                previousDocId = document.getDocId();
                if ((currentDocument + jump) < numberOfDocuments) {
                    int jumpDistance = getJumpOverNextDocuments(finalSet.tailSet(document, false), jump, previousDocId) + getJumpOverCurrentDocument(document); ///////////// -1 ????
                    currentNumber = ContextHelper.encodeNumber(jumpDistance);
                    binaryWriter.write(currentNumber, 0, currentNumber.length); // writing Jump
                    offset += currentNumber.length;
                }
            } else {
                currentNumber = ContextHelper.encodeNumber(document.getDocId() - previousDocId);
                binaryWriter.write(currentNumber, 0, currentNumber.length);
                offset += currentNumber.length;
                previousDocId = previousDocId + (document.getDocId() - previousDocId);
            }
            currentNumber = ContextHelper.encodeNumber(document.getTermFrequency().intValue());
            binaryWriter.write(currentNumber, 0, currentNumber.length); //writing TF
            offset += currentNumber.length;
            boolean firstPosition = true;
            int previousPosition = 0;
            for (int position : document.getPositions()) {
                if (firstPosition) {
                    currentNumber = ContextHelper.encodeNumber(position);
                    binaryWriter.write(currentNumber, 0, currentNumber.length); // writing position
                    previousPosition = position;
                    firstPosition = false;
                } else {
                    currentNumber = ContextHelper.encodeNumber(position - previousPosition);
                    binaryWriter.write(currentNumber, 0, currentNumber.length);
                    previousPosition = previousPosition + (position - previousPosition);
                }
                offset += currentNumber.length;
            }
            currentDocument++;
        }
    }


    public ArrayList<Pair<Integer, Long>> getTokensOffset() {
        return tokensOffset;
    }

    public void close() {
        try {
            binaryWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

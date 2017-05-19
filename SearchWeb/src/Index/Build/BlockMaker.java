package Index.Build;
import Index.DocInformation.Document;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by lex on 02.03.17.
 */
public class BlockMaker {

    private static final int blockSize = 100000;
    private static int currentBlock;
    private TreeMap<Integer, TreeSet<Document>> tfIdfPostings;

    BlockMaker() {
        tfIdfPostings = new TreeMap<>();
    }

    public void writeTfIdfPostings(BufferedWriter fileWriter) throws Exception {
        for (Map.Entry<Integer, TreeSet<Document>> posting : tfIdfPostings.entrySet()) {
            fileWriter.write(posting.getKey() + " ");
            fileWriter.write(posting.getValue().size() + " ");

            for (Document document : posting.getValue()) {
                fileWriter.write(document.getDocId() + " ");
                fileWriter.write(document.getTermFrequency() + " ");
                for (int position : document.getPositions())
                    fileWriter.write(position + " ");
            }
            fileWriter.write("\n");
        }

        fileWriter.close();
        tfIdfPostings.clear();
        currentBlock++;
    }

    public void SaveCurrentBlock() throws Exception {
        File file = new File(Resourses.indexTempPath + "block" + currentBlock);
        file.createNewFile();
        BufferedWriter blockWriter = new BufferedWriter(new FileWriter(file));
        writeTfIdfPostings(blockWriter);
    }


    public void ProccesTfIdfTermDoc(Integer termId, Integer docId, int termPosition)    //Term doc1:count1 doc2:count1 .....
    {
        if (!tfIdfPostings.containsKey(termId)) {
            Document newDocInformator = new Document(docId);
            newDocInformator.setTermFrequency(1);
            newDocInformator.setPosition(termPosition);
            TreeSet<Document> newDocSet = new TreeSet<>();
            newDocSet.add(newDocInformator);
            tfIdfPostings.put(termId, newDocSet);
        } else {
            TreeSet<Document> docSet = tfIdfPostings.get(termId);
            Document document = docSet.ceiling(new Document(docId));

            if (document == null || document.getDocId() != docId) {
                Document newDocInformator = new Document(docId);
                newDocInformator.setTermFrequency(1);
                newDocInformator.setPosition(termPosition);
                docSet.add(newDocInformator);
            } else {
                document.increaseTermFrequency();
                document.setPosition(termPosition);
            }
        }

        if (tfIdfPostings.size() == blockSize) {
            try {
                SaveCurrentBlock();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
}

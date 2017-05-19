package Index.Build;


import Index.DocInformation.Document;
import Index.Utils.*;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Created by lex on 16.03.17.
 */
public class BlockMerger {
    public BlockMerger() throws Exception {
    }

    private static Pair<Integer, TreeSet<Document>> ReadPosting(BufferedReader reader) throws Exception {
        String nextLine = reader.readLine();
        if (nextLine == null)
            return null;

        String[] postingLine = nextLine.split("\\s+");
        int currentPosition = 0;
        Integer postKey = new Integer(Integer.parseInt(postingLine[currentPosition++]));
        int numberOfDocuments = Integer.parseInt(postingLine[currentPosition++]);
        TreeSet<Document> setOfDocuments = new TreeSet<>();
        for (int i = 0; i < numberOfDocuments; i++) {
            Document nextDocument = new Document(Integer.parseInt(postingLine[currentPosition++]));
            nextDocument.setTermFrequency(Integer.parseInt(postingLine[currentPosition++]));
            for (int j = 0; j < nextDocument.getTermFrequency(); j++)
                nextDocument.setPosition(Integer.parseInt(postingLine[currentPosition++]));
            setOfDocuments.add(nextDocument);
        }
        return new Pair<>(postKey, setOfDocuments);
    }

    private TreeSet<Document> mergeDocs(TreeSet<Document> firstSet, TreeSet<Document> secondSet) {
        for (Document document : secondSet) {
            Document commonDocument = (Document) ((TreeSet) firstSet).ceiling(document);
            if (commonDocument == null || commonDocument.getDocId() != document.getDocId())
                firstSet.add(document);
            else {
                commonDocument.increaseTermFrequency(document.getTermFrequency());
                commonDocument.setPositions(document.getPositions());
            }
        }
        return firstSet;
    }

    private ArrayList<Pair<Pair<Integer, TreeSet<Document>>, BufferedReader>> UpdateTermPostings(ArrayList<Pair<Pair<Integer, TreeSet<Document>>, BufferedReader>> postBlockSet, Integer minTermId, IndexWriter indexWriter) throws Exception {
        ArrayList<Pair<Pair<Integer, TreeSet<Document>>, BufferedReader>> nextpostBlock = new ArrayList<>();
        TreeSet<Document> finalPost = new TreeSet<>();

        for (Pair<Pair<Integer, TreeSet<Document>>, BufferedReader> p : postBlockSet) {
            if (p.getKey().getKey().equals(minTermId)) {
                finalPost = mergeDocs(finalPost, p.getKey().getValue());
                Pair<Integer, TreeSet<Document>> nextPost = ReadPosting(p.getValue());
                if (nextPost != null) {
                    nextpostBlock.add(new Pair<>(nextPost, p.getValue()));
                }
            } else
                nextpostBlock.add(p);
        }
        indexWriter.WritePostingsBinary(minTermId, finalPost);
        return nextpostBlock;
    }


    private ArrayList<Pair<Pair<Integer, TreeSet<Document>>, BufferedReader>> GetPostingBlockSet(File[] files) throws Exception {
        ArrayList<Pair<Pair<Integer, TreeSet<Document>>, BufferedReader>> postingBlockSet = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Pair<Integer, TreeSet<Document>> keyPost = ReadPosting(reader);
            postingBlockSet.add(new Pair<>(keyPost, reader));
        }
        return postingBlockSet;
    }

    public void MergeBlocks() throws Exception {

        ArrayList<Pair<Pair<Integer, TreeSet<Document>>, BufferedReader>> postingBlockSet = GetPostingBlockSet(new File(Resourses.testBlocksFolder).listFiles());
        final Comparator<Pair<Integer, TreeSet<Document>>> comparator = Comparator.comparingInt(Pair::getKey);
        Integer minTermId;
        try (IndexWriter indexWriter = new IndexWriter(Resourses.indexBinaryPath)) {
            do {
                Stream<Pair<Integer, TreeSet<Document>>> postings = postingBlockSet.stream().map(element -> element.getKey());
                minTermId = postings.min(comparator).map(p -> p.getKey()).get();
                System.out.println(minTermId);
            } while ((postingBlockSet = UpdateTermPostings(postingBlockSet, minTermId, indexWriter)).size() > 0);

            DictionaryWriter.WriteTermOffset(indexWriter.getTokensOffset());
        }
    }
}

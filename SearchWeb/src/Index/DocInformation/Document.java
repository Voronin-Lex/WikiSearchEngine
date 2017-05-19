package Index.DocInformation;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by lex on 5/18/17.
 */
public class Document implements Comparable<Document> {

    private Integer docId;
    private Integer termFrequency;
    private SortedSet<Integer> positions;


    public Document(Integer id) {
        this.docId = id;
        termFrequency = 0;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }

    public void setPosition(Integer position) {

        if (positions == null) {
            positions = new TreeSet<>();
        }
        positions.add(position);
    }

    public void setPositions(Collection<Integer> positions) {
        this.positions.addAll(positions);
    }

    public Integer getDocId() {
        return docId;
    }

    public Integer getTermFrequency() {
        return termFrequency;
    }

    public SortedSet<Integer> getPositions() {
        return positions;
    }

    public void increaseTermFrequency() {
        termFrequency++;
    }

    public void increaseTermFrequency(int termFrequency) {
        this.termFrequency += termFrequency;
    }

    @Override
    public int compareTo(Document doc) {
        return docId.compareTo(doc.docId);
    }

    @Override
    public String toString() {
        return docId.toString() + "=" + termFrequency.toString() + "=" + getPositions();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Document)) return false;
        Document document = (Document) obj;
        return (this.docId == document.docId);
    }
}

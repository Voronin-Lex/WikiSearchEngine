package Search.DocInformation;

import java.util.ArrayList;

/**
 * Created by lex on 4/3/17.
 */
public class MultiTermDocument implements Comparable<MultiTermDocument>{

    private Integer docId;
    private ArrayList<ArrayList<Integer>> positionsOfTerms;

    public MultiTermDocument() {
        positionsOfTerms = new ArrayList<>();
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public void setPositionsOfTerms(ArrayList<ArrayList<Integer>> positionsOfTerms) {
        this.positionsOfTerms = positionsOfTerms;
    }

    public Integer getDocId() {

        return docId;
    }

    public ArrayList<ArrayList<Integer>> getPositionsOfTerms() {
        return positionsOfTerms;
    }

    @Override
    public String toString()
    {
        return docId.toString()+"="+positionsOfTerms;
    }

    @Override
    public int compareTo(MultiTermDocument doc) {
        return docId.compareTo(doc.docId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MultiTermDocument)) return false;

        MultiTermDocument document = (MultiTermDocument) obj;
        return (this.docId.equals(document.docId));
    }
}

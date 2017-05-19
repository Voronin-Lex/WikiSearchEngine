package Search.QuerryProcessors;

import Common.ContextHelper;
import Search.Utils.DataGetter;
import javafx.util.Pair;

import java.util.*;

import static java.lang.Math.log10;

/**
 * Created by lex on 17.03.17.
 */
public class RangeQueryProcessor extends DataProcessor{

    public  double getDocWeigh(Integer docId, ArrayList<TreeMap<Integer, Integer>> docSet, int startIndex) {
        double completeWeigh = 0;
        for (int i = startIndex; i < docSet.size(); i++) {
            if (docSet.get(i).containsKey(docId)) {
                int frequency = docSet.get(i).get(docId);
                completeWeigh += (1 + log10(frequency)) * log10((double) Resources.numberOfArticles / docSet.get(i).size());
            }
        }
        return completeWeigh;
    }

    public  ArrayList<Pair<Double, Integer>> getDocstoWeigh(TreeMap<Integer, Double> unionMapDocs) {
        ArrayList<Pair<Double, Integer>> docsToWeigh = new ArrayList<>();
        for (Map.Entry<Integer, Double> doc : unionMapDocs.entrySet()) {
            docsToWeigh.add(new Pair<>(doc.getValue(), doc.getKey()));
        }
        return docsToWeigh;
    }

    public ArrayList<Pair<Integer, String>> RangeDocs(ArrayList<TreeMap<Integer, Integer>> docsArray) throws Exception {

        TreeMap<Integer, Double> unionDocMap = new TreeMap<>();
        for (int i = 0; i < docsArray.size(); i++) {
            for (Map.Entry<Integer, Integer> doc : docsArray.get(i).entrySet()) {

                if (!unionDocMap.containsKey(doc.getKey())) {
                    double weigh = getDocWeigh(doc.getKey(), docsArray, i);
                    unionDocMap.put(doc.getKey(), weigh);
                }
            }
        }

        ArrayList<Pair<Double, Integer>> docsToWeigh = getDocstoWeigh(unionDocMap);
        Collections.sort(docsToWeigh, Comparator.comparing(p -> p.getKey()));
        Collections.reverse(docsToWeigh);

        ArrayList<Pair<Integer, String>> weighedDocs = new ArrayList<>();
        for (Pair<Double, Integer> pair : docsToWeigh) {
            weighedDocs.add(new Pair<>(pair.getValue(), DataGetter.GetArticleName(pair.getValue())));
        }
        return weighedDocs;
    }

    public  ArrayList<Pair<Integer, String>> GetArticles(String query) throws Exception {
        query = ContextHelper.getNormalForm(query);
        System.out.println(query);
        String[] tokens = query.split("\\s+");

        ArrayList<TreeMap<Integer, Integer>> unionDocuments = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            int termId = DataGetter.GetTermKey(tokens[i]);
            long offset = DataGetter.GetTermOffset(termId);
            TreeMap<Integer, Integer> docs = GetTermDocs(offset);
            unionDocuments.add(docs);
        }
        return RangeDocs(unionDocuments);
    }
}

package Search.QuerryProcessors;

import Search.Evaluators.BooleanEvaluator;
import Search.Utils.DataGetter;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by lex on 10.03.17.
 */
public class BooleanQueryProcessor extends DataProcessor{

    public static Set<Integer> GetAllArticles() {
        try {
            return DataGetter.GetAllArticles();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public  TreeSet<Integer> GetDocSet(long termOffset) throws Exception {

        return new TreeSet<>(super.GetTermDocs(termOffset).keySet());
    }

    public  TreeSet<Integer> GetDocuments(String word) {
        try {
            Integer termId = DataGetter.GetTermKey(word);
            if (termId == null)
                return new TreeSet<>();
            long termOffset = DataGetter.GetTermOffset(termId);
            return GetDocSet(termOffset);
        } catch (Exception e) {
            return null;
        }
    }


    public  ArrayList<Pair<Integer, String>> GetArticles(String querry) throws Exception {
        BooleanEvaluator evaluator = new BooleanEvaluator(this);
        System.out.println(querry);
        querry = querry.replaceAll(" ", "&&");
        Set<Integer> docs = evaluator.evaluate(querry.toLowerCase());
        ArrayList<Pair<Integer, String>> resultSet = new ArrayList<>();
        for (int doc : docs) {
            resultSet.add(new Pair<>(doc, DataGetter.GetArticleName(doc)));
        }
        return resultSet;
    }

}

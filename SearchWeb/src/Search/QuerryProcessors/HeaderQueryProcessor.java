package Search.QuerryProcessors;

import Search.Evaluators.BooleanEvaluator;
import Common.ContextHelper;
import Search.Utils.DataGetter;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by lex on 5/16/17.
 */
public class HeaderQueryProcessor {

    public static String getHeaderQuery(String query)
    {
        ArrayList<String> words = new ArrayList<>();
        for(String word : query.split("\\s+"))
            words.add(word+"$");

        return String.join(" ", words);
    }

    public  ArrayList<Pair<Integer, String>> GetArticles(String querry) throws Exception {
        BooleanEvaluator evaluator = new BooleanEvaluator(new BooleanQueryProcessor());
        querry = ContextHelper.getNormalForm(querry);
        querry = getHeaderQuery(querry);
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

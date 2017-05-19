package Search.QuerryProcessors;

import Search.DocInformation.*;
import Common.ContextHelper;
import Search.Utils.DataGetter;
import Search.Utils.DocIntersector;
import Search.Utils.PositionIntersector;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;


/**
 * Created by lex on 4/7/17.
 */
public class QuoteQueryProcessor {

    public  ArrayList<Pair<Integer, String>> GetQuoteArticles(String querry) throws Exception {

        DocIntersector docIntersector = new DocIntersector();
        String[] tokens = Stream.of(querry.split("\"|/")).filter(s->s.length()>0 && !s.startsWith(" ")).toArray(size-> new String[size]);
        querry = ContextHelper.getNormalForm(tokens[0]);
        System.out.println(querry);
        Set<MultiTermDocument> documents = docIntersector.evaluateIntersection(querry.toLowerCase());
        ArrayList<Pair<Integer, String>> resultSet = new ArrayList<>();

        PositionIntersector positionIntersector = new PositionIntersector();
        for (MultiTermDocument document : documents)
        {
            if (positionIntersector.intersectPositions(document.getPositionsOfTerms(), Integer.parseInt(tokens[1])))
                resultSet.add(new Pair<>(document.getDocId(), DataGetter.GetArticleName(document.getDocId())));
        }
        return resultSet;
    }
}

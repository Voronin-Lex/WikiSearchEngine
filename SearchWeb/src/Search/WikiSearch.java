package Search;

import Search.QuerryProcessors.*;
import javafx.util.Pair;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lex on 07.03.17.
 */
@WebServlet(name = "Search.WikiSearch", urlPatterns = "/search")
public class WikiSearch extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String querry = request.getParameter("query");
        String searchType = request.getParameter("search");
        try {
            ArrayList<Pair<Integer, String>> resultDocs=null;
            if (searchType.equals("boolean")) {
                BooleanQueryProcessor booleanQueryProcessor = new BooleanQueryProcessor();
                resultDocs = booleanQueryProcessor.GetArticles(querry);
            }
            if (searchType.equals("TF_IDF")) {
                RangeQueryProcessor rangeQueryProcessor = new RangeQueryProcessor();
                resultDocs = rangeQueryProcessor.GetArticles(querry);
            }
            if(searchType.equals("Quote")) {
                QuoteQueryProcessor quoteQueryProcessor = new QuoteQueryProcessor();
                resultDocs = quoteQueryProcessor.GetQuoteArticles(querry);
            }
            if(searchType.equals("Header")) {
                HeaderQueryProcessor headerQueryProcessor = new HeaderQueryProcessor();
                resultDocs = headerQueryProcessor.GetArticles(querry);
            }

            Integer docCount = resultDocs.size();
            if (resultDocs.size() > 100) {
                resultDocs = new ArrayList<>(resultDocs.subList(0, 100));
            }
            request.setAttribute("answer", resultDocs);
            request.setAttribute("docCount", docCount);
            request.getRequestDispatcher("jsp/SearchPage.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

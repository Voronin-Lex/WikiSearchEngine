package Search.Utils;

import Search.QuerryProcessors.Resources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by lex on 09.03.17.
 */
public class DataGetter {

    private static TreeMap<String, Integer> TermIdDictionary;
    private static TreeMap<Integer, String> ArticlesDictionary;
    private static TreeMap<Integer, Long> TermOffsetDictionary;

    public static void PrepareTokenDictionary() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(Resources.dictionaryTokenPath));
        TermIdDictionary = new TreeMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.length() > 0) {
                String[] postingLine = Arrays.stream(line.split("\\s+")).filter(s -> s.length() > 0).toArray(size -> new String[size]);
                if (postingLine.length > 1) {
                    Integer postKey = new Integer(Integer.parseInt(postingLine[0]));
                    String termValue = String.join(" ", Arrays.copyOfRange(postingLine, 1, postingLine.length));
                    TermIdDictionary.put(termValue, postKey);
                }
            }
        }
    }

    public static Integer GetTermKey(String term) throws Exception {
        if (TermIdDictionary == null)
            PrepareTokenDictionary();
        return TermIdDictionary.get(term);

    }

    public static void PrepareTermOffsetDictionary() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(Resources.dictionaryOffsetPath));
        TermOffsetDictionary = new TreeMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] termOffset = line.split("\\s+");
            Integer termId = new Integer(Integer.parseInt(termOffset[0]));
            Long offset = new Long(Long.parseLong(termOffset[1]));
            TermOffsetDictionary.put(termId, offset);
        }
    }

    public static Long GetTermOffset(Integer termId) throws Exception {
        if (TermOffsetDictionary == null)
            PrepareTermOffsetDictionary();
        return TermOffsetDictionary.get(termId);
    }

    public static void PrepareArticleDictionary() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(Resources.articleDictionary));
            ArticlesDictionary = new TreeMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] article = line.split("\\s+");
                Integer articleId = new Integer(Integer.parseInt(article[0]));
                String articleName = String.join(" ", Arrays.copyOfRange(article, 1, article.length));
                ArticlesDictionary.put(articleId, articleName);
            }
    }

    public static String GetArticleName(Integer id) throws Exception {
        if (ArticlesDictionary == null)
            PrepareArticleDictionary();
        return ArticlesDictionary.get(id);
    }

    public static Set<Integer> GetAllArticles() throws Exception {
        if (ArticlesDictionary == null)
            PrepareArticleDictionary();
        return ArticlesDictionary.keySet();
    }

}

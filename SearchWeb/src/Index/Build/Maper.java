package Index.Build;

import java.util.TreeMap;

/**
 * Created by lex on 03.03.17.
 */
public class Maper {

    private int tokenId;
    private TreeMap<String, Integer> termIdMap;
    private TreeMap<Integer, String> articleIdMap;

    public TreeMap<Integer, String> getArticleIdMap() {
        return articleIdMap;
    }

    public TreeMap<String, Integer> getTermIdMap() {
        return termIdMap;
    }

    Maper() {
        termIdMap = new TreeMap<>();
        articleIdMap = new TreeMap<>();
    }

    public void mapToken(String token) {
        if (!termIdMap.containsKey(token.toLowerCase())) {
            termIdMap.put(token.toLowerCase(), tokenId);
            tokenId++;
        }
    }
}

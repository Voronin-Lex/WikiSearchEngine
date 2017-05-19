package Index.Build;

/**
 * Created by lex on 02.03.17.
 */
public class Resourses {

    public static final String wikiRootDir = "/home/lex/Documents/shared";
    public static final String wikiPlainText = "/home/lex/Documents/PlainText";
    public static final String wikiStemmedPath = "/home/lex/Documents/StemmedText";
    public static final String wikiSubSetDir = "/home/lex/Documents/WikiPart";
    public static final String wikiPure= "/home/lex/Documents/PureWiki";
    public static final String indexTempPath = "/home/lex/Documents/index/";
    public static final String indexBinaryPath = "/home/lex/Documents/indexResult/binaryIndex";
    public static final String termOffsetPath = "/home/lex/Documents/indexResult/termOffset.txt";
    public static final String articleIdName = "/home/lex/Documents/indexResult/Articles.txt";
    public static final String tokensPath = "/home/lex/Documents/indexResult/outTokens.txt";
    public static final String testBlocksFolder = "/home/lex/Documents/index";
    public static final String resultFile = "/home/lex/TestDir/finalBlock/finalFile.txt";
    public static final String stemPath = "/home/lex/stem/mystem";
    public static final String testIndex = "/home/lex/Documents/Tests/testIndex";

    static final String docIDPattern = "<doc id=(.*?) url=(.*?) title=(.*?)>";
    static final String rubbishPattern = "&lt;a.*?&gt;|&lt;/a&gt;|<.*?></.*?>";
    static final String rubbushDoc = "\\{.*?\\}";
    static final String rubbishBody = "\\S+\\{|}|\\?+|\\|";

    static final String delimiter = "\\.+|,+|\\s+|;+|:+|!+|\\?|—+|\"+|»+|«+|&lt;.*?&gt;|<.*?>|\\)+" +
            "|\\(+|\\{+|\\+}+|\\+|/+|№+|“+|„+|\\[+|\\+]+|…+|@+|#+|\\$+|%+|^+|&+|\\'+" +
            "|\\*+|\\-+|\\−+| +|’+|‘+|\\\\xa0+|\\\\x2013+|=+";

    Resourses()
    {
    }
}

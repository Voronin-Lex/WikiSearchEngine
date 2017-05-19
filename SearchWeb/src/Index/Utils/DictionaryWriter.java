package Index.Utils;

import Index.Build.*;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lex on 3/29/17.
 */
public class DictionaryWriter {

    public static void writeTokens(TreeMap<String, Integer> tokens) throws IOException {

        FileWriter twriter = new FileWriter(Resourses.tokensPath);
        for (Map.Entry node : tokens.entrySet()) {
            twriter.write(node.getValue() + "  " + node.getKey() + "\n");
        }

        twriter.close();
    }

    public static void writeArticleIdName(TreeMap<Integer, String> map) throws Exception {
        FileWriter writer = new FileWriter(Resourses.articleIdName);
        for (Map.Entry node : map.entrySet()) {
            writer.write(node.getKey() + " " + node.getValue() + "\n");
        }
        writer.close();
    }

    public static void WriteTermOffset(ArrayList<Pair<Integer, Long>> termOffset) throws Exception {

        FileWriter writer = new FileWriter(Resourses.termOffsetPath);
        for (Pair<Integer, Long> p : termOffset)
            writer.write(p.getKey() + " " + p.getValue() + "\n");
        writer.close();

    }
}

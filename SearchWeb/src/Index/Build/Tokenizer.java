package Index.Build;

import Index.Utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lex on 24.02.17.
 */
public class Tokenizer {

    Pattern docStart;
    Pattern rubbish;
    Pattern rubbishStemmedHead;
    Pattern rubbishStemmedBody;
    Integer docId = 0;
    int wordInDocPosition;
    Maper maper;
    BlockMaker blockMaker;

    public Tokenizer(Maper maper, BlockMaker blockMaker) {
        docStart = Pattern.compile(Resourses.docIDPattern);
        rubbish = Pattern.compile(Resourses.rubbishPattern);
        rubbishStemmedHead = Pattern.compile(Resourses.rubbushDoc);
        rubbishStemmedBody = Pattern.compile(Resourses.rubbishBody);
        wordInDocPosition = 0;

        this.maper = maper;
        this.blockMaker = blockMaker;
    }

    public void processWiki() throws Exception {
        for (File dir : new File(Resourses.wikiSubSetDir).listFiles()) {
            if (dir.isDirectory()) {
                System.out.println("Processing folder: " + dir.getPath());
                for (File file : new File(dir.getPath()).listFiles()) {
                    if (file.isFile()) {
                        File readFile = new File(file.getPath());
                        BufferedReader reader = new BufferedReader(new FileReader(readFile));
                        String text;
                        while ((text = reader.readLine()) != null) {
                            TokenizeString(text);
                        }
                    }
                }
            }
        }
        finishTokenize();
    }

    private void ProcessString(String[] line, boolean header) throws Exception {
        for (String s : line) {
            if (header)
                s += "$";
            maper.mapToken(s);
            Integer tokenKey = maper.getTermIdMap().get(s.toLowerCase());
            if (tokenKey != null) {
                wordInDocPosition++;
                blockMaker.ProccesTfIdfTermDoc(tokenKey, docId, wordInDocPosition);
            } else
                throw new Exception(tokenKey + "is invalid");
        }
    }

    private void TokenizeString(String text) throws Exception {
        Matcher docString = docStart.matcher(text);
        Matcher rubbishString;

        if (docString.find()) {
            docId = Integer.parseInt(docString.group(1).substring(1, docString.group(1).length() - 1));
            text = docString.group(3).substring(1, docString.group(3).length() - 1);
            maper.getArticleIdMap().put(docId, text);
            wordInDocPosition = 0;
            rubbishString = rubbish.matcher(text);
            text = rubbishString.replaceAll("");
            ProcessString(text.split(Resourses.delimiter), true);

        } else {
            ProcessString(text.split("\\s+"), false);
        }
    }

    public void finishTokenize() throws Exception {
        blockMaker.SaveCurrentBlock();
        DictionaryWriter.writeTokens(maper.getTermIdMap());
        DictionaryWriter.writeArticleIdName(maper.getArticleIdMap());
    }
}

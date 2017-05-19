package Index.Build;


/**
 * Created by lex on 02.03.17.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Tokenizer tokenizer = new Tokenizer(new Maper(), new BlockMaker());
        tokenizer.processWiki();
        BlockMerger merger = new BlockMerger();
        merger.MergeBlocks();

    }
}

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import org.interma.FixMessageTokenizer;
import org.interma.AnalyzerUtils;

public class Main {
    public static void main(String[] args)
    {
        try {
            char[] delimiter = new char[]{0x01};
            String d = new String(delimiter);

            //Analyzer ana = new MyWhitespaceAnalyzer();
            Analyzer a = new Analyzer() {
                @Override
                protected TokenStreamComponents createComponents(String fieldName) {
                    //Tokenizer tokenizer = new PathHierarchyTokenizer();
                    Tokenizer tokenizer = new FixMessageTokenizer();
                    return new TokenStreamComponents(tokenizer, tokenizer);
                }
            };
            String text = "126=abc; ;55=cde fg;100=nba ; 130=tyu;;167=mnn";
            AnalyzerUtils.displayTokens(a, text);
            System.out.println();

            text = "126=abc"+d+d+"45=nba;72=ab";
            AnalyzerUtils.displayTokens(a, text);
            a.close();
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("hello world");
    }
}

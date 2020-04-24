


import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import org.interma.IntermaTokenizer;
import org.interma.AnalyzerUtils;

public class MyClass {
    public static void main(String[] args)
    {
        String text = "126=abc; ;55=cde fg;100=nba";

        try {
            //Analyzer ana = new MyWhitespaceAnalyzer();
            Analyzer a = new Analyzer() {
                @Override
                protected TokenStreamComponents createComponents(String fieldName) {
                    //Tokenizer tokenizer = new PathHierarchyTokenizer();
                    Tokenizer tokenizer = new IntermaTokenizer();
                    return new TokenStreamComponents(tokenizer, tokenizer);
                }
            };
            AnalyzerUtils.displayTokens(a, text);

            a.close();
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("hello world");
    }
}

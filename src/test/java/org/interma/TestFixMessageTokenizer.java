package org.interma;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;

import java.io.StringReader;

public class TestFixMessageTokenizer extends BaseTokenStreamTestCase {
    String d = new String(new char[]{0x01});

    public void testBasic1() throws Exception {
        String text = "35=abc de";
        FixMessageTokenizer t = new FixMessageTokenizer(newAttributeFactory());
        t.setReader(new StringReader(text));
        assertTokenStreamContents(t,
                new String[]{"35=abc de","35=","=abc de","abc","de"},
                new int[]{0, 0, 2, 3, 7},
                new int[]{9, 3, 9, 6, 9},
                new int[]{1, 1, 1, 1, 1},
                text.length());
    }
    public void testDelimiter() throws Exception {
        String text = d+"35=a, 36=fd";
        FixMessageTokenizer t = new FixMessageTokenizer(newAttributeFactory(), "0x01,");
        t.setReader(new StringReader(text));
        assertTokenStreamContents(t,
                new String[]{"35=a","35=","=a","a","36=fd","36=","=fd","fd"});
    }
}

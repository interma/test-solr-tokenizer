package org.interma;


import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

/**
 * 用于分词器测试的一个简单工具类(用于打印分词情况，包括Term的起始位置和结束位置(即所谓的偏 * 移量)，位置增量，Term字符串，Term字符串类型(字符串/阿拉伯数字之类的))
 * @author Lanxiaowei
 *
 */
public class AnalyzerUtils {
    public static void displayTokens(Analyzer analyzer,String text) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("text", text);
        displayTokens(tokenStream);
        tokenStream.close();
    }

    private static void displayTokens(TokenStream tokenStream) throws IOException {
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        TypeAttribute typeAttribute = tokenStream.addAttribute(TypeAttribute.class);

        tokenStream.reset();
        int position = 0;
        while (tokenStream.incrementToken()) {
            int increment = positionIncrementAttribute.getPositionIncrement();
            if(increment > 0) {
                position = position + increment;
                System.out.print(position + ":");
            }
            int startOffset = offsetAttribute.startOffset();
            int endOffset = offsetAttribute.endOffset();
            String term = charTermAttribute.toString();
            System.out.println("[" + term + "]" + ":[" + startOffset + "-->" + endOffset + "):" + typeAttribute.type());
        }
    }

}
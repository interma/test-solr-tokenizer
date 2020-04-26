package org.interma;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeFactory;

public class IntermaTokenizer extends Tokenizer {

    public IntermaTokenizer() {
        this( DEFAULT_TOKEN_ATTRIBUTE_FACTORY, DEFAULT_BUFFER_SIZE, DEFAULT_DELIMITER, DEFAULT_DELIMITER, DEFAULT_SKIP);
    }

    public IntermaTokenizer(AttributeFactory factory) {
        this( factory, DEFAULT_BUFFER_SIZE, DEFAULT_DELIMITER, DEFAULT_DELIMITER, DEFAULT_SKIP);
    }

    public IntermaTokenizer
            (AttributeFactory factory, int bufferSize, char delimiter, char replacement, int skip) {
        super(factory);
        if (bufferSize < 0) {
            throw new IllegalArgumentException("bufferSize cannot be negative");
        }
        if (skip < 0) {
            throw new IllegalArgumentException("skip cannot be negative");
        }
        termAtt.resizeBuffer(bufferSize);

        this.delimiter = delimiter;
        this.replacement = replacement;
        this.skip = skip;
        resultToken = new StringBuilder(bufferSize);
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final char DEFAULT_DELIMITER = '/';
    public static final int DEFAULT_SKIP = 0;

    private final char delimiter;
    private final char replacement;
    private final int skip;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posAtt = addAttribute(PositionIncrementAttribute.class);
    private int startPosition = 0;
    private int skipped = 0;
    private boolean endDelimiter = false;
    private StringBuilder resultToken;

    private int charsRead = 0;


    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        if(resultToken.length() == 0){
            posAtt.setPositionIncrement(1);
        }
        else{
            posAtt.setPositionIncrement(0);
        }

        int length = 0;
        boolean eof = false;
        while (true) {
            int c = input.read();
            if (c < 0) {
                eof = true;
                break;
            }
            if (c == ';')
                break;
            length++;
            termAtt.append((char)c);
        }

        if (eof && length == 0)
            return false;

        termAtt.setLength(length);
        offsetAtt.setOffset(correctOffset(startPosition), correctOffset(startPosition+length));
        startPosition += length;
        resultToken.setLength(0);
        resultToken.append(termAtt.buffer(), 0, length);
        return true;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        // set final offset
        int finalOffset = correctOffset(charsRead);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        resultToken.setLength(0);
        charsRead = 0;
        endDelimiter = false;
        skipped = 0;
        startPosition = 0;
    }
}


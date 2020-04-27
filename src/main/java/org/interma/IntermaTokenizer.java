package org.interma;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizerImpl;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeFactory;

class Term {
    public String str;
    public int offset;

    public Term(String str, int off) {
        this.str = str;
        this.offset = off;
    }
}

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

        this.v = new Vector();
        this.scanner = new StandardTokenizerImpl(input);
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

    private int charsRead = 0;

    private Vector v;
    private StandardTokenizerImpl scanner;

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        // note: set this value to 1
        posAtt.setPositionIncrement(1);

        // iterator vector
        if (v.size() > 0) {
            Term term = (Term)(v.get(0));
            termAtt.append(term.str);
            termAtt.setLength(term.str.length());
            offsetAtt.setOffset(correctOffset(term.offset), correctOffset(term.offset+term.str.length()));

            v.remove(0);
            return true;
        }

        StringBuilder tokensb = new StringBuilder();
        String token = new String();

        boolean eof = false;
        while (true) {
            int c = input.read();
            if (c < 0) {
                token = tokensb.toString().trim();
                eof = true;
                break;
            }
            if (c == ';') {
                token = tokensb.toString().trim();
                if (token.length() > 0) {
                    break;
                }
                else {
                    tokensb.setLength(0);
                    continue;
                }
            }

            tokensb.append((char)c);
        }

        if (eof && token.length() == 0)
            return false;

        //generate all sub terms: key=, =value
        generate_subterms(token, startPosition);


        termAtt.append(token);
        termAtt.setLength(token.length());

        // note: length isn't the actual length (see final offset)
        offsetAtt.setOffset(correctOffset(startPosition), correctOffset(startPosition+token.length()));
        startPosition += token.length();

        return true;
    }

    private void generate_subterms(String token, int offset) throws IOException {
        int pos = token.indexOf("=");
        if (pos < 0)
            return; // not a valid fix message
        String key_term = token.substring(0,pos+1);
        String val_term = token.substring(pos);

        if (key_term.length() > 0)
            v.addElement(new Term(key_term, offset));
        if (val_term.length() > 0)
            v.addElement(new Term(val_term, offset+pos));

        String value = token.substring(pos+1);
        // generate all value subterms
        Reader reader = new StringReader(value);
        scanner.yyreset(reader);

        while(true) {
            int tokenType = scanner.getNextToken();

            if (tokenType == StandardTokenizerImpl.YYEOF) {
                reader.close();
                return;
            }

            v.addElement(new Term(scanner.yytext(), offset+pos+1+scanner.yychar()));
        }
    }

    @Override
    public final void end() throws IOException {
        //TODO correct finaloffset

        super.end();
        // set final offset
        //int finalOffset = correctOffset(charsRead);
        int finalOffset = correctOffset(startPosition);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        charsRead = 0;
        endDelimiter = false;
        skipped = 0;
        startPosition = 0;

        v.clear();
        scanner.yyreset(input);
    }
}


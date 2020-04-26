package org.interma;

import java.util.Map;

import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

public class IntermaTokenizerFactory extends TokenizerFactory {
    public IntermaTokenizerFactory(Map<String, String> args) {
        super(args);

        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public IntermaTokenizer create(AttributeFactory factory) {
        IntermaTokenizer tokenizer = new IntermaTokenizer(factory);

        return tokenizer;
    }
}


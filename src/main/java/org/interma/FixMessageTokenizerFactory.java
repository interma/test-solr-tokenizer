package org.interma;

import java.util.Map;

import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

public class FixMessageTokenizerFactory extends TokenizerFactory {
    private final String delimiter;

    public FixMessageTokenizerFactory(Map<String, String> args) {
        super(args);
        delimiter = get(args, "delimiter", FixMessageTokenizer.DEFAULT_DELIMITER);

        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }

    @Override
    public FixMessageTokenizer create(AttributeFactory factory) {
        FixMessageTokenizer tokenizer = new FixMessageTokenizer(factory, delimiter);
        return tokenizer;
    }
}


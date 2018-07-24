/*
 * Bootcamp 3 Solution
 * CPSC 5003, Seattle University
 * This is free and unencumbered software released into the public domain.
 */
package WR;

/**
 * Scanner that extracts lowercase words from the stream of characters
 * according to the rules given in the assignment
 * @author bc3soln
 */
public class WordScanner {
    // Characters that can be considered apostrophes
    private static final char[] apostropheChars = {
        '’', '\''
    };
    // characters that can be considered hyphens
    private static final char[] hyphenChars = {
        '-', '‐', '-',                          // hyphen
    };
    // all punctuation characters
    private static final char[] punctuationChars = {
        '’', '\'',                              // apostrophe
        '`',                                    // backtick
        '(', ')', '[', ']', '{', '}', '<', '>', // brackets
        '+', '-', '=',                          // math operations
        ':',                                    // colon
        ',',                                    // comma
        '‒', '–', '—', '―',                     // dashes
        '…',                                    // ellipsis
        '!',                                    // exclamation mark
        '.',                                    // full stop/period
        '«', '»',                               // guillemets
        '-', '‐',                               // hyphen
        '?',                                    // question mark
        '‘', '’', '“', '”', '"',                // quotation marks
        ';',                                    // semicolon
        '/',                                    // slash/stroke
        '⁄',                                    // solidus
        '␠',                                    // space?
        '·',                                    // interpunct
        '&',                                    // ampersand
        '@',                                    // at sign
        '*',                                    // asterisk
        '\\',                                   // backslash
        '•',                                    // bullet
        '^',                                    // caret
        '¤', '¢', '$', '€', '£', '¥', '₩', '₪', // currency
        '†', '‡',                               // dagger
        '°',                                    // degree
        '¡',                                    // inverted exclamation point
        '¿',                                    // inverted question mark
        '¬',                                    // negation
        '#',                                    // number sign (hashtag)
        '№',                                    // numero sign ()
        '%', '‰', '‱',                        // percent and related signs
        '¶',                                    // pilcrow
        '′',                                    // prime
        '§',                                    // section sign
        '~',                                    // tilde/swung dash
        '¨',                                    // umlaut/diaeresis
        '_',                                    // underscore/understrike
        '|', '¦',                               // vertical/pipe/broken bar
        '⁂',                                   // asterism
        '☞',                                   // index/fist
        '∴',                                   // therefore sign
        '‽',                                   // interrobang
        '※'                                   // reference mark
    };
    // Stream of characters that supports getChar(), peekChar(), and next()
    private final CharStream stream;
    // used to build each word
    private StringBuilder sb;

    /**
     * Creates a WordReader class that uses the given character stream.
     * @param stream the character stream to use
     */
    public WordScanner(CharStream stream) {
        this.stream = stream;
        discardWhitespacePunctuation();
    }

    /**
     * Constructs the next word according to the rules in the assignment
     * description.
     * @return next word constructed from a stream of characters according to
     * the rules
     */
    public String nextWord() {
        return getNextWord();
    }

    /**
     * True if there is another word that can be read.
     * @return true if there is another word available to be scanned
     */
    public boolean hasNextWord() {
        return !isEOF();
    }

    /**
     * True if the current character in the stream can be considered whitespace
     * @return true if stream char is whitespace
     */
    private boolean isWhitespaceChar() {
        return Character.isWhitespace(stream.getChar());
    }

    /**
     * True if the current character in the stream can be considered a hyphen
     * @return true if stream char is hyphen
     */
    private boolean isHyphenChar() {
        final char c = stream.getChar();
        for (char pChar : hyphenChars) {
            if (c == pChar) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the peek character is a newline or carriage return
     * @return true if \n or \r character
     */
    private boolean isPeekNewlineChar() {
        final char c = stream.getPeek();
        return c == '\n' || c == '\r';
    }

    /**
     * True if the curr character in the stream can be considered an apostrophe
     * @return true if stream char is an apostrophe
     */
    private boolean isApostropheChar() {
        final char c = stream.getChar();
        for (char pChar : apostropheChars) {
            if (c == pChar) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if the curr character in the stream can be considered punctuation
     * @return true if stream char is punctuation
     */
    private boolean isCurrPunctuationChar() {
        final char c = stream.getChar();
        return isPunctuationChar(c);
    }

    /**
     * True if the given char is considered punctuation
     * @param c character to examine
     * @return true if the given char is considered punctuation
     */
    private static boolean isPunctuationChar(char c) {
        for (char pChar : punctuationChars) {
            if (c == pChar) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if the next character in the stream is whitespace or punctuation
     * @return true if the next character in the stream is WS or punctuation
     */
    private boolean isPeekWhitespacePunctuationChar() {
        final char c = stream.getPeek();
        return Character.isWhitespace(c) || isPunctuationChar(c);
    }

    /**
     * True if the current stream character signals that all chars were read
     * @return true if the stream has no more characters
     */
    private boolean isEOF() {
        return stream.getChar() == CharStream.EOF;
    }

    /**
     * Reads the characters from the stream and discards any whitespace or
     * punctuation. When finished, the current character in the stream is not
     * whitespace or punctuation, but it could be EOF.
     */
    private void discardWhitespacePunctuation() {
        while (!isEOF() && (isWhitespaceChar() || isCurrPunctuationChar())) {
            stream.next();
        }
    }

    /**
     * <p>Precondition: All of the whitespace is removed before calling the
     * method, so the next char is guaranteed to be either a valid word start or
     * EOF.</p> <p>PostCondition: All the trailing whitespace is removed, but
     * not appended to result, so next call does not start with whitespace </p>
     * @return next word constructed from a stream of characters
     */
    private String getNextWord() {
        sb = new StringBuilder();
        State state = State.WORD_STARTED;

        while (true) {
            switch (state) {
                case WORD_STARTED:
                    addCharToWord();
                    if (isApostropheChar()) {
                        state = State.WORD_APOSTROPHE;
                    } else if (isHyphenChar()) {
                        if (isPeekNewlineChar()) {
                            discardWhitespacePunctuation();
                        } else if (isPeekWhitespacePunctuationChar()) {
                            state = State.WORD_FINISHED;
                        }
                    } else if (isEOF() || isWhitespaceChar() ||
                               isCurrPunctuationChar()) {
                        state = State.WORD_FINISHED;
                    }
                    break;
                case WORD_APOSTROPHE:
                    addCharToWord();
                    if (isEOF() || isWhitespaceChar() ||
                        isCurrPunctuationChar()) {
                        state = State.WORD_FINISHED;
                    }
                    break;
                case WORD_FINISHED:
                    // satisfy the post-condition
                    discardWhitespacePunctuation();
                    return sb.toString().toLowerCase();
            }
        }
    }

    /**
     * Add the current character in the stream to the word and advances to
     * next.
     */
    private void addCharToWord() {
        sb.append(stream.getChar());
        stream.next();
    }

    /**
     * States that the scanner can be in
     */
    private enum State {
        WORD_STARTED, WORD_FINISHED, WORD_APOSTROPHE,
    }
}

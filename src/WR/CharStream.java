/*
 * Bootcamp 3 Solution
 * CPSC 5003, Seattle University
 * This is free and unencumbered software released into the public domain.
 */
package WR;

import java.io.IOException;
import java.io.Reader;

/**
 * Uses a Reader to provide a stream of characters interface to the user.
 * @author bc3soln
 */
public class CharStream {
    public static final char EOF = (char) -1;   // End of File marker
    private final Reader reader;                // reader to get chars from
    private char curr;                          // current position's character
    private char next;                          // next char in reader stream

    /**
     * Constructor for CharQueue class.
     */
    public CharStream(Reader reader) {
        this.reader = reader;
        next();
        next();
    }

    /**
     * Reads the character from the reader.
     * @return character read or EOF
     */
    private char read() {
        try {
            return (char) reader.read();
        } catch (IOException e) {
            return EOF;
        }
    }

    /**
     * Gets the character that will be read next.
     * @return character that will be read next
     */
    public char getPeek() {
        return this.next;
    }

    /**
     * Gets the current character of the stream.
     * @return the current character
     */
    public char getChar() {
        return this.curr;
    }

    /**
     * Advances the stream position to the next character.
     */
    public void next() {
        this.curr = this.next;
        this.next = read();
    }
}

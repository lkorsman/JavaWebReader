/*
 * Luke Korsman
 * This is free and unencumbered software released into the public 
 * domain.
 */

/*
 * This is a driver file to test each object in the package is 
 * working. The goal of this program is to read web pages and find
 * the most common words and how often they occur.
 */
package WR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Driver {

	/**
	 * Main entry point into the program
	 * 
	 * @param args not used
	 * @throws IOException if web page URL does not exist
	 */
	public static void main(String[] args) throws IOException {
		String url;
		String body;
		String urlStart = "http://";
		BufferedWriter writer = null;
		String filename = "WCfile.txt";
		File outputFile = new File(filename);
		writer = new BufferedWriter(new FileWriter(outputFile));
		ArrayList<LoadedPassage> loadedPassages = new ArrayList<>();
		Scanner kybd = new Scanner(System.in);

		try {

			// Get URL
			System.out.print("Enter a URL to analyze: ");
			url = kybd.nextLine();

			if (!url.toLowerCase().startsWith(urlStart))
				url = urlStart + url;

			Document doc = Jsoup.connect(url).get();

			// Get body of web page
			body = doc.text();

			// Write body to BufferedWriter
			writer.write(body);
			writer.close();

			// Load file created from URL
			loadFileCmd(filename, loadedPassages);

			// Analyze file
			analyzeCmd(filename, loadedPassages);

			System.out.println("File written and closed!");
		} catch (IOException e) {
			System.out.println("Error: cannot read URL");
		}

		kybd.close();
	}

	/**
	 * Loads the given file into the system. The words are processed and
	 * counted. The results of the load can be displayed with the list command.
	 * 
	 * @param lineScanner unscanned characters from the user's input
	 * @param loadedPassages currently loaded passages to modify
	 */
	private static void loadFileCmd(String filename,
			ArrayList<LoadedPassage> loadedPassages) {

		final int HASH_CAPACITY = 509;
		final String fname = filename.trim();

		try {
			Reader reader = new FileReader(fname);
			CharStream charStream = new CharStream(reader);
			WordScanner wordScanner = new WordScanner(charStream);
			WordCounter currCounter = new WordCounter(HASH_CAPACITY);
			while (wordScanner.hasNextWord()) {
				final String word = wordScanner.nextWord();
				currCounter.incrementWordCount(word);
			}
			reader.close();
			removeCommonWordsFrom(currCounter);
			loadedPassages.add(new LoadedPassage(fname, currCounter));
			System.out.println("Loaded " + fname);
		} catch (FileNotFoundException e) {
			System.out.println("Error: file not found.");
		} catch (IOException e) {
			System.out.println("Error: file read error.");
		}
	}

	/**
	 * Iterates through all the known common words and removes them from the
	 * given counter.
	 * 
	 * @param wordCounter WordCounter to remove the common words from.
	 */
	private static void removeCommonWordsFrom(WordCounter wordCounter) {
		for (String word : CommonWordList.getWords()) {
			wordCounter.removeWord(word);
		}
	}

	/**
	 * Analyzes the given text or file contents
	 * 
	 * @param lineScanner unscanned characters from the user's input
	 * @param passages currently loaded passages
	 */
	private static void analyzeCmd(String filename,
			ArrayList<LoadedPassage> passages) {
		final String fname = filename.trim();
		analyzeFile(fname, passages);
	}

	/**
	 * Analyzes the words that are read from the file instead of user entry
	 * 
	 * @param fname file name to read words from
	 * @param passages currently loaded passages
	 */
	private static void analyzeFile(String fname,
			ArrayList<LoadedPassage> passages) {
		try {
			Reader reader = new FileReader(fname);
			analyzeText(reader, passages);
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: file not found.");
		} catch (IOException e) {
			System.out.println("Error: unable to close file.");
		}
	}

	/**
	 * Analyzes the given text against the loaded passages
	 * 
	 * @param reader character stream to analyze
	 * @param passages loaded passages to analyze against
	 */
	private static void analyzeText(Reader reader,
			ArrayList<LoadedPassage> passages) {
		CharStream charStream = new CharStream(reader);
		WordScanner wordScanner = new WordScanner(charStream);
		ArrayList<String> wordBag = new ArrayList<>();
		HashSet<String> duplicates = new HashSet<String>();

		while (wordScanner.hasNextWord()) {
			final String word = wordScanner.nextWord();
			// only analyze if this is not a common word and not yet seen
			if (!CommonWordList.contains(word)
					&& !duplicates.contains(word)) {
				duplicates.add(word);
				wordBag.add(word);
			}
		}
		if (wordBag.size() == 0) {
			System.out.println("Error: all words provided were too common");
			return;
		}

		ArrayList<Integer> titleLengths = new ArrayList<>();
		titleLengths.add(14);
		System.out.printf("%16s", "given word");
		for (LoadedPassage passageEntry : passages) {
			titleLengths.add(passageEntry.passageTitle.length());
			System.out.print("   " + passageEntry.passageTitle);
		}
		System.out.println();

		for (int i : titleLengths) {
			String dashes = new String(new char[i + 2]).replace("\0", "-");
			System.out.print("+" + dashes);
		}
		System.out.println("+");

		for (String word : wordBag) {
			System.out.printf("%16s", word);
			for (LoadedPassage pe : passages) {
				final int width = pe.passageTitle.length() + 3;
				final int cnt = pe.wordCounter.getWordCount(word);
				System.out.printf("%" + width + "d", cnt);
			}
			System.out.println();
		}
	}

	/**
	 * Abstraction for the loaded passage entry.
	 */
	private static class LoadedPassage {
		private final String passageTitle; // title of the loaded passage
		private final WordCounter wordCounter; // counter of the words in it

		/**
		 * Constructs a loaded passage entry.
		 * 
		 * @param passageTitle title of the loaded passage
		 * @param wordCounter counter of the words in the passage
		 */
		private LoadedPassage(String passageTitle,
				WordCounter wordCounter) {
			this.passageTitle = passageTitle;
			this.wordCounter = wordCounter;
		}
	}
}

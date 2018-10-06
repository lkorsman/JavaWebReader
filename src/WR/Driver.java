/*
 * Luke Korsman
 * This is free and unencumbered software released into the public 
 * domain.
 */

/*
 * The goal of this program is to read web pages and find
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Driver {

	/**
	 * Main entry point into the program
	 * 
	 * @param args URL to have analyzed for word count
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

		try {
			// Get URL
			if (args.length != 1) {
				writer.close();
				return;
			} else {
				url = args[0];

				// Add 'http://' to beginning of URL if not provided
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
				analyzeCmd(filename, loadedPassages, url);

				System.out.println("File written and closed!");
			}
		} catch (IOException e) {
			System.out.println("Error: cannot read URL");
		}
	}

	/**
	 * Loads the given file into the system. The words are processed and counted.
	 * The results of the load can be displayed with the list command.
	 * 
	 * @param filename name of the file URL body to be written to
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
				final String WORD = wordScanner.nextWord();
				currCounter.incrementWordCount(WORD);
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
	 * Analyzes the given text or file contents counting word frequency
	 * 
	 * @param filename name of the file to be analyzed
	 * @param passages a list of files to be analyzed
	 * @param url the URL from which the file was generated
	 */
	private static void analyzeCmd(String filename,
			ArrayList<LoadedPassage> passages, String url) {
		final String fname = filename.trim();
		analyzeFile(fname, passages, url);
	}

	/**
	 * Analyzes the words that are read from the file
	 * 
	 * @param fname name of the file to be analyzed
	 * @param passages list of files to be analyzed
	 * @param url the URL from which the fname was generated
	 */
	private static void analyzeFile(String fname,
			ArrayList<LoadedPassage> passages, String url) {
		try {
			Reader reader = new FileReader(fname);
			analyzeText(reader, passages, url);
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: file not found.");
		} catch (IOException e) {
			System.out.println("Error: unable to close file.");
		}
	}

	/**
	 * Analyzes the given text, generates an output file and prints common words
	 * to the console
	 * 
	 * @param reader character stream to analyze
	 * @param passages loaded passages to analyze against
	 * @param url the URL from which the fname was generated
	 */
	private static void analyzeText(Reader reader,
			ArrayList<LoadedPassage> passages, String url)
			throws IOException {

		// FIXME add try/catch for IO

		final String FILE_EXTENSION = ".txt";
		final String WWW_CHECK = "http://www.";
		CharStream charStream = new CharStream(reader);
		WordScanner wordScanner = new WordScanner(charStream);
		ArrayList<String> wordBag = new ArrayList<>();
		HashSet<String> duplicates = new HashSet<String>();
		BufferedWriter writer = null;
		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyyMMdd");
		String timeString = now.format(formatter);
		String filename = "AnalyzedFile.txt";

		String beginUrl = url.substring(0, 11);

		if (beginUrl.equals(WWW_CHECK)) {
			filename = url.substring(11);
		} else {
			filename = url.substring(7);
		}
		filename = filename.replace(".", "");
		filename += timeString;
		filename += FILE_EXTENSION;

		File outputFile = new File(filename);
		writer = new BufferedWriter(new FileWriter(outputFile));

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
			writer.close();
			return;
		}

		ArrayList<Integer> titleLengths = new ArrayList<>();
		titleLengths.add(14);
		System.out.printf("%16s", "given word");
		writer.write("Website: " + url + "\n\n");
		writer.write("Top 10 Words:\n");
		writer.write(" given word\t\t");
		for (LoadedPassage passageEntry : passages) {
			titleLengths.add(passageEntry.passageTitle.length());
			System.out.print("   " + passageEntry.passageTitle);
			writer.write("Word Count");
		}
		System.out.println();
		writer.write("\n");

		for (int i : titleLengths) {
			String dashes = new String(new char[i + 4]).replace("\0", "-");
			System.out.print("+" + dashes);
			writer.write("+" + dashes);
		}
		System.out.println("+");
		writer.write("+\n");

		// Array to sort words by frequency
		ArrayList<TwoVariableHolder> outputArray = new ArrayList<>();
		TwoVariableHolder temp;

		for (String word : wordBag) {
			System.out.printf("%16s", word);
			for (LoadedPassage pe : passages) {
				final int width = pe.passageTitle.length() + 3;
				final int cnt = pe.wordCounter.getWordCount(word);
				System.out.printf("%" + width + "d", cnt);
				temp = new TwoVariableHolder(cnt, word);
				outputArray.add(temp);
			}

			System.out.println();
		}

		// Sort output in descending order
		outputArray = sortOutputArray(outputArray);

		TwoVariableHolder tempHolder;
		for (int i = 0; i < 10; i++) {
			tempHolder = outputArray.get(i);

			writer.write(tempHolder.toString());
		}

		writer.close();
	}

	/**
	 * Sorts an ArrayList of TwoVariableHolder objects in desc order based on the
	 * TwoVariableHolder's count variable
	 * 
	 * @param outputArray An ArrayList of TwoVariableHolder objects
	 * @return a non-ascending ordered ArrayList of TwoVariableHolder objects
	 */
	private static ArrayList<TwoVariableHolder> sortOutputArray(
			ArrayList<TwoVariableHolder> outputArray) {
		Collections.sort(outputArray, new Comparator<TwoVariableHolder>() {
			public int compare(TwoVariableHolder var1,
					TwoVariableHolder var2) {
				return var1.getCount() - var2.getCount();
			}
		});

		// Sort descending
		Collections.reverse(outputArray);
		return outputArray;
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

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
import java.io.FileWriter;
import java.io.IOException;

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
		String title;
		String body;
		BufferedWriter writer = null;
		String filename = "WCfile.txt";
		File outputFile = new File(filename);
		writer = new BufferedWriter(new FileWriter(outputFile));

		try {
			Document doc = Jsoup.connect("http://www.google.com").get();

			// title = doc.title();
			body = doc.text();
			writer.write(body);
			// System.out.println("Title: " + title);
			// System.out.println("Body: " + body);
			writer.close();
			System.out.println("File written and closed!");
		} catch (IOException e) {
			System.out.println("Error: cannot read URL");
		}
	}
}

package WR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Driver {

	public static void main(String[] args) throws IOException {
		String title;
		String body;
		BufferedWriter writer = null;
		String filename = "WCfile.txt";
		File outputFile = new File(filename);
		writer = new BufferedWriter(new FileWriter(outputFile));

		try {
			Document doc = Jsoup.connect("http://www.lukekorsman.tk").get();

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

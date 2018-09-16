This program allows a user to enter in a URL and get the top 10
most common words on that web page. The program uses the Jsoup library
to connect to a web page. Then it counts the frequency of each word
(excluding common words) and writes the top 10 common words to a file.

To run this program:
1) Download the following files
	- CharStream.java
	- CommonWordList.java
	- Driver.java 
	- TwoVariableHolder.java
	- WordCounter.java
	- WordScanner.java
	- jsoup-1.11.3.jar
	
2) Export the files as a Runnable JAR file with the Driver file as 
	the Launch Configuration
	
3) To run on the command line/terminal:
	-Navigate to the directory where your Runnable JAR file was saved
	 and type the following on the command line:
	 java -jar [Name of your JAR file].jar $URL
	-$URL is the URL to be analyzed
/*
 * Luke Korsman
 * CPSC 5003, Seattle University
 * This is free and unencumbered software released into the public domain.
 */
package WR;

/**
 * This class contains the functionality of a hash table for Strings. It also
 * keeps track of the unique and total String counts in the hash table.
 * 
 * @author lukekorsman
 *
 */
public class WordCounter {
	private int size; // The capacity of the hash table
	private Bucket[] table; // Array of Bucket objects
	private int uniqueWords; // Counter for unique words
	private int totalWords; // Counter for total words

	/**
	 * Constructor which creates the initial hash table, with all elements
	 * initially set to null.
	 * 
	 * @param capacity the size of the hash table
	 */
	public WordCounter(int capacity) {
		size = capacity;
		table = new Bucket[capacity];
		uniqueWords = 0;
		totalWords = 0;
	}

	/**
	 * Capacity of the hash table originally set by the user in the constructor
	 * 
	 * @return return the hash table's capacity
	 */
	public int getCapacity() {
		return size;
	}

	/**
	 * The number of unique words currently in the hash table
	 * 
	 * @return the number of unique words in the current hash table
	 */
	public int getUniqueWordCount() {
		return uniqueWords;
	}

	/**
	 * The total number of words currently in the hash table
	 * 
	 * @return the number of total words in the current hash table
	 */
	public int getTotalWordCount() {
		return totalWords;
	}

	/**
	 * Returns true if the hash table has no entries
	 * 
	 * @return true if the hash table is empty
	 */
	public boolean isEmpty() {
		for (int i = 0; i < size; i++) {
			if (table[i] != null)
				return false;
		}
		return true;
	}

	/**
	 * Adds a word to the has table.
	 * 
	 * @param word a word provided by the user
	 * @return the count of instances of the word in the hash table
	 */
	public int incrementWordCount(String word) {
		int hashCode = word.hashCode();
		totalWords++;
		return put(word, hashCode);
	}

	/**
	 * Returns the count of instances of a user's specified word in the hash
	 * table. If word was not found in the hash table, it returns 0.
	 * 
	 * @param word the word given by the user
	 * @return the count of instances of the word in the hash table or 0 if the
	 *         word was not found in the hash table.
	 */
	public int getWordCount(String word) {
		int hashCode = word.hashCode();
		int index = getIndex(hashCode);
		int counter = 0;
		Bucket start;

		if (table[index] == null)
			return counter;
		else {
			start = table[index];
			while (start != null) {
				if (start.word.compareTo(word) == 0) {
					counter = start.count;
					return counter;
				}
				start = start.next;
			}
		}
		return counter;
	}

	/**
	 * Removes all instances of a word provided by the user from the hash table
	 * 
	 * @param word the word provided by the user
	 */
	public void removeWord(String word) {
		Bucket parent = null;
		int index = getIndex(word.hashCode());

		if (table[index] == null)
			return;

		// If word to remove is the head of linked list
		if (table[index].word.compareTo(word) == 0) {
			totalWords = totalWords - table[index].count;
			uniqueWords--;
			table[index] = table[index].next;
			return;
		}

		// Otherwise, find the the node (Bucket) to remove
		Bucket curr;
		parent = table[index];
		for (curr = table[index].next; curr != null; curr = curr.next) {
			if (curr.word.compareTo(word) == 0) {
				totalWords = totalWords - curr.count;
				uniqueWords--;
				parent.next = curr.next;
				return;
			} else {
				parent = curr;
			}
		}
	}

	/**
	 * Creates a non-negative value index for the hash table based on a word's
	 * hash code
	 * 
	 * @param hashCode the hashCode of the word
	 * @return a value between 0 and the hash table's capacity
	 */
	private int getIndex(int hashCode) {
		int index;

		if (hashCode < 0)
			index = (size + (hashCode % size)) % size;
		else {
			index = hashCode % size;
		}
		return index;
	}

	/**
	 * Adds a word to the hash table. If the word already exists in the hash
	 * table then it increments that word's count instead of adding a duplicate
	 * entry. If word could not be added to the table, it returns a -1.
	 * 
	 * @param word the word provided by the user
	 * @param hashCode the hashCode of the word
	 * @return the count of the instances of the word in the hash table
	 */
	private int put(String word, int hashCode) {
		int index = getIndex(hashCode);

		if (table[index] == null) {
			table[index] = new Bucket(word, 1, null);
			uniqueWords++;
			return table[index].count;
		} else {
			Bucket head = table[index];
			while (head != null) {
				if (head.word.compareTo(word) == 0) {
					head.count = head.count + 1;
					return head.count;
				}
				if (head.next == null) {
					head.next = new Bucket(word, 1, null);
					uniqueWords++;
					return head.next.count;
				}
				head = head.next;
			}
		}
		return -1;
	}

	/**
	 * Bucket entry for the WordCounter parent class
	 */
	private static class Bucket {
		private String word;
		private int count;
		private Bucket next;

		/**
		 * Creates a bucket to store a word and it's count in the hash table
		 * 
		 * @param word a word to be stored in the bucket
		 * @param count the count of instances of the word
		 * @param next a reference to the next bucket in the linked list
		 */
		public Bucket(String word, int count, Bucket next) {
			this.word = word;
			this.count = count;
			this.next = next;
		}
	}
}

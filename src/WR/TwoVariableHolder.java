package WR;

public class TwoVariableHolder {
	private int count;
	private String word;

	public TwoVariableHolder(int count, String word) {
		this.count = count;
		this.word = word;
	}

	@Override
	public String toString() {
		return String.format("%16s, %10d\n", word, count);
	}

	public int getCount() {
		return this.count;
	}

	public String getWord() {
		return this.word;
	}

}

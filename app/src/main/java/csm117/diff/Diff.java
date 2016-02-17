package csm117.diff;

import java.util.ArrayList;

public final class Diff {
	class Edit {
		public int index;
		public boolean isInsert;
		public int length;
		public String change;
		public Edit(int index, boolean isInsert, int length, String change) {
			this.index = index;
			this.isInsert = isInsert;
			this.length = length;
			this.change = change;
		}
	}
	private Edit[] changes;

	/**
	 * Used to decide ganularity of diff.
	 * If it returns newline characters, will behave like gnu diff
	 * If it returns non-alphanumeric, it will divide into words
	 * If it returns newlines, periods, exclamation points, and question marks,
	 *   then will divide into sentences.
	 * If it returns true, will perform on a character-by-character basis.
	 */
	public static boolean isDividingChar(char c) {
		return !Character.isLetterOrDigit(c);
	}
	public static ArrayList<String> splitString(String s) {
		ArrayList<String> list = new ArrayList<>();
		int len = s.length();
		int previous = 0;
		for (int i = 0; i < len; i++) {
			if (isDividingChar(s.charAt(i))) {
				list.add(s.substring(previous, i+1));
				previous = i+1;
			}
		}
		if (previous != len)
			list.add(s.substring(previous, len));
		return list;
	}


	private Diff(ArrayList<String> a, ArrayList<String> b, int[][] v, int depth, int diagonal) {
		int ending_length;
		changes = null;
		if (a.size() > v[depth][diagonal + (depth-1)]) {
			ending_length = a.size() - v[depth][diagonal + (depth-1)];
			changes = new Edit[depth + ending_length-1];
			int j = depth-1;
			for (int i = v[depth][diagonal + (depth-1)]; i < a.size(); i++) {
				changes[j] = new Edit(i, false, a.get(i).length(), a.get(i));
				j++;
			}
		} else {
			ending_length = b.size() - (v[depth][diagonal + (depth-1)] - diagonal);
			changes = new Edit[depth + ending_length-1];
			int j = depth-1;
			for (int i = v[depth][diagonal + (depth-1)] - diagonal; i < b.size(); i++) {
				changes[j] = new Edit(a.size(), true, b.get(i).length(), b.get(i));
				j++;
			}
		}
		// The path does not include the final inserts and deletes to make the length equal.
		// Add these first.
		int k = diagonal;
		for (int i = depth-1; i > 0; i--) {
			int offset = i-1;
			//System.out.println("k: " + k +", i: " + i);
			//System.out.println("delete: " + (k-1+offset) + ", insert: " + (k+1+offset));
			if (k+1+offset >= v[i].length || (k-1+offset >= 0 && v[i][k-1+offset] >= v[i][k+1+offset])) {
				// Handle delete
				int x = v[i][k-1+offset];
				changes[i-1] = new Edit(x, false, a.get(x).length(), a.get(x));
				k--;
			} else {
				// Handle insert
				int x = v[i][k+1+offset];
				int y = x - k-1;
				changes[i-1] = new Edit(x, true, b.get(y).length(), b.get(y));
				k++;
			} // Note: unchanged text implicitly handled - no edit created.
		}
	}

	/**
	 * Creates a diff of A and B, using the algorithm from this paper:
	 * http://www.xmailserver.org/diff2.pdf
	 * "An O(ND) Difference Algorithm and Its Variations"
	 * by Eugene W. Myers
	 */
	public static Diff createDiff(String Before, String After) {
		ArrayList<String> alist = splitString(Before);
		ArrayList<String> blist = splitString(After);
		int max = alist.size() + blist.size(); // Maximum value of k
		int offset;
		// Optimization possible: only half of allV used
		// By dividing all indices by 2, can shrink space by half.
		int[][] allV = new int[max+1][];
		allV[0] = new int[1];
		allV[0][0] = 0;
		for (int d = 0; d < max; d++) {
			offset = d - 1;
			int[] v = allV[d];
			int[] v2 = new int[2*d+1];
			allV[d+1] = v2;
			for (int k = -d; k <= d; k += 2) {
				int x;
				if (k == -d || (k != d && v[offset + k - 1] < v[offset + k + 1]))
					x = v[offset + k + 1];
				else
					x = v[offset + k - 1] + 1;
				int y = x - k;
				while (x < alist.size() && y < blist.size()
				  && alist.get(x).equals(blist.get(y))) {
					x++;
					y++;
				}
				v2[(offset+1) + k] = x;
				if (x >= alist.size() || y >= blist.size()) {
					/*for (int i = 0; i <= d+1; i++) {
						System.out.print("[");
						for (int j : allV[i]) {
							System.out.print(j + " ");
						}
						System.out.println("]");
					}
					System.out.println("d: " + (d+1) + ", k: " + k);*/
					return new Diff(alist, blist, allV, d+1, k);
				}
			}
		}
		System.out.println("Error occurred: should never reach here.");
		return null;
	}

	/**
	 * Public wrapper for colring string function:
	 * takes original string, rather than array list.
	 */
	public String getColoredHTMLString(String original) {
		return getColoredHTMLString(splitString(original));
	}

	/**
	 * Given an array list reperesntation of the initial string
	 * (the string before the patch is applied), output the
	 * combination of the old and new string with changes colored.
	 * Deleted text is in red, added text in green.
	 */
	private String getColoredHTMLString(ArrayList<String> original) {
		String result = "<!DOCTYPE html>\n";
		result += "<head><style type=text/css>\n";
		result += "span.insert { color: #00DD00 }\n";
		result += "span.delete { color: #DD0000; text-decoration: line-through }\n";
		result += "</style></head><body>\n<p>";
		int k = 0;
		boolean currentlyFormatting = false;
		boolean formattingInsert = false;
		String endFormatting = "";
		for (Edit change : changes) {
			// Copy over any text that is the same, unaltered.
			while (k < change.index) {
				// Stop any current coloring of text
				if (currentlyFormatting) {
					result += endFormatting;
					endFormatting = "";
					currentlyFormatting = false;
				}
				result += original.get(k);
				k++;
				// newlines are not respected by html, so encode them:
				// Assumes that \n is a dividing character.
				if (result.charAt(result.length() - 1) == '\n') {
					result += "</p>\n<p>";
				}
			}
			if (change.isInsert) {
				if (!currentlyFormatting || !formattingInsert) {
					result += endFormatting;
					result += "<span class=\"insert\">";
					endFormatting = "</span>";
					formattingInsert = true;
				}
				result += change.change;
			}
			else {
				if (!currentlyFormatting || formattingInsert) {
					result += endFormatting;
					result += "<span class=\"delete\">";
					endFormatting = "</span>";
					formattingInsert = false;
				}
				result += original.get(k);
				k++;
			}
			currentlyFormatting = true;
			// newlines are not respected by html, so encode them:
			// Assumes that \n is a dividing character.
			if (result.charAt(result.length() - 1) == '\n') {
				result += endFormatting;
				endFormatting = "";
				currentlyFormatting = false;
				result += "</p>\n<p>";
			}
		}
		result += endFormatting;
		while (k < original.size()) {
			result += original.get(k);
			k++;
			// newlines are not respected by html, so encode them:
			// Assumes that \n is a dividing character.
			if (result.charAt(result.length() - 1) == '\n') {
				result += "</p>\n<p>";
			}
		}
		result += "</p>\n</body>";
		return result;
	}

	@Override
	public String toString() {
		String s = "";
		for (Edit e : changes) {
			s += "{token: " + e.index;
			if (e.isInsert)
				s += " insert " + e.change;
			else
				s += " delete " + e.change;
			s += "}";
		}
		return s;
	}
}

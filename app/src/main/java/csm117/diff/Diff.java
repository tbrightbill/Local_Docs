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
	    // Based on the algorithm, it is possible that v extends beyond (a.size(), b.size()).
	    // These extra steps do not correspond to insertions or deletions, so skip these.
	    // v[depth][i] - a.size()  gives extra x steps.
	    // (v[depth][i] - diagonal) - b.size()  gives extra y steps.
	    int extraSteps = 2*v[depth][diagonal + (depth-1)] - diagonal - b.size() - a.size();
		changes = new Edit[depth - extraSteps - 1];
		int k = diagonal;
		if (k+1+depth >= v[depth].length || (k-1+depth >= 0 && v[depth][k-1+depth] >= v[depth][k+1+depth]))
		    k--;
		else
		    k++;
		for (int i = depth-1; i > 0; i--) {
			//int offset = i;
			if (k+1+i >= v[i].length || (k-1+i >= 0 && v[i][k-1+i] >= v[i][k+1+i])) {
				// Handle delete
				int x = v[i][k-1+i];
				if (x < a.size())
				    changes[i-1 - extraSteps] = new Edit(x, false, a.get(x).length(), a.get(x));
				else
				    extraSteps--;
				k--;
			} else {
				// Handle insert
				int x = v[i][k+1+i];
				int y = x - k-2;
				if (y < b.size())
				    changes[i-1 - extraSteps] = new Edit(x, true, b.get(y).length(), b.get(y));
				else
				    extraSteps--;
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
				if (x >= alist.size() && y >= blist.size()) {
					return new Diff(alist, blist, allV, d+1, k);
				}
			}
		}
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
		String result = "<p>";
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
					result += "<font color=\"#00DD00\">";
					endFormatting = "</font>";
					formattingInsert = true;
				}
				result += change.change;
			}
			else {
				if (!currentlyFormatting || formattingInsert) {
					result += endFormatting;
					result += "<font color=\"#DD0000\">";
					endFormatting = "</font>";
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
		result += "</p>";
		return result;
	}

	@Override
	public String toString() {
		String s = "";
		for (Edit e : changes) {
		    if (e == null) {
		        s += "null";
		        continue;
		    }
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

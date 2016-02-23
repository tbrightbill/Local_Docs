package csm117.diff;

/**
 * Created by tbrightbill on 2/20/16.
 * This is an immutable class representing a change to a file.
 * Index refers to the index of the word in the original file
 * that the change takes place - either the word removed,
 * or the word before which an insert should take place.
 */

final public class Edit {
	final public int index;
	final public boolean isInsert;
	final public String change;
	public Edit(int index, boolean isInsert, String change) {
		this.index = index;
		this.isInsert = isInsert;
		this.change = change;
	}
}
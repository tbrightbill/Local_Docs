package csm117.localdocs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.text.Html;

import java.util.ArrayList;

import csm117.diff.Diff;
import csm117.diff.Edit;

public class MergeActivity extends AppCompatActivity {

	public final static String EXTRA_MY_VERSION = "csm117.localdocs.MY_VERSION";
	public final static String EXTRA_THEIR_VERSION = "csm117.localdocs.THEIR_VERSION";
	public final static String EXTRA_PARENT_VERSION = "csm117.localdocs.MY_PARENT";

	public final static String EXTRA_MERGED = "csm117.localdocs.MERGED";

	private String combinedText = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar bar = getSupportActionBar();
		if (bar != null)
			bar.setDisplayHomeAsUpEnabled(true);


		Intent intent = getIntent();
		String parent = intent.getStringExtra(EXTRA_PARENT_VERSION);
		String mine = intent.getStringExtra(EXTRA_MY_VERSION);
		String theirs = intent.getStringExtra(EXTRA_THEIR_VERSION);

		if (mine == null)
			mine = "My version of this was also fairly simple.";
		if (theirs == null)
			theirs = "I deleted a lot of stuff.  The original was kind of stupid.";
		if (parent == null)
			parent = "The original version of this was fairly simple.";

		Diff myDiff = Diff.createDiff(parent, mine);
		Diff theirDiff = Diff.createDiff(parent, theirs);
		//String combined = fullyAutomatedMerge(Diff.splitString(parent), myDiff.getChanges(), theirDiff.getChanges());
		String[] result = partialMerge(Diff.splitString(parent), myDiff.getChanges(), theirDiff.getChanges());
		//Diff diff = Diff.createDiff(mine, combined);
		Diff diff = Diff.createDiff(result[0], result[1]);
		String base = diff.getColoredHTMLString(result[0]);
		ScrollingMovementMethod method = new ScrollingMovementMethod();
		TextView merged = (TextView) findViewById(R.id.mergeText);
		merged.setText(Html.fromHtml(base));
		//merged.setText(Html.fromHtml(combined));
		merged.setMovementMethod(method);
		//combinedText = combined;
		combinedText = result[1];
	}

	public void accept(View view) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_MERGED, combinedText);
		setResult (Activity.RESULT_OK, intent);
		finish();
	}

	public static String[] partialMerge(ArrayList<String> original, ArrayList<Edit> otoa, ArrayList<Edit> otob) {
		int a = 0; // Index into otoa
		int b = 0; // Index into otob
		int o = 0; // Index into original string.
		String resultA = "";
		String resultB = "";
		String chunkOriginal = "";
		String chunkA = "";
		String chunkB = "";
		while (a < otoa.size() && b < otob.size()) {
			Edit aedit = otoa.get(a);
			Edit bedit = otob.get(b);
			if (o < aedit.index && o < bedit.index) {
				// Handle previous unstable chunk
				if (!(chunkOriginal.isEmpty() && chunkA.isEmpty() && chunkB.isEmpty())) {
					if (chunkOriginal.equals(chunkA)) {
						// Nonconflicting edit by B:
						// Propagate edit to both versions
						resultA += chunkB;
						resultB += chunkB;
					} else if (chunkOriginal.equals(chunkB)) {
						// Nonconflicting edit by A
						// Propagate edit to both versions
						resultA += chunkA;
						resultB += chunkA;
					} else {
						// Conflicting edit.
						// May be falsely conflicting, but will be detected
						// by CompareChangeActivity.
						resultA += chunkA;
						resultB += chunkB;
					}
				}
				chunkA = "";
				chunkB = "";
				chunkOriginal = "";
				do {
					chunkOriginal += original.get(o);
					o++;
				} while (o < aedit.index && o < bedit.index);
				resultA += chunkOriginal;
				resultB += chunkOriginal;
				chunkOriginal = "";
			}

			if (o == aedit.index && o == bedit.index) {
				if (!aedit.isInsert && !bedit.isInsert) {
					// If both are now deletions, handle the delete.
					chunkOriginal += original.get(o);
					o++;
					a++;
					b++;
				} else {
					if (aedit.isInsert) {
						chunkA += aedit.change;
						a++;
					}
					if (bedit.isInsert) {
						chunkB += bedit.change;
						b++;
					}
					// Don't handle a deletion of a word until
					// both a and b are done with insertions.
				}
			} else if (o == aedit.index) {
				if (aedit.isInsert) {
					chunkA += aedit.change;
					a++;
				} else {
					chunkOriginal += original.get(o);
					chunkB += original.get(o);
					o++;
					a++;
				}
			} else if (o == bedit.index) {
				if (bedit.isInsert) {
					chunkB += bedit.change;
					b++;
				} else {
					chunkOriginal += original.get(o);
					chunkA += original.get(o);
					o++;
					b++;
				}
			}
		}
		// We have finished all edits in one of the versions.
		// If we are currently in a conflicting chunk,
		// the remaining chunk should be conflicting.
		// Otherwise, this entire chunk is non-conflicting.

		while (a < otoa.size()) {
			Edit aedit = otoa.get(a);
			if (o < aedit.index) {
				if (!(chunkA.isEmpty() && chunkB.isEmpty() && chunkOriginal.isEmpty())) {
					if (chunkOriginal.equals(chunkA)) {
						// Nonconflicting edit by B:
						// Propagate edit to both versions
						resultA += chunkB;
						resultB += chunkB;
					} else if (chunkOriginal.equals(chunkB)) {
						// Nonconflicting edit by A
						// Propagate edit to both versions
						resultA += chunkA;
						resultB += chunkA;
					} else {
						// Conflicting edit.
						// May be falsely conflicting, but will be detected
						// by CompareChangeActivity.
						resultA += chunkA;
						resultB += chunkB;
					}
					chunkA = "";
					chunkB = "";
					chunkOriginal = "";
				}
				do {
					chunkOriginal += original.get(o);
					o++;
				} while (o < aedit.index);
				resultA += chunkOriginal;
				resultB += chunkOriginal;
				chunkOriginal = "";
			}
			if (aedit.isInsert) {
				chunkA += aedit.change;
				a++;
			} else {
				chunkB += original.get(o);
				chunkOriginal += original.get(o);
				o++;
				a++;
			}
		}
		while (b < otob.size()) {
			Edit bedit = otob.get(b);
			if (o < bedit.index) {
				// Finally, combine the last chunks.
				if (!(chunkA.isEmpty() && chunkB.isEmpty() && chunkOriginal.isEmpty())) {
					if (chunkOriginal.equals(chunkA)) {
						// Nonconflicting edit by B:
						// Propagate edit to both versions
						resultA += chunkB;
						resultB += chunkB;
					} else if (chunkOriginal.equals(chunkB)) {
						// Nonconflicting edit by A
						// Propagate edit to both versions
						resultA += chunkA;
						resultB += chunkA;
					} else {
						// Conflicting edit.
						// May be falsely conflicting, but will be detected
						// by CompareChangeActivity.
						resultA += chunkA;
						resultB += chunkB;
					}
					chunkA = "";
					chunkB = "";
					chunkOriginal = "";
				}
				do {
					chunkOriginal += original.get(o);
					o++;
				} while (o < bedit.index);
				resultA += chunkOriginal;
				resultB += chunkOriginal;
				chunkOriginal = "";
			}
			if (bedit.isInsert) {
				chunkB += bedit.change;
				b++;
			} else {
				chunkA += original.get(o);
				chunkOriginal += original.get(o);
				o++;
				b++;
			}
		}
		if (chunkOriginal.equals(chunkA)) {
			// Nonconflicting edit by B:
			// Propagate edit to both versions
			resultA += chunkB;
			resultB += chunkB;
		} else if (chunkOriginal.equals(chunkB)) {
			// Nonconflicting edit by A
			// Propagate edit to both versions
			resultA += chunkA;
			resultB += chunkA;
		} else {
			// Conflicting edit.
			// May be falsely conflicting, but will be detected
			// by CompareChangeActivity.
			resultA += chunkA;
			resultB += chunkB;
		}
		//chunkA = "";
		//chunkB = "";
		chunkOriginal = "";
		while (o < original.size()) {
			chunkOriginal += original.get(o);
			o++;
		}
		resultA += chunkOriginal;
		resultB += chunkOriginal;

		return new String[] {resultA, resultB};
	}

	public static String fullyAutomatedMerge(ArrayList<String> original, ArrayList<Edit> otoa, ArrayList<Edit> otob) {
		int a = 0; // Index into otoa
		int b = 0; // Index into otob
		int o = 0; // Index into original string.
		String result = "";
		while (a < otoa.size() && b < otob.size()) {
			Edit aedit = otoa.get(a);
			Edit bedit = otob.get(b);
			while (o < aedit.index && o < bedit.index) {
				result += original.get(o);
				o++;
			}

			if (o == aedit.index && o == bedit.index) {
				// Edits took place at same location
				// Check if actually conflicting first
				if (aedit.isInsert == bedit.isInsert &&
						aedit.change.equals(bedit.change)) {
					// Changes actually the same: just play back one.
					if (aedit.isInsert)
						result += aedit.change;
					else
						o++;
					a++;
					b++;
					continue;
				}
				// Either one is an insert, and one is a delete,
				// Or both are inserts of different words.
				// Assume that A acted first
			}
			if (o == aedit.index) {
				//Either nonconflicting edit by A,
				//Or there was a conflicting edit, and we are
				//assuming A edits first.
				if (aedit.isInsert)
					result += aedit.change;
				else
					o++;
				a++;
			} else {
				// Nonconflicting edit by B
				if (bedit.isInsert)
					result += bedit.change;
				else
					o++;
				b++;
			}
		}

		// All remaining changes are nonconflicting: play them back.
		while (a < otoa.size()) {
			Edit aedit = otoa.get(a);
			while (o < aedit.index) {
				result += original.get(o);
				o++;
			}
			if (aedit.isInsert)
				result += aedit.change;
			else
				o++;
			a++;
		}
		while (b < otob.size()) {
			Edit bedit = otob.get(b);
			while (o < bedit.index) {
				result += original.get(o);
				o++;
			}
			if (bedit.isInsert)
				result += bedit.change;
			else
				o++;
			b++;
		}
		// All edits have now been played: finish copying over rest of string.
		while (o < original.size()) {
			result += original.get(o);
			o++;
		}
		return result;
	}

}

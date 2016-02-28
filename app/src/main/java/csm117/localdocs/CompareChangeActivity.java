package csm117.localdocs;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.util.AttributeSet;
import android.content.Intent;

import csm117.diff.Diff;
import csm117.diff.Edit;
import java.util.ArrayList;

import csm117.localdocs.R;

public class CompareChangeActivity extends AppCompatActivity {
	public final static String EXTRA_PREVIOUS_VERSION = "csm117.localdocs.PREVIOUS_VERSION";
	public final static String EXTRA_NEW_VERSION = "csm117.localdocs.NEXT_VERSION";

	public final static String EXTRA_ACCEPTED_CHANGES = "csm117.localdocs.ACCEPTED_CHANGES";

	private enum Choice {
		UNDECIDED, ACCEPT, REJECT
	}

	private Diff diff = null;
	private ArrayList<String> tokens = null;
	private Choice[] decisions = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_change);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar bar = getSupportActionBar();
		if (bar != null)
			bar.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		String prior = intent.getStringExtra(EXTRA_PREVIOUS_VERSION);
		String next = intent.getStringExtra(EXTRA_NEW_VERSION);
		// For testing, if not started by another activity.
		if (prior == null)
			prior = "come and learn about TLF's FREE programs (Scholarships, Mentoring, Job/Internship Placement and Career and Professional Development Workshops) that we provide for students pursuing a career in advertising, marketing and public relations.";
		if (next == null)
			next = "Come and learn about the LAGRANT Foundation's free programs that we provide for students pursuing a career in advertising, marketing and public relations!";
		diff = Diff.createDiff(prior, next);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.compare_layout);

		decisions = new Choice[diff.size()];
		for (int i = 0; i < decisions.length; i++)
			decisions[i] = Choice.UNDECIDED;

		//TextView testView = new TextView(this);
		//testView.setText(diff.toString());
		//layout.addView(testView);

		tokens = Diff.splitString(prior);
		if (decisions.length == 0) {
			Intent resIntent = new Intent();
			resIntent.putExtra(EXTRA_ACCEPTED_CHANGES, constructMergedString());
			setResult (Activity.RESULT_OK, resIntent);
			finish();
		}

		ArrayList<Edit> edits = diff.getChanges();
		int tokenIndex = 0;
		int editStart;
		int i = 0;
		int id = 1;
		int previousRowId = 0;
		while(i < edits.size()) {
			String unaltered = "";
			Edit e = edits.get(i);
			while (tokenIndex < e.index) {
				unaltered += tokens.get(tokenIndex);
				tokenIndex++;
			}
			if (unaltered.length() > 0) {
				TextView sameText = new TextView(this);
				sameText.setId(id);
				sameText.setText(unaltered);
				LayoutParams relative = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if (id > 1) {
					//relative.addRule(RelativeLayout.LEFT_OF, id - 1);
					relative.addRule(RelativeLayout.BELOW, previousRowId);
					//relative.addRule(RelativeLayout.END_OF, id-1);
				}
				else {
					relative.addRule(RelativeLayout.ALIGN_PARENT_TOP);
					relative.addRule(RelativeLayout.ALIGN_PARENT_START);
					relative.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				}
				sameText.setLayoutParams(relative);
				previousRowId = id;
				id++;
				layout.addView(sameText);
			}
			editStart = i;
			String altered = "";
			boolean inserting = true;
			while (i >= edits.size() || tokenIndex >= e.index || altered.length() > 0) {
				if (altered.length() > 0 && (i >= edits.size() || tokenIndex < e.index ||
						 inserting != e.isInsert)) {
					/*LinearLayout subLayout = new LinearLayout(this);
					subLayout.setLayoutParams(
							new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
					);
					subLayout.setOrientation(LinearLayout.HORIZONTAL);*/
					ArrayList<View> line = new ArrayList<View>();
					Button accept = new Button(this);
					Button reject = new Button(this);
					TextView changedText = new TextView(this);
					accept.setId(id);
					changedText.setId(id + 1);
					reject.setId(id + 2);
					line.add(accept);
					line.add(changedText);
					line.add(reject);
					accept.setBackgroundResource(android.R.drawable.ic_input_add);
					//accept.setText("+");
					accept.setOnClickListener(
							new DecisionListener(Choice.ACCEPT, editStart, i, line));

					LayoutParams relative = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					if (id > 1) {
						//relative.addRule(RelativeLayout.RIGHT_OF, id-1);
						relative.addRule(RelativeLayout.BELOW, previousRowId);
						//relative.addRule(RelativeLayout.RIGHT_OF, id-1)
						//relative.addRule(RelativeLayout.END_OF, id-1);
					}
					else {
						relative.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						relative.addRule(RelativeLayout.ALIGN_PARENT_START);
						relative.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					}
					accept.setLayoutParams(relative);
					layout.addView(accept);

					changedText.setText(altered);
					if (inserting)
						changedText.setBackgroundResource(R.color.colorGreen);
					else
						changedText.setBackgroundResource(R.color.colorRed);
					relative = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					relative.addRule(RelativeLayout.RIGHT_OF, id);
					//relative.addRule(RelativeLayout.BELOW, id);
					relative.addRule(RelativeLayout.END_OF, id);
					if (id > 1)
						relative.addRule(RelativeLayout.BELOW, previousRowId);
					relative.addRule(RelativeLayout.ALIGN_BASELINE, id);
					//changedText.setLayoutParams(relative);
					layout.addView(changedText, relative);

					reject.setBackgroundResource(android.R.drawable.ic_delete);
					//reject.setText("X");
					reject.setOnClickListener(
							new DecisionListener(Choice.REJECT, editStart, i, line));
					relative = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					relative.addRule(RelativeLayout.RIGHT_OF, id+1);
					//relative.addRule(RelativeLayout.BELOW, id+1);
					relative.addRule(RelativeLayout.END_OF, id+1);
					if (id > 1)
						relative.addRule(RelativeLayout.BELOW, previousRowId);
					relative.addRule(RelativeLayout.ALIGN_BASELINE, id+1);
					//reject.setLayoutParams(relative);
					layout.addView(reject, relative);

					altered = "";
					editStart = i;
					previousRowId = id+1;
					id += 3;
				}
				if (i >= edits.size() || tokenIndex < e.index)
					break;
				inserting = e.isInsert;
				altered += e.change;
				if (!inserting)
					tokenIndex++;
				i++;
				if (i < edits.size())
					e = edits.get(i);
			}
		}
		String remaining = "";
		while (tokenIndex < tokens.size()) {
			remaining += tokens.get(tokenIndex);
			tokenIndex++;
		}
		if (remaining.length() > 0) {
			TextView sameText = new TextView(this);
			sameText.setText(remaining);
			if (id > 0) {
				LayoutParams relative = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//relative.addRule(RelativeLayout.RIGHT_OF, id - 1);
				relative.addRule(RelativeLayout.BELOW, previousRowId);
				//relative.addRule(RelativeLayout.END_OF, id-1);
				sameText.setLayoutParams(relative);
			}
			layout.addView(sameText);
		}

	}

	private class DecisionListener implements View.OnClickListener {
		private int start_index;
		private int end_index;
		private Choice choice;
		private ArrayList<View> views;

		public DecisionListener(Choice c, int start, int end, ArrayList<View> v) {
			start_index = start;
			end_index = end;
			choice = c;
			views = v;
		}
		@Override
		public void onClick(View view) {
			for (int i = start_index; i < end_index; i++)
				decisions[i] = choice;
			// Remove the buttons
			views.get(0).setVisibility(View.GONE);
			views.get(views.size()-1).setVisibility(View.GONE);
			boolean isInsert = diff.getChanges().get(start_index).isInsert;
			if (choice == Choice.ACCEPT && !isInsert ||
					choice == Choice.REJECT && isInsert) {
				// If an insert was rejected,
				// Or if a deletion was accepted, remove said text.
				for (int i = 1; i < views.size() - 1; i++) {
					views.get(i).setVisibility(View.GONE);
				}
			}
			else {
				for (int i = 1; i < views.size() - 1; i++) {
					views.get(i).setBackgroundResource(R.color.colorDullYellow);
				}
			}
			if (allDecisionsMade()) {
				Intent intent = new Intent();
				intent.putExtra(EXTRA_ACCEPTED_CHANGES, constructMergedString());
				setResult (Activity.RESULT_OK, intent);
				finish();
			}
		}
	}

	private boolean allDecisionsMade() {
		if (decisions == null)
			return true;
		for (Choice c : decisions) {
			if (c == Choice.UNDECIDED)
				return false;
		}
		return true;
	}

	// precondition: allDecisionsMade is true.
	private String constructMergedString() {
		String merged = "";
		int j = 0;
		int i = 0;
		ArrayList<Edit> changes = diff.getChanges();
		while( i < changes.size()) {
			Edit e = changes.get(i);
			while (j < e.index) {
				merged += tokens.get(j);
				j++;
			}
			while (e != null && j >= e.index) {
				if (decisions[i] == Choice.ACCEPT) {
					if (e.isInsert) {
						merged += e.change;
					} else {
						j++;
					}
				}
				// That edit was handled, move on to next.
				i++;
				if (i < changes.size())
					e = changes.get(i);
				else
					e = null;
			}
		}
		while (j < tokens.size()) {
			merged += tokens.get(j);
			j++;
		}
		return merged;
	}
}

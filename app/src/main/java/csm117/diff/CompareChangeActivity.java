package csm117.diff;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;

import csm117.diff.Diff;
import csm117.diff.Edit;
import java.util.ArrayList;

import csm117.localdocs.R;

public class CompareChangeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compare_change);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar bar = getSupportActionBar();
		if (bar != null)
			bar.setDisplayHomeAsUpEnabled(true);

		String test1 = "Come and learn about TLF's FREE programs (Scholarships, Mentoring, Job/Internship Placement and Career and Professional Development Workshops) that we provide for students pursuing a career in advertising, marketing and public relations.";
		String test2 = "Come and learn about the LAGRANT Foundation's free programs that we provide for students pursuing a career in advertising, marketing and public relations.";
		Diff diff = Diff.createDiff(test1, test2);
		LinearLayout layout = (LinearLayout) findViewById(R.id.compare_layout);

		ArrayList<String> tokens = Diff.splitString(test1);
		ArrayList<Edit> edits = diff.getChanges();
		int tokenIndex = 0;
		int i = 0;
		while(i < edits.size()) {
			String unaltered = "";
			Edit e = edits.get(i);
			while (tokenIndex < e.index) {
				unaltered += tokens.get(tokenIndex);
				tokenIndex++;
			}
			if (unaltered.length() > 0) {
				TextView sameText = new TextView(this);
				sameText.setText(unaltered);
				layout.addView(sameText);
			}
			String altered = "";
			boolean inserting = true;
			while (i > edits.size() || tokenIndex >= e.index || altered.length() > 0) {
				if (altered.length() > 0 && (i > edits.size() || tokenIndex < e.index ||
						inserting != e.isInsert)) {
					LinearLayout subLayout = new LinearLayout(this);
					subLayout.setLayoutParams(
							new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
					);
					subLayout.setOrientation(LinearLayout.HORIZONTAL);

					ImageButton accept = new ImageButton(this);
					accept.setBackgroundResource(android.R.drawable.ic_input_add);
					subLayout.addView(accept);

					TextView changedText = new TextView(this);
					changedText.setText(altered);
					if (inserting)
						changedText.setBackgroundResource(R.color.colorGreen);
					else
						changedText.setBackgroundResource(R.color.colorRed);
					subLayout.addView(changedText);

					ImageButton reject = new ImageButton(this);
					reject.setBackgroundResource(android.R.drawable.ic_delete);
					subLayout.addView(reject);

					layout.addView(subLayout);
					altered = "";
				}
				if (i > edits.size() || tokenIndex < e.index)
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
			layout.addView(sameText);
		}

	}

}

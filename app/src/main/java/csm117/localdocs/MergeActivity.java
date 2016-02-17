package csm117.localdocs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.text.Html;
import csm117.diff.Diff;

public class MergeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		ActionBar bar = getSupportActionBar();
		if (bar != null)
			bar.setDisplayHomeAsUpEnabled(true);

		String mine = "A very long text. This text is very long - much much longer than the other lines.  Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.  Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.  Blah Blah Blah Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.";
		String theirs = "Very long text. This text is very long - much much longer than the other lines.  Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.  Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.  Blah Blah Blah Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.";

		Diff diff = Diff.createDiff(theirs, mine);
		String base = "";
		if (diff != null)
			base = diff.getColoredHTMLString(theirs);
		((TextView) findViewById(R.id.myText)).setText(Html.fromHtml(mine));
		((TextView) findViewById(R.id.theirText)).setText(Html.fromHtml(theirs));
		((TextView) findViewById(R.id.mergeText)).setText(Html.fromHtml(base));
	}

}

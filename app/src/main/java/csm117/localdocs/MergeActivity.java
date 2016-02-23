package csm117.localdocs;

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
import csm117.diff.Diff;

public class MergeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merge);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar bar = getSupportActionBar();
		if (bar != null)
			bar.setDisplayHomeAsUpEnabled(true);

		String mine = "A very long text. This text is very long - much much longer than the other lines.  Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.  Blah Blah Blah Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly. Change at end of text.";
		String theirs = "Change at beginning.  Very long text. This text is very long - much much longer than the other lines.  Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.  Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.  Blah Blah Blah Very long text. This text is very long - much much longer than the other lines.  This is to test line-wrapping behavior, and to see if the text below is shifted down incorrectly.";

		Diff diff = Diff.createDiff(theirs, mine);
		String base = "";
		if (diff != null)
			base = diff.getColoredHTMLString(theirs);
		ScrollingMovementMethod method = new ScrollingMovementMethod();
		TextView myView = (TextView) findViewById(R.id.myText);
		myView.setText(Html.fromHtml(mine));
		myView.setMovementMethod(method);
		TextView theirView = (TextView) findViewById(R.id.theirText);
		theirView.setText(Html.fromHtml(theirs));
		theirView.setMovementMethod(method);
		TextView combined = (TextView) findViewById(R.id.mergeText);
		combined.setText(Html.fromHtml(base));
		combined.setMovementMethod(method);
	}

}

package dd.android.shenmajia.billshow;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BillFormActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_bill_form, menu);
		return true;
	}

}

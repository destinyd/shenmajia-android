package dd.android.shenmajia.billshow;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import dd.android.shenmajia.common.ShenmajiaApi;

public class LauncherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		PropertiesUtil.readConfiguration(this);
		// Toast.makeText(this, Settings.access_token,
		// Toast.LENGTH_LONG).show();
		if (ShenmajiaApi.get_me(this)) {
			ShenmajiaApi.change_activity(this, DashboardActivity.class);
		}
		else
		{
			ShenmajiaApi.change_activity(this, LoginActivity.class);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_launcher, menu);
		return true;
	}

}

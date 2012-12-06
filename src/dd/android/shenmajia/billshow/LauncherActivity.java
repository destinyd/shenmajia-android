package dd.android.shenmajia.billshow;

import android.app.Activity;
import android.os.Bundle;
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

}

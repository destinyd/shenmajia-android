package dd.android.shenmajia.billshow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import dd.android.shenmajia.common.ShenmajiaApi;

public class LoginActivity extends Activity {

//	String username = "user@example.com";
//	String password = "sekret";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void loginClick(View v) {

		EditText et_email = (EditText) this.findViewById(R.id.et_email);
		EditText et_password = (EditText) this.findViewById(R.id.et_password);

		String email = et_email.getText().toString();
		String password = et_password.getText().toString();

		if(!ShenmajiaApi.get_access_token(this,email, password))
				return;

		if(ShenmajiaApi.get_me(this))
		{
			ShenmajiaApi.change_activity(this,DashboardActivity.class);
		}
	}


	public void regClick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, RegActivity.class);
		startActivity(intent);
		// 如果不关闭当前的会出现好多个页面
		this.finish();
	}

}

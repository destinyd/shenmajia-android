package dd.android.shenmajia.billshow;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		UMFeedbackService.enableNewReplyNotification(this, NotificationType.AlertDialog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sign, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 如果不关闭当前的会出现好多个页面
		return MenusController.signOptionsItemSelected(this,item);
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
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}

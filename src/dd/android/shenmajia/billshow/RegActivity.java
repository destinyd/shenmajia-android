package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import dd.android.shenmajia.api.V1;
import dd.android.shenmajia.common.ShenmajiaApi;

public class RegActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
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

	public void regClick(View v) {
		EditText et_email = (EditText) this.findViewById(R.id.et_email);
		EditText et_password = (EditText) this.findViewById(R.id.et_password);
		EditText et_password_confirm = (EditText) this
				.findViewById(R.id.et_password_confirm);
		EditText et_username = (EditText) this.findViewById(R.id.et_username);

		String email = et_email.getText().toString();
		String password = et_password.getText().toString();
		String password_confirm = et_password_confirm.getText().toString();
		String username = et_username.getText().toString();

		JSONObject json = V1.reg(email, password, password_confirm, username);
		try {
			if (json.containsKey("id")) {
				Settings.getFactory().username = json.getString("username");
				Settings.getFactory().email = json.getString("email");
			} else {
				ArrayList<String> errors = new ArrayList<String>();
				Set<String> keys = json.keySet();
				String output = "";
				for (String key : keys) {
					errors.add(key + ":" + json.getString(key));
				}
				output = TextUtils.join(", ", errors);
				Toast.makeText(this, output, Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "注册失败", Toast.LENGTH_LONG).show();
			return;
		}
		
		if(!ShenmajiaApi.get_access_token(this,email, password))
			return;
		if(ShenmajiaApi.get_me(this))
		{
			ShenmajiaApi.change_activity(this,DashboardActivity.class);
		}
	}

	public void loginClick(View v) {

		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		// 如果不关闭当前的会出现好多个页面
		this.finish();

	}

}

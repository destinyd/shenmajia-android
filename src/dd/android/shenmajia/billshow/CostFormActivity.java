package dd.android.shenmajia.billshow;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import dd.android.shenmajia.common.ShenmajiaApi;

public class CostFormActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cost_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 如果不关闭当前的会出现好多个页面
		return MenusController.mainOptionsItemSelected(this,item);
	}

	public void submit(View v) {
		EditText et_money = (EditText) findViewById(R.id.et_money);
		EditText et_desc = (EditText) findViewById(R.id.et_desc);
		String str_money = et_money.getText().toString().trim();
		if(str_money.length() == 0
				){
			Toast.makeText(this,
					"消费金额不能为空",
					Toast.LENGTH_LONG).show();
			return;
		}
		Float money = Float.parseFloat(str_money);
		if(money <= 0
				){
			Toast.makeText(this,
					"消费金额必须大于0",
					Toast.LENGTH_LONG).show();
			return;
		}		
		String desc = et_desc.getText().toString();
		if(ShenmajiaApi.create_cost(money, desc)){
			DashboardActivity.factory().finish();
			ShenmajiaApi.change_activity(this, DashboardActivity.class);
		}

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

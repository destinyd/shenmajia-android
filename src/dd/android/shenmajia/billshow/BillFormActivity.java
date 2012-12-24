package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import dd.android.shenmajia.api.BillPrice;
import dd.android.shenmajia.api.Good;
import dd.android.shenmajia.billshow.adapter.BillPriceAdapter;
import dd.android.shenmajia.common.ShenmajiaApi;

public class BillFormActivity extends Activity {

	static BillFormActivity factory;
	static Integer place_id;
	static String place_name;
	Intent mIntent;
	ListView lv_bill_prices;

	static String format_delete_bill_price = "确定要删除%s吗？";
	static String format_submit = "总额为%.2f,你支付了其中的%.2f,确定要提交吗？";

	BillPrice long_click_bill_price;

	static List<BillPrice> bill_prices = new ArrayList<BillPrice>();

	static String format_norm = "(%s)";


	Runnable runnable;
	double double_old_total = 0.0;
	TextView tv_total;
	EditText et_total, et_cost;
	String format_total = "%.2f";

	// Thread
//	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();
	// Thread
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_form);
		factory = this;
		mIntent = getIntent();
		Integer p_place_id = mIntent.getIntExtra("place_id", 0);
		place_name = mIntent.getStringExtra("place_name");
		if(p_place_id != place_id){
			place_id = p_place_id;
			bill_prices.clear();
		}
		Log.d("place_name", place_id.toString());
		Log.d("place_name", place_name);
		if (place_name == null) {
			ShenmajiaApi.change_activity(this, DashboardActivity.class);
			return;
		}
		lv_bill_prices = (ListView) findViewById(R.id.lv_bill_prices);

		tv_total = (TextView) findViewById(R.id.tv_total);
		et_total = (EditText) findViewById(R.id.et_total);
		et_cost = (EditText) findViewById(R.id.et_cost);

		bind_bill_prices();

		bind_long_click();

		set_timer();
		
		if (bill_prices.size() == 0) {
			show_search_good_dialog();
		}
	}

	void set_timer() {
		runnable = new Runnable() {
			@Override
			public void run() {
				double total = 0.0;
				for (BillPrice bp : bill_prices) {
					total += bp.total();
				}
				if (total != double_old_total) {
					double_old_total = total;
					String str_total = String.format(format_total, total);
					Log.d("str_total", str_total);
					tv_total.setText(str_total);
					et_total.setText(str_total);
					et_cost.setText(str_total);
				}
				// 要做的事情
				mainHandler.postDelayed(this, 1000);
			}
		};
		mainHandler.postDelayed(runnable, 1000);
	}

	public void bind_long_click() {
		lv_bill_prices
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						long_click_bill_price = (BillPrice) (lv_bill_prices
								.getItemAtPosition(arg2));
						new AlertDialog.Builder(BillFormActivity.this)
								.setTitle("提示")
								.setMessage(
										String.format(format_delete_bill_price,
												long_click_bill_price.name))
								.setPositiveButton("确认", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										bill_prices
												.remove(long_click_bill_price);

										bind_bill_prices();
										dialog.dismiss();
									}
								}).setNegativeButton("取消", null).show();
						return false;
					}
				});
	}

	public void click_btn_add_bill_price(View v) {
		show_search_good_dialog();
	}

	private void show_search_good_dialog() {
		Intent intent = new Intent();
		intent.setClass(BillFormActivity.this, SearchGoodActivity.class);
		intent.putExtra("place_id", place_id);
		this.startActivity(intent);
	}

	public void submit(View v) {
		Double total = Double.parseDouble(et_total.getText().toString());
		Double cost = Double.parseDouble(et_cost.getText().toString());
		new AlertDialog.Builder(BillFormActivity.this).setTitle("提示")
				.setMessage(String.format(format_submit, total, cost))
				.setPositiveButton("确认", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Double total = Double.parseDouble(et_total.getText()
								.toString());
						Double cost = Double.parseDouble(et_cost.getText()
								.toString());
						if (ShenmajiaApi.create_bill(place_id, total, cost,
								bill_prices)) {

							PlacesActivity.factory.finish();
							mainHandler.removeCallbacks(runnable);
							bill_prices.clear();

							ShenmajiaApi.change_activity(BillFormActivity.this,
									DashboardActivity.class);
						}
						dialog.dismiss();
					}
				}).setNegativeButton("取消", null).show();
	}
//
//	AlertDialog ad;

	public void bind_bill_prices() {
		BillPriceAdapter bill_price_adapter = new BillPriceAdapter(
				BillFormActivity.this, bill_prices);
		lv_bill_prices.setAdapter(bill_price_adapter);
	}



	public void add_bill_price(Good good) {
		BillPrice bp = BillPrice.from_good(good);
		// Toast.makeText(BillFormActivity.this,
		// hash.get("id").toString(), Toast.LENGTH_SHORT).show();
		Log.d("click_item", good.toString());

		// selected_bill_prices.add(good);
		Boolean has_it = false;
		for (BillPrice bill_price : bill_prices) {
			if (bill_price.good_id == bp.good_id) {
				has_it = true;
				break;
			}
		}
		if (!has_it) {
			bill_prices.add(bp);

			bind_bill_prices();
		}
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
		return MenusController.mainOptionsItemSelected(this, item);
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

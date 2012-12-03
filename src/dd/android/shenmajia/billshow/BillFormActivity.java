package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import dd.android.shenmajia.api.BillPrice;
import dd.android.shenmajia.api.Good;
import dd.android.shenmajia.billshow.adapter.BillPriceAdapter;
import dd.android.shenmajia.billshow.adapter.GoodAdapter;
import dd.android.shenmajia.common.ShenmajiaApi;

public class BillFormActivity extends Activity {

	static Integer place_id;
	static String place_name;
	Intent mIntent;
	ListView lv_bill_prices;
	EditText et_search_good_q;
	View dialog_search_good = null;
	View dialog_good_form = null;
	ListView lv_goods = null;

	String format_delete_bill_price = "确定要删除%s吗？";

	BillPrice long_click_bill_price;

	List<Good> goods;
	static List<BillPrice> bill_prices = new ArrayList<BillPrice>();

	// static List<HashMap<String, Object>> search_goods;
	// static List<HashMap<String, Object>> selected_bill_prices = new
	// ArrayList<HashMap<String, Object>>();
	static String format_norm = "(%s)";

	// Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();

	// Thread

	Runnable runnable;
	double double_old_total = 0.0;
	TextView tv_total;
	EditText et_total;
	String format_total = "%.2f";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bill_form);
		mIntent = getIntent();
		place_id = mIntent.getIntExtra("place_id", 0);
		place_name = mIntent.getStringExtra("place_name");
		Log.d("place_name", place_name);
		// place_id = savedInstanceState.getInt("place_id");
		// place_name = savedInstanceState.getString("place_name");
		// TextView tv_place_name = (TextView)findViewById(R.id.tv_place_name);
		// tv_place_name.setText(place_id.toString() + ":" + place_name);
		lv_bill_prices = (ListView) findViewById(R.id.lv_bill_prices);

		tv_total = (TextView) findViewById(R.id.tv_total);
		et_total = (EditText) findViewById(R.id.et_total);

		bind_bill_prices();

		bind_long_click();

		set_timer();

		// dialog_search_good = inflater
		// .inflate(R.layout.dialog_search_good, null);

		// et_search_good_q = (EditText) dialog_search_good
		// .findViewById(R.id.et_search_good_q);
		//
		// lv_goods = (ListView) dialog_search_good.findViewById(R.id.lv_goods);
		// Log.d("search_good_q",et.getText().toString());
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

	public void submit(View v) {
		new AlertDialog.Builder(BillFormActivity.this)
		.setTitle("提示")
		.setMessage("确认提交?")
		.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {

				Double total = Double.parseDouble(et_total.getText()
						.toString());
				if (ShenmajiaApi.create_bill(place_id, total,
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

	public void search_good(View v) {
		service.submit(new Runnable() {

			@Override
			public void run() {

				Log.d("search_good_q", et_search_good_q.getText().toString());
				String result = ShenmajiaApi.get_search_good(et_search_good_q
						.getText().toString());
				// List<Good>
				goods = JSON.parseArray(result, Good.class);
				//
				// search_goods = new ArrayList<HashMap<String, Object>>();
				//
				// for (Good good : goods) {
				// HashMap<String, Object> item = new HashMap<String, Object>();
				// item.put("id", good.id);
				// item.put("name", good.name);
				// item.put("unit", good.unit);
				// if (good.norm == null || good.norm.trim() == "")
				// item.put("norm", "");
				// else
				// item.put("norm", String.format(format_norm, good.norm));
				// search_goods.add(item);
				//
				// }

				mainHandler.post(new Runnable() {
					@Override
					public void run() {// 这将在主线程运行
						GoodAdapter good_adapter = new GoodAdapter(
								BillFormActivity.this, goods);
						// SimpleAdapter adapter = new SimpleAdapter(
						// BillFormActivity.this, search_goods,
						// R.layout.good_item, new String[] { "id",
						// "name", "norm", "unit" }, new int[] {
						// 0, R.id.tv_name, R.id.tv_norm,
						// R.id.tv_unit });
						lv_goods.setAdapter(good_adapter);

					}
				});
			}
		});
	}

	AlertDialog ad;

	public void bind_bill_prices() {
		BillPriceAdapter bill_price_adapter = new BillPriceAdapter(
				BillFormActivity.this, bill_prices);
		// SimpleAdapter adapter = new SimpleAdapter(BillFormActivity.this,
		// selected_bill_prices, R.layout.bill_price_item, new String[] { "id",
		// "name", "norm", "unit","price","amount" }, new int[] { 0,
		// R.id.tv_name,
		// R.id.tv_norm, R.id.tv_unit, R.id.et_price,R.id.et_amount });
		lv_bill_prices.setAdapter(bill_price_adapter);
	}

	public void show_search_good_dialog() {
		if (dialog_search_good != null) {
			BillFormActivity.this.removeDialog(R.layout.dialog_search_good);
			dialog_search_good = null;
		}
		LayoutInflater inflater = getLayoutInflater();

		dialog_search_good = inflater
				.inflate(R.layout.dialog_search_good, null);
		et_search_good_q = (EditText) dialog_search_good
				.findViewById(R.id.et_search_good_q);
		lv_goods = (ListView) dialog_search_good.findViewById(R.id.lv_goods);

		bind_lv_goods_click();

		ad = new AlertDialog.Builder(BillFormActivity.this).setTitle("搜索商品")
				.setView(dialog_search_good).setPositiveButton("取消", null)
				.setNegativeButton("新建商品", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialog_good_form != null) {
							BillFormActivity.this
									.removeDialog(R.layout.dialog_good_form);
							dialog_good_form = null;
						}

						LayoutInflater inflater = getLayoutInflater();
						dialog_good_form = inflater.inflate(
								R.layout.dialog_good_form, null);
						new AlertDialog.Builder(BillFormActivity.this)
								.setTitle("新建商品").setView(dialog_good_form)
								.setPositiveButton("提交", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).setNeutralButton("取消", null).show();
					};
				}).show();
	}

	public void bind_lv_goods_click() {
		lv_goods.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				Good good = (Good) lv_goods.getItemAtPosition(myItemInt);
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
				ad.dismiss();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_bill_form, menu);
		return true;
	}

}

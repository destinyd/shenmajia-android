package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import dd.android.shenmajia.api.Good;
import dd.android.shenmajia.billshow.adapter.GoodAdapter;
import dd.android.shenmajia.common.ShenmajiaApi;

public class GoodSearchActivity extends Activity {

	Integer place_id;

	EditText et_search_good_q;
	PullToRefreshListView lv_goods = null;
	Integer page = 1, state = 0;
	String q = "";
	GoodAdapter good_adapter = null;
	List<Good> goods = new ArrayList<Good>(), n_goods;
	View dialog_good_form = null;

	// Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();

	// Thread
	ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_good_search);

		Intent mIntent = getIntent();
		place_id = mIntent.getIntExtra("place_id", 0);
		Log.d("search_good place_id", place_id.toString());

		et_search_good_q = (EditText) findViewById(R.id.et_search_good_q);
		lv_goods = (PullToRefreshListView) findViewById(R.id.lv_goods);
		bind_lv_goods_listeners();

		load_place_goods();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public void load_place_goods(View v) {
		load_place_goods();
	}

	public void load_place_goods() {
		page = 1;
		state = 0;
		load_place_goods(page);
	}

	public void load_place_goods(final Integer page) {
		if (dialog == null) {
			dialog = ShenmajiaApi.loading(this, "读取本地点商品...");
			service.submit(new Runnable() {
				@Override
				public void run() {
					n_goods = ShenmajiaApi.get_place_goods(place_id, page);
					if (page == 1) {
						goods.clear();
					}
					goods.addAll(n_goods);
					mainHandler.post(new Runnable() {
						@Override
						public void run() {// 这将在主线程运行
							if (good_adapter == null) {
								good_adapter = new GoodAdapter(
										GoodSearchActivity.this, goods);
								lv_goods.setAdapter(good_adapter);
							} else {
								good_adapter.notifyDataSetChanged();
							}
							dialog.dismiss();
							dialog = null;
						}
					});
				}
			});
		}

	}

	public void search_good(View v) {
		page = 1;
		state = 1;
		q = et_search_good_q.getText().toString();
		search_good(page);
	}

	void search_good(final Integer page) {
		Log.d("search_good_q", q);
		Log.d("search_good_page", page.toString());
		if (dialog == null) {
			dialog = ShenmajiaApi.loading(this, "搜索商品...");		
		service.submit(new Runnable() {
			@Override
			public void run() {
				String result = ShenmajiaApi.get_search_good(q, page);
				n_goods = JSON.parseArray(result, Good.class);
				if (page == 1) {
					goods.clear();
				}
				goods.addAll(n_goods);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {// 这将在主线程运行
						if (good_adapter == null) {
							good_adapter = new GoodAdapter(
									GoodSearchActivity.this, goods);
							lv_goods.setAdapter(good_adapter);
						} else {
							good_adapter.notifyDataSetChanged();
						}
						dialog.dismiss();
						dialog = null;
					}
				});
			}
		});
		}
	}

	public void new_good(View v) {
		show_dialog_good_form();
	}

	public void show_dialog_good_form() {
		if (dialog_good_form != null) {
			GoodSearchActivity.this.removeDialog(R.layout.dialog_good_form);
			dialog_good_form = null;
		}

		LayoutInflater inflater = getLayoutInflater();
		dialog_good_form = inflater.inflate(R.layout.dialog_good_form, null);
		new AlertDialog.Builder(GoodSearchActivity.this).setTitle("新建商品")
				.setView(dialog_good_form)
				.setPositiveButton("提交", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = ((EditText) dialog_good_form
								.findViewById(R.id.et_name)).getText()
								.toString().trim();
						String unit = ((EditText) dialog_good_form
								.findViewById(R.id.et_unit)).getText()
								.toString().trim();
						String taglist = ((EditText) dialog_good_form
								.findViewById(R.id.et_taglist)).getText()
								.toString().trim();
						String norm = ((EditText) dialog_good_form
								.findViewById(R.id.et_norm)).getText()
								.toString().trim();
						String barcode = ((EditText) dialog_good_form
								.findViewById(R.id.et_barcode)).getText()
								.toString().trim();
						String origin = ((EditText) dialog_good_form
								.findViewById(R.id.et_origin)).getText()
								.toString().trim();
						String desc = ((EditText) dialog_good_form
								.findViewById(R.id.et_desc)).getText()
								.toString().trim();
						if (name.length() > 0 && unit.length() > 0) {
							Good good = ShenmajiaApi.create_good(name, unit,
									taglist, norm, barcode, origin, desc);
							add_bill_price(good);
							dialog.dismiss();
						}
					}
				}).setNeutralButton("取消", null).show();
	};

	public void bind_lv_goods_listeners() {
		lv_goods.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int myItemInt, long mylng) {
				Good good = goods.get(myItemInt - 1);// .getItemAtPosition(myItemInt);
				add_bill_price(good);
			}

		});

		lv_goods.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				page += 1;
				// q = et_search_good_q.getText().toString();
				if (state == 0) {
					load_place_goods(page);
				} else {
					search_good(page);
				}
			}
		});
	}

	protected void add_bill_price(Good good) {
		BillFormActivity.factory.add_bill_price(good);
		finish();
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

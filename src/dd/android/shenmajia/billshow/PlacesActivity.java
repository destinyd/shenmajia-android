package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import dd.android.shenmajia.api.Place;
import dd.android.shenmajia.billshow.adapter.PlaceAdapter;
import dd.android.shenmajia.common.ShenmajiaApi;

public class PlacesActivity extends Activity {
	public static PlacesActivity factory = null;

	PullToRefreshListView lv_places;

	static List<Place> places = new ArrayList<Place>();
	List<Place> n_places;
	ProgressDialog dialog = null;
	PlaceAdapter places_adapter = null;
	// PlacesActivity _factory = null;

	// Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();
	// Thread

	static Integer page = 1, state = 0; // 0 near 1 search

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		// _factory = this;
		lv_places = (PullToRefreshListView) findViewById(R.id.lv_places);
		factory = this;
		set_places_view_click();

		if (places.size() == 0) {
			load_near_places();
		} else {
			bind_places();
		}
	}

	public void load_near_places(View v) {
		load_near_places();
	}

	private void load_near_places() {
		state = 0;
		page = 1;
		places.clear();
		load_near_places(1);
	}

	private void load_near_places(final int page) {
		if (dialog == null) {
			dialog = ShenmajiaApi.loading(PlacesActivity.this, "读取周边地点...");
			service.submit(new Runnable() {

				@Override
				public void run() {
					get_near_places(page);
					// final List<HashMap<String, Object>> data =
					// search_result_to_data(result);
					mainHandler.post(new Runnable() {
						@Override
						public void run() {// 这将在主线程运行
							// bind_places_view(data);
							// places_adapter.notifyDataSetChanged();
							Log.d("load_near_places", "load_near_places");
							places.addAll(n_places);
							bind_places();
							dialog.dismiss();
							dialog = null;
						}

					});
				}

			});
		}
	}

	public void bind_places() {
		Log.d("bind_places", "bind_places");
		if (places_adapter == null) {
			ListView actualListView = lv_places.getRefreshableView();
			places_adapter = new PlaceAdapter(PlacesActivity.this, places);
			actualListView.setAdapter(places_adapter);
		} else {
			places_adapter.notifyDataSetChanged();
		}
		lv_places.onRefreshComplete();
	}

	public void get_near_places(final int page) {
		String result = ShenmajiaApi.get_near_places(page);
		n_places = JSON.parseArray(result, Place.class);
	}

	public String get_search_q() {
		EditText et = (EditText) findViewById(R.id.et_place_name);
		return et.getText().toString();
	}

	public void load_search_place(Integer page) {
		String q = get_search_q();
		load_search_place(q, page);
	}

	public void load_search_place() {
		state = 1;
		page = 1;
		places.clear();
		load_search_place(1);
	}

	//
	// private void load_search_place(String q) {
	// load_search_place(q, 1);
	// }

	private void load_search_place(final String q, final int page) {
		if (dialog == null) {
			dialog = ShenmajiaApi.loading(PlacesActivity.this, "搜索中...");
			service.submit(new Runnable() {

				@Override
				public void run() {
					get_search_places(q, page);
					mainHandler.post(new Runnable() {
						@Override
						public void run() {// 这将在主线程运行
							Log.d("load_search_place", "load_search_place");
							places.addAll(n_places);
							bind_places();
							dialog.dismiss();
							dialog = null;
						}
					});
				}

			});
		}
	}

	public void get_search_places(final String q, final int page) {
		String result = ShenmajiaApi.get_search_place(q, page);
		n_places = JSON.parseArray(result, Place.class);
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

	// String distance_m_format = "%.0f 米";
	// public void near_places() {
	// String result = ShenmajiaApi.get_near_places();
	//
	// List<HashMap<String, Object>> data = search_result_to_data(result);
	// bind_places_view(data);
	// }

	public void search_place(View v) {
		load_search_place();
	}

	public void set_places_view_click() {
		// 用SimpleAdapter 绑定ListView控件
		lv_places.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Place select_place = places.get(arg2);
				Intent intent = new Intent();
				intent.setClass(PlacesActivity.this, BillFormActivity.class);
				intent.putExtra("place_id", select_place.id);
				intent.putExtra("place_name", select_place.name);
				Log.d("select place_id", select_place.id.toString());
				Log.d("select place_name", select_place.name);
				PlacesActivity.this.startActivity(intent);
				// PlacesActivity.this.finish();
				Toast.makeText(PlacesActivity.this,
						"填写在" + places.get(arg2).name + "的账单",
						Toast.LENGTH_SHORT).show();
			}

		});
		//
		lv_places.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				lv_places.setLastUpdatedLabel(DateUtils
						.formatDateTime(getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL));
				if (state == 1) {
					load_search_place();
				} else {
					load_near_places();
				}

			}

		});

		// Add an end-of-list listener
		lv_places.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (dialog == null) {
					page += 1;
					if (state == 1) {
						load_search_place(page);
					} else {
						load_near_places(page);
					}
				}
			}
		});

	}
}

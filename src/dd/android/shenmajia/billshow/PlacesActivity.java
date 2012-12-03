package dd.android.shenmajia.billshow;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import dd.android.shenmajia.api.Place;
import dd.android.shenmajia.billshow.adapter.PlaceAdapter;
import dd.android.shenmajia.common.ShenmajiaApi;

public class PlacesActivity extends Activity {
	public static PlacesActivity factory = null;

	ListView lv_places;

	static List<Place> places = null;
	ProgressDialog dialog = null;
	PlaceAdapter places_adapter;
	// PlacesActivity _factory = null;

	// Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();

	// Thread

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
		// _factory = this;
		lv_places = (ListView) findViewById(R.id.lv_places);
		factory = this;
		set_places_view_click();

		if (places == null) {
			load_near_places();
		} else {
			bind_places();
		}
	}

	public void load_near_places(View v) {
		load_near_places();
	}

	private void load_near_places() {
		load_near_places(1);
	}

	private void load_near_places(final int page) {
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

						bind_places();
						dialog.dismiss();
					}

				});
			}

		});
	}

	public void bind_places() {
		places_adapter = new PlaceAdapter(PlacesActivity.this, places);
		lv_places.setAdapter(places_adapter);
	}

	public void get_near_places(final int page) {
		String result = ShenmajiaApi.get_near_places(page);
		places = JSON.parseArray(result, Place.class);
	}

	private void load_search_place(String q) {
		load_search_place(q, 1);
	}

	private void load_search_place(final String q, final int page) {
		service.submit(new Runnable() {

			@Override
			public void run() {
				String result = ShenmajiaApi.get_search_place(q, page);
				places = JSON.parseArray(result, Place.class);

				mainHandler.post(new Runnable() {
					@Override
					public void run() {// 这将在主线程运行
						bind_places();
						dialog.dismiss();
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_places, menu);
		return true;
	}

	// String distance_m_format = "%.0f 米";
	// public void near_places() {
	// String result = ShenmajiaApi.get_near_places();
	//
	// List<HashMap<String, Object>> data = search_result_to_data(result);
	// bind_places_view(data);
	// }

	public void search_place(View v) {
		EditText et = (EditText) findViewById(R.id.et_place_name);
		String q = et.getText().toString();
		load_search_place(q);
		dialog = ShenmajiaApi.loading(PlacesActivity.this, "搜索中...");
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
	}
}

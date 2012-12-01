package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import dd.android.shenmajia.api.Place;
import dd.android.shenmajia.common.ShenmajiaApi;

public class PlacesActivity extends Activity {
	public static PlacesActivity factory = null;

	ListView lv_places;

	String distance_km_format = "%.2f 公里";
	String distance_m_format = "%.0f 米";
	List<Place> list;
	ProgressDialog dialog = null;
//	PlacesActivity _factory = null;

	//Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler() ;
	//Thread

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
//		_factory = this;
		lv_places = (ListView) findViewById(R.id.lv_places);

		// near_places();
		set_places_view_click();
		dialog = ShenmajiaApi.loading(PlacesActivity.this,"读取周边地点...");
		load_near_places();
		factory = this;
	}

	private void load_near_places(final int page) {
		service.submit(new Runnable() {

			@Override
			public void run() {
				String result = ShenmajiaApi.get_near_places(page);
				final List<HashMap<String, Object>> data = search_result_to_data(result);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {// 这将在主线程运行
						bind_places_view(data);
						dialog.dismiss();
					}
				});
			}
		});
	}
	
	private void load_search_place(String q) {
		load_search_place(q,1);
	}
	
	private void load_search_place(final String q,final int page) {
		service.submit(new Runnable() {

			@Override
			public void run() {
				String result = ShenmajiaApi.get_search_place(q,page);

				final List<HashMap<String, Object>> data = search_result_to_data(result);				
				mainHandler.post(new Runnable() {
					@Override
					public void run() {// 这将在主线程运行
						bind_places_view(data);
						dialog.dismiss();
					}
				});
			}
		});
	}	

	private void load_near_places() {
		load_near_places(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_places, menu);
		return true;
	}

	// String distance_m_format = "%.0f 米";
	public void near_places() {
		String result = ShenmajiaApi.get_near_places();

		List<HashMap<String, Object>> data = search_result_to_data(result);
		bind_places_view(data);
	}

	public void search_place(View v) {
		EditText et = (EditText) findViewById(R.id.et_place_name);
		String q = et.getText().toString();
		load_search_place(q);
		dialog = ShenmajiaApi.loading(PlacesActivity.this,"搜索中...");
//		String result = ShenmajiaApi.get_search_place(q);

//		List<HashMap<String, Object>> data = search_result_to_data(result);
//		bind_places_view(data);
	}

	private void bind_places_view(List<HashMap<String, Object>> data) {
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.place_item, new String[] { "id", "name", "address",
						"distance" }, new int[] { 0, R.id.tv_title,
						R.id.tv_address, R.id.tv_distance });
		lv_places.setAdapter(adapter);

	}

	public List<HashMap<String, Object>> search_result_to_data(String result) {
		list = JSON.parseArray(result, Place.class);
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		for (Place place : list) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			Double distance = calculateHaversineMI(place.lat, place.lon,
					Settings.lat, Settings.lng);
			// distance = Math.sqrt( (place.lat - Settings.lat) * (place.lat -
			// Settings.lat) + (place.lng - Settings.lng) * (place.lng -
			// Settings.lng));
			item.put("id", place.id);
			item.put("name", place.name);
			item.put("address", place.address);
			if (distance > 1.0)
				item.put("distance",
						String.format(distance_km_format, distance));
			else {
				item.put("distance",
						String.format(distance_m_format, distance * 1000));
			}
			data.add(item);

		}
		return data;
	}

	public void set_places_view_click() {
		// 用SimpleAdapter 绑定ListView控件
		lv_places.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) lv_places
						.getItemAtPosition(arg2);
				Intent intent = new Intent();
				intent.setClass(PlacesActivity.this, BillFormActivity.class);
				intent.putExtra("place_id", (Integer) map.get("id"));
				intent.putExtra("place_name", (String) map.get("name"));
				Log.d("select place_id", map.get("id").toString());
				Log.d("select place_name", (String) map.get("name"));
				PlacesActivity.this.startActivity(intent);
				// PlacesActivity.this.finish();
				Toast.makeText(PlacesActivity.this,
						"填写在" + list.get(arg2).name + "的账单", Toast.LENGTH_SHORT)
						.show();
			}

		});
	}

	public static double calculateHaversineMI(double lat1, double long1,
			double lat2, double long2) {
		Log.d("pos", String.format("%f,%f %f,%f", lat1, long1, lat2, long2));
		double dlong = (long2 - long1) * (Math.PI / 180.0f);
		double dlat = (lat2 - lat1) * (Math.PI / 180.0f);
		double a = Math.pow(Math.sin(dlat / 2.0), 2)
				+ Math.cos(lat1 * (Math.PI / 180.0f))
				* Math.cos(lat2 * (Math.PI / 180.0f))
				* Math.pow(Math.sin(dlong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;
		Log.d("distance", String.format("%f", d));
		return d;
	}
}

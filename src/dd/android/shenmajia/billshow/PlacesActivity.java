package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
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

import dd.android.shenmajia.common.ShenmajiaApi;

public class PlacesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_places);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_places, menu);
		return true;
	}

	String distance_km_format = "%.2f 公里";
	String distance_m_format = "%.0f 米";
//	String distance_m_format = "%.0f 米";

	public void search_place(View v) {
		EditText et = (EditText) findViewById(R.id.et_place_name);
		String q = et.getText().toString();
		String result = ShenmajiaApi.get_search_place(q);
		final List<Place> list = JSON.parseArray(result, Place.class);

		ListView listView = (ListView) findViewById(R.id.lv_places);

		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		for (Place place : list) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			Double distance = calculateHaversineMI(place.lat, place.lon,
					Settings.lat, Settings.lng);
			// distance = Math.sqrt( (place.lat - Settings.lat) * (place.lat -
			// Settings.lat) + (place.lng - Settings.lng) * (place.lng -
			// Settings.lng));
			item.put("id", place.id);
			item.put("title", place.name);
			item.put("address", place.address);
			if(distance > 1.0)
				item.put("distance", String.format(distance_km_format, distance));
			else{
				item.put("distance", String.format(distance_m_format, distance * 1000));
			}
			data.add(item);

		}
		// 用SimpleAdapter 绑定ListView控件
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.place_item, new String[] { "title", "address",
						"distance" }, new int[] { R.id.tv_title,
						R.id.tv_address, R.id.tv_distance });
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(PlacesActivity.this,
						"This is " + list.get(arg2).name, Toast.LENGTH_SHORT)
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
		Log.d("distance",String.format("%f",d));
		return d;
	}
}

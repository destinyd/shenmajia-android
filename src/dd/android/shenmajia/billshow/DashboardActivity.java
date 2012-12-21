package dd.android.shenmajia.billshow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import dd.android.shenmajia.common.ShenmajiaApi;
import dd.android.shenmajia.common.WiFiInfoManager;
import dd.android.shenmajia.common.WiFiInfoManager.WifiInfo;

public class DashboardActivity extends Activity {

	static DashboardActivity _factory = null;

	ProgressDialog dialog = null;

	// Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();

	// Thread
	//
	public static DashboardActivity factory() {
		if (_factory == null)
			_factory = new DashboardActivity();
		// else
		// _factory.init();
		return _factory;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		_factory = this;
		init_location();
		init();
		dialog = ShenmajiaApi.loading(DashboardActivity.this);
	}

	public void init() {
		service.submit(new Runnable() {

			@Override
			public void run() {

				final JSONObject json = ShenmajiaApi.get_dashboard();

				// String result = ShenmajiaApi.get_near_places(page);
				mainHandler.post(new Runnable() {
					@Override
					public void run() {// 这将在主线程运行
						init_text_view(json);
						dialog.dismiss();
					}
				});
			}
		});
	}

	public void init_text_view(JSONObject json) {
		TextView tv_username = (TextView) findViewById(R.id.tv_username);
		TextView tv_bills_count = (TextView) findViewById(R.id.tv_bills_count);
		TextView tv_costs_sum = (TextView) findViewById(R.id.tv_costs_sum);

		tv_username.setText(Settings.getFactory().username);
		tv_bills_count.setText(json.getString("bills_count"));
		tv_costs_sum.setText(json.getString("costs_sum"));
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_dashboard, menu);
	// return true;
	// }

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

	public void to_bill_form(View v) {
		Intent intent = new Intent();
		intent.setClass(this, PlacesActivity.class);
		startActivity(intent);
	}

	public void to_cost_form(View v) {
		Intent intent = new Intent();
		intent.setClass(this, CostFormActivity.class);
		startActivity(intent);
	}

	public void to_bills(View v) {
		Intent intent = new Intent();
		intent.setClass(this, BillsActivity.class);
		startActivity(intent);
	}

	public void to_costs(View v) {
		Intent intent = new Intent();
		intent.setClass(this, CostsActivity.class);
		startActivity(intent);
	}

	@SuppressWarnings("static-access")
	private void init_location() {
		LocationManager locationManager;
		String serviceName = this.LOCATION_SERVICE;
		locationManager = (LocationManager) this.getSystemService(serviceName);
		// 查询条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		// if (location != null) {
		updateWithNewLocation(location);
		// 设置监听器，自动更新的最小时间为间隔30分钟，最小位移变化超过100米
		locationManager.requestLocationUpdates(provider, 300000, 100,
				locationListener);
		// } else {
		// getLocation();
		// }
	}
//
	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	private void updateWithNewLocation(Location location) {
		String latLongString;
		TextView myLocationText;
		myLocationText = (TextView) this.findViewById(R.id.tv_plus);
		if (location != null) {
			Settings.setLoc(location);
			latLongString = "维度:" + Settings.getFactory().lat + "\n经度:" + Settings.getFactory().lng;
		} else {
			latLongString = "无法获取地理信息";
			// getLocation();
		}
		myLocationText.setText(latLongString);
	}
}

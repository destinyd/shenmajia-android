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

		tv_username.setText(Settings.username);
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
			Settings.lat = location.getLatitude();
			Settings.lng = location.getLongitude();
			latLongString = "维度:" + Settings.lat + "\n经度:" + Settings.lng;
		} else {
			latLongString = "无法获取地理信息";
			// getLocation();
		}
		myLocationText.setText(latLongString);
	}

	private void getLocation() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation gsmCell = (GsmCellLocation) tm.getCellLocation();
		if (gsmCell == null) {
			WiFiInfoManager wifi_manager = new WiFiInfoManager(this);
			Location location = getWIFILocation(wifi_manager.getWifiInfo());
			if (location != null)
				updateWithNewLocation(location);
			return;
		}
		int cid = gsmCell.getCid();
		int lac = gsmCell.getLac();
		String netOperator = tm.getNetworkOperator();
		int mcc = Integer.valueOf(netOperator.substring(0, 3));
		int mnc = Integer.valueOf(netOperator.substring(3, 5));
		JSONObject holder = new JSONObject();
		JSONArray array = new JSONArray();
		JSONObject data = new JSONObject();
		holder.put("version", "1.1.0");
		holder.put("host", "maps.google.com");
		holder.put("address_language", "zh_CN");
		holder.put("request_address", true);
		holder.put("radio_type", "gsm");
		holder.put("carrier", "HTC");
		data.put("cell_id", cid);
		data.put("location_area_code", lac);
		data.put("mobile_countyr_code", mcc);
		data.put("mobile_network_code", mnc);
		array.put(data);
		holder.put("cell_towers", array);

		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://www.google.com/loc/json");
		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(holder.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		httpPost.setEntity(stringEntity);
		HttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity httpEntity = httpResponse.getEntity();
		InputStream is = null;
		try {
			is = httpEntity.getContent();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader reader = new BufferedReader(isr);
		StringBuffer stringBuffer = new StringBuffer();
		try {
			String result = "";
			while ((result = reader.readLine()) != null) {
				stringBuffer.append(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		TextView txtInfo = (TextView) this.findViewById(R.id.tv_plus);
		txtInfo.setText(stringBuffer.toString());
	}

	// 上面是取到WIFI的mac地址的方法，下面是把地址发送给google服务器,代码如下：
	public static Location getWIFILocation(WifiInfo wifi) {
		if (wifi == null) {
			Log.i("TAG", "wifi is null.");
			return null;
		}
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		JSONObject holder = new JSONObject();
		try {
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");

			JSONObject data;
			JSONArray array = new JSONArray();
			if (wifi.mac != null && wifi.mac.trim().length() > 0) {
				data = new JSONObject();
				data.put("mac_address", wifi.mac);
				data.put("signal_strength", 8);
				data.put("age", 0);
				array.put(data);
			}
			holder.put("wifi_towers", array);
			Log.i("TAG", "request json:" + holder.toString());
			StringEntity se;
			try {
				se = new StringEntity(holder.toString());
			} catch (UnsupportedEncodingException e) {

				e.printStackTrace();
				return null;
			}
			post.setEntity(se);
			HttpResponse resp;
			try {
				resp = client.execute(post);
			} catch (ClientProtocolException e) {

				e.printStackTrace();
				return null;
			} catch (IOException e) {

				e.printStackTrace();
				return null;
			}
			int state = resp.getStatusLine().getStatusCode();
			if (state == HttpStatus.SC_OK) {
				HttpEntity entity = resp.getEntity();
				if (entity != null) {
					BufferedReader br;
					try {
						br = new BufferedReader(new InputStreamReader(
								entity.getContent()));
					} catch (IllegalStateException e) {
						e.printStackTrace();
						return null;
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
					StringBuffer sb = new StringBuffer();
					String resute = "";
					try {
						while ((resute = br.readLine()) != null) {
							sb.append(resute);
						}
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					Log.i("TAG", "response json:" + sb.toString());
					data = JSONObject.parseObject(sb.toString());
					data = (JSONObject) data.get("location");

					Location loc = new Location(
							android.location.LocationManager.NETWORK_PROVIDER);
					loc.setLatitude((Double) data.get("latitude"));
					loc.setLongitude((Double) data.get("longitude"));
					loc.setAccuracy(Float.parseFloat(data.get("accuracy")
							.toString()));
					loc.setTime(System.currentTimeMillis());
					return loc;
				} else {
					return null;
				}
			} else {
				Log.v("TAG", state + "");
				return null;
			}

		} catch (Exception e) {
			Log.d("wifi_exception", e.getMessage());
			return null;
		}
	}

	// public String getAddressbyGeoPoint(GeoPoint gp)
	// {
	// String strReturn = "";
	// try
	// {
	// /* 创建GeoPoint不等于null */
	// if (gp != null)
	// {
	// /* 创建Geocoder对象，用于获得指定地点的地址 */
	// Geocoder gc = new Geocoder(Map_index.this, Locale.getDefault());
	//
	// /* 取出地理坐标经纬度*/
	// double geoLatitude = (int)gp.getLatitudeE6()/1E6;
	// double geoLongitude = (int)gp.getLongitudeE6()/1E6;
	//
	// /* 自经纬度取得地址（可能有多行）*/
	// List<Address> lstAddress = gc.getFromLocation(geoLatitude, geoLongitude,
	// 1);
	// StringBuilder sb = new StringBuilder();
	//
	// /* 判断地址是否为多行 */
	// if (lstAddress.size() > 0)
	// {
	// Address adsLocation = lstAddress.get(0);
	//
	// for (int i = 0; i < adsLocation.getMaxAddressLineIndex(); i++)
	// {
	// sb.append(adsLocation.getAddressLine(i)).append("\n");
	// }
	// sb.append(adsLocation.getLocality()).append("\n");
	// sb.append(adsLocation.getPostalCode()).append("\n");
	// sb.append(adsLocation.getCountryName());
	// }
	//
	// /* 将取得到的地址组合后放到stringbuilder对象中输出用 */
	// strReturn = sb.toString();
	// }
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	// return strReturn;
	// }

	// public class CellIDInfo {
	//
	// public int cellId;
	// public String mobileCountryCode;
	// public String mobileNetworkCode;
	// public int locationAreaCode;
	// public String radioType;
	//
	// public CellIDInfo() {
	// }
	// }
	//
	// private CdmaCellLocation location = null;
	//
	// private void init_location() {
	// TextView tv = (TextView)findViewById(R.id.tv_plus);
	// // TODO Auto-generated method stub
	// TelephonyManager tm = (TelephonyManager)
	// getSystemService(Context.TELEPHONY_SERVICE);
	// int type = tm.getNetworkType();
	// //在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA，电信的3G为EVDO
	// //String OperatorName = tm.getNetworkOperatorName();
	// Location loc = null;
	// ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
	// //中国电信为CTC
	// //NETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType
	// //NETWORK_TYPE_CDMA电信2G是CDMA
	// if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type ==
	// TelephonyManager.NETWORK_TYPE_CDMA || type
	// ==TelephonyManager.NETWORK_TYPE_1xRTT)
	// {
	// Log.d("NETWORK_TYPE","电信2G是CDMA");
	// location = (CdmaCellLocation) tm.getCellLocation();
	// int cellIDs = location.getBaseStationId();
	// int networkID = location.getNetworkId();
	// StringBuilder nsb = new StringBuilder();
	// nsb.append(location.getSystemId());
	// CellIDInfo info = new CellIDInfo();
	// info.cellId = cellIDs;
	// info.locationAreaCode = networkID; //ok
	// info.mobileNetworkCode = nsb.toString();
	// info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
	// info.radioType = "cdma";
	// CellID.add(info);
	// }
	// //移动2G卡 + CMCC + 2
	// //type = NETWORK_TYPE_EDGE
	// else if(type == TelephonyManager.NETWORK_TYPE_EDGE)
	// {
	// Log.d("NETWORK_TYPE","移动2G卡 + CMCC + 2 ");
	// GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();
	// int cellIDs = location.getCid();
	// int lac = location.getLac();
	// CellIDInfo info = new CellIDInfo();
	// info.cellId = cellIDs;
	// info.locationAreaCode = lac;
	// info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);
	// info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
	// info.radioType = "gsm";
	// CellID.add(info);
	// }
	// //联通的2G经过测试 China Unicom 1 NETWORK_TYPE_GPRS
	// else if(type == TelephonyManager.NETWORK_TYPE_GPRS)
	// {
	// Log.d("NETWORK_TYPE","联通的2G经过测试 China Unicom   1 NETWORK_TYPE_GPRS");
	// GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();
	// int cellIDs = location.getCid();
	// int lac = location.getLac();
	// CellIDInfo info = new CellIDInfo();
	// info.cellId = cellIDs;
	// info.locationAreaCode = lac;
	// //经过测试，获取联通数据以下两行必须去掉，否则会出现错误，错误类型为JSON Parsing Error
	// //info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);
	// //info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
	// info.radioType = "gsm";
	// CellID.add(info);
	// }
	// else
	// {
	// tv.setText("Current Not Support This Type.");
	// }
	//
	// loc = callGear(CellID);
	//
	// if(loc != null)
	// {
	// try {
	//
	// StringBuilder sb = new StringBuilder();
	// String pos = getLocation(loc);
	// sb.append("CellID:");
	// sb.append(CellID.get(0).cellId);
	// sb.append("+\n");
	//
	// sb.append("home_mobile_country_code:");
	// sb.append(CellID.get(0).mobileCountryCode);
	// sb.append("++\n");
	//
	// sb.append("mobileNetworkCode:");
	// sb.append(CellID.get(0).mobileNetworkCode);
	// sb.append("++\n");
	//
	// sb.append("locationAreaCode:");
	// sb.append(CellID.get(0).locationAreaCode);
	// sb.append("++\n");
	// sb.append(pos);
	//
	// tv.setText(sb.toString());
	//
	//
	// } catch (Exception e) {
	// //
	// e.printStackTrace();
	// }
	// }
	//
	// }
	//
	// private Location callGear(ArrayList<CellIDInfo> cellID) {
	// if (cellID == null)
	// return null;
	// DefaultHttpClient client = new DefaultHttpClient();
	// HttpPost post = new HttpPost("http://www.google.com/loc/json");
	// JSONObject holder = new JSONObject();
	// try {
	// holder.put("version", "1.0");
	// holder.put("host", "maps.google.com");
	// holder.put("home_mobile_country_code",
	// cellID.get(0).mobileCountryCode);
	// holder.put("home_mobile_network_code",
	// cellID.get(0).mobileNetworkCode);
	// holder.put("radio_type", cellID.get(0).radioType);
	// holder.put("request_address", true);
	// if ("460".equals(cellID.get(0).mobileCountryCode))
	// holder.put("address_language", "zh_CN");
	// else
	// holder.put("address_language", "en_US");
	// JSONObject data, current_data;
	// JSONArray array = new JSONArray();
	// current_data = new JSONObject();
	// current_data.put("cell_id", cellID.get(0).cellId);
	// current_data.put("location_area_code",
	// cellID.get(0).locationAreaCode);
	// current_data.put("mobile_country_code",
	// cellID.get(0).mobileCountryCode);
	// current_data.put("mobile_network_code",
	// cellID.get(0).mobileNetworkCode);
	// current_data.put("age", 0);
	// array.put(current_data);
	// if (cellID.size() > 2) {
	// for (int i = 1; i < cellID.size(); i++) {
	// data = new JSONObject();
	// data.put("cell_id", cellID.get(i).cellId);
	// data.put("location_area_code",
	// cellID.get(i).locationAreaCode);
	// data.put("mobile_country_code",
	// cellID.get(i).mobileCountryCode);
	// data.put("mobile_network_code",
	// cellID.get(i).mobileNetworkCode);
	// data.put("age", 0);
	// array.put(data);
	// }
	// }
	// holder.put("cell_towers", array);
	// StringEntity se = new StringEntity(holder.toString());
	// Log.e("Location send", holder.toString());
	// post.setEntity(se);
	// HttpResponse resp = client.execute(post);
	// HttpEntity entity = resp.getEntity();
	//
	// BufferedReader br = new BufferedReader(new InputStreamReader(
	// entity.getContent()));
	// StringBuffer sb = new StringBuffer();
	// String result = br.readLine();
	// while (result != null) {
	// Log.e("Locaiton receive", result);
	// sb.append(result);
	// result = br.readLine();
	// }
	// if (sb.length() <= 1)
	// return null;
	// data = JSONObject.parseObject(sb.toString());
	// data = (JSONObject) data.get("location");
	//
	// Location loc = new Location(LocationManager.NETWORK_PROVIDER);
	// loc.setLatitude((Double) data.get("latitude"));
	// loc.setLongitude((Double) data.get("longitude"));
	// loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
	// loc.setTime(GetUTCTime());
	// return loc;
	// } catch (Exception e) {
	// return null;
	// }
	// }
	//
	// /**
	// * 获取地理位置
	// *
	// * @throws Exception
	// */
	// private String getLocation(Location itude) throws Exception {
	// String resultString = "";
	//
	// /** 这里采用get方法，直接将参数加到URL上 */
	// String urlString = String.format(
	// "http://maps.google.cn/maps/geo?key=abcdefg&q=%s,%s",
	// itude.getLatitude(), itude.getLongitude());
	// Log.i("URL", urlString);
	//
	// /** 新建HttpClient */
	// HttpClient client = new DefaultHttpClient();
	// /** 采用GET方法 */
	// HttpGet get = new HttpGet(urlString);
	// try {
	// /** 发起GET请求并获得返回数据 */
	// HttpResponse response = client.execute(get);
	// HttpEntity entity = response.getEntity();
	// BufferedReader buffReader = new BufferedReader(
	// new InputStreamReader(entity.getContent()));
	// StringBuffer strBuff = new StringBuffer();
	// String result = null;
	// while ((result = buffReader.readLine()) != null) {
	// strBuff.append(result);
	// }
	// resultString = strBuff.toString();
	//
	// /** 解析JSON数据，获得物理地址 */
	// if (resultString != null && resultString.length() > 0) {
	// JSONObject jsonobject = JSONObject.parseObject(resultString);
	// JSONArray jsonArray = new JSONArray(jsonobject.get("Placemark")
	// .toString());
	// resultString = "";
	// for (int i = 0; i < jsonArray.length(); i++) {
	// resultString = jsonArray.getJSONObject(i).getString(
	// "address");
	// }
	// }
	// } catch (Exception e) {
	// throw new Exception("获取物理位置出现错误:" + e.getMessage());
	// } finally {
	// get.abort();
	// client = null;
	// }
	//
	// return resultString;
	// }
	//
	// public long GetUTCTime() {
	// Calendar cal = Calendar.getInstance(Locale.CHINA);
	// int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
	// int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
	// cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
	// return cal.getTimeInMillis();
	// }

}

package dd.android.shenmajia.common;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import dd.android.shenmajia.api.BillPrice;
import dd.android.shenmajia.api.Good;
import dd.android.shenmajia.api.V1;
import dd.android.shenmajia.billshow.PropertiesUtil;
import dd.android.shenmajia.billshow.Settings;

public class ShenmajiaApi {
	public static Boolean get_access_token(Activity activity, String username,
			String password) {
		JSONObject result = V1.login(username, password);
		Log.d("result", result.toString());
		// try {
		if (result.containsKey("access_token")) {
			Settings.access_token = result.getString("access_token");
			Log.d("Settings.access_token", Settings.access_token);
			return true;
		} else {
			if (result.containsKey("error_description")) {
				Toast.makeText(activity, result.getString("error_description"),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(activity, "获取token失败", Toast.LENGTH_LONG).show();
			}
		}
		// } catch (Exception e) {
		// e.printStackTrace();
		// try {
		// Toast.makeText(activity,
		// (String) result.get("error_description"),
		// Toast.LENGTH_LONG).show();
		// return false;
		// } catch (JSONException e1) {
		// e1.printStackTrace();
		// Toast.makeText(activity, e1.getMessage(), Toast.LENGTH_LONG)
		// .show();
		// }
		// }
		return false;

	}

	public static Boolean get_me(Activity activity) {
		if (Settings.access_token == null || Settings.access_token == "")
			return false;
		JSONObject api_me_result = V1.me(Settings.access_token);
		if (api_me_result == null)
			return false;
		// 发送请求
		try {

			Settings.username = api_me_result.getString("username");
			Settings.id = api_me_result.getInteger("id");
			PropertiesUtil.writeConfiguration(activity);
			return true;
			// Toast.makeText(activity, retSrc, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (api_me_result.containsKey("error_description")) {
					Toast.makeText(activity,
							api_me_result.getString("error_description"),
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
				Toast.makeText(activity, e1.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
		}

		return false;
	}

	public static JSONObject get_dashboard() {
		return V1.dashboard(Settings.access_token);
	}

	public static String get_search_place(String q) {
		return get_search_place(q, 1);
	}

	public static String get_search_place(String q, Integer page) {
		return V1.search_place(Settings.access_token, q, Settings.lat,
				Settings.lng, page);
	}
	
	public static String get_search_good(String q) {
		return get_search_good(q, 1);
	}

	public static String get_search_good(String q, Integer page) {
		return V1.search_good(Settings.access_token, q, page);
	}	

	public static String get_near_places() {
		return get_near_places(1);
	}

	public static String get_near_places(Integer page) {
		return V1.search_place(Settings.access_token, "", Settings.lat,
				Settings.lng, page);
	}

	public static Boolean create_cost(float money, String desc) {
		JSONObject json = V1.create_cost(Settings.access_token, money, desc);
		return true;
	}
	
	public static Boolean create_bill(Integer place_id,
			Double total,Double cost, List<BillPrice> bill_prices) {
		JSONObject json = V1.create_bill(Settings.access_token, place_id, total,cost, bill_prices);
		return true;
	}

	public static Good create_good(String name,
			String unit, String taglist, String norm, String barcode,
			String origin, String desc) {
		return V1.create_good(Settings.access_token, name, unit, taglist, norm, barcode,
				origin, desc);
	}
	public static void change_activity(Activity activity, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		activity.startActivity(intent);
		activity.finish();
	}

	public static ProgressDialog loading(Activity activity) {
		return loading(activity,"");
	}
	
	public static ProgressDialog loading(Activity activity,String p_message) {
		String message = p_message;
		if(message.length() == 0)
			message = "读取中,请稍后...";
		return ProgressDialog.show(activity, // context
				"", // title
				message, // message
				true);
	}
}

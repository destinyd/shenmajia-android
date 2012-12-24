package dd.android.shenmajia.common;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import dd.android.shenmajia.api.Bill;
import dd.android.shenmajia.api.BillPrice;
import dd.android.shenmajia.api.Cost;
import dd.android.shenmajia.api.Good;
import dd.android.shenmajia.api.V1;

public class ShenmajiaApi {
	public static Boolean get_access_token(Activity activity, String username,
			String password) {
		JSONObject result = V1.login(username, password);
		if(result == null){
			Log.d("login failure","server broken");
			Toast.makeText(activity, "网络异常", Toast.LENGTH_LONG).show();
			return false;
		}
		Log.d("result", result.toString());
		// try {
		if (result.containsKey("access_token")) {
			Settings.getFactory().access_token = result.getString("access_token");
			Log.d("Settings.getFactory().access_token", Settings.getFactory().access_token);
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
		if (Settings.getFactory().access_token == null || Settings.getFactory().access_token == "")
			return false;
		JSONObject api_me_result = V1.me(Settings.getFactory().access_token);
		if (api_me_result == null)
			return false;
		// 发送请求
		try {

			Settings.getFactory().username = api_me_result.getString("username");
			Settings.getFactory().id = api_me_result.getInteger("id");
//			PropertiesUtil.writeConfiguration(activity);
			PropertiesUtil.writeConfiguration();
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
		return V1.dashboard(Settings.getFactory().access_token);
	}

	public static String get_search_place(String q) {
		return get_search_place(q, 1);
	}

	public static String get_search_place(String q, Integer page) {
		return V1.search_place(Settings.getFactory().access_token, q, Settings.getFactory().lat,
				Settings.getFactory().lng, page);
	}

//	public static String get_search_good(String q) {
//		return get_search_good(q, 1);
//	}

	public static String get_search_good(String q, Integer page) {
		return V1.search_good(Settings.getFactory().access_token, q, page);
	}

	// public static String get_near_places() {
	// return get_near_places(1);
	// }

	public static String get_near_places(Integer page) {
		return V1.search_place(Settings.getFactory().access_token, "", Settings.getFactory().lat,
				Settings.getFactory().lng, page);
	}

	public static List<Cost> get_costs(Integer page) {
		return V1.costs(Settings.getFactory().access_token, page);
	}
	
	public static List<Good> get_place_goods(Integer place_id,Integer page) {
		return V1.place_goods(Settings.getFactory().access_token, place_id, page);
	}

	public static Boolean create_cost(float money, String desc) {
		JSONObject json = V1.create_cost(Settings.getFactory().access_token, money, desc);
		return true;
	}

	public static Boolean create_bill(Integer place_id, Double total,
			Double cost, List<BillPrice> bill_prices) {
		JSONObject json = V1.create_bill(Settings.getFactory().access_token, place_id,
				total, cost, bill_prices);
		return true;
	}

	public static Good create_good(String name, String unit, String taglist,
			String norm, String barcode, String origin, String desc) {
		return V1.create_good(Settings.getFactory().access_token, name, unit, taglist, norm,
				barcode, origin, desc);
	}
	public static void change_activity(Activity activity, Class<?> cls) {
		change_activity(activity,cls,true);
	}
	public static void change_activity(Activity activity, Class<?> cls,
			boolean finish) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		activity.startActivity(intent);
		if (finish) {
			activity.finish();
		}
	}

	public static ProgressDialog loading(Activity activity) {
		return loading(activity, "");
	}

	public static ProgressDialog loading(Activity activity, String p_message) {
		String message = p_message;
		if (message.length() == 0)
			message = "读取中,请稍后...";
		return ProgressDialog.show(activity, // context
				"", // title
				message, // message
				true);
	}

	public static List<Bill> get_bills(int page) {
		return V1.bills(Settings.getFactory().access_token, page);
	}
}

package dd.android.shenmajia.common;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import dd.android.shenmajia.api.V1;
import dd.android.shenmajia.billshow.PropertiesUtil;
import dd.android.shenmajia.billshow.Settings;

public class ShenmajiaApi {
	public static Boolean get_access_token(Activity activity, String username,
			String password) {
		Log.d("username",username);
		Log.d("password",password);
		JSONObject result = V1.login(username, password);
		Log.d("result",result.toString());
		// try {
		if (result.containsKey("access_token")) {
			Settings.access_token = result.getString("access_token");
			Log.d("Settings.access_token",Settings.access_token);
			return true;
		} else {
			if (result.containsKey("error_description")) {
				Toast.makeText(activity, result.getString("error_description"),
						Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(activity, "获取token失败",
						Toast.LENGTH_LONG).show();
			}
		}
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// try {
		// Toast.makeText(activity,
		// (String) result.get("error_description"),
		// Toast.LENGTH_LONG).show();
		// return false;
		// } catch (JSONException e1) {
		// // TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				if (api_me_result.containsKey("error_description")) {
					Toast.makeText(activity,
							api_me_result.getString("error_description"),
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
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
		return get_search_place(q,1);
	}
	public static String get_search_place(String q,Integer page) {
		return V1.search_place(Settings.access_token, q, Settings.lat, Settings.lng,page);
	}	

	public static void change_activity(Activity activity, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(activity, cls);
		activity.startActivity(intent);
		activity.finish();
	}
	
	public static Boolean create_cost(Activity activity,float money,String desc) {
		JSONObject json =  V1.create_cost(Settings.access_token, money, desc);
		return true;
	}
}

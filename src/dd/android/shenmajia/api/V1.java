package dd.android.shenmajia.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class V1 {

	
	public static String client_id = "9434f1ecf27bf7acf618ba20acb77135cee47f897ba38121dc9bf140e57b993a";
	public static String client_secret = "fbc028a39f5df36798e7613e9a576fed7a4dbc6b495e3a411ad6374875a84b80";
	public static String domain_url = "http://remote.shenmajia.tk";
	public static String token_url = domain_url + "/oauth/token";
	public static String me_url = domain_url
			+ "/api/v1/me.json?access_token=%s";
	public static String dashboard_url = domain_url
			+ "/api/v1/dashboard.json?access_token=%s";
	public static String bills_url = domain_url
			+ "/api/v1/dashboard.json?access_token=%s";
	public static String search_place_url = domain_url
			+ "/api/v1/places/search.json?access_token=%s&q=%s&lat=%.3f&lon=%.3f&page=%d";
	public static String reg_url = domain_url + "/api/v1/reg";
	public static String costs_url = domain_url + "/api/v1/costs";	
	
	public static JSONObject reg(String email, String password, String password_confirm,
			String username) {
		HttpPost request = new HttpPost(reg_url);
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		// params.add(new BasicNameValuePair("client_id", client_id));
		// params.add(new BasicNameValuePair("client_secret",
		// client_secret));
//		params.add(new BasicNameValuePair("grant_type", grant_type));
		params.add(new BasicNameValuePair("user[email]", email));
		params.add(new BasicNameValuePair("user[password]", password));
		params.add(new BasicNameValuePair("user[password_confirmation]", password_confirm));
		params.add(new BasicNameValuePair("user[username]", username));

		// 绑定到请求 Entry
		try {
			request.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		// 发送请求
		HttpResponse httpResponse;
		String retSrc = "";
		JSONObject result = null;
		try {
			httpResponse = new DefaultHttpClient().execute(request);
			retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("r reg",retSrc);
			result = JSONObject.parseObject(retSrc);
//			Toast.makeText(this, retSrc, Toast.LENGTH_LONG).show();
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
	public static JSONObject me(String access_token) {
		HttpResponse httpResponse1;
		String retSrc = "";

		String url = String.format(me_url, access_token);
		HttpGet request_me = new HttpGet(url);
		JSONObject result1 = null;
		try {
			httpResponse1 = new DefaultHttpClient().execute(request_me);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			retSrc = EntityUtils.toString(httpResponse1.getEntity());
			Log.d("r me",retSrc);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			result1 = JSONObject.parseObject(retSrc);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result1;
	}
	
	public static JSONObject login(String username,String password) {
		HttpPost request = new HttpPost(token_url);
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("client_id", client_id));
		params.add(new BasicNameValuePair("client_secret",
				client_secret));
		params.add(new BasicNameValuePair("grant_type", "password"));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));

		// 绑定到请求 Entry
		try {
			request.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 发送请求
		HttpResponse httpResponse;
		String retSrc = "";
		try {
			httpResponse = new DefaultHttpClient().execute(request);
			retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("r login",retSrc);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		JSONObject result = null;
		try {
			result = JSONObject.parseObject(retSrc);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		return result;
	}
	
	public static JSONObject dashboard(String access_token) {
		HttpResponse httpResponse1;
		String retSrc = "";

		String url = String.format(dashboard_url, access_token);
		HttpGet request = new HttpGet(url);
		JSONObject result = null;
		try {
			httpResponse1 = new DefaultHttpClient().execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			retSrc = EntityUtils.toString(httpResponse1.getEntity());
			Log.d("r dashboard",retSrc);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			result = JSONObject.parseObject(retSrc);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}	
	
	public static JSONObject places(String access_token) {
		HttpResponse httpResponse1;
		String retSrc = "";

		String url = String.format(bills_url, access_token);
		HttpGet request = new HttpGet(url);
		JSONObject result = null;
		try {
			httpResponse1 = new DefaultHttpClient().execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			retSrc = EntityUtils.toString(httpResponse1.getEntity());
			Log.d("r places",retSrc);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			result = JSONObject.parseObject(retSrc);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
	public static String search_place(String access_token,String q,Double lat,Double lng,Integer page) {
		HttpResponse httpResponse1;
		String retSrc = "";
		
		if(lat == 0 || lng == 0)
			return null;

		String url = String.format(search_place_url, access_token,q,lat,lng,page);
		Log.d("url",url);
		HttpGet request = new HttpGet(url);
		try {
			httpResponse1 = new DefaultHttpClient().execute(request);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			retSrc = EntityUtils.toString(httpResponse1.getEntity());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d("search_place",retSrc);
		return retSrc;
	}	
	
	public static JSONObject create_cost(String access_token,Float money,String desc) {
		HttpPost request = new HttpPost(costs_url);
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		// params.add(new BasicNameValuePair("client_id", client_id));
		// params.add(new BasicNameValuePair("client_secret",
		// client_secret));
//		params.add(new BasicNameValuePair("grant_type", grant_type));
		params.add(new BasicNameValuePair("access_token", access_token));
		params.add(new BasicNameValuePair("cost[money]", money.toString()));
		params.add(new BasicNameValuePair("cost[desc]", URLEncoder.encode(desc)));

		// 绑定到请求 Entry
		try {
			request.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		// 发送请求
		HttpResponse httpResponse;
		String retSrc = "";
		JSONObject result = null;
		try {
			httpResponse = new DefaultHttpClient().execute(request);
			retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("r costs create",retSrc);
			result = JSONObject.parseObject(retSrc);
//			Toast.makeText(this, retSrc, Toast.LENGTH_LONG).show();
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}	
}
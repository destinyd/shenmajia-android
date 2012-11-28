package dd.android.shenmajia.common;

import java.io.Serializable;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiInfoManager implements Serializable {
	private static final long serialVersionUID = -4582739827003032383L;

	private Context context;

	public WiFiInfoManager(Context context) {
		super();
		this.context = context;
	}

	public WifiInfo getWifiInfo() {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = new WifiInfo();
		info.mac = manager.getConnectionInfo().getBSSID();
		Log.i("TAG", "WIFI MAC is:" + info.mac);
		return info;
	}

	public class WifiInfo {

		public String mac;

		public WifiInfo() {
			super();
		}
	}

}
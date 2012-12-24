package dd.android.shenmajia.billshow;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;

public class BillShowApp extends Application {

	static BillShowApp mDemoApp;

	// 百度MapAPI的管理类
	public BMapManager mBMapMan = null;
	MKLocationManager mLocationManager = null; 

	// 授权Key
	// 申请地址：http://developer.baidu.com/map/android-mobile-apply-key.htm
	static String mStrKey = null;// =
									// "1C7A140FA3322987B625537CE3BA30CE078EB42B";
	boolean m_bKeyRight = true; // 授权Key正确，验证通过

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is " + iError);
			Toast.makeText(BillShowApp.mDemoApp.getApplicationContext(),
					"您的网络出错啦！", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "
					+ iError);
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(BillShowApp.mDemoApp.getApplicationContext(),
						"请在BMapApiDemoApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG)
						.show();
				BillShowApp.mDemoApp.m_bKeyRight = false;
			}
		}
	}

	@Override
	public void onCreate() {
		if (mStrKey == null) {
			try {
				ApplicationInfo appInfo = this.getPackageManager()
						.getApplicationInfo(getPackageName(),
								PackageManager.GET_META_DATA);
				mStrKey = appInfo.metaData.getString("BAIDU_APPKEY");
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.v("BMapApiDemoApp", "onCreate");
		mDemoApp = this;
		mBMapMan = new BMapManager(this);
		boolean isSuccess = mBMapMan
				.init(this.mStrKey, new MyGeneralListener());
		// 初始化地图sdk成功，设置定位监听时间
		if (isSuccess) {
			mLocationManager = mBMapMan.getLocationManager();
			mLocationManager.enableProvider((int) MKLocationManager.MK_GPS_PROVIDER);
			mLocationManager.setNotifyInternal(10, 5);
//			mBMapMan.getLocationManager().setNotifyInternal(10, 5);
		} else {
			// 地图sdk初始化失败，不能使用sdk
		}
		super.onCreate();
	}

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

}

package dd.android.shenmajia.billshow;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import dd.android.shenmajia.common.PropertiesUtil;
import dd.android.shenmajia.common.Settings;
import dd.android.shenmajia.common.ShenmajiaApi;
import dd.android.shenmajia.common.UpdataInfo;

public class LauncherActivity extends Activity {

	protected static final int UPDATA_CLIENT = 0;
	protected static final int GET_UNDATAINFO_ERROR = -1;
	protected static final int DOWN_ERROR = -11;
	static UpdataInfo info;
	ProgressDialog pd;
	static String path_downloads = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/downloads";
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_CLIENT:
				// 对话框通知用户升级程序
				showUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				// 服务器超时
				Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1)
						.show();
				LoginMain();
				break;
			case DOWN_ERROR:
				// 下载apk失败
				Toast.makeText(getApplicationContext(), "下载新版本失败", 1).show();
				LoginMain();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		// PropertiesUtil.readConfiguration(this);
		if (Settings.factory == null) {
			PropertiesUtil.readConfiguration();
		}

//		new CheckVersionTask().run();
		UmengUpdateAgent.update(this);
		LoginMain();
	}

	/*
	 * 获取当前程序的版本号
	 */
	private double getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		return Double.parseDouble(packInfo.versionName);
	}

	// String version_name = null, remote_version_name = "";
	double remote_version_name = 0, version_name = 1.0;

	/*
	 * 从服务器获取xml解析并进行比对版本号
	 */
	public class CheckVersionTask implements Runnable {

		public void run() {
			try {
				// 从资源文件获取服务器 地址
				// String url =
				// getResources().getString(R.string.update_info_url);
				String url = "http://192.168.1.4:3000/apk_info.json";
				HttpResponse httpResponse1 = null;
				String retSrc = "";

				HttpGet request_me = new HttpGet(url);
				try {
					httpResponse1 = new DefaultHttpClient().execute(request_me);
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					retSrc = EntityUtils.toString(httpResponse1.getEntity());
					Log.d("r get update_info", retSrc);
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				info = UpdataInfo.getUpdataInfo(retSrc);
				remote_version_name = info.version;
				version_name = getVersionName();

				if (remote_version_name <= version_name) {
					Log.d("update manager", "版本号相同无需升级");
					LoginMain();
				} else {
					Log.d("update manager", "版本号不同 ,提示用户升级 ");
					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// 待处理
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}

	/*
	 * 
	 * 弹出对话框通知用户更新程序
	 * 
	 * 弹出对话框的步骤： 1.创建alertDialog的builder. 2.要给builder设置属性, 对话框的内容,样式,按钮
	 * 3.通过builder 创建一个对话框 4.对话框show()出来
	 */
	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("更新通知");
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本：");
		sb.append(version_name);
		sb.append("\n最新版本：");
		sb.append(info.version);
		sb.append("\n更新内容：\n");
		sb.append(info.desc);
		builer.setMessage(sb.toString());
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.d("VersionManager", "下载apk,更新");
				downLoadApk();
				LoginMain();
			}
		});
		// 当点取消按钮时进行登录
		builer.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				LoginMain();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	protected void downLoadApk() {
		try {
			Uri uri = Uri.parse(info.apk_url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} catch (Exception e) {
		}

	}

	protected void LoginMain() {
		if (ShenmajiaApi.get_me(this)) {
			ShenmajiaApi.change_activity(this, DashboardActivity.class);
		} else {
			ShenmajiaApi.change_activity(this, LoginActivity.class);
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

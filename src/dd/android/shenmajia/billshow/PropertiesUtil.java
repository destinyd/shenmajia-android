package dd.android.shenmajia.billshow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class PropertiesUtil {
	static final String TAG = "PropertiesUtil";
	// private static final String PREFERENCES = "settings.preferences";
	public static final String smj_path = Environment
			.getExternalStorageDirectory() + "/.smj";
	public static final String smj_settings_name = "settings.json";

	// private static Properties props = null;
	//
	// public static Properties getProperties(Context c) {
	// if (props != null)
	// return props;
	// Properties props = new Properties();
	// try {
	// // 方法一：通过activity中的context攻取setting.properties的FileInputStream
	// InputStream in = c.getAssets().open("setting.properties ");
	// // 方法二：通过class获取setting.properties的FileInputStream
	// // InputStream in =
	// //
	// PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
	// props.load(in);
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	//
	// return props;
	// }
	//
	// public static Properties putProperties(Context c) {
	// if(props != null)
	// return props;
	// Properties props = new Properties();
	// try {
	// // 方法一：通过activity中的context攻取setting.properties的FileInputStream
	// InputStream in = c.getAssets().open("setting.properties");
	// // 方法二：通过class获取setting.properties的FileInputStream
	// // InputStream in =
	// //
	// PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
	// props.load(in);
	// saveConfig(c,c.getp)
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	//
	// return props;
	// }
	//
	// public void saveConfig(Context context, String file, Properties
	// properties) {
	// try {
	// FileOutputStream s = new FileOutputStream(file, false);
	// properties.store(s, "");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public static String readInStream(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			Log.i("FileTest", e.getMessage());
		}
		return null;
	}

	/**
	 * 1、判断SD卡是否存在
	 */
	public static boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	// public static void writeConfiguration(Context context) {
	public static void writeConfiguration() {
		if (!hasSdcard())
			return;
		String str_json = JSON.toJSONString(Settings.getFactory());
		File file = new File(smj_path);
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(smj_path, smj_settings_name);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.d(TAG, "IOException");
				e.printStackTrace();
			}
		}

		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.write(str_json);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// DataOutputStream out = null;
		// try {
		// out = new DataOutputStream(context.openFileOutput(PREFERENCES,
		// // MODE_PRIVATE));
		// 0));
		// out.writeUTF(Settings.getFactory().access_token);
		// out.writeDouble(Settings.getFactory().lat);
		// out.writeDouble(Settings.getFactory().lng);
		// out.flush();
		// } catch (FileNotFoundException e) {
		// // Ignore
		// } catch (IOException e) {
		// // noinspection ResultOfMethodCallIgnored
		// context.getFileStreamPath(PREFERENCES).delete();
		// } finally {
		// if (out != null) {
		// try {
		// out.close();
		// } catch (IOException e) {
		// // Ignore
		// }
		// }
		// }
	}

	// public static void readConfiguration(Context context) {
	public static void readConfiguration() {
		File file = new File(smj_path, smj_settings_name);
		String str_json = null;
		FileInputStream fi;
		try {
			fi = new FileInputStream(file);
			str_json = readInStream(fi);

			if (str_json.length() > 0) {
				Settings.setFactory(JSON.parseObject(str_json, Settings.class));
			}
		} catch (FileNotFoundException e) {
			Log.d(TAG, "read FileNotFoundException");
			e.printStackTrace();
		}

		// char[] buffer = new char[1024];

		// while (fi.read(buffer) != -1) {
		// sb.append(buffer);
		// }

		// DataInputStream in = null;
		// try {
		// in = new DataInputStream(context.openFileInput(PREFERENCES));
		// Settings.getFactory().access_token = in.readUTF();
		// Settings.getFactory().lat = in.readDouble();
		// Settings.getFactory().lng = in.readDouble();
		// } catch (FileNotFoundException e) {
		// // Ignore
		// } catch (IOException e) {
		// // Ignore
		// } finally {
		// if (in != null) {
		// try {
		// in.close();
		// } catch (IOException e) {
		// // Ignore
		// }
		// }
		// }
	}
}

package dd.android.shenmajia.billshow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;

public class PropertiesUtil {
	private static final String PREFERENCES = "settings.preferences";

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
	// // TODO Auto-generated catch block
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
	// // TODO Auto-generated catch block
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

	public static void writeConfiguration(Context context) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(context.openFileOutput(PREFERENCES,
			// MODE_PRIVATE));
					0));
			out.writeUTF(Settings.access_token);
			out.writeDouble(Settings.lat);
			out.writeDouble(Settings.lng);
			out.flush();
		} catch (FileNotFoundException e) {
			// Ignore
		} catch (IOException e) {
			// noinspection ResultOfMethodCallIgnored
			context.getFileStreamPath(PREFERENCES).delete();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}

	public static void readConfiguration(Context context) {
		DataInputStream in = null;
		try {
			in = new DataInputStream(context.openFileInput(PREFERENCES));
			Settings.access_token = in.readUTF();
			Settings.lat	=	in.readDouble();
			Settings.lng	=	in.readDouble();
		} catch (FileNotFoundException e) {
			// Ignore
		} catch (IOException e) {
			// Ignore
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}
}

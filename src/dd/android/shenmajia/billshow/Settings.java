package dd.android.shenmajia.billshow;


public class Settings {
	public String access_token = null;
	public String email = null;
	public String password = null;
	public String username = null;
	public int id = 0;
	public double lat = 0, lng = 0;

	public static Settings factory = null;
	public static final String TAG = "Settings";

	public static Settings getFactory() {
		if (factory == null)
			factory = new Settings();
		return factory;
	}

	public static void setFactory(Settings s) {
		factory = s;
	}

}

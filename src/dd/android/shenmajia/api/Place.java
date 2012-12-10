package dd.android.shenmajia.api;

import android.util.Log;

public class Place {
	static String distance_km_format = "%.2f 公里";
	static String distance_m_format = "%.0f 米";

	public String name, address, tel, city;
	public double lat, lon;
	public Integer id, city_id;
	double distance;
	String str_distance = null;

	public String getDistance(Double to_lat, Double to_lng) {
		if (str_distance == null) {
			Double distance = calculateHaversineMI(lat, lon, to_lat,
					to_lng);
			if (distance > 1.0)
				str_distance = String.format(distance_km_format, distance);
			else {

				str_distance = String
						.format(distance_m_format, distance * 1000);
			}
		}
		return str_distance;
	}

	public static double calculateHaversineMI(double lat1, double long1,
			double lat2, double long2) {
		Log.d("pos", String.format("%f,%f %f,%f", lat1, long1, lat2, long2));
		double dlong = (long2 - long1) * (Math.PI / 180.0f);
		double dlat = (lat2 - lat1) * (Math.PI / 180.0f);
		double a = Math.pow(Math.sin(dlat / 2.0), 2)
				+ Math.cos(lat1 * (Math.PI / 180.0f))
				* Math.cos(lat2 * (Math.PI / 180.0f))
				* Math.pow(Math.sin(dlong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;
		Log.d("distance", String.format("%f", d));
		return d;
	}
}

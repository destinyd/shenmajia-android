package dd.android.shenmajia.billshow;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;

//public class PlaceSelectActivity extends MapActivity {
	public class PlaceSelectActivity extends Activity {

//	MapController mc;
//	GeoPoint gp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_select);

//		MapView mv = (MapView)findViewById(R.id.mapView);
//		mv.setClickable(true);
//		mv.setBuiltInZoomControls(true);
//		mc = mv.getController();
//		Geocoder gc = new Geocoder(this);
//		try{
//			List<Address> addresses = gc.getFromLocationName("柳州市水南路", 15);
//			for(Address address : addresses){
//				gp = new GeoPoint(
//						(int)(address.getLatitude()*1E6),
//						(int)(address.getLongitude()*1E6)
//						);
//				setTitle(address.getFeatureName());
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
////		MyOverlay myOverlay = new MyOverlay;
////		Overlay myOverlay = new Overlay();
//		mc.setZoom(20);
//		mc.animateTo(gp);
	}


//	@Override
//	protected boolean isRouteDisplayed() {
//		// TODO Auto-generated method stub
//		return false;
//	}
	public void onResume() {
	    super.onResume();
	    MobclickAgent.onResume(this);
	}
	public void onPause() {
	    super.onPause();
	    MobclickAgent.onPause(this);
	}
}

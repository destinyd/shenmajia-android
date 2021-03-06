package dd.android.shenmajia.billshow;

import android.app.Activity;
import android.view.MenuItem;

import com.umeng.fb.UMFeedbackService;

import dd.android.shenmajia.common.PropertiesUtil;
import dd.android.shenmajia.common.Settings;
import dd.android.shenmajia.common.ShenmajiaApi;

public class MenusController {
	public static boolean mainOptionsItemSelected(Activity context,
			MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_feedback:
			UMFeedbackService.openUmengFeedbackSDK(context);
			break;
		case R.id.write_bill_menu:
			ShenmajiaApi.change_activity(context, PlacesActivity.class);
			break;
		case R.id.write_cost_menu:
			ShenmajiaApi.change_activity(context, CostFormActivity.class);
			break;
		case R.id.logout_menu:
			Settings.getFactory().access_token = "";
			// PropertiesUtil.writeConfiguration(context);
			PropertiesUtil.writeConfiguration();
			ShenmajiaApi.change_activity(context, LoginActivity.class);
			break;
		case R.id.exit:
			System.exit(0);
			break;
		default:
			return false;
		}
		return true;
	}

	public static boolean signOptionsItemSelected(Activity context,
			MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_feedback:
			UMFeedbackService.openUmengFeedbackSDK(context);
			break;		
		case R.id.menu_login:
			ShenmajiaApi.change_activity(context, LoginActivity.class);
			break;
		case R.id.menu_reg:
			ShenmajiaApi.change_activity(context, RegActivity.class);
			break;
		case R.id.exit:
			System.exit(0);
			break;
		default:
			return false;
		}
		return true;
	}
}

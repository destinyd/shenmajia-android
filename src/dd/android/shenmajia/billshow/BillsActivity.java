package dd.android.shenmajia.billshow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import dd.android.shenmajia.api.Bill;
import dd.android.shenmajia.billshow.adapter.BillAdapter;
import dd.android.shenmajia.common.ShenmajiaApi;

public class BillsActivity extends Activity {
	PullToRefreshListView lv_bills;
	List<Bill> bills = new ArrayList<Bill>();
	List<Bill> n_bills;
	Integer page = 1;
	Dialog dialog;
	BillAdapter bills_adapter = null;

	// Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();

	// Thread

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bills);
		lv_bills = (PullToRefreshListView) findViewById(R.id.lv_bills);
		bind_bills_view_action();
		load_bills();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 如果不关闭当前的会出现好多个页面
		return MenusController.mainOptionsItemSelected(this, item);
	}

	private void bind_bills_view_action() {
		lv_bills.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				lv_bills.setLastUpdatedLabel(DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));
				load_bills();
			}

		});

		// Add an end-of-list listener
		lv_bills.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (dialog == null) {
					page += 1;
					load_bills(page);
				}
			}
		});

	}

	private void load_bills() {
		page = 1;
		bills.clear();
		load_bills(1);
	}

	private void load_bills(final int page) {
		if (dialog == null) {
			dialog = ShenmajiaApi.loading(BillsActivity.this);
			service.submit(new Runnable() {

				@Override
				public void run() {
					get_bills(page);
					// final List<HashMap<String, Object>> data =
					// search_result_to_data(result);
					mainHandler.post(new Runnable() {
						@Override
						public void run() {// 这将在主线程运行
							// bind_bills_view(data);
							// bills_adapter.notifyDataSetChanged();
							Log.d("load_bills", "load_bills");
							bills.addAll(n_bills);
							bind_bills();
							dialog.dismiss();
							dialog = null;
						}

					});
				}

			});
		}
	}

	public void get_bills(final int page) {
		n_bills = ShenmajiaApi.get_bills(page);
	}

	public void bind_bills() {
		Log.d("bind_bills", "bind_bills");
		if (bills_adapter == null) {
			ListView actualListView = lv_bills.getRefreshableView();
			bills_adapter = new BillAdapter(BillsActivity.this, bills);
			actualListView.setAdapter(bills_adapter);
		} else {
			bills_adapter.notifyDataSetChanged();
		}
		lv_bills.onRefreshComplete();
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

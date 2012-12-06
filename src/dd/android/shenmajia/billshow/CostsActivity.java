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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import dd.android.shenmajia.api.Cost;
import dd.android.shenmajia.billshow.adapter.CostAdapter;
import dd.android.shenmajia.common.ShenmajiaApi;

public class CostsActivity extends Activity {
	PullToRefreshListView lv_costs;
	List<Cost> costs = new ArrayList<Cost>();
	List<Cost> n_costs;
	Integer page = 1;
	Dialog dialog;
	CostAdapter costs_adapter = null;

	// Thread
	private ExecutorService service = Executors.newSingleThreadExecutor();
	private Handler mainHandler = new Handler();

	// Thread

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_costs);
		lv_costs = (PullToRefreshListView) findViewById(R.id.lv_costs);
		bind_costs_view_action();
		load_costs();
	}

	private void bind_costs_view_action() {
		lv_costs.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				lv_costs.setLastUpdatedLabel(DateUtils.formatDateTime(
						getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));
				load_costs();
			}

		});

		// Add an end-of-list listener
		lv_costs.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (dialog == null) {
					page += 1;
					load_costs(page);
				}
			}
		});

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

	private void load_costs() {
		page = 1;
		costs.clear();
		load_costs(1);
	}

	private void load_costs(final int page) {
		if (dialog == null) {
			dialog = ShenmajiaApi.loading(CostsActivity.this);
			service.submit(new Runnable() {

				@Override
				public void run() {
					get_costs(page);
					// final List<HashMap<String, Object>> data =
					// search_result_to_data(result);
					mainHandler.post(new Runnable() {
						@Override
						public void run() {// 这将在主线程运行
							// bind_costs_view(data);
							// costs_adapter.notifyDataSetChanged();
							Log.d("load_costs", "load_costs");
							costs.addAll(n_costs);
							bind_costs();
							dialog.dismiss();
							dialog = null;
						}

					});
				}

			});
		}
	}

	public void get_costs(final int page) {
		n_costs = ShenmajiaApi.get_costs(page);
	}

	public void bind_costs() {
		Log.d("bind_costs", "bind_costs");
		if (costs_adapter == null) {
			ListView actualListView = lv_costs.getRefreshableView();
			costs_adapter = new CostAdapter(CostsActivity.this, costs);
			actualListView.setAdapter(costs_adapter);
		} else {
			costs_adapter.notifyDataSetChanged();
		}
		lv_costs.onRefreshComplete();
	}

}

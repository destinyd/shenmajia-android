package dd.android.shenmajia.billshow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dd.android.shenmajia.api.Cost;
import dd.android.shenmajia.billshow.R;

public class CostAdapter extends BaseAdapter {
	private List<Cost> costs;
	Context context;
	
	public CostAdapter(Context context,List<Cost> costs){
		this.costs = costs;
		this.context = context;
	}

	@Override
	public int getCount() {
		return (costs==null)?0:costs.size();
	}

	@Override
	public Object getItem(int position) {
		return costs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public class ViewHolder{
		TextView tv_money;
		TextView tv_desc;
		TextView tv_time;
	}

	//滑动
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Cost cost = (Cost)getItem(position);
		ViewHolder viewHolder = null;
		if(convertView==null){
//			Log.d("MyBaseAdapter", "新建convertView,position="+position);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_cost, null);
			
			viewHolder = new ViewHolder();
			viewHolder.tv_money = (TextView)convertView.findViewById(
					R.id.tv_money);
			viewHolder.tv_desc = (TextView)convertView.findViewById(
					R.id.tv_desc);
			viewHolder.tv_time = (TextView)convertView.findViewById(
					R.id.tv_time);			
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.tv_money.setText(String.valueOf(cost.money));
		viewHolder.tv_desc.setText(cost.desc);
		viewHolder.tv_time.setText(cost.created_at.toLocaleString());
				
		return convertView;
	}

}

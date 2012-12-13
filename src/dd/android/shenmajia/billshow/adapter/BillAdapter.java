package dd.android.shenmajia.billshow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dd.android.shenmajia.api.Bill;
import dd.android.shenmajia.billshow.R;

public class BillAdapter extends BaseAdapter {
	private List<Bill> bills;
	Context context;
	
	public BillAdapter(Context context,List<Bill> bills){
		this.bills = bills;
		this.context = context;
	}

	@Override
	public int getCount() {
		return (bills==null)?0:bills.size();
	}

	@Override
	public Object getItem(int position) {
		return bills.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public class ViewHolder{
		TextView tv_total;
//		TextView tv_desc;
		TextView tv_time;
	}

	//滑动
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Bill bill = (Bill)getItem(position);
		ViewHolder viewHolder = null;
		if(convertView==null){
//			Log.d("MyBaseAdapter", "新建convertView,position="+position);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_bill, null);
			
			viewHolder = new ViewHolder();
			viewHolder.tv_total = (TextView)convertView.findViewById(
					R.id.tv_total);
//			viewHolder.tv_desc = (TextView)convertView.findViewById(
//					R.id.tv_desc);
			viewHolder.tv_time = (TextView)convertView.findViewById(
					R.id.tv_time);			
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.tv_total.setText(String.valueOf(bill.total));
//		viewHolder.tv_desc.setText(bill.desc);
		viewHolder.tv_time.setText(bill.created_at.toLocaleString());
				
		return convertView;
	}

}

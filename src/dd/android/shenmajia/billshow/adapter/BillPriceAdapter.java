package dd.android.shenmajia.billshow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import dd.android.shenmajia.api.BillPrice;
import dd.android.shenmajia.billshow.R;

public class BillPriceAdapter extends BaseAdapter {
	private List<BillPrice> bill_prices;
	Context context;

	public BillPriceAdapter(Context context, List<BillPrice> bill_prices) {
		this.bill_prices = bill_prices;
		this.context = context;
	}

	@Override
	public int getCount() {
		return (bill_prices == null) ? 0 : bill_prices.size();
	}

	@Override
	public Object getItem(int position) {
		return bill_prices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		TextView tv_name,tv_unit,tv_norm;
		EditText et_price,et_amount;
	}

	// 滑动
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BillPrice bill_price = (BillPrice) getItem(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			// Log.d("MyBaseAdapter", "新建convertView,position="+position);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_bill_price, null);

			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tv_unit = (TextView) convertView
					.findViewById(R.id.tv_unit);
			viewHolder.tv_norm = (TextView) convertView
					.findViewById(R.id.tv_norm);
			viewHolder.et_amount = (EditText) convertView
					.findViewById(R.id.et_amount);
			viewHolder.et_price = (EditText) convertView
					.findViewById(R.id.et_price);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			// Log.d("MyBaseAdapter", "旧的convertView,position="+position);
		}

		viewHolder.tv_name.setText(bill_price.name);
		viewHolder.tv_unit.setText(bill_price.unit);
		viewHolder.tv_norm.setText(bill_price.getNorm());
		viewHolder.et_amount.setText(String.valueOf(bill_price.amount));
		viewHolder.et_price.setText(String.valueOf(bill_price.price));
		
		viewHolder.et_amount.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					EditText et = (EditText)v;
					try{
						bill_price.amount = Double.parseDouble(et.getText().toString());
					}
					catch(Exception e){
						bill_price.amount = 1.0;
					}
				}
			}
		});
		
		viewHolder.et_price.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					EditText et = (EditText)v;
					try{
						bill_price.price = Double.parseDouble(et.getText().toString());
					}
					catch(Exception e){
						bill_price.price = 0.0;
					}
				}
			}
		});		

		return convertView;
	}
}
package dd.android.shenmajia.billshow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import dd.android.shenmajia.api.Place;
import dd.android.shenmajia.billshow.R;
import dd.android.shenmajia.billshow.Settings;

public class PlaceAdapter extends BaseAdapter {
	private List<Place> places;
	Context context;
	
	public PlaceAdapter(Context context,List<Place> places){
		this.places = places;
		this.context = context;
	}

	@Override
	public int getCount() {
		return (places==null)?0:places.size();
	}

	@Override
	public Object getItem(int position) {
		return places.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public class ViewHolder{
		TextView tv_name;
		TextView tv_distance;
		TextView tv_address;
//		ImageView imageView;
	}

	//滑动
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Place place = (Place)getItem(position);
		ViewHolder viewHolder = null;
		if(convertView==null){
//			Log.d("MyBaseAdapter", "新建convertView,position="+position);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_place, null);
			
			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView)convertView.findViewById(
					R.id.tv_name);
			viewHolder.tv_distance = (TextView)convertView.findViewById(
					R.id.tv_distance);
			viewHolder.tv_address = (TextView)convertView.findViewById(
					R.id.tv_address);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.tv_name.setText(place.name);
		viewHolder.tv_distance.setText(place.getDistance(Settings.getFactory().lat, Settings.getFactory().lng));
		viewHolder.tv_address.setText(place.address);
		
		//对ListView中的每一行信息配置OnClick事件
//		convertView.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(context, 
//						"[convertView.setOnClickListener]点击了"+place.name, 
//						Toast.LENGTH_SHORT).show();
//			}
//			
//		});
		
		return convertView;
	}

}

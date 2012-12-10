package dd.android.shenmajia.billshow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dd.android.shenmajia.api.Good;
import dd.android.shenmajia.billshow.R;

public class GoodAdapter extends BaseAdapter {
	private List<Good> goods;
	Context context;
	
	public GoodAdapter(Context context,List<Good> goods){
		this.goods = goods;
		this.context = context;
	}

	@Override
	public int getCount() {
		return (goods==null)?0:goods.size();
	}

	@Override
	public Object getItem(int position) {
		return goods.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public class ViewHolder{
		TextView tv_name;
		TextView tv_unit;
		TextView tv_norm;
		ImageView iv_good;
	}

	//滑动
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Good good = (Good)getItem(position);
		ViewHolder viewHolder = null;
		if(convertView==null){
//			Log.d("MyBaseAdapter", "新建convertView,position="+position);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_good, null);
			
			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView)convertView.findViewById(
					R.id.tv_name);
			viewHolder.tv_unit = (TextView)convertView.findViewById(
					R.id.tv_unit);
			viewHolder.tv_norm = (TextView)convertView.findViewById(
					R.id.tv_norm);
			viewHolder.iv_good = (ImageView)convertView.findViewById(
					R.id.iv_good);			
			
			//动态增加1个ImageView
//			viewHolder.imageView = new ImageView(context);
//			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
//					LinearLayout.LayoutParams.WRAP_CONTENT,
//					LinearLayout.LayoutParams.WRAP_CONTENT);
//			mParams.gravity = Gravity.CENTER;
//			mParams.width=50;
//			viewHolder.imageView.setLayoutParams(mParams);
			//这个ImageView放到ListView的第2列之后
//			((LinearLayout)convertView).addView(viewHolder.imageView,2);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
//			Log.d("MyBaseAdapter", "旧的convertView,position="+position);
		}
		
//		viewHolder.tv_name.setText(String.valueOf(good.id));
		viewHolder.tv_name.setText(good.name);
		viewHolder.tv_unit.setText(good.unit);
		viewHolder.tv_norm.setText(good.getNorm());
//		asynLoadBitmap(viewHolder.iv_good,position);//异步去加载图片

		
//		//对ListView中第1个TextView配置OnClick事件
//		viewHolder.tv_name.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(context, 
//						"[textViewItem01.setOnClickListener]点击了"+good.name, 
//						Toast.LENGTH_SHORT).show();
//			}
//		});
		
		//对ListView中的每一行信息配置OnClick事件
//		convertView.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(context, 
//						"[convertView.setOnClickListener]点击了"+good.name, 
//						Toast.LENGTH_SHORT).show();
//			}
//			
//		});
		
		//对ListView中的每一行信息配置OnLongClick事件
//		convertView.setOnLongClickListener(new OnLongClickListener(){
//			@Override
//			public boolean onLongClick(View v) {
//				Toast.makeText(context, 
//						"[convertView.setOnLongClickListener]点击了"+good.name, 
//						Toast.LENGTH_SHORT).show();
//				return true;
//			}
//		});
		
		return convertView;
	}
	
//	private HashMap<Integer,Bitmap> cache = null;
//
//	public void asynLoadBitmap(ImageView image,Integer position){
//		Bitmap bit = cache.get(position);
//		if(bit ==null){
//			//使用Task或者是线程去加载图片
//			new LoadTask(image,new Handler(),position).execute();
//		}else{
//			image.setImageBitmap(bit);
//		}
//	}
//	ImageView view;
//	Handler handler;
//	int position;
//	
//	public void LoadTask extends Runable(){
//
//		public LoadTask(ImageView view,Handler handler,int position){
//			this.view = view;
//			this.handler = handler;
//			this.position = position;
//		}
//		public void run(){
//			Bitmap bit = loadBitmap();
//			setImageBitmap(view,bit,handler,position);
//		}
//	}
//
//	private void setImageBitmap(final ImageView view,final Bitmap bit,Handler handler,int position){
//		if(view.getTag() == position){
//			handler.post(new Raunable(){
//				public void run(){
//					view.setImageBitmap(bit);
//				}
//			});
//		}
//	}
}

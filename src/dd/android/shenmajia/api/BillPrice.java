package dd.android.shenmajia.api;

public class BillPrice {
	static String format_norm = "(%s)";
	public String name,unit,image;
	private String _norm = null;
	public String getNorm(){
			if(_norm == null || _norm.trim().length() == 0){
				return "";
			}
			else
				return String.format(format_norm, _norm); 
	}
	public void setNorm(String p_norm){
		_norm = p_norm;
	}	
	
	public double price = 0.0,amount = 1.0;
	public Integer good_id,id;
	public double total(){
			return price * amount;
	}
	public static BillPrice from_good(Good good){
		BillPrice bp = new BillPrice();
		bp.name = good.name;
		bp.unit = good.unit;
		bp._norm = good._norm;
		bp.good_id = good.id;
		return bp;
	}
}

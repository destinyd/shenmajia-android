package dd.android.shenmajia.api;

public class BillPrice {
	public String name,unit,norm,image;
	public double price = 0.0,amount = 1.0;
	public Integer good_id,id;
	public double total(){
			return price * amount;
	}
	public static BillPrice from_good(Good good){
		BillPrice bp = new BillPrice();
		bp.name = good.name;
		bp.unit = good.unit;
		bp.norm = good.norm;
		bp.image = good.image;
		bp.good_id = good.id;
		return bp;
	}
}

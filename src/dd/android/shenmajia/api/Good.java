package dd.android.shenmajia.api;

public class Good {

	public String name,desc,unit,barcode,origin;
	public Integer id;
	public String _norm = null;
	public SMJImage image,large,pin,medium,list;
	
	static String format_norm = "(%s)";	
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
}

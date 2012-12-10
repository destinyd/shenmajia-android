package dd.android.shenmajia.common;

import com.alibaba.fastjson.JSON;

public class UpdataInfo {
	public String desc,apk_url;
	public double version; 
	/*
	 * 用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
	 */
	public static UpdataInfo getUpdataInfo(String str_json) throws Exception {
		if(str_json == null || str_json.length() == 0){
			throw new Exception("str_json updata info failure");
		}
		UpdataInfo info = JSON.parseObject(str_json,UpdataInfo.class);
		return info;
	}
}

package com.yunbo.control;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kobjects.base64.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ab.util.AbDateUtil;
import com.ab.util.AbStrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunbo.mode.History;

public class HistoryUtil {
	public static History data=new History();
	public static List<History> allList = new ArrayList<History>();
	public static long seek=0L;
	public static String GetTime(long whs) {
		String time = "";
		long h = whs / 3600000;
		long m = (whs / 60000) % 60;
		long s = (whs / 1000) % 60;
		time += String.valueOf(h);
		if (String.valueOf(m).length() < 2)
			time += ":0" + String.valueOf(m);
		else
			time += ":" + String.valueOf(m);

		if (String.valueOf(s).length() < 2)
			time += ":0" + String.valueOf(s);
		else
			time += ":" + String.valueOf(s);
		return time;
	}
	public static void setName(String url) {
		// TODO Auto-generated method stub
		String name = url;
		if (url.startsWith("thunder://")) {
			url=url.replace("thunder://", "");
			try {
				name=new String(Base64.decode(url),"gbk");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		url=name = URLDecoder.decode(name);
		if (url.startsWith("ftp://") || url.startsWith("http://")) {
			try {
				URL url1=new URL(name);
				data.setName(url1.getFile());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}if (url.startsWith("ed2k://")) {
			Pattern pat = Pattern
					.compile("ed2k:\\/\\/\\|file\\|([^\\|]+?)\\|(\\d+)\\|([A-Z0-9]{32})\\|(h=[A-Z0-9]{32}\\|)?\\/?");
			Matcher mat = pat.matcher(name);
			// System.out.println(url);
			while (mat.find()) {
				 name = "" + mat.group(1);
					data.setName(name);
			}
		}

		if (url.startsWith("magnet:?xt=urn:btih:") && url.contains("&")&& url.contains("&dn=")) {
			for (String nameStr : url.split("&")) {
				if (nameStr.startsWith("&dn=")) {
					data.setName(nameStr.replace("&dn=", ""));
				}
			}
		}
		if (AbStrUtil.isEmpty(data.getName())) {
			data.setName(name);
		}
	}
	public static void save(Context ctx) {
		// TODO Auto-generated method stub
		data.setDate(AbDateUtil.getCurrentDate(AbDateUtil.dateFormatYMDHMS));
		History temp=null;
		for (History history : allList) {
			if (history.getUrl().equals(data.getUrl())) {
				temp=history;break;
			}
		}
		if (temp!=null) {
			allList.remove(temp);
		}
		allList.add(data);
		SharedPreferences SAVE = ctx.getSharedPreferences("history", ctx.MODE_PRIVATE);  
	    Editor editor = SAVE.edit(); 
	    Gson gson = new Gson();  
	    String json = gson.toJson(allList); 
	    editor.putString("list",json);  
	    editor.commit(); 
	}

	public static void read(Context ctx){

		SharedPreferences SAVE = ctx.getSharedPreferences("history", ctx.MODE_PRIVATE);  
		String json=SAVE.getString("list",null); 
		if (!AbStrUtil.isEmpty(json)) {
		    Gson gson = new Gson();  
			allList=gson.fromJson(json,  
	                new TypeToken<List<History>>() {  
	                }.getType());  
		}if (allList==null) {
			allList = new ArrayList<History>();
		}
	}
}

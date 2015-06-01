package sdis.trafficar.json;

import java.util.ArrayList;

public class MyJSON {
	
	private boolean success;
	private String message;
	
	private ArrayList<String> keys;
	private ArrayList<String> values;
	
	private ArrayList<MyJSONArray> arrays;
	
	public MyJSON(boolean success, String message) {
		this.success = success;
		this.message = message;
		this.keys = new ArrayList<String>();
		this.values = new ArrayList<String>();
		this.arrays = new ArrayList<MyJSONArray>();
	}
	
	public void put(String key, String value) {
		keys.add(key);
		values.add(value);
	}
	
	public void putArray(String key, ArrayList<String> values, boolean isObject) {
		MyJSONArray array = new MyJSONArray(key, values, isObject);
		arrays.add(array);
	}
	
	
	public String toString() {
		String result = "{ \"success\" : ";
		
		if(success) result += "\"true\"";
		else result += "\"false\"";
				
		result += ", \"message\" : \"" +  message + "\"";
		
		if(keys.size() == values.size()) {
			for(int i = 0; i < keys.size(); i++) {
				result += ", \"" + keys.get(i) + "\" : " + "\"" + values.get(i) + "\" ";
			}
		}
		
		if(arrays.size() > 0) {
			result += " , ";
			for(int i = 0; i < arrays.size(); i++) {
				if(i < (arrays.size() - 1)) 
					result += arrays.get(i).toString() + ", ";
				else
					result += arrays.get(i) + " ";
			}
		}

		
		result += "}";
		
		return result;
				
	}

}

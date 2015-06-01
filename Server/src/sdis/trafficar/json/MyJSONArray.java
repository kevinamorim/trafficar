package sdis.trafficar.json;

import java.util.ArrayList;

public class MyJSONArray {
	
	private String key;
	private ArrayList<String> values;
	private boolean isObject;
	
	public MyJSONArray(String key, ArrayList<String> values, boolean isObject) {
		this.key = key;
		this.values = values;
		this.isObject = isObject;
	}
	
	public void put(String value) {
		values.add(value);
	}
	
	public String toString() {
		String result = "\"" + key + "\" : [ ";
		
		for(int i = 0; i < values.size(); i++) {
			if(i < (values.size() - 1)) 
				if(isObject) 
					result += values.get(i) + ", ";
				else
					result += "\"" + values.get(i) + "\", ";
			else 
				if(isObject)
					result += values.get(i);
				else	
					result += "\"" + values.get(i) + "\" ";
		}
		
		result += "]";
		
		return result;
		
	}

}

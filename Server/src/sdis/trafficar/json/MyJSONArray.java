package sdis.trafficar.json;

import java.util.ArrayList;

public class MyJSONArray {
	
	private String key;
	private ArrayList<String> values;
	
	public MyJSONArray(String key, ArrayList<String> values) {
		this.key = key;
		this.values = values;
	}
	
	public void put(String value) {
		values.add(value);
	}
	
	public String toString() {
		String result = "\"" + key + "\" : [ ";
		
		for(int i = 0; i < values.size(); i++) {
			if(i < (values.size() - 1)) 
				result += "\"" + values.get(i) + "\", ";
			else 
				result += "\"" + values.get(i) + "\" ";
		}
		
		result += "]";
		
		return result;
		
	}

}

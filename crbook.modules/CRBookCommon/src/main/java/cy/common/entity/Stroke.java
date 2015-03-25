package cy.common.entity;

import org.json.JSONArray;
import org.json.JSONObject;

public class Stroke {
	public static String ID_KEY="id";
	public static String COLOR_KEY="color";
	public static String WIDTH_KEY="width";
	public static String POINTS_KEY="points";
	
	private int id;
	private String color;
	private int width;
	private int[] points;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int[] getPoints() {
		return points;
	}
	public void setPoints(int[] points) {
		this.points = points;
	}
	
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put(ID_KEY, id);
		obj.put(COLOR_KEY, color);
		obj.put(WIDTH_KEY, width);
		JSONArray jarray = new JSONArray();
		if (points!=null){
			for (int i=0; i<points.length; i++){
				jarray.put(points[i]);
			}
		}
		obj.put(POINTS_KEY, jarray);
		return obj;
	}
	
	public void fromJSONObject(JSONObject obj){
		if (obj.has(ID_KEY)){
			id = obj.optInt(ID_KEY);
		}
		if (obj.has(COLOR_KEY)){
			color = obj.optString(COLOR_KEY);
		}
		if (obj.has(WIDTH_KEY)){
			width = obj.optInt(WIDTH_KEY);
		}
		if (obj.has(POINTS_KEY)){
			JSONArray jarray = obj.optJSONArray(POINTS_KEY);
			points = new int[jarray.length()];
			for (int i=0; i<points.length; i++){
				points[i]=jarray.optInt(i);
			}
		}
	}
}

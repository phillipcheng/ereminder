package cy.common.entity;

import org.json.JSONObject;

public interface IJSON {
	public void dataToJSON();
	
	public JSONObject toJSONObject();
	public void fromTopJSONObject(JSONObject obj);
	
	public String toTopJSONString();
	public void fromTopJSONString(String json);
}

package infoViewer.model.json;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONParser {

	public void parseJSON(JSONObject jsonObject) throws JSONException;
}

package hookfb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

	protected void parseJson(String json) {
		try {

			JSONObject obj = new JSONObject(json);
			
			JSONArray arr = obj.getJSONArray("entry");
			sysout("id", "" + arr.getJSONObject(0).getString("id"));
			sysout("time","" + arr.getJSONObject(0).getLong("time"));			
			sysout("messaging.0.sender.id","" + arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("sender").getString("id"));
			sysout("messaging.0.recipient.id", "" + arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("recipient").getString("id"));
			sysout("messaging.0.timestamp","" + arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getLong("timestamp"));
			sysout("messaging.0.message.text", arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getString("text"));
			sysout("messaging.0.seq","" + arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getLong("seq"));
			sysout("messaging.0.mid", arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getString("mid"));			
			sysout("messaging.0.message.nlp.entities.sentiment.0.value", arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getJSONObject("nlp").getJSONObject("entities").getJSONArray("sentiment").getJSONObject(0).getString("value"));
			sysout("messaging.0.message.nlp.entities.sentiment.0.value", arr.getJSONObject(0).getJSONArray("messaging").getJSONObject(0).getJSONObject("message").getJSONObject("quick_reply").getJSONObject("entities").getString("payload"));

			

		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
	}

	private void sysout(String a, String b) {
		System.out.println(a + ":  " + b );
	}

}

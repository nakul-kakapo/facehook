package hookfb;

import java.util.HashMap;
import java.util.Map;

/**
 * Horrible cobbled together class to return a variety of JSON messages that implement the Facebook SendAPI
 * to send to Facebook.
 *
 */
public class RequestMessages {

	private String imgUrl = "http://yourWhitelistedUrl.co.za/images/image.png";
	private Map<Integer, String> jsonMessageMap = new HashMap<>();

	// STATIC
	public static int GREETING = 1;
	public static int PICS = 3;
	public static int LIST = 4;
	public static int BUTTONS = 2;

	private String getGreeting(String rId) {
		
		String greeting = "{ 'messaging_type': 'RESPONSE', 'recipient': {" + " 'id': '" + rId + "' },'message': {"
				+ " 'text': 'greetings1!'}}";
		return greeting;
		
	}

	private String getPayload(String rId) {
		
		String s = "{ " + "  'recipient':{ 'id':'" + rId + "' " + "  }, 'message':{ "
				+ "    'text': 'Here is a quick reply!', " + "    'quick_replies':[  { "
				+ "        'content_type':'text', 'title':'Send Your Location?', " // This is just a dumb clickable title
				+ "        'payload':'<POSTBACK_PAYLOAD>', " + "        'image_url':'" + this.imgUrl + "'  }, " // the tiny icon image in the title
				+ "      {  'content_type':'location'  } " // This identifies the type of message
				+ "    ]  } } ";
		return s;
		
	}

	private String getListJson(String rId) {
		String showList = new ListRequest().getListJson();
		showList = showList.replace("<PSID>", rId);	
		return showList;
	}
	
	private String getButtonsJson(String rId) {
		String showList = new ButtonsRequest().getButtonsJson();
		showList = showList.replace("<PSID>", rId);	
		return showList;
	}

	public RequestMessages(String rId) {
		
		jsonMessageMap = new HashMap<>();
		jsonMessageMap.put(GREETING, getGreeting(rId));
		jsonMessageMap.put(BUTTONS, getButtonsJson(rId));
		jsonMessageMap.put(PICS, getPayload(rId));
		jsonMessageMap.put(LIST, getListJson(rId));
	}

	public String getMessage(Integer type) {
		return jsonMessageMap.get(type);
	}
}
package hookfb;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HTTPFacebookMessengerJSONWebhook implements HttpHandler {
	private String challenge = "";

	/*
	 * I got the following information from Stackoverflow.
	 * 
	 * GET PERMANENT ACCESS TOKEN AS FOLLOWS
	 * 
	 * 1. Get User Short-Lived Access Token Go to the Graph API Explorer (https://developers.facebook.com/tools/explorer) Select the application you
	 * want to get the access token for (in the "Application" drop-down menu, not the "My Apps" menu). Click "Get Token" "Get User Access Token". In
	 * the pop-up, under the "Extended Permissions" tab, check "manage_pages". Click "Get Access Token". Grant access from a Facebook account that has
	 * access to manage the target page. Note that if this user loses access the final, never-expiring access token will likely stop working. The
	 * token that appears in the "Access Token" field is your short-lived access token.
	 * 
	 * 2. Generate Long-Lived Access Token Following these instructions from the Facebook docs, make a GET request to
	 * 
	 * https://graph.facebook.com/v2.10/oauth/access_token?grant_type=fb_exchange_token&client_id={app_id}&client_secret={app_secret}&fb_exchange_token
	 * ={short_lived_token}
	 * 
	 * entering in your app's ID and secret and the short-lived token generated in the previous step.
	 * 
	 * You cannot use the Graph API Explorer. For some reason it gets stuck on this request. I think it's because the response isn't JSON, but a query
	 * string. Since it's a GET request, you can just go to the URL in your browser.
	 * 
	 * The response should look like this:
	 * 
	 * {"access_token":"ABC123","token_type":"bearer","expires_in":5183791}
	 * 
	 * "ABC123" will be your long-lived access token. You can put it into the Access Token Debugger to verify. Under "Expires" it should have
	 * something like "2 months".
	 * 
	 * 3. Get User ID Using the long-lived access token, make a GET request to
	 * 
	 * https://graph.facebook.com/v2.10/me?access_token={long_lived_access_token}
	 * 
	 * The id field is your account ID. You'll need it for the next step.
	 * 
	 * 4. Get Permanent Page Access Token Make a GET request to
	 * 
	 * https://graph.facebook.com/v2.10/{account_id}/accounts?access_token={long_lived_access_token}
	 * 
	 * The JSON response should have a data field under which is an array of items the user has access to. Find the item for the page you want the
	 * permanent access token from. The access_token field should have your permanent access token. Copy it and test it in the Access Token Debugger.
	 * Under "Expires" it should say "Never".
	 */

	private static String yourFBPageID = "101354462332185";
	private static String yourTestAppId = "605616767265281";
	private static final String yourTestAppSecret = "141687bbc4a3f800057f64f3746f2de3";
	private static final String PSID = "AAAAAAAAAAAAAAAAA";
	private static final String ASID = "BBBBBBBBBBBBBBBBBBBBB";

	private static final String usrAccTok = "";

	/*
	 * Get short term Page Access Tokens here: https://developers.facebook.com/tools/explorer/ Click "Application" and choose your app. Click Get
	 * Token and choose Page Access Token. Then choose your page and it auto populates the "Access Token" field. Use the string in the field.
	 */
	private static String shortTermPageAccTok = "asdfasdfasdfasdfasdfasfdasfdas";

	private static String longLivedAccTok = "zxcvzxcvxzcvzxcvzxvzxcvzxv";
	private static String permPageAccTok = "EAAImziRIagEBADJsShpSv836ursoM1QZCEbcuCEKRX58jX5AmToWWInQl2AvhtdUaBKQjKWxF961imqioGGmwLl0X6ffBNxwFXAoTBDnFOyvHCZAdnsaDe27pULZBeVi4uPHXtotHY0307Dg1doNHa5rVzgH0FbJFOcrUR4umryoD8dJIbV";

	private static String urlTempMe = "https://graph.facebook.com/v2.6/me/messages?access_token=" + shortTermPageAccTok;
	private static String urlTempPage = "https://graph.facebook.com/v2.6/" + yourFBPageID + "/messages?access_token="
			+ shortTermPageAccTok;

	private static String urlPermMe = "https://graph.facebook.com/v2.6/me/messages?access_token=" + permPageAccTok;
	private static String urlPermPage = "https://graph.facebook.com/v2.6/" + yourFBPageID + "/messages?access_token="
			+ permPageAccTok;

	private static String url = urlTempMe;

	// CONSTANT
	private static final int PORT = 4025;
	private static final String CONTEXT = "/webhookcontext";
	private static final boolean VERIFY_WEBHOOK = false;
	private static final String HUB_CHALLENGE = "hub.challenge";

	// private static final int ASYNC_REPLY = RequestMessageFactory.GREETING;
	// private static final int ASYNC_REPLY = RequestMessageFactory.PICS;
	private static final int ASYNC_REPLY = RequestMessages.LIST;

	// private static final int ASYNC_REPLY = RequestMessageFactory.BUTTONS;

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new HTTPFacebookMessengerJSONWebhook().run();
	}

	/**
	 * 
	 */
	public void run() {

		// get page access token

		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 10);
			server.setExecutor(Executors.newFixedThreadPool(10));
			server.start();
			server.createContext(CONTEXT, this);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	* 
	*/
	public void handle(HttpExchange httpExch) throws IOException {
		String query = httpExch.getRequestURI().getQuery();

		if (query != null) {
			Map<String, String> params = queryToMap(query);
			challenge = params.get(HUB_CHALLENGE);
		}

		InputStream inputStream = httpExch.getRequestBody();
		StringBuilder sb = new StringBuilder();
		int i;
		while ((i = inputStream.read()) != -1) {
			sb.append((char) i);
		}
		inputStream.close();
		String incomingJSONContent = sb.toString();
		(new JsonParser()).parseJson(incomingJSONContent);

		replyOK(httpExch); // SYNCHRONOUS REPLY TO FB

		if (!VERIFY_WEBHOOK && !incomingJSONContent.contains("delivery") && !incomingJSONContent.contains("read")) {
			// get recipient ID from incoming message
			asyncReply(httpExch);
		}
	}
	
	/**
	 * This method sends an asynchronous message to Facebook (See the Facebook Messenger SendAPI specification).
	 * @see <a href="https://developers.facebook.com/docs/messenger-platform/reference/send-api"</a>
	 * 
	 * @param httpExch
	 */
	private void asyncReply(HttpExchange httpExch) {
		// Get this from the incoming message in due course after POC
		String recipientId = PSID;

		String message = (new RequestMessages(recipientId)).getMessage(ASYNC_REPLY);

		HttpURLConnection conn;
		URL oURL;
		InputStream in = null;
		try {
			oURL = new URL(url);

			conn = (HttpURLConnection) oURL.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-type", ("application/json"));

			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.forEach(conn::setRequestProperty);
			try (OutputStream out = conn.getOutputStream()) {
				out.write(message.getBytes());
			}

			int httpStatusCode = conn.getResponseCode();
			StringBuilder response = new StringBuilder();

			if (httpStatusCode >= 200 && httpStatusCode < 300) {
				in = conn.getInputStream();
			} else {
				in = conn.getErrorStream();
			}
			if (in == null) {
				response.append("No Response");
			} else {
				int i;
				while ((i = in.read()) != -1) {
					response.append((char) i);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
	}

	/**
	 * Sending synchronous 200 OK response as required by FB API Spec for the webhook. 
	 * This method is also used in order to send the challenge back when verifying a webhook.
	 * @see <a href="https://developers.facebook.com/docs/messenger-platform/getting-started/webhook-setup"</a>
	 * @param httpExch
	 */
	private void replyOK(HttpExchange httpExch) {
		try {
			Headers responseHeaders = httpExch.getResponseHeaders();
			responseHeaders.add("Content-Type", ("application/json"));
			httpExch.sendResponseHeaders(200, challenge.length());
			try (OutputStream os = httpExch.getResponseBody()) {
				if (!challenge.isEmpty()) {
					os.write(challenge.getBytes());
				}
			}

		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
	}

	/**
	 * Utility method to get the FB Webhook Challenge when it is sent. 
	 * @param query
	 * @return
	 */
	private Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<>();
		for (String param : query.split("&")) {
			String[] entry = param.split("=");
			if (entry.length > 1) {
				result.put(entry[0], entry[1]);
			} else {
				result.put(entry[0], "");
			}
		}
		return result;
	}

}

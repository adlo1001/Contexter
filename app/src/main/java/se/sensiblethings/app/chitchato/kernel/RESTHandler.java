package se.sensiblethings.app.chitchato.kernel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RESTHandler {
	private ArrayList<NameValuePair> params;
	private ArrayList<NameValuePair> headers;
	private String url;
	private static String LOGGER_SERVER_IP = "185.102.215.188";
	//private static String LOGGER_SERVER_IP = "localhost";
	private String url_temp = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.groupholder";
	private int responseCode;
	private String message;
	private static String response;
	String date_pattern = "yyyy-MM-dd'T'HH:mm:ss";
	private static SimpleDateFormat format;
	static String date ;/*= new java.util.Date();*/
	

	public enum RequestMethod {
		GET, POST
	}

	public String getResponse() {

		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int code) {
		responseCode = code;
	}

	public RESTHandler(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
		format = new SimpleDateFormat(date_pattern);
		
	}

	public void AddParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void AddHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));

	}
   
	public String getLOGGER_SERVER_IP() {
		return LOGGER_SERVER_IP;
	}

	public void setLOGGER_SERVER_IP(String lOGGER_SERVER_IP) {
		LOGGER_SERVER_IP = lOGGER_SERVER_IP;
	}

	public void Execute(RequestMethod method, String arg0) throws Exception {
		switch (method) {
		case GET: {
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}
			HttpGet request = new HttpGet(url + combinedParams);

			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			System.out.println(url + combinedParams);
			executeRequest(request, url);
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);

			StringEntity se = new StringEntity(arg0);
			se.setContentType("application/xml");

			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
			request.setEntity(se);
			// if (!params.isEmpty()) {

			// request.setEntity(new UrlEncodedFormEntity(params,
			// HTTP.UTF_8));

			System.out.print(EntityUtils.toString(request.getEntity()));
			// }
			executeRequest(request, url);
			break;

		}
		}

	}

	private void executeRequest(HttpUriRequest request, String url) {
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse;

		try {

			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();
			HttpEntity entity = httpResponse.getEntity();
			System.out.println(responseCode + ":" + message + ":");
			if (entity != null) {
				InputStream instream = entity.getContent();
				response = convertStreamToString(instream, "UTF-8");
				// Log.i("RESPONSE", EntityUtils.toString(entity));
				instream.close();
			}
		} catch (ClientProtocolException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			e.printStackTrace();
		}

	}

	private static String convertStreamToString(InputStream is, String enc) {

		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, enc));

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + " ");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static String restNewGroupPayLoad(String user_ID, String group_Name,
			String desc, String group_password_str, int age) {
		String xml = "";
		TreeMap<String, String> tmp = new TreeMap<String, String>();
		tmp = getPeers(user_ID);
		xml = /* "<?xml version=\"1.0\"?>"" */
		"<groupHolder>" + "\n<groupIndex>" + 0 + "</groupIndex>"
				+ "\n<groupName>" + group_Name + "</groupName>"
				+ "\n<fullName>" + tmp.get("fullName") + "</fullName>\n"
				+ "<userID>" + user_ID + "</userID>\n" + "<userPWRD>"
				+ group_password_str + "</userPWRD>" + "\n<userSex>"
				+ tmp.get("userSex") + "</userSex>\n" + "<description>" + desc
				+ "</description>\n" + "<userStatus>" + tmp.get("userStatus")
				+ "</userStatus>" + "\n<adminProperty>"
				+ tmp.get("adminProperty") + "</adminProperty>\n" + "<userAge>"
				+ age + "</userAge>"
				+ "\n<profileImageurl>unknown</profileImageurl>"
				+ "\n</groupHolder>";
		return xml;
	}

	public static String restNewPeerPayLoad(String user_ID, String group_Name,
			String full_Name, String user_sex, String phone_Number,
			String desc, String user_status, String admin_property,
			String user_Age, String image_url) {
		String xml = "";
		TreeMap<String, String> tmp = new TreeMap<String, String>();
		tmp = getPeers(user_ID);
		xml = /* "<?xml version=\"1.0\"?>"" */
		"<peerHolder>" + "\n<groupName>" + group_Name + "</groupName>"
				+ "\n<fullName>" + full_Name + "</fullName>\n" + "<userID>"
				+ user_ID + "</userID>\n" + "<userPWRD>" + "123456"
				+ "</userPWRD>" + "\n<userSex>" + user_sex + "</userSex>\n"
				+ "<phoneNumber>" + phone_Number + "</phoneNumber>\n"
				+ "<description>" + desc + "</description>\n" + "<userStatus>"
				+ user_status + "</userStatus>" + "\n<adminProperty>"
				+ admin_property + "</adminProperty>\n" + "<userAge>"
				+ user_Age + "</userAge>" + "\n<profileImageUrl>" + image_url
				+ "</profileImageUrl>" + "\n</peerHolder>";
		return xml;
	}

	public static String restNewChatPayLoad(String user_ID, String receiverID,
			String chat_message) {
		String xml = "";
		TreeMap<String, String> tmp = new TreeMap<String, String>();
		date = format.format(new Date());
		xml = /* "<?xml version=\"1.0\"?>"" */
		"<peerChatHistory>"
				+ "\n<chatMessages>"
				+ chat_message
				+ "</chatMessages>"
				+ "\n<userID>"
				+ user_ID
				+ "</userID>"
				+"\n<recieverID>" + receiverID + "</recieverID>" + 
				"\n<timeStampOne>"
				+ date + "</timeStampOne>"
				+ "\n</peerChatHistory>";
		return xml;
	}

	public static String restNewPublicChatPayLoad(String user_ID,
			String groupName, String chat_message) {
		String xml = "";
		
		TreeMap<String, String> tmp = new TreeMap<String, String>();
		date = format.format(new Date());
		xml = /* "<?xml version=\"1.0\"?>"" */
		"<publicChatHistory>"
				+ "\n<chatMessages>"
				+ chat_message
				+ "</chatMessages>"
				+ "\n<userID>"
				+ user_ID
				+ "</userID>"
				+ "\n<groupLeaderId>"
				+ user_ID
				+ "</groupLeaderId>"
				+ "\n<groupName>"
				+ groupName
				+ "</groupName>"
				/* +"\n<recieverID>" + receiverID + "</recieverID>" + */+ "\n<timeStampOne>"
				+ date + "</timeStampOne>"
				+ "\n</publicChatHistory>";
		return xml;
	}

	public static void filpflopLamps(String ip, boolean _mstatus) {
		String on_off_status = "OFF";
		if (_mstatus)
			on_off_status = "ON";
		else
			on_off_status = "OFF";
		String file_url = "http://" + ip + "/";
		RESTHandler resthandler = new RESTHandler(file_url);
		resthandler.AddParam("status", on_off_status);
		try {
			resthandler.Execute(RequestMethod.GET, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response = resthandler.getResponse();
		// System.out.println(response);
	}

	public TreeMap<String, String> getGroups() {
		TreeMap<String, String> tr = new TreeMap<String, String>();
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.groupholder/";
		RESTHandler resthandler = new RESTHandler(url);
		System.out
				.println("Group :>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		String response;
		int i = 0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			resthandler.Execute(RequestMethod.GET, "");
			response = resthandler.getResponse();
			Document doc = db
					.parse(new InputSource(new StringReader(response)));

			NodeList nodelist = doc.getElementsByTagName("groupName");

			Node node;
			while (i < nodelist.getLength()) {
				nodelist = doc.getElementsByTagName("groupName");
				node = nodelist.item(i);
				tr.put(node.getTextContent(), node.getTextContent());
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tr;

	}

	public static void addNewGroup(String user_id, String group_name,
			String group_interest, String group_password_str, int group_age_vl) {

		String xml_payload = restNewGroupPayLoad(user_id, group_name,
				group_interest, group_password_str, group_age_vl);
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.groupholder/";
		RESTHandler resthandler = new RESTHandler(url);
		try {
			resthandler.Execute(RequestMethod.POST, xml_payload);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addNewPeer(String user_ID, String group_Name,
			String full_Name, String user_sex, String phone_Number,
			String desc, String user_status, String admin_property,
			String user_Age, String image_url) {

		String xml_payload = restNewPeerPayLoad(user_ID, group_Name, full_Name,
				user_sex, phone_Number, desc, user_status, admin_property,
				user_Age, image_url);
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.peerholder/";
		RESTHandler resthandler = new RESTHandler(url);

		try {
			resthandler.Execute(RequestMethod.POST, xml_payload);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addNewMessage(String user_ID, String recieverID,
			String message) {

		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.peerchathistory/";
		RESTHandler resthandler = new RESTHandler(url);

		try {
			resthandler.Execute(RequestMethod.POST,
					restNewChatPayLoad(user_ID, recieverID, message));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void addNewPublicMessage(String user_ID, String group_nm,
			String message) {

		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.publicchathistory/";
		RESTHandler resthandler = new RESTHandler(url);

		try {
			resthandler.Execute(RequestMethod.POST,
					restNewPublicChatPayLoad(user_ID, group_nm, message));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static TreeMap<String, String> getPeers(String user_id) {
		TreeMap<String, String> tr = new TreeMap<String, String>();
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.peerholder";
		RESTHandler resthandler = new RESTHandler(url);

		String response;
		int i = 0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			resthandler.Execute(RequestMethod.GET, "");
			response = resthandler.getResponse();
			Document doc = db
					.parse(new InputSource(new StringReader(response)));
			NodeList nodelist = doc.getElementsByTagName("userID");
			Node node;

			while (i < nodelist.getLength()) {
				nodelist = doc.getElementsByTagName("userID");
				node = nodelist.item(i);
				if (node.getTextContent().equalsIgnoreCase(user_id)) {

					tr.put("UserID", node.getTextContent());

					nodelist = doc.getElementsByTagName("groupName");
					node = nodelist.item(i);
					tr.put("groupName", node.getTextContent());

					nodelist = doc.getElementsByTagName("fullName");
					node = nodelist.item(i);
					tr.put("fullName", node.getTextContent());

					nodelist = doc.getElementsByTagName("userPWRD");
					node = nodelist.item(i);
					tr.put("userPWRD", node.getTextContent());

					nodelist = doc.getElementsByTagName("userSex");
					node = nodelist.item(i);
					tr.put("userSex", node.getTextContent());

					nodelist = doc.getElementsByTagName("description");
					node = nodelist.item(i);
					tr.put("description", node.getTextContent());

					nodelist = doc.getElementsByTagName("userStatus");
					node = nodelist.item(i);
					tr.put("userStatus", node.getTextContent());

					nodelist = doc.getElementsByTagName("adminProperty");
					node = nodelist.item(i);
					tr.put("adminProperty", node.getTextContent());

					nodelist = doc.getElementsByTagName("userAge");
					node = nodelist.item(i);
					tr.put("userAge", node.getTextContent());

					nodelist = doc.getElementsByTagName("profileImageUrl");
					node = nodelist.item(i);
					tr.put("profileImageUrl", node.getTextContent());

					break;
				}

				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tr;

	}

	public static TreeMap<String, String> getPeersByGroup(String group_nm) {
		TreeMap<String, String> tr = new TreeMap<String, String>();
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.peerholder";
		RESTHandler resthandler = new RESTHandler(url);

		String response;
		int i = 0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			resthandler.Execute(RequestMethod.GET, "");
			response = resthandler.getResponse();
			Document doc = db
					.parse(new InputSource(new StringReader(response)));
			NodeList nodelist = doc.getElementsByTagName("userID");
			Node node;
             
			while (i < nodelist.getLength()) {
				nodelist = doc.getElementsByTagName("groupName");
				node = nodelist.item(i);

				if (node.getTextContent().equalsIgnoreCase(group_nm)) {
					tr.put("groupName_" + i, node.getTextContent());
					nodelist = doc.getElementsByTagName("userID");
					node = nodelist.item(i);
					tr.put("userID_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("fullName");
					node = nodelist.item(i);
					tr.put("fullName_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("userPWRD");
					node = nodelist.item(i);
					tr.put("userPWRD_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("userSex");
					node = nodelist.item(i);
					tr.put("userSex_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("description");
					node = nodelist.item(i);
					tr.put("description_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("userStatus");
					node = nodelist.item(i);
					tr.put("userStatus_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("adminProperty");
					node = nodelist.item(i);
					tr.put("adminProperty_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("userAge");
					node = nodelist.item(i);
					tr.put("userAge_" + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("profileImageUrl");
					node = nodelist.item(i);
					tr.put("profileImageUrl_" + i, node.getTextContent());
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tr;

	}
	
	
	public static String getPeerID(String user_name, String group_name) {
		TreeMap<String, String> tr = new TreeMap<String, String>();
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.peerholder";
		RESTHandler resthandler = new RESTHandler(url);
		String user_id_ = "unknown@unknown";
		String response;
		int i = 0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			resthandler.Execute(RequestMethod.GET, "");
			response = resthandler.getResponse();
			Document doc = db
					.parse(new InputSource(new StringReader(response)));
			NodeList nodelist = doc.getElementsByTagName("userID");
			Node node;

			while (i < nodelist.getLength()) {
				nodelist = doc.getElementsByTagName("groupName");
				node = nodelist.item(i);

				if (node.getTextContent().equalsIgnoreCase(group_name)) {
					nodelist = doc.getElementsByTagName("fullName");
					node = nodelist.item(i);
					if (node.getTextContent().contains(user_name)) {
                         
						nodelist = doc.getElementsByTagName("userID");
						node = nodelist.item(i);
						user_id_ = node.getTextContent();
						System.out.println("FullName:" +user_name+"UserID:"+user_id_);
						break;
					} 
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user_id_;

	}


	public static TreeMap<String, ArrayList> getPublicChat(String user_id,
			String group_nm) {
		//TreeMap<String, ArrayList> tr = new TreeMap<String, ArrayList>(
			//	new zComparator());
		TreeMap<String, ArrayList> tr = new TreeMap<String, ArrayList>();
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.publicchathistory";
		RESTHandler resthandler = new RESTHandler(url);
		ArrayList<String> message_chunk;

		String response;
		int i = 0; int j=0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			resthandler.Execute(RequestMethod.GET, "");
			response = resthandler.getResponse();
			Document doc = db
					.parse(new InputSource(new StringReader(response)));
			NodeList nodelist = doc.getElementsByTagName("chatIndex");
			Node node;
			j = nodelist.getLength()-1;

			while (i < nodelist.getLength()) {
				nodelist = doc.getElementsByTagName("groupName");
				node = nodelist.item(j);

				if (node.getTextContent().equalsIgnoreCase(group_nm) && tr.size()<15) {

					message_chunk = new ArrayList<String>();
					nodelist = doc.getElementsByTagName("chatIndex");
					node = nodelist.item(j);
					message_chunk.add(node.getTextContent());

					nodelist = doc.getElementsByTagName("userID");
					node = nodelist.item(j);
					message_chunk.add(node.getTextContent());
					// tr.put("userID_" + group_nm + i, node.getTextContent());

					nodelist = doc.getElementsByTagName("chatMessages");
					node = nodelist.item(j);
					message_chunk.add(node.getTextContent());
					System.out.println(node.getTextContent() + "----->");
					// tr.put("chatMessages_" + group_nm + i,
					// node.getTextContent());

					nodelist = doc.getElementsByTagName("timeStampOne");
					node = nodelist.item(j);
					message_chunk.add(node.getTextContent());

					// tr.put("timeStampOne_" + group_nm + i,
					// node.getTextContent());

					nodelist = doc.getElementsByTagName("groupLeaderId");
					node = nodelist.item(j);
					message_chunk.add(node.getTextContent());
					// tr.put("groupLeaderId_" + group_nm + i,
					// node.getTextContent());

					// nodelist = doc.getElementsByTagName("profileImageUrl");
					// node = nodelist.item(i);
					// tr.put("profileImageUrl_"+i, node.getTextContent());
					nodelist = doc.getElementsByTagName("groupName");
					node = nodelist.item(j);
					tr.put(message_chunk.get(0) + "~:~group_name"
							+ node.getTextContent() + "_" + j, message_chunk);

				}
				i++;j--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tr;
	}
	

	public static TreeMap<String, ArrayList> getPrivateChat(String user_id,
			String reciever_ID) {
		TreeMap<String, ArrayList> tr = new TreeMap<String, ArrayList>(
				new zComparator());
		String url = "http://"+LOGGER_SERVER_IP+":8080/ChitchatoWBS/webresources/_mentityclasses.peerchathistory";
		RESTHandler resthandler = new RESTHandler(url);
		ArrayList<String> message_chunk;

		String response;
		int i = 0;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			resthandler.Execute(RequestMethod.GET, "");
			response = resthandler.getResponse();
			Document doc = db
					.parse(new InputSource(new StringReader(response)));
			NodeList nodelist = doc.getElementsByTagName("chatIndex");
			Node node;

			while (i < nodelist.getLength()) {
				nodelist = doc.getElementsByTagName("userID");
				node = nodelist.item(i);

				if (node.getTextContent().equalsIgnoreCase(user_id)) {

					nodelist = doc.getElementsByTagName("recieverID");
					node = nodelist.item(i);
					if (node.getTextContent().equalsIgnoreCase(reciever_ID)) {
						node = nodelist.item(i);
						message_chunk = new ArrayList<String>();

						//message_chunk.add(node.getTextContent());
						// tr.put("userID_" + group_nm + i,
						// node.getTextContent());
						
						nodelist = doc.getElementsByTagName("chatIndex");
						node = nodelist.item(i);
						message_chunk.add(node.getTextContent());

						nodelist = doc.getElementsByTagName("recieverID");
						node = nodelist.item(i);
						message_chunk.add(node.getTextContent());

						nodelist = doc.getElementsByTagName("chatMessages");
						node = nodelist.item(i);
						message_chunk.add(node.getTextContent());

						nodelist = doc.getElementsByTagName("timeStampOne");
						node = nodelist.item(i);
						message_chunk.add(node.getTextContent());

						//nodelist = doc.getElementsByTagName("groupLeaderId");
						//node = nodelist.item(i);
						//message_chunk.add(node.getTextContent());

						nodelist = doc.getElementsByTagName("recieverID");
						node = nodelist.item(i);
						tr.put(message_chunk.get(0) + "~:~reciever_ID"
								+ node.getTextContent() + "_" + i,
								message_chunk);
					}

				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tr;
	}

}

class zComparator implements Comparator<String> {

	@Override
	public int compare(String e1, String e2) {
		String[] temp_1 = e1.split("~:~");
		String[] temp_2 = e2.split("~:~");
		int index_1 = Integer.parseInt(temp_1[0]);
		int index_2 = Integer.parseInt(temp_2[0]);
		if (index_1 > index_2)
			return 1;
		else
			return -1;

	}
}

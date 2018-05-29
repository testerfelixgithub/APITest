package Gome.AutoTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gome.erm.login.LoginFromMobile;

import md5Test.TestMD5;

public class HttpClientHelper {
	private static String charset = "utf-8";
	private static String protocol = "http";
	//��ʼ���������
	public APICase apiCase;                //APICase����

	private String urlParam = "";				//URL,��ʽ��http://api.bs.pre.gomeplus.com
	private String params = "";					//���������һ����get��������Ҫ��url�д��ݵĲ���
	private String body = "";					//body������һ������post�����д��ݵĲ���

	private JSONObject header = new JSONObject();				//����header
	private JSONObject cookie = new JSONObject();				//����cookie
	private JSONObject userInfo = new JSONObject();			//�û���Ϣ�������ֻ��š����룬������ǰ��¼
	//��ȡ�����б�
	private List<String> paramsOutput = new ArrayList<String>();		//��Ҫ�ӱ��ӿڷ��ؽ������ȡ�Ĳ���
	private List<String> paramsInput = new ArrayList<String>();		//��Ҫ�������ӿڷ��ؽ������ȡ�Ĳ����ڱ��ӿ�����Ĳ���
	//�����ġ����ر��Ķ���
	private StringBuffer requestStr = new StringBuffer();		//������
	private StringBuffer responseStr = new StringBuffer();		//���ر���


	/**
	 * ���캯��
	 * @param apiCase
	 * @throws Exception 
	 */
	public HttpClientHelper(APICase apiCase) throws Exception{
		this.apiCase = apiCase;
		this.urlParam = joinUrl();//ƴ��������url·��

		this.header = stringToJsonObject(this.apiCase.getHeaderStr());
		this.cookie = stringToJsonObject(this.apiCase.getCookieStr());
		this.userInfo = stringToJsonObject(this.apiCase.getUserInfo());
		this.body = this.apiCase.getBodyStr();
		this.params = this.apiCase.getParams();

		//��ȡ�����б�
		this.paramsOutput = stringToList(this.apiCase.getParamOutput());
		this.paramsInput = stringToList(this.apiCase.getParamInput());
		//��ȡ���滻��Ҫ����Ĳ���
		this.getParamsInput();

		//�����ġ����ر��Ķ���
		this.requestStr = new StringBuffer();
		this.responseStr = new StringBuffer();
	}

	/**
	 * ��Config.PUBLIC_PARAMS�л�ȡ�������滻param��body�е��ַ���
	 */
	private void getParamsInput() {
		//���ݲ�����
		if (this.paramsInput != null && this.paramsInput.size() > 0) {
			for (String paramIn : this.paramsInput) {
				String newStr = "\\$\\{" + paramIn + "\\}";
				if (Config.PUBLIC_PARAMS.containsKey(paramIn)) {
					String value = String.valueOf(Config.PUBLIC_PARAMS.get(paramIn));
					if (this.params != null) {
						this.params = this.params.replaceAll(newStr, value);
					}
					if (this.body != null) {
						this.body = this.body.replaceAll(newStr, value);
					}
				} else {
					//�����б���û�У��ǲ��Ǹ��׳��쳣������
				}
			}
		}
	}

	/**
	 * ��ʼ��url��Ϣ����Э�顢�������ӿ�url��ַƴ������
	 * @return String-��ʽ����url��Ϣ���磺http://api.bs.pre.gomeplus.com
	 * @throws Exception 
	 */
	private String joinUrl() throws Exception {
		StringBuffer sb = new StringBuffer();

		String serverAddr = this.apiCase.getServerAddr();
		if(serverAddr == null || serverAddr.equals("")){
			LogHelper.exception(new Exception("������IPΪ�գ�����!"));
			throw new Exception("������IPΪ�գ�����!");
		}
		if(!serverAddr.contains(this.protocol)){//���������ip�ַ����в�����Э�飬����Ҫ�ֶ�����
			sb.append("http://");
		}
		//�ж������Ƿ���"/"���ӣ����������Ҫȥ��
		if(serverAddr.endsWith("/")){
			sb.append(serverAddr.substring(0,serverAddr.length()-1));
		}else{
			sb.append(serverAddr.substring(0,serverAddr.length()));
		}

		String urlStr = this.apiCase.getURL();
		if(urlStr == null || urlStr.equals("")){
			LogHelper.exception(new Exception("�ӿ�·��Ϊ�գ�����!"));
			throw new Exception("�ӿ�·��Ϊ�գ�����!");
		}
		//�ж�url·�����Ƿ���"/"���ӣ����û������Ҫ����
		if(urlStr.startsWith("/")){
			sb.append(urlStr);
		}else{
			sb.append("/"+urlStr);
		}
		return sb.toString();
	}

	/**
	 * ִ�е�������������ִ�к�Ľ��д��APICase������󷵻�
	 * @return APICase-ִ�к������
	 */
	public APICase runSingleCase(){
		switch (this.apiCase.getRequestMethod().toUpperCase()) {
		case "GET":
			this.httpClientGet();
			break;
		case "POST":
			this.httpClientPost();
			break;
		case "PUT":
			this.httpClientPut();
			break;
		case "DELETE":
			this.httpClientDelete();
			break;
		default:
			break;
		}

		return this.apiCase;
	}

	/**
	 * ִ��Get����
	 */
	private void httpClientGet() {
		// �����������
		this.addParams();

		HttpGet httpGet = new HttpGet(urlParam);

		httpGet = (HttpGet) this.addHeader(httpGet);
		if (this.userInfo != null && this.userInfo.size() > 0) {
			String userName = null,pwd = null,deviceNo = null,deviceDesc = null,ip = null;
			if(this.userInfo.containsKey("userName")){
				userName = this.userInfo.get("userName").toString();
			}
			if(this.userInfo.containsKey("password")){
				pwd = this.userInfo.get("password").toString();
			}
			if(this.userInfo.containsKey("deviceNo")){
				deviceNo = this.userInfo.get("deviceNo").toString();
			}
			if(this.userInfo.containsKey("deviceDesc")){
				deviceDesc = this.userInfo.get("deviceDesc").toString();
			}
			if(this.userInfo.containsKey("ip")){
				ip = this.userInfo.get("ip").toString();
			}
			
			String token = this.login(userName, pwd, deviceNo, deviceDesc, ip);
			if(token != null && !token.equals("")){
				JSONObject jo = new JSONObject();
				jo.put("staffScn", token);
				httpGet = (HttpGet) this.addCookie(httpGet,jo);
			}
		}
		if(this.cookie != null && this.cookie.size()>0){
			httpGet = (HttpGet) this.addCookie(httpGet,this.cookie);	
		}
		

		// �����������-body����--get���󲻻���body
		apiCase.setRequestStr(requestStr.toString());
		this.execRequest(httpGet);

	}



	/**
	 * ִ��post����
	 */
	private void httpClientPost() {
		this.addParams();
		HttpPost httpPost = new HttpPost(urlParam);

		httpPost = (HttpPost) this.addHeader(httpPost);
		if (this.userInfo != null && this.userInfo.size() > 0) {
			String userName = null,pwd = null,deviceNo = null,deviceDesc = null,ip = null;
			if(this.userInfo.containsKey("userName")){
				userName = this.userInfo.get("userName").toString();
			}
			if(this.userInfo.containsKey("password")){
				pwd = this.userInfo.get("password").toString();
			}
			if(this.userInfo.containsKey("deviceNo")){
				deviceNo = this.userInfo.get("deviceNo").toString();
			}
			if(this.userInfo.containsKey("deviceDesc")){
				deviceDesc = this.userInfo.get("deviceDesc").toString();
			}
			if(this.userInfo.containsKey("ip")){
				ip = this.userInfo.get("ip").toString();
			}
			
			//====================
//			JSONObject jo = new JSONObject();
//			jo.put("staffScn", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1MjY4OTcxMzksInN1YiI6Int1c2VyTmFtZT1DWEoxNTAzMjd9IiwiaXNzIjoiZXJtTW9iaWxlIiwiYXVkIjoiZW1wbG95ZWVBcHAiLCJleHAiOjE1Mjc1MDE5MzksIm5iZiI6MTUyNjg5NzEzOX0.Pwrm4ta5SyQiszam4ePXkrUIlqlylgBapI74vtvN-00");
//			httpPost = (HttpPost) this.addCookie(httpPost,jo);	
			//=======================
			String token = this.login(userName, pwd, deviceNo, deviceDesc, ip);
			if(token != null && !token.equals("")){
				JSONObject jo = new JSONObject();
				jo.put("staffScn", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1MjY4OTcxMzksInN1YiI6Int1c2VyTmFtZT1DWEoxNTAzMjd9IiwiaXNzIjoiZXJtTW9iaWxlIiwiYXVkIjoiZW1wbG95ZWVBcHAiLCJleHAiOjE1Mjc1MDE5MzksIm5iZiI6MTUyNjg5NzEzOX0.Pwrm4ta5SyQiszam4ePXkrUIlqlylgBapI74vtvN-00");
				httpPost = (HttpPost) this.addCookie(httpPost,jo);	
			}
		}
		if(this.cookie !=null && this.cookie.size()>0){
			httpPost = (HttpPost) this.addCookie(httpPost,this.cookie);	
		}
		
		// �����������-body����
		httpPost = this.addBody(httpPost);

		this.apiCase.setRequestStr(this.requestStr.toString());
		this.execRequest(httpPost);


	}
	/**
	 * ִ��put����
	 */
	private void httpClientPut() {
		LogHelper.error("Put�����ݲ�֧�֣�");
	}
	/**
	 * ִ��delete����
	 */
	private void httpClientDelete() {
		LogHelper.error("Delete�����ݲ�֧�֣�");

	}


	/**
	 * ����parameter����parameter��url��ַ����ƴ��
	 * param֧��2�ָ�ʽ
	 * ��{key:value}
	 * ��key=value&key2=value2
	 */
	private void addParams(){

		// �����������
		if(this.apiCase.getParams()!=null && this.apiCase.getParams().startsWith("{") && this.apiCase.getParams().endsWith("}")){
			JSONObject jo = stringToJsonObject(this.params);
			StringBuffer sbParams = new StringBuffer();
			if (jo != null && jo.size() > 0) {
				for (Entry<String, Object> entry : jo.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					try {
						sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
					sbParams.append("&");
				}
			}
			if (sbParams != null && sbParams.length() > 0) {
				this.urlParam = this.urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
			}
		}else if(this.params.contains("&")){
			this.urlParam = this.urlParam + "?" + this.params;
		}


		requestStr.append(apiCase.getRequestMethod().toUpperCase() +" " + urlParam + System.getProperty("line.separator"));


	}
	/**
	 * ���header��Ϣ
	 * @param request-HttpUriRequest
	 * @return HttpUriRequest �����header������
	 */
	private HttpUriRequest addHeader(HttpUriRequest request){
		if(this.header == null || this.header.size() == 0){
			return request;
		}
		this.requestStr.append("Request Headers:"+this.header.toString()+System.getProperty("line.separator"));
		for (Entry<String, Object> entry : header.entrySet()) {
			request.setHeader(entry.getKey(),String.valueOf(entry.getValue()));
			try {
				request.addHeader(entry.getKey(),URLEncoder.encode(String.valueOf(entry.getValue()),charset));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return request;
	}

	/**
	 * ���header��Ϣ
	 * @param request-HttpUriRequest
	 * @return HttpUriRequest �����header������
	 */
	private HttpUriRequest addCookie(HttpUriRequest request,JSONObject cookie){
		if (cookie != null && cookie.size() > 0) {
			requestStr.append("Cookies:"+cookie.toString()+System.getProperty("line.separator"));
			StringBuffer sbCookie = new StringBuffer();
			for (Entry<String, Object> entry : cookie.entrySet()) {
				sbCookie.append(entry.getKey());
				sbCookie.append("=");
				try {
					sbCookie.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				sbCookie.append(";");
			}
			request.addHeader("Cookie",sbCookie.substring(0, sbCookie.length() - 1));
		}
		return request;
	}

	private HttpPost addBody(HttpPost httpPost){
		if(body != null){
			if(this.body.trim().startsWith("body=") || this.body.trim().startsWith("{") ||
					this.body.trim().endsWith("}")){

			}else{
				LogHelper.error("Body������ʽ�������飡");
			}
			requestStr.append("Body:"+ this.body+System.getProperty("line.separator"));
			//json��ʽ
			StringEntity entity = new StringEntity(this.apiCase.getBodyStr(), Charset.forName("UTF-8"));
			httpPost.setEntity(entity);
			//text��ʽ
			//			List<NameValuePair> list = new ArrayList<NameValuePair>();
			//			Iterator<Entry<String, Object>> iterator = body.entrySet().iterator();
			//			while (iterator.hasNext()) {
			//				Entry<String, Object> elem = iterator.next();
			//				list.add(new BasicNameValuePair(elem.getKey(), String.valueOf(elem.getValue())));
			//			}
			//			try{
			//				if (list.size() > 0) {
			//					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
			//					httpPost.setEntity(entity);
			//				}
			//				System.out.println(httpPost.getEntity());
			//			}catch (Exception e){
			//				throw new RuntimeException(e);
			//			}
		}
		return httpPost;
	}

	private String login(String userName,String password,String deviceNo,String devicesDesc,String ip){
		String token = null;
		if(userName == null || userName.equals("")){
			LogHelper.error("��¼��Ϣȱ�١��û���������ȷ�ϣ�");
			return token;
		}
		if(password == null || password.equals("")){
			LogHelper.error("��¼��Ϣȱ�١����롿����ȷ�ϣ�");
			return token;
		}
		if(deviceNo == null || deviceNo.equals("")){
			LogHelper.error("��¼��Ϣȱ�١��豸id������ȷ�ϣ�");
			return token;
		}
		if(devicesDesc == null || devicesDesc.equals("")){
			LogHelper.error("��¼��Ϣȱ�١��豸����������ȷ�ϣ�");
			return token;
		}
		if(ip == null || ip.equals("")){
			LogHelper.error("��¼��Ϣȱ�١�ip������ȷ�ϣ�");
			return token;
		}
		JSONObject jo = LoginFromMobile.getUserInfo("LJL150430", "Aa123456" ,"0001_null_null_null","iphone6s","127.0.0.1");

		if(jo.get("data") != null){
			JSONObject data = (JSONObject) jo.get("data");
			token = data.get("token").toString();
		}
		return token;
	}


	/**
	 * �������󣬽����д��apiCase��������
	 * @param request HttpUriRequest���󣬰���httpget��httpPost
	 * @return �������APICase
	 */
	public APICase execRequest(HttpUriRequest request){
		BufferedReader br = null;
		StringBuffer resultBuffer = null;
		int responseCode;
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			HttpResponse response = client.execute(request);
			// ��ȡ��������Ӧ����
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"utf-8"));
			responseCode = response.getStatusLine().getStatusCode();
			this.apiCase.setResponseCode_actual(responseCode);
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			this.responseStr.append(resultBuffer);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		apiCase.setResponseStr(this.responseStr.toString());
		//������֤
		String tmpResult = "Passed";
		if(apiCase.getResponseCode_expect() != apiCase.getResponseCode_actual()){
			//			apiCase.setResult("Failed");
			tmpResult = "Failed";
		}
		if(apiCase.getVerifications() != null && apiCase.getVerifications() != ""){
			String[] vers = apiCase.getVerifications().split(",");//Ӣ�Ķ���	
			if(vers.length<=1){
				vers = apiCase.getVerifications().split("��");//���Ķ���
			}
			for(String s: vers){
				if(apiCase.getResponseStr()!= null && !apiCase.getResponseStr().contains(s)){
					tmpResult = "Failed";
					break;
				}
			}
		}

		apiCase.setResult(tmpResult);
		//��ȡ����
		Map map = extractParams(this.paramsOutput, this.apiCase.getResponseStr());

		return apiCase;
	}

	/**
	 * �ڷ��������л�ȡ������ֵ
	 * @param paramOutput String-Ҫ��ȡ�ı����б�
	 * @param responsStr  String-���ص��ַ�����responseStr
	 * @return map-��ȡ�ı����ֵ�
	 */
	private static Map<String, Object> extractParams(List<String> keys,String responseStr){
		if(keys == null || keys.size()<1){
			return null;
		}
		Map<String, Object> params = new HashMap<>();
		//		List<String> keys = stringToList(paramOutput);
		JSONObject tmpMap = stringToJsonObject(responseStr);
		for(String key:keys){
			//			System.out.println(key);
			Object o = getValueFromMap(key, tmpMap);
			if(o != null){
				params.put(key, o);
				Config.PUBLIC_PARAMS.put(key, o);
			}
		}
		return params;
	}
	/**
	 * �ݹ������Ҫ��key������ȡvalue
	 * @param key
	 * @param jsonObject
	 * @return object 
	 */
	private static Object getValueFromMap(String key,JSONObject jsonObject){
		Object o = null;
		if(jsonObject.containsKey(key)){
			o = jsonObject.get(key);
		}else{
			for(Object tmekey:jsonObject.keySet()){
				//				System.out.println(jsonObject.get(tmekey).getClass().toString());
				if(jsonObject.get(tmekey) instanceof JSONObject){
					o = getValueFromMap(key, (JSONObject) jsonObject.get(tmekey));
				}
			}
		}
		return o;
	}

	/**
	 * ͨ������Ͱ��ṩ��fastjson����ʵ��Ϊmap��ʽ�ģ��ַ���ת��Ϊmap��ʽ
	 * @param String
	 * @return JSONObject
	 */
	private static JSONObject stringToJsonObject(String jsonStr){
		//gson���ַ���תΪmapʱ�Ὣint����ת��Ϊduble���ͣ�����С���㣬ִ��ʱ�ͻᱨ������������
		//		Gson gson = new Gson();
		//		Map<String, Object> params = new HashMap<String, Object>();
		//		params = gson.fromJson(ac.getParams(), params.getClass());
		if(jsonStr == null || jsonStr == ""){
			return null;
		}
		if(!jsonStr.startsWith("{") || !jsonStr.endsWith("}")){

			LogHelper.error("����Ĳ�����ʽ���ԣ���ȷ�ϣ���ȷ��ʽΪ{key:value}");
			LogHelper.info(jsonStr);
			return null;
		}
		JSONObject ob = JSON.parseObject(jsonStr);

		return ob;
	}

	/**
	 * ���ַ���ת��Ϊlist
	 * @param listStr
	 * @return
	 */
	private static List<String> stringToList(String listStr){
		if(listStr == null){
			return null;
		}
		List<String> pList = new ArrayList<>();
		String reg="\\W";//����ĸ������
		//�������װ�ɶ��� 
		Pattern p=Pattern.compile(reg); 
		//����������Ҫ���õ��ַ������������ȡƥ�������󡣰Ѷ����ʽ����װ����ƥ�������С� 
		String[] tmp = p.split(listStr);
		for(String t:tmp){
			if(t != "" && t.length()>0){
				pList.add(t);
			}
		}
		return pList;
	}
}

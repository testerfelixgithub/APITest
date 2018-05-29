package Gome.AutoTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
import com.alibaba.fastjson.JSONReader;

import md5Test.TestMD5;

/**
 * @Description:����http���������
 */
public class HttpClientHelperold {


	private static String charset = "utf-8";
	private static String protocol = "http";
	//��ʼ���������
	APICase apiCase;
	String urlParam;// = apiCase.getServerAddr() + apiCase.getURL();
	String params;// = stringToMap(apiCase.getParams());
	JSONObject header;// = stringToMap(apiCase.getHeaderStr());
	JSONObject cookie;// = stringToMap(apiCase.getCookieStr());
	JSONObject body;// = stringToMap(apiCase.getBodyStr());
	JSONObject userInfo;// = stringToMap(apiCase.getUserInfo());
	//��ȡ�����б�
	List<String> paramsOutput;// = apiCase.getParamOutput();
	List<String> paramsInput;// =  apiCase.getParamInput();
	//�����ġ����ر��Ķ���
	StringBuffer requestStr;// 
	StringBuffer responseStr;//
	

	/**
	 * ���캯��
	 * @param apiCase
	 */
	public HttpClientHelperold(APICase apiCase){
		this.apiCase = apiCase;
		this.init();
	}

	/**
	 * ִ�е�������
	 * @param apiCase
	 * @return ����APICase
	 */
	public APICase RunSingleCase(){

		//���ݲ�����
		if(this.paramsInput.size()>0){
			for(String paramIn:this.paramsInput){
				String newStr = "\\$\\{" + paramIn + "\\}";
				if(Config.PUBLIC_PARAMS.containsKey(paramIn)){
					String value = String.valueOf(Config.PUBLIC_PARAMS.get(paramIn)) ;
					if(this.params != null){
						this.params = this.params.replaceAll(newStr, value);
					}
					if(this.body != null && this.body.size()>0){
						this.body = stringToMap(this.body.toJSONString().replaceAll(newStr, value));
					}
				}else{
					//�����б���û�У��ǲ��Ǹ��׳��쳣������
				}
			}
		}
		switch (this.apiCase.getRequestMethod().toUpperCase()) {
		case "GET":
			//			result = HttpClientHelper.httpClientGet(ac.getURL(), params, "utf-8");
			this.apiCase = this.httpClientGet();
			break;
		case "POST":
			this.apiCase = this.httpClientPost();
			break;
		case "PUT":
			LogHelper.error("put������δʵ�֡�");
			break;
		case "DELETE":
			LogHelper.error("delete������δʵ�֡�");
			break;
		default:
			break;
		}


		return apiCase;

	}



	private void init(){

		this.urlParam = initUrl();//ƴ��������url·��

		this.header = stringToMap(this.apiCase.getHeaderStr());
		this.cookie = stringToMap(this.apiCase.getCookieStr());
		this.initBody();
		this.userInfo = stringToMap(this.apiCase.getUserInfo());
		this.params = this.apiCase.getParams();

		//��ȡ�����б�
		this.paramsOutput = stringToList(this.apiCase.getParamOutput());
		this.paramsInput =  stringToList(this.apiCase.getParamInput());

		//�����ġ����ر��Ķ���
		this.requestStr = new StringBuffer();
		this.responseStr = new StringBuffer();
	}
	
	/**
	 * ��ʼ��body���ݡ�body���ݿ������������������
	 * ��json��ʽ������ֱ�ӽ�����jsonobject
	 * �ڸ�ʽΪbody={}�����ݣ���Ҫ��������
	 */
	private void initBody(){
		String tmp = this.apiCase.getBodyStr();
		if(tmp.trim().startsWith("body=")){
			tmp = tmp.replace("body=", "");
			JSONObject jo = new JSONObject();
			jo.put("body", stringToMap(tmp));
			this.body = jo;
		}else{
			this.body = stringToMap(this.apiCase.getBodyStr());	
		}
		
	}
	/**
	 * ��ʼ��url�������ṩ��������url��ַ��װ
	 * @return String �磺http://api.bs.gomeplus.com/v3/shop/shop
	 */
	private String initUrl(){
		StringBuffer sb = new StringBuffer();
		if(!this.apiCase.getServerAddr().contains(protocol)){//���������ip�ַ����в�����Э�飬����Ҫ�ֶ�����
			sb.append("http://");
		}
		//�ж������Ƿ���"/"���ӣ����������Ҫȥ��
		String tmp = this.apiCase.getServerAddr();
		if(tmp.endsWith("/")){
			sb.append(tmp.substring(0,tmp.length()-1));
		}else{
			sb.append(tmp.substring(0,tmp.length()));
		}

		//�ж�url·�����Ƿ���"/"���ӣ����û������Ҫ����
		if(this.apiCase.getURL().startsWith("/")){
			sb.append(this.apiCase.getURL());
		}else{
			sb.append("/"+this.apiCase.getURL());
		}
		return sb.toString();
	}

	/**
	 * ͨ������Ͱ��ṩ��fastjson����ʵ��Ϊmap��ʽ�ģ��ַ���ת��Ϊmap��ʽ
	 * @param mapStr
	 * @return map<String,Object>
	 */
	private static JSONObject stringToMap(String mapStr){
		//gson���ַ���תΪmapʱ�Ὣint����ת��Ϊduble���ͣ�����С���㣬ִ��ʱ�ͻᱨ������������
		//		Gson gson = new Gson();
		//		Map<String, Object> params = new HashMap<String, Object>();
		//		params = gson.fromJson(ac.getParams(), params.getClass());
		if(mapStr == null || !mapStr.startsWith("{") || !mapStr.endsWith("}")){
			LogHelper.error("����Ĳ�����ʽ���ԣ���ȷ�ϣ���ȷ��ʽΪ{key:value}");
			LogHelper.info(mapStr);
			return null;
		}
		JSONObject ob = JSON.parseObject(mapStr);
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

	/**
	 * ��ʼ��reques���󣬰���header��cookie����Ϣ
	 * @param request
	 * @return HttpUriRequest
	 */
	public HttpUriRequest initRequest(HttpUriRequest request){
		if (header != null && header.size() > 0) {
			requestStr.append("Request Headers:"+header.toString()+System.getProperty("line.separator"));
			for (Entry<String, Object> entry : header.entrySet()) {
				request.setHeader(entry.getKey(),String.valueOf(entry.getValue()));
				try {
					request.addHeader(entry.getKey(),URLEncoder.encode(String.valueOf(entry.getValue()),charset));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		//�Ƿ���Ҫ��¼
		if (userInfo != null && userInfo.size() > 0) {
			String userId = (String) userInfo.get("userId");
			String pwd = (String) userInfo.get("pwd");
			Map<String, Object> rrr = login(userId,pwd);
			//����¼���ȡ����sn�ȷŵ�header��

		}
		//���cookie
		if (cookie != null && cookie.size() > 0) {
			requestStr.append("Cookies:"+cookie.toString()+System.getProperty("line.separator"));
			StringBuffer sbCookie = new StringBuffer();
			for (Entry<String, Object> entry : cookie.entrySet()) {
				sbCookie.append(entry.getKey());
				sbCookie.append("=");
				try {
					sbCookie.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				sbCookie.append(";");
			}
			request.addHeader("Cookies",sbCookie.substring(0, sbCookie.length() - 1));
		}
		return request;
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
		String[] vers = apiCase.getVerifications().split(",");//Ӣ�Ķ���
		if(vers.length<=1){
			vers = apiCase.getVerifications().split("��");//���Ķ���
		}
		for(String s: vers){
			if(!apiCase.getResponseStr().contains(s)){
				tmpResult = "Failed";
				break;
			}
		}
		apiCase.setResult(tmpResult);
		//��ȡ����
		Map map = extractParams(this.paramsOutput, this.apiCase.getResponseStr());

		return apiCase;
	}

	/**
	 * @Description:ʹ��HttpClient����post����
	 */
	public APICase httpClientPost() {
		this.initParams();
		HttpPost httpPost = new HttpPost(urlParam);
		httpPost = (HttpPost) this.initRequest(httpPost);
		// �����������-body����
		if(body != null && body.size()>0){
			requestStr.append("Body:"+body.toString()+System.getProperty("line.separator"));
			//json��ʽ
//			String tmp = "body={\"topicId\":\"5aa1e53d7e3a7302a796192f\"}";
//			StringEntity entity = new StringEntity(body.toString(), Charset.forName("UTF-8"));
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

		apiCase.setRequestStr(requestStr.toString());
		return this.execRequest(httpPost);
	}
	/**
	 * �ڷ��������л�ȡ������ֵ
	 * @param paramOutput String-Ҫ��ȡ�ı����б�
	 * @param responsStr  String-���ص��ַ�����responseStr
	 * @return map-��ȡ�ı����ֵ�
	 */
	private  Map<String, Object> extractParams(List<String> keys,String responseStr){
		Map<String, Object> params = new HashMap<>();
		//		List<String> keys = stringToList(paramOutput);
		JSONObject tmpMap = stringToMap(responseStr);
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
	 * @Description:ʹ��HttpClient����get����
	 */
	@SuppressWarnings("resource")
	public APICase httpClientGet() {
		// �����������
		this.initParams();

		HttpGet httpGet = new HttpGet(urlParam);
		httpGet = (HttpGet) this.initRequest(httpGet);
		// �����������-body����--get���󲻻���body
		apiCase.setRequestStr(requestStr.toString());
		return this.execRequest(httpGet);
	}
	/**
	 * ��ʽ��url��������url��������
	 */
	private void initParams(){
		// �����������
		if(this.apiCase.getParams().startsWith("{") && this.apiCase.getParams().endsWith("}")){
			JSONObject jo = stringToMap(this.params);
			StringBuffer sbParams = new StringBuffer();
			if (jo != null && jo.size() > 0) {
				for (Entry<String, Object> entry : jo.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					try {
						sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
					} catch (UnsupportedEncodingException e) {
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

	public static Map<String, Object> login(String userID,String pwd){
		Map<String, Object> map = new HashMap<String, Object>();
		String result = TestMD5.getMD5(userID, pwd, "ios");
		System.out.print(result);
		return map;
	}



	/**
	 * @Description:��ȡһ�����ݣ�contentLe���ݳ���Ϊ0ʱ����ȡ��Ӧͷ��Ϣ����Ϊ0ʱ������
	 */
	private static String readLine(InputStream is, int contentLength, String charset) throws IOException {
		List<Byte> lineByte = new ArrayList<Byte>();
		byte tempByte;
		int cumsum = 0;
		if (contentLength != 0) {
			do {
				tempByte = (byte) is.read();
				lineByte.add(Byte.valueOf(tempByte));
				cumsum++;
			} while (cumsum < contentLength);// cumsum����contentLength��ʾ�Ѷ���
		} else {
			do {
				tempByte = (byte) is.read();
				lineByte.add(Byte.valueOf(tempByte));
			} while (tempByte != 10);// ���з���ascii��ֵΪ10
		}

		byte[] resutlBytes = new byte[lineByte.size()];
		for (int i = 0; i < lineByte.size(); i++) {
			resutlBytes[i] = (lineByte.get(i)).byteValue();
		}
		return new String(resutlBytes, charset);
	}



}

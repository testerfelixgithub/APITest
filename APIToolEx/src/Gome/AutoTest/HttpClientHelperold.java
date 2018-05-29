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
 * @Description:发送http请求帮助类
 */
public class HttpClientHelperold {


	private static String charset = "utf-8";
	private static String protocol = "http";
	//初始化请求变量
	APICase apiCase;
	String urlParam;// = apiCase.getServerAddr() + apiCase.getURL();
	String params;// = stringToMap(apiCase.getParams());
	JSONObject header;// = stringToMap(apiCase.getHeaderStr());
	JSONObject cookie;// = stringToMap(apiCase.getCookieStr());
	JSONObject body;// = stringToMap(apiCase.getBodyStr());
	JSONObject userInfo;// = stringToMap(apiCase.getUserInfo());
	//提取参数列表
	List<String> paramsOutput;// = apiCase.getParamOutput();
	List<String> paramsInput;// =  apiCase.getParamInput();
	//请求报文、返回报文定义
	StringBuffer requestStr;// 
	StringBuffer responseStr;//
	

	/**
	 * 构造函数
	 * @param apiCase
	 */
	public HttpClientHelperold(APICase apiCase){
		this.apiCase = apiCase;
		this.init();
	}

	/**
	 * 执行单条用例
	 * @param apiCase
	 * @return 返回APICase
	 */
	public APICase RunSingleCase(){

		//传递参数。
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
					//参数列表中没有，是不是该抛出异常？？？
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
			LogHelper.error("put方法暂未实现。");
			break;
		case "DELETE":
			LogHelper.error("delete方法暂未实现。");
			break;
		default:
			break;
		}


		return apiCase;

	}



	private void init(){

		this.urlParam = initUrl();//拼接域名和url路径

		this.header = stringToMap(this.apiCase.getHeaderStr());
		this.cookie = stringToMap(this.apiCase.getCookieStr());
		this.initBody();
		this.userInfo = stringToMap(this.apiCase.getUserInfo());
		this.params = this.apiCase.getParams();

		//提取参数列表
		this.paramsOutput = stringToList(this.apiCase.getParamOutput());
		this.paramsInput =  stringToList(this.apiCase.getParamInput());

		//请求报文、返回报文定义
		this.requestStr = new StringBuffer();
		this.responseStr = new StringBuffer();
	}
	
	/**
	 * 初始化body数据。body数据可能有以下两种情况：
	 * ①json格式，可以直接解析成jsonobject
	 * ②格式为body={}的数据，需要单独处理
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
	 * 初始化url，根据提供的域名和url地址组装
	 * @return String 如：http://api.bs.gomeplus.com/v3/shop/shop
	 */
	private String initUrl(){
		StringBuffer sb = new StringBuffer();
		if(!this.apiCase.getServerAddr().contains(protocol)){//如果域名或ip字符串中不包括协议，则需要手动加上
			sb.append("http://");
		}
		//判断域名是否有"/"连接，如果有则需要去掉
		String tmp = this.apiCase.getServerAddr();
		if(tmp.endsWith("/")){
			sb.append(tmp.substring(0,tmp.length()-1));
		}else{
			sb.append(tmp.substring(0,tmp.length()));
		}

		//判断url路径间是否有"/"连接，如果没有则需要加上
		if(this.apiCase.getURL().startsWith("/")){
			sb.append(this.apiCase.getURL());
		}else{
			sb.append("/"+this.apiCase.getURL());
		}
		return sb.toString();
	}

	/**
	 * 通过阿里巴巴提供的fastjson将（实际为map格式的）字符串转换为map格式
	 * @param mapStr
	 * @return map<String,Object>
	 */
	private static JSONObject stringToMap(String mapStr){
		//gson将字符串转为map时会将int类型转换为duble类型，加上小数点，执行时就会报错。方法废弃。
		//		Gson gson = new Gson();
		//		Map<String, Object> params = new HashMap<String, Object>();
		//		params = gson.fromJson(ac.getParams(), params.getClass());
		if(mapStr == null || !mapStr.startsWith("{") || !mapStr.endsWith("}")){
			LogHelper.error("输入的参数格式不对，请确认！正确格式为{key:value}");
			LogHelper.info(mapStr);
			return null;
		}
		JSONObject ob = JSON.parseObject(mapStr);
		return ob;
	}

	/**
	 * 将字符串转换为list
	 * @param listStr
	 * @return
	 */
	private static List<String> stringToList(String listStr){
		if(listStr == null){
			return null;
		}
		List<String> pList = new ArrayList<>();
		String reg="\\W";//非字母、数字
		//将对象封装成对象。 
		Pattern p=Pattern.compile(reg); 
		//让正则对象和要作用的字符串相关联，获取匹配器对象。把多个方式都封装到了匹配器当中。 
		String[] tmp = p.split(listStr);
		for(String t:tmp){
			if(t != "" && t.length()>0){
				pList.add(t);
			}
		}
		return pList;
	}

	/**
	 * 初始化reques对象，包括header、cookie等信息
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
		//是否需要登录
		if (userInfo != null && userInfo.size() > 0) {
			String userId = (String) userInfo.get("userId");
			String pwd = (String) userInfo.get("pwd");
			Map<String, Object> rrr = login(userId,pwd);
			//将登录后获取到的sn等放到header中

		}
		//添加cookie
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
	 * 发起请求，将结果写入apiCase，并返回
	 * @param request HttpUriRequest请求，包括httpget、httpPost
	 * @return 带结果的APICase
	 */
	public APICase execRequest(HttpUriRequest request){
		BufferedReader br = null;
		StringBuffer resultBuffer = null;
		int responseCode;
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			HttpResponse response = client.execute(request);
			// 读取服务器响应数据
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
		//进行验证
		String tmpResult = "Passed";
		if(apiCase.getResponseCode_expect() != apiCase.getResponseCode_actual()){
			//			apiCase.setResult("Failed");
			tmpResult = "Failed";
		}
		String[] vers = apiCase.getVerifications().split(",");//英文逗号
		if(vers.length<=1){
			vers = apiCase.getVerifications().split("，");//中文逗号
		}
		for(String s: vers){
			if(!apiCase.getResponseStr().contains(s)){
				tmpResult = "Failed";
				break;
			}
		}
		apiCase.setResult(tmpResult);
		//提取参数
		Map map = extractParams(this.paramsOutput, this.apiCase.getResponseStr());

		return apiCase;
	}

	/**
	 * @Description:使用HttpClient发送post请求
	 */
	public APICase httpClientPost() {
		this.initParams();
		HttpPost httpPost = new HttpPost(urlParam);
		httpPost = (HttpPost) this.initRequest(httpPost);
		// 构建请求参数-body参数
		if(body != null && body.size()>0){
			requestStr.append("Body:"+body.toString()+System.getProperty("line.separator"));
			//json格式
//			String tmp = "body={\"topicId\":\"5aa1e53d7e3a7302a796192f\"}";
//			StringEntity entity = new StringEntity(body.toString(), Charset.forName("UTF-8"));
			StringEntity entity = new StringEntity(this.apiCase.getBodyStr(), Charset.forName("UTF-8"));
			httpPost.setEntity(entity);
			//text格式
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
	 * 在返回数据中获取变量的值
	 * @param paramOutput String-要提取的变量列表
	 * @param responsStr  String-返回的字符串，responseStr
	 * @return map-提取的变量字典
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
	 * @Description:使用HttpClient发送get请求
	 */
	@SuppressWarnings("resource")
	public APICase httpClientGet() {
		// 构建请求参数
		this.initParams();

		HttpGet httpGet = new HttpGet(urlParam);
		httpGet = (HttpGet) this.initRequest(httpGet);
		// 构建请求参数-body参数--get请求不会有body
		apiCase.setRequestStr(requestStr.toString());
		return this.execRequest(httpGet);
	}
	/**
	 * 格式化url参数，将url与参数组合
	 */
	private void initParams(){
		// 构建请求参数
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
	 * @Description:读取一行数据，contentLe内容长度为0时，读取响应头信息，不为0时读正文
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
			} while (cumsum < contentLength);// cumsum等于contentLength表示已读完
		} else {
			do {
				tempByte = (byte) is.read();
				lineByte.add(Byte.valueOf(tempByte));
			} while (tempByte != 10);// 换行符的ascii码值为10
		}

		byte[] resutlBytes = new byte[lineByte.size()];
		for (int i = 0; i < lineByte.size(); i++) {
			resutlBytes[i] = (lineByte.get(i)).byteValue();
		}
		return new String(resutlBytes, charset);
	}



}

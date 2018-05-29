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
	//初始化请求变量
	public APICase apiCase;                //APICase对象

	private String urlParam = "";				//URL,格式：http://api.bs.pre.gomeplus.com
	private String params = "";					//请求参数，一般是get请求中需要在url中传递的参数
	private String body = "";					//body参数，一般是在post请求中传递的参数

	private JSONObject header = new JSONObject();				//请求header
	private JSONObject cookie = new JSONObject();				//请求cookie
	private JSONObject userInfo = new JSONObject();			//用户信息，包括手机号、密码，用于提前登录
	//提取参数列表
	private List<String> paramsOutput = new ArrayList<String>();		//需要从本接口返回结果中提取的参数
	private List<String> paramsInput = new ArrayList<String>();		//需要从其他接口返回结果中提取的并用于本接口输入的参数
	//请求报文、返回报文定义
	private StringBuffer requestStr = new StringBuffer();		//请求报文
	private StringBuffer responseStr = new StringBuffer();		//返回报文


	/**
	 * 构造函数
	 * @param apiCase
	 * @throws Exception 
	 */
	public HttpClientHelper(APICase apiCase) throws Exception{
		this.apiCase = apiCase;
		this.urlParam = joinUrl();//拼接域名和url路径

		this.header = stringToJsonObject(this.apiCase.getHeaderStr());
		this.cookie = stringToJsonObject(this.apiCase.getCookieStr());
		this.userInfo = stringToJsonObject(this.apiCase.getUserInfo());
		this.body = this.apiCase.getBodyStr();
		this.params = this.apiCase.getParams();

		//提取参数列表
		this.paramsOutput = stringToList(this.apiCase.getParamOutput());
		this.paramsInput = stringToList(this.apiCase.getParamInput());
		//提取并替换需要输入的参数
		this.getParamsInput();

		//请求报文、返回报文定义
		this.requestStr = new StringBuffer();
		this.responseStr = new StringBuffer();
	}

	/**
	 * 从Config.PUBLIC_PARAMS中获取参数并替换param或body中的字符串
	 */
	private void getParamsInput() {
		//传递参数。
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
					//参数列表中没有，是不是该抛出异常？？？
				}
			}
		}
	}

	/**
	 * 初始化url信息，将协议、域名、接口url地址拼接起来
	 * @return String-格式化的url信息。如：http://api.bs.pre.gomeplus.com
	 * @throws Exception 
	 */
	private String joinUrl() throws Exception {
		StringBuffer sb = new StringBuffer();

		String serverAddr = this.apiCase.getServerAddr();
		if(serverAddr == null || serverAddr.equals("")){
			LogHelper.exception(new Exception("域名或IP为空，请检查!"));
			throw new Exception("域名或IP为空，请检查!");
		}
		if(!serverAddr.contains(this.protocol)){//如果域名或ip字符串中不包括协议，则需要手动加上
			sb.append("http://");
		}
		//判断域名是否有"/"连接，如果有则需要去掉
		if(serverAddr.endsWith("/")){
			sb.append(serverAddr.substring(0,serverAddr.length()-1));
		}else{
			sb.append(serverAddr.substring(0,serverAddr.length()));
		}

		String urlStr = this.apiCase.getURL();
		if(urlStr == null || urlStr.equals("")){
			LogHelper.exception(new Exception("接口路径为空，请检查!"));
			throw new Exception("接口路径为空，请检查!");
		}
		//判断url路径间是否有"/"连接，如果没有则需要加上
		if(urlStr.startsWith("/")){
			sb.append(urlStr);
		}else{
			sb.append("/"+urlStr);
		}
		return sb.toString();
	}

	/**
	 * 执行单条用例，并将执行后的结果写入APICase对象，最后返回
	 * @return APICase-执行后的用例
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
	 * 执行Get请求
	 */
	private void httpClientGet() {
		// 构建请求参数
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
		

		// 构建请求参数-body参数--get请求不会有body
		apiCase.setRequestStr(requestStr.toString());
		this.execRequest(httpGet);

	}



	/**
	 * 执行post请求
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
		
		// 构建请求参数-body参数
		httpPost = this.addBody(httpPost);

		this.apiCase.setRequestStr(this.requestStr.toString());
		this.execRequest(httpPost);


	}
	/**
	 * 执行put请求
	 */
	private void httpClientPut() {
		LogHelper.error("Put请求暂不支持！");
	}
	/**
	 * 执行delete请求
	 */
	private void httpClientDelete() {
		LogHelper.error("Delete请求暂不支持！");

	}


	/**
	 * 处理parameter，将parameter与url地址进行拼接
	 * param支持2种格式
	 * ①{key:value}
	 * ②key=value&key2=value2
	 */
	private void addParams(){

		// 构建请求参数
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
	 * 添加header信息
	 * @param request-HttpUriRequest
	 * @return HttpUriRequest 添加了header的请求
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
	 * 添加header信息
	 * @param request-HttpUriRequest
	 * @return HttpUriRequest 添加了header的请求
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
				LogHelper.error("Body参数格式错误，请检查！");
			}
			requestStr.append("Body:"+ this.body+System.getProperty("line.separator"));
			//json格式
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
		return httpPost;
	}

	private String login(String userName,String password,String deviceNo,String devicesDesc,String ip){
		String token = null;
		if(userName == null || userName.equals("")){
			LogHelper.error("登录信息缺少【用户名】，请确认！");
			return token;
		}
		if(password == null || password.equals("")){
			LogHelper.error("登录信息缺少【密码】，请确认！");
			return token;
		}
		if(deviceNo == null || deviceNo.equals("")){
			LogHelper.error("登录信息缺少【设备id】，请确认！");
			return token;
		}
		if(devicesDesc == null || devicesDesc.equals("")){
			LogHelper.error("登录信息缺少【设备描述】，请确认！");
			return token;
		}
		if(ip == null || ip.equals("")){
			LogHelper.error("登录信息缺少【ip】，请确认！");
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
		//进行验证
		String tmpResult = "Passed";
		if(apiCase.getResponseCode_expect() != apiCase.getResponseCode_actual()){
			//			apiCase.setResult("Failed");
			tmpResult = "Failed";
		}
		if(apiCase.getVerifications() != null && apiCase.getVerifications() != ""){
			String[] vers = apiCase.getVerifications().split(",");//英文逗号	
			if(vers.length<=1){
				vers = apiCase.getVerifications().split("，");//中文逗号
			}
			for(String s: vers){
				if(apiCase.getResponseStr()!= null && !apiCase.getResponseStr().contains(s)){
					tmpResult = "Failed";
					break;
				}
			}
		}

		apiCase.setResult(tmpResult);
		//提取参数
		Map map = extractParams(this.paramsOutput, this.apiCase.getResponseStr());

		return apiCase;
	}

	/**
	 * 在返回数据中获取变量的值
	 * @param paramOutput String-要提取的变量列表
	 * @param responsStr  String-返回的字符串，responseStr
	 * @return map-提取的变量字典
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
	 * 递归查找需要的key，并获取value
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
	 * 通过阿里巴巴提供的fastjson将（实际为map格式的）字符串转换为map格式
	 * @param String
	 * @return JSONObject
	 */
	private static JSONObject stringToJsonObject(String jsonStr){
		//gson将字符串转为map时会将int类型转换为duble类型，加上小数点，执行时就会报错。方法废弃。
		//		Gson gson = new Gson();
		//		Map<String, Object> params = new HashMap<String, Object>();
		//		params = gson.fromJson(ac.getParams(), params.getClass());
		if(jsonStr == null || jsonStr == ""){
			return null;
		}
		if(!jsonStr.startsWith("{") || !jsonStr.endsWith("}")){

			LogHelper.error("输入的参数格式不对，请确认！正确格式为{key:value}");
			LogHelper.info(jsonStr);
			return null;
		}
		JSONObject ob = JSON.parseObject(jsonStr);

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
}

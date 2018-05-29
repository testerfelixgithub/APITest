package Gome.AutoTest;

import java.util.Map;

import javax.xml.crypto.Data;

public class APICase {
	
	public int id;				//序号
	public String project; 		//项目
	public String module;		//所属模块
	public String apiName;		//接口名称
	public String caseID;		//用例id
	public String caseDesc;		//用例描述
	public String requestMethod;	//请求方式(get/post)
	public String serverAddr;		//域名/IP
	public String URL;				//url地址
	public String headerStr;		//请求Header
	public String cookieStr;		//Cookie
	public String params;			//请求参数
	public String bodyStr;			//Body参数
	public String userInfo;			//用户信息，来判断是否需要登录
	public String paramOutput;			//需要从本接口返回结果中提取的参数
	public String paramInput;			//本接口需要从其他接口获取的参数
	public String tester;				//测试人员
	public String developor;			//开发人员
	public String wikiAddr;				//wiki地址
	public int responseCode_expect;			//返回code-预期
	public String verifications;			//验证内容，json格式
	public String requestStr;			//请求报文（请求方式+请求url+body等）
	public int responseCode_actual;			//返回code-实际
	public String responseStr;			//返回报文
	public String result;					//测试结果
	public Data exectData;				//执行日期
	public String note;					//备注
	
	
	/**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * 设置id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return 项目名称
     */
    public String getProject() {
        return project;
    }

    /**
     * 设置项目名称
     * @param project
     */
    public void setProject(String project) {
        this.project = project;
    }
    /**
     * @return 模块名称
     */
    public String getModule() {
        return module;
    }
    /**
     * 设置模块名称
     * @param module
     */
    public void setModule(String module) {
        this.module = module;
    }
    /**
     * @return 返回接口名称
     */
    public String getApiName() {
        return apiName;
    }
    /**
     * 设置接口名称
     * @param apiName
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
    /**
     * @return 返回用例id
     */
    public String getCaseID() {
        return caseID;
    }
    /**
     * 设置用例id
     * @param caseID
     */
    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }
    /**
     * @return 返回用例说明
     */
    public String getCaseDesc() {
        return caseDesc;
    }
    /**
     * 设置用例说明
     * @param caseDesc
     */
    public void setCaseDesc(String caseDesc) {
        this.caseDesc = caseDesc;
    }
    /**
     * @return String 返回请求方式，get、post、put、delete
     */
    public String getRequestMethod() {
        return requestMethod;
    }
    /**
     * 设置请求方式，get、post、put、delete
     * @param requestMethod
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
    /**
     * @return 返回域名地址，String
     */
    public String getServerAddr() {
        return serverAddr;
    }
    /**
     * 设置域名地址，String
     * @param serverAddr
     */
    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }
    /**
     * @return 返回url路径地址
     */
    public String getURL() {
        return URL;
    }
    /**
     * 设置URL路径
     * @param uRL
     */
    public void setURL(String uRL) {
        URL = uRL;
    }
    /**
     * @return String 返回header字符串，json格式
     */
    public String getHeaderStr() {
        return headerStr;
    }
    /**
     * String 设置header字符串，json格式
     * @param headerStr
     */
    public void setHeaderStr(String headerStr) {
        this.headerStr = headerStr;
    }
    /**
     * @return 返回cookie字符串，json格式
     */
    public String getCookieStr() {
        return cookieStr;
    }
    /**
     * 设置cookie字符串，json格式
     * @param cookieStr
     */
    public void setCookieStr(String cookieStr) {
        this.cookieStr = cookieStr;
    }
    /**
     * @return 返回参数字符串，json格式或拼接完成的字符串
     * {"name":"sam","sex":"man"}
     * name=sam&sex=man
     */
    public String getParams() {
        return params;
    }
    /**
     * 设置参数字符串，json格式或拼接完成的字符串
     * {"name":"sam","sex":"man"}
     * name=sam&sex=man
     * @param params
     */
    public void setParams(String params) {
        this.params = params;
    }
    /**
     * @return 返回body参数，json格式
     */
    public String getBodyStr() {
        return bodyStr;
    }
    /**
     * 设置body参数，json格式
     * @param bodyStr
     */
    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }
    /**
     * @return 返回用户信息，用于判断是否需要登录
     * {"userid":"136","pwd":"asdf"}
     */
    public String getUserInfo() {
        return userInfo;
    }
    /**
     * 设置用户信息，用于判断是否需要登录
     * {"userid":"136","pwd":"asdf"}
     * @param userInfo
     */
    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
    /**
     * @return 返回本接口要提取的参数列表，["param1","param2"]
     */
    public String getParamOutput() {
        return paramOutput;
    }
    /**
     * 设置本接口要提取的参数列表，["param1","param2"]
     * @param paramOutput
     */
    public void setParamOutput(String paramOutput) {
        this.paramOutput = paramOutput;
    }
    /**
     * @return 返回本接口需要引入的参数列表，["param1","param2"]
     */
    public String getParamInput() {
        return paramInput;
    }
    /**
     * 本接口需要引入的参数列表，["param1","param2"]
     * @param paramInput
     */
    public void setParamInput(String paramInput) {
        this.paramInput = paramInput;
    }
    /**
     * @return 测试人员
     */
    public String getTester() {
        return tester;
    }
    /**
     * 测试人员
     * @param tester
     */
    public void setTester(String tester) {
        this.tester = tester;
    }
    /**
     * @return  开发人员
     */
    public String getDevelopor() {
        return developor;
    }

    /**
     * 开发人员
     * @param developor
     */
    public void setDevelopor(String developor) {
        this.developor = developor;
    }
    /**
     * @return wiki地址
     */
    public String getWikiAddr() {
        return wikiAddr;
    }
    /**
     * wiki地址
     * @param wikiAddr
     */
    public void setWikiAddr(String wikiAddr) {
        this.wikiAddr = wikiAddr;
    }
    /**
     * @return 请求信息
     */
    public String getRequestStr() {
        return requestStr;
    }
    /**
     * 请求信息
     * @param requestStr
     */
    public void setRequestStr(String requestStr) {
        this.requestStr = requestStr;
    }

    /**
     * @return response字符串
     */
    public String getResponseStr() {
        return responseStr;
    }
    /**
     * response字符串
     * @param responseStr
     */
    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }
    /**
     * @return 测试结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 预期返回code
     * @return
     */
    public int getResponseCode_expect() {
        return responseCode_expect;
    }

    /**
     * 预期返回code
     * @param responseCode_expect
     */
    public void setResponseCode_expect(int responseCode_expect) {
        this.responseCode_expect = responseCode_expect;
    }

    /**
     * 验证点，如果有多个，用逗号分隔（，,）
     * @return 验证点
     */
    public String getVerifications() {
        return verifications;
    }

    /**
     * 验证点，如果有多个，用逗号分隔（，,）
     * @param verifications
     */
    public void setVerifications(String verifications) {
        this.verifications = verifications;
    }

    /**
     * 实际返回code
     * @return 实际返回code
     */
    public int getResponseCode_actual() {
        return responseCode_actual;
    }

    /**
     * 实际返回code
     * @param responseCode_actual
     */
    public void setResponseCode_actual(int responseCode_actual) {
        this.responseCode_actual = responseCode_actual;
    }

    /**

     * 测试结果
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }
    /**
     * @return 执行日期
     */
    public Data getExectData() {
        return exectData;
    }
    /**
     * 执行日期
     * @param exectData
     */
    public void setExectData(Data exectData) {
        this.exectData = exectData;
    }
    /**
     * @return 备注
     */
    public String getNote() {
        return note;
    }
    /**
     * 备注
     * @param note
     */
    public void setNote(String note) {
        this.note = note;
    }
	
	



}

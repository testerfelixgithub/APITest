package Gome.AutoTest;

import java.util.Map;

import javax.xml.crypto.Data;

public class APICase {
	
	public int id;				//���
	public String project; 		//��Ŀ
	public String module;		//����ģ��
	public String apiName;		//�ӿ�����
	public String caseID;		//����id
	public String caseDesc;		//��������
	public String requestMethod;	//����ʽ(get/post)
	public String serverAddr;		//����/IP
	public String URL;				//url��ַ
	public String headerStr;		//����Header
	public String cookieStr;		//Cookie
	public String params;			//�������
	public String bodyStr;			//Body����
	public String userInfo;			//�û���Ϣ�����ж��Ƿ���Ҫ��¼
	public String paramOutput;			//��Ҫ�ӱ��ӿڷ��ؽ������ȡ�Ĳ���
	public String paramInput;			//���ӿ���Ҫ�������ӿڻ�ȡ�Ĳ���
	public String tester;				//������Ա
	public String developor;			//������Ա
	public String wikiAddr;				//wiki��ַ
	public int responseCode_expect;			//����code-Ԥ��
	public String verifications;			//��֤���ݣ�json��ʽ
	public String requestStr;			//�����ģ�����ʽ+����url+body�ȣ�
	public int responseCode_actual;			//����code-ʵ��
	public String responseStr;			//���ر���
	public String result;					//���Խ��
	public Data exectData;				//ִ������
	public String note;					//��ע
	
	
	/**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * ����id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * @return ��Ŀ����
     */
    public String getProject() {
        return project;
    }

    /**
     * ������Ŀ����
     * @param project
     */
    public void setProject(String project) {
        this.project = project;
    }
    /**
     * @return ģ������
     */
    public String getModule() {
        return module;
    }
    /**
     * ����ģ������
     * @param module
     */
    public void setModule(String module) {
        this.module = module;
    }
    /**
     * @return ���ؽӿ�����
     */
    public String getApiName() {
        return apiName;
    }
    /**
     * ���ýӿ�����
     * @param apiName
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
    /**
     * @return ��������id
     */
    public String getCaseID() {
        return caseID;
    }
    /**
     * ��������id
     * @param caseID
     */
    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }
    /**
     * @return ��������˵��
     */
    public String getCaseDesc() {
        return caseDesc;
    }
    /**
     * ��������˵��
     * @param caseDesc
     */
    public void setCaseDesc(String caseDesc) {
        this.caseDesc = caseDesc;
    }
    /**
     * @return String ��������ʽ��get��post��put��delete
     */
    public String getRequestMethod() {
        return requestMethod;
    }
    /**
     * ��������ʽ��get��post��put��delete
     * @param requestMethod
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
    /**
     * @return ����������ַ��String
     */
    public String getServerAddr() {
        return serverAddr;
    }
    /**
     * ����������ַ��String
     * @param serverAddr
     */
    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }
    /**
     * @return ����url·����ַ
     */
    public String getURL() {
        return URL;
    }
    /**
     * ����URL·��
     * @param uRL
     */
    public void setURL(String uRL) {
        URL = uRL;
    }
    /**
     * @return String ����header�ַ�����json��ʽ
     */
    public String getHeaderStr() {
        return headerStr;
    }
    /**
     * String ����header�ַ�����json��ʽ
     * @param headerStr
     */
    public void setHeaderStr(String headerStr) {
        this.headerStr = headerStr;
    }
    /**
     * @return ����cookie�ַ�����json��ʽ
     */
    public String getCookieStr() {
        return cookieStr;
    }
    /**
     * ����cookie�ַ�����json��ʽ
     * @param cookieStr
     */
    public void setCookieStr(String cookieStr) {
        this.cookieStr = cookieStr;
    }
    /**
     * @return ���ز����ַ�����json��ʽ��ƴ����ɵ��ַ���
     * {"name":"sam","sex":"man"}
     * name=sam&sex=man
     */
    public String getParams() {
        return params;
    }
    /**
     * ���ò����ַ�����json��ʽ��ƴ����ɵ��ַ���
     * {"name":"sam","sex":"man"}
     * name=sam&sex=man
     * @param params
     */
    public void setParams(String params) {
        this.params = params;
    }
    /**
     * @return ����body������json��ʽ
     */
    public String getBodyStr() {
        return bodyStr;
    }
    /**
     * ����body������json��ʽ
     * @param bodyStr
     */
    public void setBodyStr(String bodyStr) {
        this.bodyStr = bodyStr;
    }
    /**
     * @return �����û���Ϣ�������ж��Ƿ���Ҫ��¼
     * {"userid":"136","pwd":"asdf"}
     */
    public String getUserInfo() {
        return userInfo;
    }
    /**
     * �����û���Ϣ�������ж��Ƿ���Ҫ��¼
     * {"userid":"136","pwd":"asdf"}
     * @param userInfo
     */
    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
    /**
     * @return ���ر��ӿ�Ҫ��ȡ�Ĳ����б�["param1","param2"]
     */
    public String getParamOutput() {
        return paramOutput;
    }
    /**
     * ���ñ��ӿ�Ҫ��ȡ�Ĳ����б�["param1","param2"]
     * @param paramOutput
     */
    public void setParamOutput(String paramOutput) {
        this.paramOutput = paramOutput;
    }
    /**
     * @return ���ر��ӿ���Ҫ����Ĳ����б�["param1","param2"]
     */
    public String getParamInput() {
        return paramInput;
    }
    /**
     * ���ӿ���Ҫ����Ĳ����б�["param1","param2"]
     * @param paramInput
     */
    public void setParamInput(String paramInput) {
        this.paramInput = paramInput;
    }
    /**
     * @return ������Ա
     */
    public String getTester() {
        return tester;
    }
    /**
     * ������Ա
     * @param tester
     */
    public void setTester(String tester) {
        this.tester = tester;
    }
    /**
     * @return  ������Ա
     */
    public String getDevelopor() {
        return developor;
    }

    /**
     * ������Ա
     * @param developor
     */
    public void setDevelopor(String developor) {
        this.developor = developor;
    }
    /**
     * @return wiki��ַ
     */
    public String getWikiAddr() {
        return wikiAddr;
    }
    /**
     * wiki��ַ
     * @param wikiAddr
     */
    public void setWikiAddr(String wikiAddr) {
        this.wikiAddr = wikiAddr;
    }
    /**
     * @return ������Ϣ
     */
    public String getRequestStr() {
        return requestStr;
    }
    /**
     * ������Ϣ
     * @param requestStr
     */
    public void setRequestStr(String requestStr) {
        this.requestStr = requestStr;
    }

    /**
     * @return response�ַ���
     */
    public String getResponseStr() {
        return responseStr;
    }
    /**
     * response�ַ���
     * @param responseStr
     */
    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }
    /**
     * @return ���Խ��
     */
    public String getResult() {
        return result;
    }

    /**
     * Ԥ�ڷ���code
     * @return
     */
    public int getResponseCode_expect() {
        return responseCode_expect;
    }

    /**
     * Ԥ�ڷ���code
     * @param responseCode_expect
     */
    public void setResponseCode_expect(int responseCode_expect) {
        this.responseCode_expect = responseCode_expect;
    }

    /**
     * ��֤�㣬����ж�����ö��ŷָ�����,��
     * @return ��֤��
     */
    public String getVerifications() {
        return verifications;
    }

    /**
     * ��֤�㣬����ж�����ö��ŷָ�����,��
     * @param verifications
     */
    public void setVerifications(String verifications) {
        this.verifications = verifications;
    }

    /**
     * ʵ�ʷ���code
     * @return ʵ�ʷ���code
     */
    public int getResponseCode_actual() {
        return responseCode_actual;
    }

    /**
     * ʵ�ʷ���code
     * @param responseCode_actual
     */
    public void setResponseCode_actual(int responseCode_actual) {
        this.responseCode_actual = responseCode_actual;
    }

    /**

     * ���Խ��
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }
    /**
     * @return ִ������
     */
    public Data getExectData() {
        return exectData;
    }
    /**
     * ִ������
     * @param exectData
     */
    public void setExectData(Data exectData) {
        this.exectData = exectData;
    }
    /**
     * @return ��ע
     */
    public String getNote() {
        return note;
    }
    /**
     * ��ע
     * @param note
     */
    public void setNote(String note) {
        this.note = note;
    }
	
	



}

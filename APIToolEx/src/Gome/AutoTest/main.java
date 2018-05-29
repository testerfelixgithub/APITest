package Gome.AutoTest;

import java.io.File;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.gome.erm.login.LoginFromMobile;

public class main {
	
	private static void getToken(){
		
		String token = "";
		
		JSONObject jo = LoginFromMobile.getUserInfo("LJL150430", "Aa123456" ,"0001_null_null_null","iphone6s","127.0.0.1");
		
		if(jo.get("data") != null){
			JSONObject data = (JSONObject) jo.get("data");
			token = data.get("token").toString();
		}
		System.out.println(token);
	}
	
	private static int getDay(String dateStr){
		int result = 0;
		String[] tmp = dateStr.split("\\D");
		int[] days = {31,28,31,30,31,30,31,30,31,31,30,31};
		if(tmp.length==3){
			int year = Integer.valueOf(tmp[0]);
			int month = Integer.valueOf(tmp[1]);
			int day = Integer.valueOf(tmp[2]);
			for(int i=0;i<month-1;i++){
				result += days[i];
			}
			result += day;
			if(year%4==0){
				result += 1;
			}
			
		}
		System.out.println(result);
		return result;
	}

	public static void main(String[] args) {
//		main.getToken();
//		main.getDay("2018/1/31");
//		main.getDay("2018/2/5");
//		main.getDay("2018/3/5");
//		main.getDay("2000/3/5");
//		main.getDay("2000/12/31");
//		main.getDay("2001/12/31");
//		
//		
//		for(int i=100;i<1000;i++){
//			int a = i/100;		//百位数
//			int b = i%100/10;	//十位数
//			int c = i%10;		//个位数
//			if((a*a*a + b*b*b + c*c*c)==i){
//				System.out.println(i);
//			}
//		}
		
		Config.init();
		
//		String filePath=null;
		String filePath = "D:\\testData\\apiTest\\Cases33.xlsx";
		if(args.length == 1){
			filePath = args[0];
		}else{
			LogHelper.warn("请输入文件全路径！");
//			return;
		}
		
		if(filePath == null || filePath == ""){
			LogHelper.warn("用例文件路径为空，请重新输入。程序退出。");
			return;
		}
		File f = new File(filePath);
		if(!f.exists()){
			LogHelper.warn("文件不存在，请检查路径是否正确，程序退出！");
			return;
		}
		List<APICase> caseList = null;
		try {
			caseList = ExcelHelper.getCases(filePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		if(caseList == null || caseList.size() <1){
			LogHelper.error("未获取到任何用例数据，请检查用例文件！");
			return;
		}
		LogHelper.info("本次共有"+caseList.size()+"条用例。");
		LogHelper.info("=============开始执行================");
		for(int i=0;i<caseList.size();i++){
			APICase ac = caseList.get(i);
			LogHelper.info(ac.getApiName() + "--" + ac.getCaseDesc());
			HttpClientHelper httpClientHelper;
			try {
				httpClientHelper = new HttpClientHelper(ac);
				ac = httpClientHelper.runSingleCase();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			LogHelper.info(ac.getResponseCode_actual() + " " + ac.getResponseStr());
			caseList.set(i, ac);//更新用例列表
		}
		LogHelper.info("=============执行完毕================");
		try {
			ExcelHelper.saveResult(filePath, caseList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogHelper.exception(e);
		}
		System.exit(0);

	}

}

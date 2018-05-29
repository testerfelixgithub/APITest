package Gome.AutoTest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelHelper {



	public static void main(String[] args) {
		String filePath = "D:\\testData\\Cases.xlsx";
		//获取workbook对象

		//获取全部用例
		List<APICase> caseList = null;
		try {
			caseList = getCases(filePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogHelper.info(caseList.size());
		//执行用例
		//caseList = apiRequest.runCases(caseList);
		//将测试结果写入元excel文件
		try {
			saveResult(filePath, caseList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * 将执行过的用例保存到excel文件
	 * @param filePath-用例保存文件路径
	 * @param caseList 用例列表
	 * @throws Exception
	 */
	public static void saveResult(String filePath,List<APICase> caseList) throws Exception{
		//创建文件流   
		Workbook wb = getWorkbook(filePath);
		Sheet sheet = wb.getSheetAt(0);
		//获取最大行数
		int rownum = sheet.getPhysicalNumberOfRows();
		//            if(caseList.size() != rownum){
		//            	throw new Exception("执行结果与实际用例条数不符合，请检查。文件中有"+ rownum+"条，执行结果中"+caseList.size()+"条。");
		//            }
		//获取第一行
		Row row = sheet.getRow(0);
		//获取最大列数
		int colnum = row.getPhysicalNumberOfCells();
		//跳过表头，从第二行开始，将结果写入文件
		for (int i = 1; i<=caseList.size(); i++) {
			row = sheet.getRow(i);
			APICase ac = caseList.get(i-1);
			row.getCell(21).setCellValue(ac.getRequestStr());;//请求报文
			row.getCell(22).setCellValue(ac.getResponseCode_actual());;//response报文
			row.getCell(23).setCellValue(ac.getResponseStr());;//response报文
			if(ac.getResult().toLowerCase().equals("passed")){
				row.getCell(24).setCellValue(ac.getResult());//是否通过
			}else{

				//            		CellStyle style3 = row.getCell(24).getCellStyle();
				//            		style3.setFillBackgroundColor(HSSFColor.RED.index);
				//            		//设置单元格的填充效果
				////            		style3.setFillPattern(XSSFCellStyle.);
				//            	    row.getCell(24).setCellStyle(style3);


				Font font1 = wb.createFont();
				//设置字体
				font1.setFontName("微软雅黑");
				font1.setColor(HSSFColor.RED.index);//设置颜色
				font1.setBold(true);//加粗
				CellStyle style = wb.createCellStyle();
				style.setFont(font1);
				row.getCell(24).setCellStyle(style);
				row.getCell(24).setCellValue(ac.getResult());//是否通过
			}

		}

		//重新生成新文件，新文件名为：旧文件名+当前时间
		String extString = filePath.substring(filePath.lastIndexOf("."));
		String pre = filePath.substring(0, filePath.lastIndexOf("."));
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		filePath = pre + "-" + sdf.format(d) + extString;

		OutputStream stream = null;
		try{
			stream = new FileOutputStream(filePath);
			//写入数据   
			wb.write(stream);  
			//关闭文件流   
			stream.close(); 
		}catch (IOException e) {
			e.printStackTrace();
			stream.close();
		}
		
		LogHelper.info("执行结果已保存到文件中，请查看：" + filePath);

	}

	/**
	 * 获取全部用例信息
	 * @param filePath
	 * @return 用例列表
	 * @throws Exception 
	 */
	public static List<APICase> getCases(String filePath) throws Exception{
		Workbook wb = getWorkbook(filePath);
		Sheet sheet = null;
		Row row = null;
		List<Map<String,String>> list = null;
		String cellData = null;

		List<APICase> caseList = new ArrayList<>();

		if(wb != null){
			int sheetNum = wb.getNumberOfSheets();
//			System.out.println("共有"+ sheetNum +"个sheet");
			//获取第一个sheet
			sheet = wb.getSheetAt(0);
			//获取最大行数
			int rownum = sheet.getPhysicalNumberOfRows();
			//获取第一行
			row = sheet.getRow(0);
			//获取最大列数
			int colnum = row.getPhysicalNumberOfCells();
			//跳过表头，从第二行开始读取
			for (int i = 1; i<rownum; i++) {
				row = sheet.getRow(i);
				if(row !=null && (String) getCellFormatValue(row.getCell(1))!=""){
					APICase ac = new APICase();
					try {
						ac.setProject((String) getCellFormatValue(row.getCell(0)));//序号
						ac.setProject((String) getCellFormatValue(row.getCell(1)));//项目
						ac.setModule((String) getCellFormatValue(row.getCell(2)));//模块
						ac.setApiName((String) getCellFormatValue(row.getCell(3)));//接口名称
						ac.setCaseID((String) getCellFormatValue(row.getCell(4)));//用例id
						ac.setCaseDesc((String) getCellFormatValue(row.getCell(5)));//用例描述
						ac.setRequestMethod((String) getCellFormatValue(row.getCell(6)));//请求方式(get/post)
						ac.setServerAddr((String) getCellFormatValue(row.getCell(7)));//域名/IP
						ac.setURL((String) getCellFormatValue(row.getCell(8)));//url地址
						ac.setHeaderStr((String) getCellFormatValue(row.getCell(9)));//请求Header
						ac.setCookieStr((String) getCellFormatValue(row.getCell(10)));//Cookie
						ac.setParams((String) getCellFormatValue(row.getCell(11)));//请求参数
						ac.setBodyStr((String) getCellFormatValue(row.getCell(12)));//Body参数
						ac.setUserInfo((String) getCellFormatValue(row.getCell(13)));//是否需要登录
						ac.setParamOutput((String) getCellFormatValue(row.getCell(14)));//提取参数
						ac.setParamInput((String) getCellFormatValue(row.getCell(15)));//前置参数
						ac.setTester((String) getCellFormatValue(row.getCell(16)));//测试人员
						ac.setDevelopor((String) getCellFormatValue(row.getCell(17)));//开发人员
						ac.setWikiAddr((String) getCellFormatValue(row.getCell(18)));//wiki地址
						ac.setResponseCode_expect(Integer.valueOf((int) row.getCell(19).getNumericCellValue()));
						ac.setVerifications((String) getCellFormatValue(row.getCell(20)));
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("第"+(i+1) +"数据出错！");
					}
					
					//18-请求报文（请求方式+请求url+body等）
					//19-返回报文(code+接口返回)
					//20-测试结果
					//21-测试日期
					//22-备注
					caseList.add(ac);
				}else{
					break;
				}
			}
		}



		return caseList;

	}
	//读取excel
	private static Workbook getWorkbook(String filePath){
		Workbook wb = null;
		if(filePath==null){
			return null;
		}
		String extString = filePath.substring(filePath.lastIndexOf("."));
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			if(".xls".equals(extString)){
				return wb = new HSSFWorkbook(is);
			}else if(".xlsx".equals(extString)){
				return wb = new XSSFWorkbook(is);
			}else{
				return wb = null;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wb;
	}
	private static Object getCellFormatValue(Cell cell){
		Object cellValue = null;
		//	        1）CELL_TYPE_BLANK ：空值
		//	        2）CELL_TYPE_BOOLEAN ：布尔型
		//	        3）CELL_TYPE_ERROR ： 错误
		//	        4）CELL_TYPE_FORMULA ：公式型
		//	        5）CELL_TYPE_STRING：字符串型
		//	        6）CELL_TYPE_NUMERIC：数值型
		if(cell!=null){
			//判断cell类型
			switch(cell.getCellType()){
			case Cell.CELL_TYPE_NUMERIC:{
				cellValue = String.valueOf(cell.getNumericCellValue());
				break;
			}
			case Cell.CELL_TYPE_FORMULA:{
				//判断cell是否为日期格式
				if(DateUtil.isCellDateFormatted(cell)){
					//转换为日期格式YYYY-mm-dd
					cellValue = cell.getDateCellValue();
				}else{
					//数字
					cellValue = String.valueOf(cell.getNumericCellValue());
				}
				break;
			}
			case Cell.CELL_TYPE_STRING:{
				cellValue = cell.getRichStringCellValue().getString();
				break;
			}
			default:
				cellValue = "";
			}
		}else{
			cellValue = "";
		}
		return cellValue;
	}




}

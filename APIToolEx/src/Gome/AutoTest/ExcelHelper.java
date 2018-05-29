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
		//��ȡworkbook����

		//��ȡȫ������
		List<APICase> caseList = null;
		try {
			caseList = getCases(filePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LogHelper.info(caseList.size());
		//ִ������
		//caseList = apiRequest.runCases(caseList);
		//�����Խ��д��Ԫexcel�ļ�
		try {
			saveResult(filePath, caseList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * ��ִ�й����������浽excel�ļ�
	 * @param filePath-���������ļ�·��
	 * @param caseList �����б�
	 * @throws Exception
	 */
	public static void saveResult(String filePath,List<APICase> caseList) throws Exception{
		//�����ļ���   
		Workbook wb = getWorkbook(filePath);
		Sheet sheet = wb.getSheetAt(0);
		//��ȡ�������
		int rownum = sheet.getPhysicalNumberOfRows();
		//            if(caseList.size() != rownum){
		//            	throw new Exception("ִ�н����ʵ���������������ϣ����顣�ļ�����"+ rownum+"����ִ�н����"+caseList.size()+"����");
		//            }
		//��ȡ��һ��
		Row row = sheet.getRow(0);
		//��ȡ�������
		int colnum = row.getPhysicalNumberOfCells();
		//������ͷ���ӵڶ��п�ʼ�������д���ļ�
		for (int i = 1; i<=caseList.size(); i++) {
			row = sheet.getRow(i);
			APICase ac = caseList.get(i-1);
			row.getCell(21).setCellValue(ac.getRequestStr());;//������
			row.getCell(22).setCellValue(ac.getResponseCode_actual());;//response����
			row.getCell(23).setCellValue(ac.getResponseStr());;//response����
			if(ac.getResult().toLowerCase().equals("passed")){
				row.getCell(24).setCellValue(ac.getResult());//�Ƿ�ͨ��
			}else{

				//            		CellStyle style3 = row.getCell(24).getCellStyle();
				//            		style3.setFillBackgroundColor(HSSFColor.RED.index);
				//            		//���õ�Ԫ������Ч��
				////            		style3.setFillPattern(XSSFCellStyle.);
				//            	    row.getCell(24).setCellStyle(style3);


				Font font1 = wb.createFont();
				//��������
				font1.setFontName("΢���ź�");
				font1.setColor(HSSFColor.RED.index);//������ɫ
				font1.setBold(true);//�Ӵ�
				CellStyle style = wb.createCellStyle();
				style.setFont(font1);
				row.getCell(24).setCellStyle(style);
				row.getCell(24).setCellValue(ac.getResult());//�Ƿ�ͨ��
			}

		}

		//�����������ļ������ļ���Ϊ�����ļ���+��ǰʱ��
		String extString = filePath.substring(filePath.lastIndexOf("."));
		String pre = filePath.substring(0, filePath.lastIndexOf("."));
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		filePath = pre + "-" + sdf.format(d) + extString;

		OutputStream stream = null;
		try{
			stream = new FileOutputStream(filePath);
			//д������   
			wb.write(stream);  
			//�ر��ļ���   
			stream.close(); 
		}catch (IOException e) {
			e.printStackTrace();
			stream.close();
		}
		
		LogHelper.info("ִ�н���ѱ��浽�ļ��У���鿴��" + filePath);

	}

	/**
	 * ��ȡȫ��������Ϣ
	 * @param filePath
	 * @return �����б�
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
//			System.out.println("����"+ sheetNum +"��sheet");
			//��ȡ��һ��sheet
			sheet = wb.getSheetAt(0);
			//��ȡ�������
			int rownum = sheet.getPhysicalNumberOfRows();
			//��ȡ��һ��
			row = sheet.getRow(0);
			//��ȡ�������
			int colnum = row.getPhysicalNumberOfCells();
			//������ͷ���ӵڶ��п�ʼ��ȡ
			for (int i = 1; i<rownum; i++) {
				row = sheet.getRow(i);
				if(row !=null && (String) getCellFormatValue(row.getCell(1))!=""){
					APICase ac = new APICase();
					try {
						ac.setProject((String) getCellFormatValue(row.getCell(0)));//���
						ac.setProject((String) getCellFormatValue(row.getCell(1)));//��Ŀ
						ac.setModule((String) getCellFormatValue(row.getCell(2)));//ģ��
						ac.setApiName((String) getCellFormatValue(row.getCell(3)));//�ӿ�����
						ac.setCaseID((String) getCellFormatValue(row.getCell(4)));//����id
						ac.setCaseDesc((String) getCellFormatValue(row.getCell(5)));//��������
						ac.setRequestMethod((String) getCellFormatValue(row.getCell(6)));//����ʽ(get/post)
						ac.setServerAddr((String) getCellFormatValue(row.getCell(7)));//����/IP
						ac.setURL((String) getCellFormatValue(row.getCell(8)));//url��ַ
						ac.setHeaderStr((String) getCellFormatValue(row.getCell(9)));//����Header
						ac.setCookieStr((String) getCellFormatValue(row.getCell(10)));//Cookie
						ac.setParams((String) getCellFormatValue(row.getCell(11)));//�������
						ac.setBodyStr((String) getCellFormatValue(row.getCell(12)));//Body����
						ac.setUserInfo((String) getCellFormatValue(row.getCell(13)));//�Ƿ���Ҫ��¼
						ac.setParamOutput((String) getCellFormatValue(row.getCell(14)));//��ȡ����
						ac.setParamInput((String) getCellFormatValue(row.getCell(15)));//ǰ�ò���
						ac.setTester((String) getCellFormatValue(row.getCell(16)));//������Ա
						ac.setDevelopor((String) getCellFormatValue(row.getCell(17)));//������Ա
						ac.setWikiAddr((String) getCellFormatValue(row.getCell(18)));//wiki��ַ
						ac.setResponseCode_expect(Integer.valueOf((int) row.getCell(19).getNumericCellValue()));
						ac.setVerifications((String) getCellFormatValue(row.getCell(20)));
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("��"+(i+1) +"���ݳ���");
					}
					
					//18-�����ģ�����ʽ+����url+body�ȣ�
					//19-���ر���(code+�ӿڷ���)
					//20-���Խ��
					//21-��������
					//22-��ע
					caseList.add(ac);
				}else{
					break;
				}
			}
		}



		return caseList;

	}
	//��ȡexcel
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
		//	        1��CELL_TYPE_BLANK ����ֵ
		//	        2��CELL_TYPE_BOOLEAN ��������
		//	        3��CELL_TYPE_ERROR �� ����
		//	        4��CELL_TYPE_FORMULA ����ʽ��
		//	        5��CELL_TYPE_STRING���ַ�����
		//	        6��CELL_TYPE_NUMERIC����ֵ��
		if(cell!=null){
			//�ж�cell����
			switch(cell.getCellType()){
			case Cell.CELL_TYPE_NUMERIC:{
				cellValue = String.valueOf(cell.getNumericCellValue());
				break;
			}
			case Cell.CELL_TYPE_FORMULA:{
				//�ж�cell�Ƿ�Ϊ���ڸ�ʽ
				if(DateUtil.isCellDateFormatted(cell)){
					//ת��Ϊ���ڸ�ʽYYYY-mm-dd
					cellValue = cell.getDateCellValue();
				}else{
					//����
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

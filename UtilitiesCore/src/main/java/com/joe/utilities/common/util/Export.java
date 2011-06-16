package com.joe.utilities.common.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;







public class Export {
	
	public static void doExport_Excel(String fileName,String[] titleList,List list , HttpServletResponse response) {
		/*
			if(list== null || list.size()<=0  ){
				//System.out.println("print");
				try {
					
					response.setContentType("text/html;charset=utf-8");
					response.getWriter().write("<script>alert(\"记录不存在\");</script>");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		 */ 
		
			Date aaa = new Date();
			SimpleDateFormat aSimpleDateFormat = new java.text.SimpleDateFormat(
					"yyyy年MM月dd日hh时mm分");
			SimpleDateFormat aSimpleDateFormat1 = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
			String todayStr=df.format(new Date());
			String today = aSimpleDateFormat.format(aaa);
			String today1 = aSimpleDateFormat1.format(aaa);
		try{
			
			OutputStream os = response.getOutputStream();
			String localFileName = fileName;
			fileName = java.net.URLEncoder.encode(fileName, "UTF-8");//处理中文文件名的问题 
			fileName = new String(fileName.getBytes("UTF-8"),"GBK");//处理中文文件名的问题
			response.setContentType("application/vnd.ms-excel;");
			response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"_"+todayStr+ ".xls\"");
			// 开始写入excel
			// 加标题
			// 标题字体
			jxl.write.WritableFont wfc = new jxl.write.WritableFont(
					WritableFont.COURIER, 18, WritableFont.NO_BOLD, true);
			jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
					wfc);
			wcfFC.setAlignment(jxl.format.Alignment.CENTRE);
			wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

			// 字段字体
			jxl.write.WritableFont wfc1 = new jxl.write.WritableFont(
					WritableFont.COURIER, 10, WritableFont.NO_BOLD, true);
			jxl.write.WritableCellFormat wcfFC1 = new jxl.write.WritableCellFormat(
					wfc1);
			wcfFC1.setAlignment(jxl.format.Alignment.CENTRE);
			wcfFC1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

			// 结果字体
			jxl.write.WritableCellFormat wcfFC2 = new jxl.write.WritableCellFormat();
			wcfFC2.setAlignment(jxl.format.Alignment.CENTRE);
			wcfFC2.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

			WritableWorkbook wbook = Workbook.createWorkbook(os);
			// 写sheet名称
			WritableSheet wsheet = wbook.createSheet(localFileName, 0);

			int i = 3;
			
			for(int m = 0;m<titleList.length;m++){
				wsheet.setColumnView(m, 30);
			}
			// 加入字段名
			for(int n = 0;n<titleList.length;n++){
				wsheet.addCell(new jxl.write.Label(n, 3, titleList[n], wcfFC1));
			}
			// 加入标题
			wsheet.mergeCells(0, 0, i - 1, 0);
			wsheet.addCell(new Label(0, 0, localFileName, wcfFC));
			// 加入打印时间
			wsheet.addCell(new Label(i - 2, 1, "打印日期:"));
			wsheet.addCell(new Label(i - 1, 1, today));

			// 写入流中
			
			StringBuilder XML = new StringBuilder();
			int row=0;
			int m=0;
			long revctime=0;
			long sendtime=0;
			int compare =0;
			String str = "";
			
			for(int r=0;r<list.size();r++){
				Object[]  obj    =   (Object[])list.get(r);
				
				for(int x = 0;x<titleList.length;x++){
					wsheet.addCell(new jxl.write.Label(x, row + 4,obj[x]  == null ? " " : obj[x].toString() , wcfFC1));
				}
				row++;
			}
			wbook.write();
			wbook.close();
			os.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
}
	
		
	
	
	public static void doExport_Excel(String fileName,String[] titleList,List list ,Class obj, HttpServletResponse response) {
	  
		/*
		if(list.size()<=0){
			try{
				response.setContentType("text/html;charset=UTF-8"); 
				response.setCharacterEncoding("UTF-8"); 
				PrintWriter writer = response.getWriter(); 
				writer.write(" <script>alert('暂无记录!');</script>"); 
				writer.close(); 
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		*/
		
		Date aaa = new Date();
		SimpleDateFormat aSimpleDateFormat = new java.text.SimpleDateFormat(
				"yyyy年MM月dd日hh时mm分");
		SimpleDateFormat aSimpleDateFormat1 = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
		String todayStr=df.format(new Date());
		String today = aSimpleDateFormat.format(aaa);
		String today1 = aSimpleDateFormat1.format(aaa);
	try{
		OutputStream os = response.getOutputStream();
		response.setContentType("application/vnd.ms-excel;");
		response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"_"+todayStr+ ".xls\"");
		// 开始写入excel
		// 加标题
		// 标题字体
		jxl.write.WritableFont wfc = new jxl.write.WritableFont(
				WritableFont.COURIER, 18, WritableFont.NO_BOLD, true);
		jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
				wfc);
		wcfFC.setAlignment(jxl.format.Alignment.CENTRE);
		wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

		// 字段字体
		jxl.write.WritableFont wfc1 = new jxl.write.WritableFont(
				WritableFont.COURIER, 10, WritableFont.NO_BOLD, true);
		jxl.write.WritableCellFormat wcfFC1 = new jxl.write.WritableCellFormat(
				wfc1);
		wcfFC1.setAlignment(jxl.format.Alignment.CENTRE);
		wcfFC1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

		// 结果字体
		jxl.write.WritableCellFormat wcfFC2 = new jxl.write.WritableCellFormat();
		wcfFC2.setAlignment(jxl.format.Alignment.CENTRE);
		wcfFC2.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

		WritableWorkbook wbook = Workbook.createWorkbook(os);
		// 写sheet名称
		WritableSheet wsheet = wbook.createSheet(fileName, 0);

		int i = 3;
		
		for(int m = 0;m<titleList.length;m++){
			wsheet.setColumnView(m, 30);
		}
		// 加入字段名
		for(int n = 0;n<titleList.length;n++){
			wsheet.addCell(new jxl.write.Label(n, 3, titleList[n], wcfFC1));
		}
		// 加入标题
		wsheet.mergeCells(0, 0, i - 1, 0);
		wsheet.addCell(new Label(0, 0, fileName, wcfFC));
		// 加入打印时间
		wsheet.addCell(new Label(i - 2, 1, "打印日期:"));
		wsheet.addCell(new Label(i - 1, 1, today));

		// 写入流中
		
		StringBuilder XML = new StringBuilder();
		int row=0;
		int m=0;
		long revctime=0;
		long sendtime=0;
		int compare =0;
		String str = "";
		Reflection reflection = new Reflection();
		List returnList =reflection.GetMethodInvokeFromList(list, obj);
		for (int x =0;x<returnList.size();x++){
			List l =(List)returnList.get(x);
			for(int y = 0;y<l.size();y++){
				wsheet.addCell(new jxl.write.Label(y, row + 4,list.get(y).toString(), wcfFC1));
			}
			row++;
		}
		
		wbook.write();
		wbook.close();
		os.close();
		
	} catch (Exception e) {
		e.printStackTrace();
		try {
			response.setContentType("text/xml;charset=utf-8");
			response.getWriter().write("<table><info status=\"1\">" + e.toString()+ "</info></table>");
		} catch (Exception ex) {
		}
	} 
}

	
public static void doExport_Excel(String fileName,List<String> titleList,List list ,Class obj,List addList, HttpServletResponse response) {
	    Date aaa = new Date();
		SimpleDateFormat aSimpleDateFormat = new java.text.SimpleDateFormat(
				"yyyy年MM月dd日hh时mm分");
		SimpleDateFormat aSimpleDateFormat1 = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
		String todayStr=df.format(new Date());
		String today = aSimpleDateFormat.format(aaa);
		String today1 = aSimpleDateFormat1.format(aaa);
	try{
		OutputStream os = response.getOutputStream();
		response.setContentType("application/vnd.ms-excel;");
		response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"_"+todayStr+ ".xls\"");
		// 开始写入excel
		// 加标题
		// 标题字体
		jxl.write.WritableFont wfc = new jxl.write.WritableFont(
				WritableFont.COURIER, 18, WritableFont.NO_BOLD, true);
		jxl.write.WritableCellFormat wcfFC = new jxl.write.WritableCellFormat(
				wfc);
		wcfFC.setAlignment(jxl.format.Alignment.CENTRE);
		wcfFC.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

		// 字段字体
		jxl.write.WritableFont wfc1 = new jxl.write.WritableFont(
				WritableFont.COURIER, 10, WritableFont.NO_BOLD, true);
		jxl.write.WritableCellFormat wcfFC1 = new jxl.write.WritableCellFormat(
				wfc1);
		wcfFC1.setAlignment(jxl.format.Alignment.CENTRE);
		wcfFC1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

		// 结果字体
		jxl.write.WritableCellFormat wcfFC2 = new jxl.write.WritableCellFormat();
		wcfFC2.setAlignment(jxl.format.Alignment.CENTRE);
		wcfFC2.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);

		WritableWorkbook wbook = Workbook.createWorkbook(os);
		// 写sheet名称
		WritableSheet wsheet = wbook.createSheet(fileName, 0);

		int i = 3;
		
		for(int m = 0;m<titleList.size();m++){
			wsheet.setColumnView(m, 30);
		}
		// 加入字段名
		for(int n = 0;n<titleList.size();n++){
			wsheet.addCell(new jxl.write.Label(n, 3, titleList.get(n), wcfFC1));
		}
		// 加入标题
		wsheet.mergeCells(0, 0, i - 1, 0);
		wsheet.addCell(new Label(0, 0, fileName, wcfFC));
		// 加入打印时间
		wsheet.addCell(new Label(i - 2, 1, "打印日期:"));
		wsheet.addCell(new Label(i - 1, 1, today));

		// 写入流中
		
		StringBuilder XML = new StringBuilder();
		int row=0;
		int m=0;
		long revctime=0;
		long sendtime=0;
		int compare =0;
		String str = "";
		Reflection reflection = new Reflection();
		List returnList =reflection.GetMethodInvokeFromList(list, obj);
		for (int x =0;x<returnList.size();x++){
			List l =(List)returnList.get(x);
			for(int y = 0;y<l.size();y++){
				wsheet.addCell(new jxl.write.Label(y, row + 4,list.get(y).toString(), wcfFC1));
			}
			row++;
		}
		
		for(int r=0;r<list.size();r++){
			Class  o    =   (Class)list.get(r);
			
			for(int x = 0;x<titleList.size();x++){
		//		
			}
			
		}
		wbook.write();
		wbook.close();
		os.close();
		
	} catch (Exception e) {
		e.printStackTrace();
		try {
			response.setContentType("text/xml;charset=utf-8");
			response.getWriter().write("<table><info status=\"1\">" + e.toString()+ "</info></table>");
		} catch (Exception ex) {
		}
	} 
}


public static void doExport_Csv(String fileName,String[] titleList,List list , HttpServletResponse response) {
    Date aaa = new Date();
	SimpleDateFormat aSimpleDateFormat = new java.text.SimpleDateFormat(
			"yyyy年MM月dd日hh时mm分");
	SimpleDateFormat aSimpleDateFormat1 = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
	String todayStr=df.format(new Date());
	String today = aSimpleDateFormat.format(aaa);
	String today1 = aSimpleDateFormat1.format(aaa);
	String o_fileName = fileName;
try{
	
	fileName = java.net.URLEncoder.encode(fileName, "UTF-8");//处理中文文件名的问题 
	fileName = new String(fileName.getBytes("UTF-8"),"GBK");//处理中文文件名的问题
	response.setContentType("application/csv;charset=gb18030"); 
	response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"_"+todayStr+ ".csv\"");
	
	PrintWriter out = response.getWriter();
	out.print("\t\t"+"打印日期:"+today+"\n");//输出表名
	out.print("\t\t"+o_fileName+"\n");//输出表名
	
	
	// 加入字段名
	for(int n = 0;n<titleList.length;n++){
		out.print(titleList[n]+"\t");//输出表的列名
	}
	out.print("\n\n");
	// 写入流中
	
	StringBuilder XML = new StringBuilder();
	
	for(int r=0;r<list.size();r++){
		Object[]  obj    =   (Object[])list.get(r);
		
		for(int x = 0;x<titleList.length;x++){
			out.print(obj[x] == null ? "": obj[x].toString()+"\t");
		}
		out.print("\n");
	}
	
} catch (Exception e) {
	e.printStackTrace();
	try {
		response.setContentType("text/xml;charset=utf-8");
		response.getWriter().write("<table><info status=\"1\">" + e.toString()+ "</info></table>");
	} catch (Exception ex) {
	}
} 
}
	

/*public static void doExport_Txt(String fileName,String[] titleList,List list , HttpServletResponse response) {
    Date aaa = new Date();
	SimpleDateFormat aSimpleDateFormat = new java.text.SimpleDateFormat(
			"yyyy年MM月dd日hh时mm分");
	SimpleDateFormat aSimpleDateFormat1 = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
	String todayStr=df.format(new Date());
	String today = aSimpleDateFormat.format(aaa);
	String today1 = aSimpleDateFormat1.format(aaa);
	String o_fileName = fileName;
try{
	
	fileName = java.net.URLEncoder.encode(fileName, "UTF-8");//处理中文文件名的问题 
	fileName = new String(fileName.getBytes("UTF-8"),"GBK");//处理中文文件名的问题
	response.setContentType("application/txt;charset=gb18030"); 
	response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"_"+todayStr+ ".txt\"");
	
	PrintWriter out = response.getWriter();
	out.print("\t\t"+"打印日期:"+today+"\r\n");//输出表名
	out.print("\t\t"+o_fileName+"\r\n");//输出表名
	
	
	// 加入字段名
	for(int n = 0;n<titleList.length;n++){
		out.print(titleList[n]+"\t");//输出表的列名
	}
	out.print("\r\n");
	out.print("\r\n");
	// 写入流中
	
	StringBuilder XML = new StringBuilder();
	
	for(int r=0;r<list.size();r++){
		Object[]  obj    =   (Object[])list.get(r);
		
		for(int x = 0;x<titleList.length;x++){
			out.print(obj[x] == null ? "": obj[x].toString()+"\t");
		}
		out.print("\r\n");
	}
	
} catch (Exception e) {
	e.printStackTrace();
	try {
		response.setContentType("text/xml;charset=utf-8");
		response.getWriter().write("<table><info status=\"1\">" + e.toString()+ "</info></table>");
	} catch (Exception ex) {
	}
} 
}*/

public static void doExport_Txt(String fileName,String[] titleList,List list , HttpServletResponse response) {
    Date aaa = new Date();
	SimpleDateFormat aSimpleDateFormat = new java.text.SimpleDateFormat(
			"yyyy年MM月dd日hh时mm分");
	SimpleDateFormat aSimpleDateFormat1 = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
	String todayStr=df.format(new Date());
	String today = aSimpleDateFormat.format(aaa);
	String today1 = aSimpleDateFormat1.format(aaa);
	String o_fileName = fileName;
	int mobileIndex = 0 ;
try{
	
	fileName = java.net.URLEncoder.encode(fileName, "UTF-8");//处理中文文件名的问题 
	fileName = new String(fileName.getBytes("UTF-8"),"GBK");//处理中文文件名的问题
	response.setContentType("application/txt;charset=gb18030"); 
	response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"_"+todayStr+ ".txt\"");
	
	PrintWriter out = response.getWriter();
	out.print("\t\t"+"打印日期:"+today+"\r\n");//输出表名
	out.print("\t\t"+o_fileName+"\r\n");//输出表名
	
	
	// 加入字段名
	for(int n = 0;n<titleList.length;n++){
		out.print(titleList[n]+"\t");//输出表的列名
		if(titleList[n].equals("手机号码")){
			mobileIndex = n ;
		}
	}
	out.print("\r\n");
	out.print("\r\n");
	
	StringBuilder XML = new StringBuilder();
	String mobileListStr = "" ;
	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	for(int r=0;r<list.size();r++){
		Object[]  obj    =   (Object[])list.get(r);
		mobileListStr = obj[mobileIndex] == null ? "" : obj[mobileIndex].toString();
		if(mobileListStr.startsWith(",")){
			mobileListStr = mobileListStr.substring(1);
		}
		
		String[] mobileArr = mobileListStr.split(",");
		for(int k = 0 ; k < mobileArr.length; k++){
			
			for(int x = 0;x<titleList.length;x++){
				if(x != mobileIndex)
					out.print(obj[x] == null ? "": obj[x].toString()+"\t");
				else 
					out.print( mobileArr[k]+"\t" );
			}
			out.print("\r\n");
		}
		
	}
	
} catch (Exception e) {
	e.printStackTrace();
	try {
		response.setContentType("text/xml;charset=utf-8");
		response.getWriter().write("<table><info status=\"1\">" + e.toString()+ "</info></table>");
	} catch (Exception ex) {
	}
} 
}
	
	

}

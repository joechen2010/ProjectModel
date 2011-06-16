package com.joe.utilities.common.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class MyFile {
	
	public static final int FILE_WORD = 1;	//以word文件格式打开的常量
	public static final int FILE_EXCEL = 2;	//以excel文件格式打开的常量
	public static final int FILE_TXT = 3;	//以txt文件格式打开的常量
	
	
	private static final Log log = LogFactory.getLog(MyFile.class);
    private String message;
    public MyFile() {
    } 
    /**
     * 读取文本文件内容
     * @param filePathAndName 带有完整绝对路径的文件名
     * @param encoding 文本文件打开的编码方式
     * @return 返回文本文件的内容
     */
    public String readTxt(String filePathAndName,String encoding) throws IOException{
     
    	encoding = encoding.trim();
     StringBuffer str = new StringBuffer("");
     String st = "";
     try{
      FileInputStream fs = new FileInputStream(filePathAndName);
      InputStreamReader isr;
      if(encoding.equals("")){
       isr = new InputStreamReader(fs);
      }else{
       isr = new InputStreamReader(fs,encoding);
      }
      BufferedReader br = new BufferedReader(isr);
      try{
       String data = "";
       while((data = br.readLine())!=null){
         str.append(data+" "); 
       }
      }catch(Exception e){
       str.append(e.toString());
      }
      st = str.toString();
      fs.close();
      isr.close();
     }catch(IOException es){
      st = "";
     }
     return st;     
    }

    /**
     * 新建目录
     * @param folderPath 目录
     * @return 返回目录创建后的路径
     */
    public String createFolder(String folderPath) {
        String txt = folderPath;
        try {
            java.io.File myFilePath = new java.io.File(txt);
            txt = folderPath;
            if (!myFilePath.exists()) {
                myFilePath.mkdir();
            }
        }
        catch (Exception e) {
            message = "创建目录操作出错";
        }
        return txt;
    }
    
    /**
     * 多级目录创建
     * @param folderPath 准备要在本级目录下创建新目录的目录路径 例如 c:myf
     * @param paths 无限级目录参数，各级目录以单数线区分 例如 a|b|c
     * @return 返回创建文件后的路径 例如 c:myfac
     */
    public String createFolders(String folderPath, String paths){
        String txts = folderPath;
        try{
            String txt;
            txts = folderPath;
            StringTokenizer st = new StringTokenizer(paths,"|");
            for(int i=0; st.hasMoreTokens(); i++){
                    txt = st.nextToken().trim();
                    if(txts.lastIndexOf("/")!=-1){ 
                        txts = createFolder(txts+txt);
                    }else{
                        txts = createFolder(txts+txt+"/");    
                    }
            }
       }catch(Exception e){
           message = "创建目录操作出错！";
       }
        return txts;
    }

    
    /**
     * 新建文件
     * @param filePathAndName 文本文件完整绝对路径及文件名
     * @param fileContent 文本文件内容
     * @return
     */
    public void createFile(String filePathAndName) {
    	
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
            	log.info("开始创建文件..");
                myFilePath.createNewFile();
            }else {
            	log.info("文件已存在..");
			}
        }
        catch (Exception e) {
            message = "创建文件操作出错";
        }
    }

    
    /**
     * 新建文件
     * @param filePathAndName 文本文件完整绝对路径及文件名
     * @param fileContent 文本文件内容
     * @return
     */
    public void createFile(String filePathAndName, String fileContent) {
     
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            String strContent = fileContent;
            myFile.println(strContent);
            myFile.close();
            resultFile.close();
        }
        catch (Exception e) {
            message = "创建文件操作出错";
        }
    }


    /**
     * 有编码方式的文件创建
     * @param filePathAndName 文本文件完整绝对路径及文件名
     * @param fileContent 文本文件内容
     * @param encoding 编码方式 例如 GBK 或者 UTF-8
     * @return
     */
    public void createFile(String filePathAndName, String fileContent, String encoding) {
     
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            PrintWriter myFile = new PrintWriter(myFilePath,encoding);
            String strContent = fileContent;
            myFile.println(strContent);
            myFile.close();
        }
        catch (Exception e) {
            message = "创建文件操作出错";
        }
    } 


    /**
     * 删除文件
     * @param filePathAndName 文本文件完整绝对路径及文件名
     * @return Boolean 成功删除返回true遭遇异常返回false
     */
    public boolean delFile(String filePathAndName) {
     boolean bea = false;
        try {
            String filePath = filePathAndName;
            File myDelFile = new File(filePath);
            if(myDelFile.exists()){
             myDelFile.delete();
             bea = true;
            }else{
             bea = false;
             message = (filePathAndName+"<br>删除文件操作出错");
            }
        }
        catch (Exception e) {
            message = e.toString();
        }
        return bea;
    }
    


    /**
     * 删除文件夹
     * @param folderPath 文件夹完整绝对路径
     * @return
     */
    public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        }
        catch (Exception e) {
            message = ("删除文件夹操作出错");
        }
    }
    
    
    /**
     * 删除指定文件夹下所有文件
     * @param path 文件夹完整绝对路径
     * @return
     * @return
     */
    public boolean delAllFile(String path) {
     boolean bea = false;
        File file = new File(path);
        if (!file.exists()) {
            return bea;
        }
        if (!file.isDirectory()) {
            return bea;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            }else{
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path+"/"+ tempList[i]);//先删除文件夹里面的文件
                delFolder(path+"/"+ tempList[i]);//再删除空文件夹
                bea = true;
            }
        }
        return bea;
    }


    /**
     * 复制单个文件
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名
     * @return
     */
    public void copyFile(String oldPathFile, String newPathFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPathFile);
                byte[] buffer = new byte[1444];
                while((byteread = inStream.read(buffer)) != -1){
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }catch (Exception e) {
            message = ("复制单个文件操作出错");
        }
    }
    

    /**
     * 复制整个文件夹的内容
     * @param oldPath 准备拷贝的目录
     * @param newPath 指定绝对路径的新目录
     * @return
     */
    public void copyFolder(String oldPath, String newPath) {
        try {
            new File(newPath).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }else{
                    temp=new File(oldPath+File.separator+file[i]);
                }
                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                    (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }catch (Exception e) {
            message = "复制整个文件夹内容操作出错";
        }
    }


    /**
     * 移动文件
     * @param oldPath
     * @param newPath
     * @return
     */
    public void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }
    

    /**
     * 移动目录
     * @param oldPath
     * @param newPath
     * @return
     */
    public void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }
    public String getMessage(){
        return this.message;
    }
    
    

    public static void readline(String file,String writerfile){
        try{
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        
        FileWriter fw = new FileWriter(writerfile); //写文件操作，把得到的file对应的文件中内容写入，writerfile中去。
        BufferedWriter bw = new BufferedWriter(fw);
        String readoneline;
        int l;
        while((l = br.read()) != -1){
            readoneline = br.readLine();
            bw.write(readoneline);
            bw.newLine();
            System.out.println(readoneline);
        }
        bw.flush();
        br.close();
        bw.close();
        br.close();
        fw.close();
        fr.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    public static void readfile(String file){
    	         try{
    	        	 FileReader reader = new FileReader(file);
    	        	  BufferedReader br = new BufferedReader(reader);
    	        	  String s1 = null;
    	        	  while((s1 = br.readLine()) != null) {
    	        		  System.out.println(s1);
    	        	  }
    	        	 br.close();
    	        	 reader.close();
    	        }catch(IOException e){
    	            e.printStackTrace();
    	  }
    }

   
    /**
     * 追加内容到文件末尾
     * @param oldPath
     * @param newPath
     * @return
     */
    
    public static void writeData(String filePath, String content) {   
    	log.info("开始追加内容..");
    	try {  
	       FileWriter   fw   =   new   FileWriter(filePath,true);   
	       BufferedWriter   bw   =   new   BufferedWriter(fw); 
	       bw.newLine(); 
	       bw.write(content); 
	       bw.flush();   
	       fw.close();   
        } catch (IOException e) {   
        	log.info("追加内容失败..");
            e.printStackTrace();   
        }   

    }   

    
    public  void writeLog(String msgtype,String serviceid,String content){
    	log.info("写入txt开始。。。");
    	try {
    		MyFile myFile = new MyFile();
    		String url = this.getClass().getResource("").getPath().replaceAll("%20", " ");   
    	    String path = url.substring(0, url.indexOf("WEB-INF"));
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		String fileName = path+msgtype+"log/"+sdf.format(new Date())+"-"+serviceid+".txt";
    		myFile.createFile(fileName);
    		myFile.writeData(fileName, content);
		} catch (Exception e) {
			log.info("写入txt发生错误。。。");
			log.info(e.toString());
		}
    	
    }
    
    /*
     * 列出目录所有文件名
     * 
     * */
    
    
    public static List getFileList(String folderPath){
		 
		 List fileList = new ArrayList();
		 try{
			  File dir = new File(folderPath);
			  String[] fs = dir.list();
			  
			  if(fs==null || fs.length==0){
				  return null;
			  }
			  for (int i = 0; i < fs.length; i++){
				  fileList.add(fs[i]);
		  	  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return fileList;
	 }
 
    
    /**
	 * 此方法是按照文档格式打开文件
	 * @param fileType
	 * @param path
	 */
	public static void openFile(int fileType,String path){
		if (fileType == MyFile.FILE_EXCEL){
			try {
				Runtime.getRuntime().exec("cmd /c start excel "+path);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		} else if (fileType == MyFile.FILE_TXT){
			try {
				Runtime.getRuntime().exec("notepad "+path);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
    
    
    public static void main(String [] args){
    	MyFile myFile = new MyFile();
    	String pathString = "d:\\dd.txt";
    	myFile.createFile(pathString);
    	myFile.writeData(pathString, "f>>>888>>>sfjsdlfjl");
    }
    
    
}






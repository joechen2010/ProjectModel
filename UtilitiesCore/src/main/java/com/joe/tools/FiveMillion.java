package com.joe.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.joe.utilities.common.util.ReadURL;


public class FiveMillion 
{
	final static String[] all = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33"};
	final static String[] blue = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16"};
	//String nameStr = "新浪专家大尉,温柔刀客,东方花琦";
	//String nameStr = ",赵蕾,旋木,阿勇,彩民周刊专家";
	String nameStr = "专家小宋,风行者,云网智者,华彩朋哥,欧阳纤,华彩老田";
	//String nameStr = "华彩朋哥,欧阳纤,华彩老田";
	//String nameStr = "云网智者,筱杉,黑蝴蝶,深圳晶报,阿勇,彩民周刊专家";
	//String nameStr = "温柔刀客,新浪专家大尉,筱杉,黑蝴蝶,深圳晶报,东方花琦,阿勇,彩民周刊专家";
	//String nameStr = "深圳晶报,东方花琦,阿勇,彩民周刊专家";
	List<String> totalAnd = new ArrayList<String>();
	private Map<String,Integer> allMap = new HashMap<String,Integer>();
	private Map<String,Integer> allBlueMap = new HashMap<String,Integer>();
	private List<Forecast> fList = new ArrayList<Forecast>();
	private List<Forecast> allList = new ArrayList<Forecast>();
	private Map<String,Forecast> forecastMap = new HashMap<String,Forecast>();
	private Map<String,Integer> rightNumMap = new HashMap<String,Integer>();
	private String andOr500 = "";
	private String num = "";
	private String andOrOr500 = "";
	private String andOr = "";
	
	
    public static void main( String[] args )
    {
    	
    	  FiveMillion m = new FiveMillion();
    	  
			while(true){
		  	  m.allMap.clear();
		  	  for(String s : all){
		  		  m.allMap.put(s, 0);	
			  }
		  	  m.allBlueMap.clear();
		  	  for(String s : blue){
		  		  m.allBlueMap.put(s, 0);	
			  }
		  	  System.out.println();
		  	  m.handleNum();
		  	  m.in();
		    }
		
    }
    
    private  void in(){
    	try {
     	   System.out.println("请选择方式：");
     	   System.out.println("1.取号码");
     	   System.out.println("2.集合操作");
     	   System.out.println("3.自动");
     	   System.out.println("4.相交次数");
     	   System.out.println("5.分析");
     	   BufferedReader in = new BufferedReader(
     			   new InputStreamReader(System.in));
     	   String inStr = in.readLine();
     	   if(inStr.equals("1")){
     		   getNetData();
     	   }else if(inStr.equals("2")){
     		   control();
     	   }else if(inStr.equals("3")){
     		   getMy500Wan();
     	   }else if(inStr.equals("4")){
     		   printAndCount();
     	   }else if(inStr.equals("5")){
     		  doInForecast();
     	   }
 	    	
 		} catch (IOException e) {
 			in();
 		}      
    }
    
    private void handleNum(){
    	System.out.println("请输入期数：");
	   	BufferedReader in2 = new BufferedReader( new InputStreamReader(System.in));
	   	 String input;
		try {
			input = in2.readLine();
			num = input;
			List<String> from500 = get500AndOr(num);
			andOr500 = listToString(from500);
		}catch(Exception e){}
    }
    
    private  List<Forecast> removedForecasts() throws IOException{
    	List<Forecast> removedForecasts = getForecastList(num);
    	populateForecastMap(removedForecasts);
    	Iterator<Forecast> it = removedForecasts.iterator(); 
    	while(it.hasNext()){ 
    		if(!(nameStr.indexOf(it.next().getName()) > -1)){
    	     it.remove(); 
    	   } 
    	}
    	if(this.fList.size() <= 0){
    		this.fList.addAll(removedForecasts);
    	}
    	return removedForecasts;
    }
    
    private void populateForecastMap(List<Forecast> allForecast){
    	this.allList.addAll(allForecast);
    	for(Forecast f : allForecast){
    		forecastMap.put(f.getName(), f);
    	}
    }
    
    private void doAnd500(){
    	for(Forecast f : this.fList){
    		String and = listToString(and(f.getRed(), andOr500));
    		f.setAnd500(and);
    	}
    }
    
    private void doAndOrOr(){
    	for(Forecast f : this.fList) {
    		andOrOr500 = or(andOrOr500,f.getAnd500());
    	}
    }
    
    private String or(String or1, String or2){
    	//or1 = or1.replaceAll(",", " ");
    	or2 = or2.replaceAll(",", " ");
    	if(or2.startsWith(" ")) or2 = or2.substring(1);
    	for(String o : or2.split(" ")){
    		if(!or1.contains(o)){
    			or1 += " "+o;
    		}
    	}
    	return or1;
    }
    
    
    private void doAnd500And(){
    	for(Forecast f : this.fList) {
    		for(Forecast o : this.fList) {
        		if(!f.getName().equals(o.getName())){
        			f.getAnd500AndList().add(o.getName()+" : " +and(f.getAnd500(),o.getAnd500()).toString());
        			f.getAnd500AndDataList().add(and(f.getAnd500(),o.getAnd500()).toString());
        		}
        	}
    	}
    }
    
    private String doA500AndOr(){
    	String s = "";
    	for(Forecast f : this.fList) {
    		for(String o : f.getAnd500AndDataList()){
    			o = o.replaceAll("\\[", "").replaceAll("\\]", "");
    			if(o.contains(",")){
    				for(String n : o.split(",")){
    					s += n.replaceAll(" ", "")+","; 
    				}
    			}
    		}
    	}
    	return s;
    }
    
    private String notIn(String all, String sub){
    	String str = "";
    	all = all.replaceAll(",", " ");
    	sub = sub.replaceAll(",", " ");
    	for(String s : all.split(" ")){
    		s = s.replaceAll(" ", "");
    		if(!sub.contains((s))){
    			str += s+" ";
    		}
    	}
    	return str;
    }
    
    private void doAndWithOthers(){
    	for(Forecast f : this.fList) {
    		for(Forecast o : this.fList) {
    			if(!f.getName().equals(o.getName())){
    				List<String> list = and(f.getRed(),o.getRed());
        			f.getAndOtherDataList().add(listToString(list));
        			f.getAndOtherList().add(o.getName()+" : " +list.toString());
        			andOr += listToString(list)+" ";
    			}
        	}
    	}
    	andOr = or(andOr,"");
    }
    
    private void doCalBlue(){
    	for(Forecast o : this.fList) {
    		for(String s : o.getBlue().split(",")){
    			allBlueMap.put(s, allBlueMap.get(s)+1);
    		}
    	}
    }
    
    private void doInForecast() throws IOException{
    	removedForecasts();
    	doAnd500();
    	System.out.println("500交并: "+(andOr500.split(" ")).length +"个>  "+andOr500);
    	doAndOrOr();
    	List<String> list =stringToListAndSort(andOrOr500);
    	System.out.println("500交集后并集"+list.size()+"个>" +listToString(list));
    	doAnd500And();
    	String andAndOr = doA500AndOr();
    	List<String> l2 = stringToListAndSort(andAndOr);
    	System.out.println("与500W交集后两两交集的并集"+l2.size()+"个>");
    	outPrint(l2);
    	System.out.println();
    	doAndWithOthers();
    	List l = stringToListAndSort(andOr);
    	System.out.println("内部两两交集的并集"+l.size()+"个> ");
    	System.out.println(listToString(l));
    	System.out.println("=================内部两两交集的并集与500W的交集=================");
    	List<String> andOrAnd500 = and(andOr,andOr500);
    	Collections.sort(andOrAnd500);
    	System.out.println("内部两两交集的并集与500W的交集，共"+andOrAnd500.size()+"> ");
    	System.out.println(listToString(andOrAnd500));
    	System.out.println("内部两两交集的并集与500W的差集> ");
    	System.out.println(stringToListAndSort(notIn(andOr,andOr500)));
    	doCalBlue();
    	
    	System.out.println("=======================与500W相交集========================");
    	for(Forecast f : fList){
    		System.out.println();
    		System.out.println(f.getName()+" : " +f.getAnd500());
    	}
    	System.out.println("=======================与500W交集后的相交========================");
    	for(Forecast f : fList){
    		System.out.println();
    		System.out.println(f.getName()+" : " +f.getAnd500AndList().toString());
    	}
    	System.out.println("=======================与两两相交(不与500Wan交集)========================");
    	for(Forecast f : fList){
    		System.out.println();
    		System.out.println(f.getName()+" : " +f.getAndOtherList().toString());
    	}
    	System.out.println("=======================蓝球统计========================");
    	for(String o : allBlueMap.keySet()){
    		System.out.print(o +" : "+allBlueMap.get(o)+" ");
    	}
    	writeFinalData();
    }
    
   
    private List<String> stringToListAndSort(String str){
    	List<String> l = new ArrayList<String>();
    	str = str.replaceAll(",", " ");
    	if(str.startsWith(" ")) str = str.substring(1);
    	List<String> list = Arrays.asList(str.split(" "));
    	for(String o : list ){
    		if(!l.contains(o)){
    			l.add(o);
    		}
    	}
    	Collections.sort(l);
    	return l;
    }
    
    
    
    private String listToString(List<String> list){
    	return list.toString().replace("[", "").replace("]", "");
    }
    
    private Map<Forecast,Map<String,Integer>> getAndCountMap (String num) throws IOException{
    	Map<Forecast,List<List<String>>> andMap = getAndListMap(num);
    	Map<Forecast,Map<String,Integer>> totalNumCountMap = new HashMap<Forecast,Map<String,Integer>>();
    	for(Forecast f : andMap.keySet()){
    		List<List<String>> andList = andMap.get(f);
    		Map<String,Integer> numCountMap = new HashMap<String,Integer>();
    		numCountMap.clear();
    		for(List<String> l : andList){
    			for(String s : l){
    				int originCount = numCountMap.get(s) == null ? 0 :  numCountMap.get(s);
    				numCountMap.put(s, originCount+1);
    			}
    		}
    		totalNumCountMap.put(f, sortByValue(numCountMap));
    	}
    	return totalNumCountMap;
    }
    
    
    private Map<Forecast,List<List<String>>>  getAndListMap(String num) throws IOException{
    	List<Forecast> fList = getForecastList(num);
    	Map<Forecast,List<List<String>>> andCountMap = new HashMap<Forecast,List<List<String>>>();
    	for(Forecast f : fList){
    		List<List<String>> andList = new ArrayList<List<String>>();
    		for(Forecast another : fList){
        		if(!f.equals(another)){
        			List<String> and = and(f.getRed(), another.getRed());
        			andList.add(and);
        		}
        	}
    		andCountMap.put(f, andList);
    	}
    	return andCountMap;
    }
    
    private List<String> and(String and1, String and2){
    	List<String> andList = new ArrayList<String>();
    	and1 = and1.replaceAll(" ", "").replaceAll(",", " ");
    	and2 = and2.replaceAll(" ", "").replaceAll(",", " ");
    	for(String o : and1.split(" ")){
    		if(and2.indexOf(o) > -1 && !andList.contains(o)){
    			andList.add(o);
    		}
    	}
    	return andList;
    }
    
    
    private void destroy(FiveMillion m){
		nameStr = null;
		totalAnd.clear();
		allMap.clear();
	}
    
    
    public static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());

            }
        });
        Map result = new LinkedHashMap();

        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    private void printAndCount(){
    	System.out.println("请输入期数：");
	   	BufferedReader in2 = new BufferedReader( new InputStreamReader(System.in));
	   	 String input;
		try {
			input = in2.readLine();
			Map<Forecast,Map<String,Integer>> map = getAndCountMap(input);
			for(Forecast f : map.keySet()){
				Map<String,Integer> countMap = map.get(f);
				System.out.print(f.getName()+"	");
				for(String s : countMap.keySet()){
					System.out.print(s+" : "+countMap.get(s)+"	");
				}
				System.out.println();
			}
		}catch(Exception e){
			
		}
    }
    
    private void getMy500Wan(){
    	System.out.println("请输入期数：");
	   	BufferedReader in2 = new BufferedReader( new InputStreamReader(System.in));
	   	 String input;
		try {
			input = in2.readLine();
			String[] controlKeys = getControlKey(input);
			totalAnd.clear();
			List<String> from500 = get500AndOr(num);
	    	List<String> allList = allForcecastList(num);
	    	System.out.print("1. 500交并 与 研究所交并 交集结果：   ");
	    	List<String> l = new ArrayList<String>();
	    	l.add(getAllNumStr(allList));
	    	l.add(getAllNumStr(from500));
	    	doAnd(l,100,null);
	    	
	    	System.out.print("2. 所有交集结果：   ");
	    	doAnd(totalAnd,100,null);
	    	
	    	Integer perCount = (controlKeys[4] == null || "".equals(controlKeys[4])) ? totalAnd.size() : Integer.parseInt(controlKeys[4]);
	    	doCaculate(totalAnd,perCount,null);
	    	
	    	doCaculate(getAllForecast(controlKeys[0]),perCount,from500);
		}catch(Exception e){
			
		}
    	
    }
    
     private List<String> getAllForecast(String num) throws IOException{
    	 List<Forecast> fList = getForecastList(num);
    	 List<String> resultList = new ArrayList<String>();
    	 for(Forecast f : fList){
    		 resultList.add(f.getRed());
    	 }
    	 return resultList;
     }
    
    
    private void doCaculate(List<String> list, Integer pageSize, List<String> toAndList){
    	
    	List<List<String>> totalAndPageList = getListByPage(list,pageSize);
    	System.out.print("======================取交集结果============================== ");
    	for(List<String> singleList : totalAndPageList){
    		doAnd(singleList,100,toAndList);
    	}
    	System.out.print("======================取并集结果============================== ");
    	for(List<String> singleList : totalAndPageList){
    		doOr(singleList,toAndList);
    	}
    	System.out.print("=========================取交并结果================================ ");
    	doAndOr(list,pageSize,toAndList);
    }
    
    private List<List<String>> doAndOr(List<String> list, Integer pageSize, List<String> toAndList){
    	List<List<String>> resultList = new ArrayList<List<String>>();
    	for(int i = 0 ; i < list.size(); i++){
    		String s = list.get(i);
    		s = s.replaceAll(",", " ");
    		resultList.add(Arrays.asList(s.split(" ")));
    		if(i%(pageSize-1) == 0){
	   			for(String n : getAndOrList(resultList)){
	   				System.out.print(n +" ");
	   			}
	   			resultList.clear();
	   			System.out.println();
   		 	}
    	}
    	return resultList;
    }
    
    private List<List<String>> getListByPage(List<String> list, Integer pageSize){
    	List<String> tempList = new ArrayList<String>();
    	List<List<String>> resultList = new ArrayList<List<String>>();
    	for(int i = 0 ; i < list.size(); i++){
    		tempList.add(list.get(i));
    		 if(i%(pageSize-1) == 0){
    			 resultList.add(tempList);
    			 tempList.clear();
    		 }
    	}
    	return resultList;
    }
    
    private String[] getControlKey(String num){
    	return num.split(",");
    }
    
    private  void control(){
    	try{
    		String input = "";
    		while(true){
    			List<String> numList = new ArrayList<String>();
    			numList.clear();
    			System.out.println();
	    		System.out.println("请选择操作：");
	    	    System.out.println("1.交集");
	    	    System.out.println("2.并集");
	    	    System.out.println("3.差集");
	    	    System.out.println("4.返回上级");
	    	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	       	 	input = in.readLine();
	       	 	if("4".equals(input)){
	       	 		break;
	       	 	}
	       	 	System.out.println("输入号码：");
	    	 	BufferedReader num = new BufferedReader(new InputStreamReader(System.in));
	    	 	String str = num.readLine();
	    	 	str = str.replaceAll(",", " ");
	    	 	str = str.replace("#", " #");
	    	 	numList = Arrays.asList(str.split("#"));
	       	 	if(input.equals("2")){
	       	 		doOr(numList,null);
	       	 	}else if(input.equals("1")){
		       	 	System.out.println("输入容错范围");
		       	 	BufferedReader range = new BufferedReader(new InputStreamReader(System.in));
		       	 	String rangStr = range.readLine();
		       	 	Integer intRange = rangStr .equals("") ? 100 : Integer.parseInt(rangStr);
		       	 	doAnd(numList,intRange,null);
	       	 	}else if(input.equals("3")){
		       	 	List<String> list1 = Arrays.asList(numList.get(0).split(" "));
		       	 	List<String> list2 = Arrays.asList(numList.get(1).split(" "));
	       	 		list1.removeAll(list2);
	       	 		outPrint(list1);
	       	 	}
    		}
    	}catch(IOException e){
    		in();
    	}
    }
   
    private List<String> doOr(List<String> numList, List<String> toAndList){
    	List<String> l = new ArrayList<String>();
	 		String totalStr = "";
   	 	for(String s : numList){
   	 		totalStr += s+" ";
	 	}
   	 	List<String> totalList = Arrays.asList(totalStr.split(" "));
	 		Collections.sort(totalList);
	 		System.out.print("结果：");
   	 	 for(String n : totalList){
   	 		if(!l.contains(n)){
   	 			if(toAndList == null){
   	 				System.out.print(n +" ");
   	 			}
   	 			l.add(n);
   	 		}
	     }
   	 	System.out.println();
   	 	 if(toAndList!= null){
   	 		and(l,toAndList);
   	 	 }
   	 	 return l;
    }
    
    private void and(List<String> andList1, List<String> andList2){
    	for(String n : andList1){
			 if(andList2.contains(n)){
				System.out.print(n +" ");
			 }
	 	}
    }
    
    private void doAnd(List<String> numList, Integer intRange, List<String> toAndList){
    	for(String s : all){
	 			for(String nStr : numList){
	 				if(nStr.indexOf(s) > -1){
	 					Integer n = allMap.get(s);
	 					allMap.put(s,n+1);
	 				}
	 			}
	 		}
		List<String> l = new ArrayList<String>();
   	 	for(String o : allMap.keySet()){
   	 		if(allMap.get(o)/ numList.size()*100 >= intRange){
   	 			l.add(o);
   	 		}
   	 		allMap.put(o, 0);
   	    }
	 		Collections.sort(l);
	 		if(toAndList == null){
	 			outPrint(l);
	 		}else{
	 			and(l,toAndList);
	 		}
	 		
    }
    
    private void getNetData(){
    	
    	System.out.println("请输入期数：");
    	 BufferedReader in2 = new BufferedReader(
    			   new InputStreamReader(System.in));
    	String num;
		try {
			num = in2.readLine();
	    	System.out.println("--------------500Wan---------------");
	    	String[] controlKeys = getControlKey(num);
	    	getDataFrom500Wan(controlKeys[0]);
	    	
	    	System.out.println();
	    	System.out.println("-----------研究所-----------");
	    	handleForcecastData(controlKeys[0]);
		   
		} catch (IOException e) {
			in();
		} 
    }

    private List<String> allForcecastList(String num) throws IOException{
    	Integer start = 0;
    	Integer end = null;
    	boolean primary = false;
    	String[] controlKeys = getControlKey(num);
    	primary = "1".equals(controlKeys[1]);
    	start = Integer.parseInt(controlKeys[2]);
		end = Integer.parseInt(controlKeys[3]);
    	List<Forecast> fList = getForecastList(controlKeys[0]);
    	List<String> allList = getAnd_Or(fList,start,end,primary);
    	return allList;
    }
    
    private void handleForcecastData(String num) throws IOException{
    	handleNetData(allForcecastList(num),null);
    }
    
    
    
    private void getDataFrom500Wan(String num) throws IOException{
		handleNetData(get500AndOr(num), null);
    }
    
    private List<String> get500AndOr(String num){
    	List<String> numStrList = new ArrayList<String>();
		try {
			numStrList = getHtmlTextList(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> allList = new ArrayList<String>();
		for(String numStr : numStrList){
			numStr = numStr.replaceAll(",", " ");
			list.add(Arrays.asList(numStr.split(" ")));
			allList.addAll(Arrays.asList(numStr.split(" ")));
		}
		List<String> result = getAndOrList(list);
		return result;
    }
    
    private void handleNetData(List<String> result, List<String> allList){
    	
		   System.out.println("..........交并....................................");
	       for(String n : result){
	    	   System.out.print(n +" ");
	       }
	       System.out.println();
	       if(allList != null && allList.size()>0){
	    	   allList.removeAll(result);
	    	   System.out.println("...........不交并...................................");
		       for(String n : allList){
		    	   System.out.print(n +" ");
		       }
	       }
	      
	       System.out.println();
	       System.out.println();
	       String _23ArrayStr = "01 02 03 04 05 06 07 08 10 13 16 17 18 21 22 23 24 26 27 28 29 30";
	       System.out.println("与23码("+_23ArrayStr+")交集:");
	       List<String> _23List = Arrays.asList(_23ArrayStr.split(" "));
	       for(String nn : result){
	    	   if(_23List.contains(nn))
	    		   System.out.print(nn +" ");
	       }
    }
    
	private List<String> getHtmlTextList(String dateNum) throws IOException{
		List<String> result = new ArrayList<String>();
    	final String url = "http://zx.500wan.com/ssq/mediayc.php?expect="+dateNum;
    	String allHtml = ReadURL.getOneHtml(url);
    	String startTag = "<table id=\"curr_tb_s\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"pub_table\">";
    	String tableHtml = ReadURL.getTagContent(allHtml, startTag, "</table>", null).get(0).toString();
    	List<String> trList = ReadURL.getTagContent(tableHtml, "<td class=\"text_l\">", "</tr>", null);
    	for(String s : trList){
    		String numHtml = ReadURL.getTagContent(s, "<td class=\"num\">", "</td>",true).get(0);
    		String numStr = ReadURL.outTag(numHtml);
    		result.add(numStr);
    		//System.out.println(numStr);
    	}
    	return result;
    }
	
	private List<Forecast> getForecastList(String dateNum) throws IOException{
		List<Forecast> result = new ArrayList<Forecast>();
		final String url = "http://www.cpyjy.com/shuangseqiu/"+dateNum+"/";
		String allHtml = ReadURL.getOneHtml(url);
		String startTag = "<table width=\"100%\" border=\"1\"";
		String tableHtml = ReadURL.getTagContent(allHtml, startTag, "</table>", true).get(0).toString();
		List<String> trList = ReadURL.getTagContent(tableHtml, "<tr>", "</tr>", null);
		for(String s : trList){
    		String nameStr = ReadURL.getTagContent(s, "<td style=\"font-size:12px\">", "</td>",true).get(0);
    		String redStr = ReadURL.getTagContent(s, "<font color=red>", "</font>",true).get(0);
    		String blueStr = ReadURL.getTagContent(s, "<font color=blue>", "</font>",true).get(0);
    		String name = ReadURL.outTag(nameStr);
    		String red = ReadURL.outTag(redStr.replaceAll(" ", ""));
    		String blue = ReadURL.outTag(blueStr.replaceAll(" ", ""));
    		Forecast f = new Forecast(name, red, blue);
    		result.add(f);
    		/*System.out.println(name+" "+red+" "+blue);
    		System.out.println();
    		System.out.println();*/
    	}
		System.out.println("总共 "+result.size()+" 条记录");
		return result;
	}
	
	private List<String> getAnd_Or(List<Forecast> list, Integer start, Integer end, boolean primary){
		if(start == null || end == null || start <0 || end > list.size()){
			start = 0;
			end = list.size();
		}
		List<List<String>> templist = new ArrayList<List<String>>();
		for(int i = 0 ; i< list.size();i++){
			Forecast f = list.get(i);
			List<String> redList = Arrays.asList(f.getRed().split(","));
			if(primary && nameStr.indexOf(f.getName())> -1){
				templist.add(redList);
			}else if (start <= i && end >= i ){
				templist.add(redList);
			}
		}
		return getAndOrList(templist);
	}
	 
    
    
    private List<String> getAndOrList(List<List<String>> list){
    	List<String> result = new ArrayList<String>();
    	String andStr = null;
        for(int i = 0 ; i < list.size() ; i++){
        	int j = 0;
        	List<String> ar_current = (List<String>)list.get(i);
     	   	for(j=i+1; j<list.size();j++){
     		   List<String> ar_next = (List<String>)list.get(j);
     		   andStr = "";
         	   for(String n : ar_current){
         		   if(ar_next.contains(n)){
         			   if(!result.contains(n)){
         				  result.add(n);
         			   }
         			  andStr += n+" ";
         		   }
         	   }
         	  totalAnd.add(andStr);
     	   }
     	   
        }
        Collections.sort(result);
        return result;
    }
    
    private static void outPrint(List<String> list){
    	for(String n : list){
   	 		System.out.print(n +" ");
	     }
    }
    
    private String getAllNumStr(List<String> list){
    	String str = "";
    	for(String s : list){
    		str += s;
    	}
    	return str;
    }
    
    public static List<String> writeToDat(String path) {
    	  File file = new File(path);
    	  List<String> list = new ArrayList();
    	  double[] nums = null;
    	  try {
    	   BufferedReader bw = new BufferedReader(new FileReader(file));
    	   String line = null;
    	   while((line = bw.readLine()) != null){
    	    list.add(line);
    	   }
    	   bw.close();
    	  } catch (IOException e) {
    	   e.printStackTrace();
    	  }
    	 return list;
    }
    
    public List<String> guoLv(){
    	List<String> list = writeToDat("e://myfile//1.txt");
    	List<String> result = new ArrayList<String>();
    	List<String> finalResult = new ArrayList<String>();
    	System.out.println();
    	System.out.println("请输入: 最大个数/最小个数/胆码/杀码/上期号码/遗传最大个数/遗传最小个数/专家:个数;专家:个数");
	   	BufferedReader in2 = new BufferedReader( new InputStreamReader(System.in));
		try {
			String input = in2.readLine();
			String[] arg = input.split("/");
	    	for(String s : list){
	    		boolean flag = false;
	    		for(int i = 0 ; i< fList.size() && !flag ; i++){
	    			Forecast f = fList.get(i);
	    			int sum = getCount(f.getRed(),s);
	    			if( (sum >=Integer.parseInt(arg[0]) || sum<Integer.parseInt(arg[1]))){
	    				flag = true;
	    			}
	    		}
	    		//遗传
	    		int n = getCount(arg[4],s);
    			if(n>Integer.parseInt(arg[6]) && n<Integer.parseInt(arg[5])){
    				flag = true;
    			}
    			//胆 5/2/ / /01,11,17,18,27,31/1/1/旋木:2;黑蝴蝶:2;东方花琦:2;赵蕾:2;
    			if(arg[2] != null && !"".equals(arg[2].trim())){
	    			for(String d : arg[2].split(",")){
	    				if(s.indexOf(d)<=-1){
	    					flag = true;
	    					break;
	    				}
	    			}
	    		}
    			//杀
	    		if(arg[3] != null && !"".equals(arg[3].trim())){
	    			for(String d : arg[3].split(",")){
	    				if(s.indexOf(d)>-1){
	    					flag = true;
	    					break;
	    				}
	    			}
	    		}
    			for(String d : arg[7].split(";")){
    				String[] rightNumStr = d.split(":");
    				rightNumMap.put(rightNumStr[0], Integer.parseInt(rightNumStr[1]));
    				if(getCount(forecastMap.get(rightNumStr[0]).getRed(),s) < rightNumMap.get(rightNumStr[0])){
    					flag = true;
    					break;
    				}
    			}
	    		if(!flag){
	    			result.add(s);
	    		}
	    	}
		}catch(Exception e){}
    	return result;
    }
    
    
    public int getCount(String f, String s){
    	int i = 0;
    	for(String a : s.split(",")){
    		if(f.indexOf(a)>-1){
    			i++;
    		}
    	}
    	return i;
    }
    
    public void writeFinalData(){
    	for(String s : guoLv()){
    		appendMethodA("e://myfile//500.txt",s);
    	}
    }
    
    public static void appendMethodA(String fileName,  
    		 
    		String content){  
    		try {  
    		RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");  
    		long fileLength = randomFile.length();  
    		randomFile.seek(fileLength);  
    		randomFile.writeBytes(content); 
    		randomFile.writeBytes("\r\n");
    		randomFile.close();  
    		} catch (IOException e){  
    		e.printStackTrace();  
    		}  
    }
}

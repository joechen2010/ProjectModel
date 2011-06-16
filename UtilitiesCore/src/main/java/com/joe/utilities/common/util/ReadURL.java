package com.joe.utilities.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadURL {
	
	
	 public static String getOneHtml(final String htmlurl) throws IOException
	 {
	  URL url;
	  String temp;
	  final StringBuffer sb = new StringBuffer();
	  try
	  {
	   url = new URL(htmlurl);
	   final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "gb2312"));
	   while ((temp = in.readLine()) != null)
	   {
	    sb.append(temp);
	   }
	   in.close();
	  }
	  catch (final MalformedURLException me)
	  {
	   
	   me.getMessage();
	   throw me;
	  }
	  catch (final IOException e)
	  {
	   e.printStackTrace();
	   throw e;
	  }
	  return sb.toString();
	 }
	 
	 
	 public static String getTitle(final String s)
	 {
	  String regex;
	  String title = "";
	  final List<String> list = new ArrayList<String>();
	  regex = "<title>.*?</title>";
	  final Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
	  final Matcher ma = pa.matcher(s);
	  while (ma.find())
	  {
	   list.add(ma.group());
	  }
	  for (int i = 0; i < list.size(); i++)
	  {
	   title = title + list.get(i);
	  }
	  return outTag(title);
	 }
	 
	 
	 
	 public static List<String> getTagContent(final String html, String startTag, String endTag, Boolean returnOne)
	 {
	  
	  String regex;
	  final List<String> list = new ArrayList<String>();
	  regex = startTag+".*?"+endTag;
	  final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
	  final Matcher ma = pa.matcher(html);
	  while (ma.find())
	  {
		  list.add(ma.group());
		  if(returnOne != null && returnOne)
			  break;
	  }
	  if(list.size() <= 0)
		  list.add("");
	  
	  return  list;
	 }

	
	 public static List<String> getLink(final String s)
	 {
	  String regex;
	  final List<String> list = new ArrayList<String>();
	  regex = "<a[^>]*href=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)</a>";
	  final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
	  final Matcher ma = pa.matcher(s);
	  while (ma.find())
	  {
	   list.add(ma.group());
	  }
	  return list;
	 }

	 
	 public static List<String> getScript(final String s)
	 {
	  String regex;
	  final List<String> list = new ArrayList<String>();
	  regex = "<script.*?</script>";
	  final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
	  final Matcher ma = pa.matcher(s);
	  while (ma.find())
	  {
	   list.add(ma.group());
	  }
	  return list;
	 }

	 
	 public static List<String> getCSS(final String s)
	 {
	  String regex;
	  final List<String> list = new ArrayList<String>();
	  regex = "<style.*?</style>";
	  final Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
	  final Matcher ma = pa.matcher(s);
	  while (ma.find())
	  {
	   list.add(ma.group());
	  }
	  return list;
	 }

	 
	 public static String outTag(final String s)
	 {
	  return s.replaceAll("<.*?>", "");
	 }
	 

}

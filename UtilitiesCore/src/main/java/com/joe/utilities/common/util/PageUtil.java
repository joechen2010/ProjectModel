package com.joe.utilities.common.util;

/**
 * ibatis分页Bean
 * @author Administrator
 *
 */
public class PageUtil {

 private int curPage = 0; //当前页
 private int pageSize = 0; //每页多少行
 private int endSize ; //用于not in(select top endSize id)不在多少行内
 private int totalRow ; //共多少行
 private int totalPage ; //共多少页
 
 public int getCurPage() {
  return curPage;
 }
 public void setCurPage(int curPage) {
  
  int temp = pageSize * (curPage-1);
  this.setEndSize(temp);
  this.curPage = curPage;
 }
 public int getEndSize() {
  return endSize;
 }
 public void setEndSize(int endSize) {
  this.endSize = endSize;
 }
 public int getPageSize() {
  return pageSize;
 }
 public void setPageSize(int pageSize) {
  this.pageSize = pageSize;
 }
 public int getTotalRow() {
  return totalRow;
 }
 public void setTotalRow(int totalRow) {
  
  totalPage = totalRow/pageSize;
  if(totalRow%pageSize > 0)
   totalPage = totalPage + 1;
  
  this.totalRow = totalRow;
 }
 public int getTotalPage(){
  
  return this.totalPage;
 }
 public String getToolsMenu() {
        StringBuffer str = new StringBuffer("");
        int next, prev;
        prev = curPage - 1;
        next = curPage + 1;

        if (curPage > 1) {
            str.append(
                "<a href=\"#\" onclick=\"document.forms(0).pages.value=1;document.forms(0).submit();\"& gt;首页</a>&nbsp;");
        } else {
            str.append("<a href=\"#\">首页</a>&nbsp;");
        }
        if (curPage > 1) {
            str.append(
                "<a href=\"#\" onclick='document.forms(0).pages.value=" +prev + ";document.forms(0).submit();'>上页</a>&nbsp;");
        } else {
            str.append("<a href=\"#\">上页</a>&nbsp;");
        }
        if (curPage < totalPage) {
            str.append(
                "<a href=\"#\" onclick='document.forms(0).pages.value=" +next + ";document.forms(0).submit();'>下页</a>&nbsp;");
        } else {
            str.append("<a href=\"#\" >下页</a>&nbsp;");
        }
        if (totalPage > 1 && curPage != totalPage) {
            str.append(
                "<a href=\"#\"  onclick='document.forms(0).pages.value=" +totalPage + ";document.forms(0).submit();'>末页</a>&nbsp;&nbsp;");
        } else {
            str.append("<a href=\"#\" >末页</a>&nbsp;&nbsp;");
        }
        str.append(" 共" + totalRow + "条记录");
        str.append("  每页<SELECT size=1 name=pagesize onchange='this.form.pages.value=1;this.form.pageSize.value=this.value;this.form.submit();'>");

        if (pageSize == 3) {
            str.append("<OPTION value=3 selected>3</OPTION>");
        } else {
            str.append("<OPTION value=3>3</OPTION>");
        }

        if (pageSize == 10) {
            str.append("<OPTION value=10 selected>10</OPTION>");
        } else {
            str.append("<OPTION value=10>10</OPTION>");
        }
        if (pageSize == 20) {
            str.append("<OPTION value=20 selected>20</OPTION>");
        } else {
            str.append("<OPTION value=20>20</OPTION>");
        }
        if (pageSize == 50) {
            str.append("<OPTION value=50 selected>50</OPTION>");
        } else {
            str.append("<OPTION value=50>50</OPTION>");
        }
        if (pageSize == 100) {
            str.append("<OPTION value=100 selected>100</OPTION>");
        } else {
            str.append("<OPTION value=100>100</OPTION>");
        }
        str.append("</SELECT>");
        str.append("条 分" + pageSize + "页显示 转到");
        str.append("<SELECT size=1 name=Pagelist onchange='this.form.pages.value=this.value;this.form.submit();'>");
        for (int i = 1; i < totalPage + 1; i++) {
            if (i == curPage) {
                str.append("<OPTION value=" + i + " selected>" + i +
                           "</OPTION>");
            } else {
                str.append("<OPTION value=" + i + ">" + i + "</OPTION>");
            }
        }
        str.append("</SELECT>页");
        str.append("<INPUT type=hidden  value=" + curPage + " name=\"pages\" > ");
        str.append("<INPUT type=hidden  value=" + pageSize +
                   " name=\"pageSize\"> ");
        return str.toString();
    }

}


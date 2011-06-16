/**
 * @author jecon at 2009 3 11
 */
function createPager(max_page_value, rowsCount, onePageRows, currentPage, startUrl, endUrl){
	
    if (onePageRows <= 0) 
        document.write("页面显示的记录数不正确");
    document.write("分页显示:&nbsp;&nbsp;");
    var total_page = 1;
    var current_page = 1;
    var MAX_PAGE_INDEX = Number(max_page_value);
    var place_shift = 0
    if (MAX_PAGE_INDEX == "" || MAX_PAGE_INDEX < 2) 
        MAX_PAGE_INDEX = 10;
    current_page = currentPage;
    if (rowsCount % onePageRows == 0) {
        total_page = rowsCount / onePageRows;
    }
    else {
        total_page = Math.floor(rowsCount / onePageRows) + 1;
    }
    if (current_page > 1) 
    	
        document.write("<a href='" + startUrl + "1" + endUrl + "' class='pager'>首页<\/a>&nbsp;");
    if (current_page > 1) {    
        document.write("<a href='" + startUrl + (currentPage - 1) + endUrl + "' class='pager'>前一页<\/a>&nbsp;");
        if(currentPage>max_page_value/2 && total_page>10)
        	document.write("<a href='"+ startUrl + (currentPage -max_page_value/2) + endUrl +"' class='pager'> << <\/a>");
     }
    
    
    if ((current_page > MAX_PAGE_INDEX / 2) && (total_page > MAX_PAGE_INDEX)) {
        if (current_page < (total_page - Math.floor(MAX_PAGE_INDEX / 2))) {
            place_shift = current_page - Math.floor(MAX_PAGE_INDEX / 2);
        }
        else {
            place_shift = (total_page - MAX_PAGE_INDEX);
        }
    }
    
    for (var i = 1 + place_shift; i <= total_page && i <= MAX_PAGE_INDEX + place_shift; i++) {
        if (i == current_page) {
            document.write("&nbsp;<b>&nbsp;" + i + "&nbsp;</b>&nbsp;");
        }
        else {
        
            document.write("<a href='" + startUrl + i + endUrl + "' class='pager'>" + i + "&nbsp;</a>");
        }
    }
    
    if ((Number(currentPage * onePageRows) + Number(onePageRows)) < rowsCount){ 
    	if(currentPage >max_page_value/2 && total_page>10 && currentPage<total_page-max_page_value/2){
    		document.write("<a href='"+ startUrl + (currentPage +max_page_value/2) + endUrl +"' class='pager'> >> <\/a>");
    	}else if (total_page>10 && currentPage<total_page-max_page_value/2){
    		document.write("<a href='"+ startUrl + (currentPage +max_page_value) + endUrl +"' class='pager'> >> <\/a>");
    	}
    	
        document.write("<a href='" + startUrl + (currentPage + 1) + endUrl + "' class='pager'>后一页<\/a>&nbsp;");
    }
    if ((total_page - 1) * onePageRows > currentPage) 
        document.write("<a href='" + startUrl + total_page + endUrl + "' class='pager'>尾页<\/a>&nbsp;");
        
        
    document.write("&nbsp;每页显示" + onePageRows + "项&nbsp;&nbsp;");
    document.write("分" + total_page + "页&nbsp;&nbsp;");
    document.write("共" + rowsCount + "条记录&nbsp;&nbsp;&nbsp;");
    if(currentPage < total_page){
    	document.write("第<input type='text' id=\"gopage\" style='width:30px'  \>页");
    	document.write("<img src=\"images/go.gif\" onclick=\"getGoUrl('"+startUrl+"','"+endUrl+"');\" border=0 alt=\"查询\" style=\"cursor:hand\"/>");
    }
    
}

function getGoUrl(startUrl,endUrl){
	//alert(startUrl+endUrl);
	if(document.getElementById("gopage").value == "" || isNaN(document.getElementById("gopage").value)) {
		 alert("输入有误！");
	      return;
	 }
	 window.location.href = startUrl+document.getElementById("gopage").value+endUrl;
}

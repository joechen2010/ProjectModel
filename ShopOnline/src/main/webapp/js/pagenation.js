function createPagenation(rowsCount, pagesCount, onePageRows, currentPage, urlFirst, urlEnd) {
	document.write('共<B>'+rowsCount+'</B>项&nbsp;分为<B>'+pagesCount+'</B>页&nbsp;每页显示<B>'+onePageRows+'</B>项&nbsp;');
	if (pagesCount <= 1)
		document.write('&nbsp;首页&nbsp;&nbsp;&nbsp;上一页&nbsp;&nbsp;&nbsp;下一页&nbsp;&nbsp;&nbsp;末页&nbsp;');
	else if (currentPage == 1)
		document.write('&nbsp;首页&nbsp;&nbsp;&nbsp;上一页&nbsp;&nbsp;&nbsp;<a href="'+(urlFirst+(currentPage+1)+urlEnd)+'">下一页</a>&nbsp;&nbsp;&nbsp;<a href="'+(urlFirst+pagesCount+urlEnd)+'">末页</a>&nbsp;');
	else if (currentPage != pagesCount)
		document.write('&nbsp;<a href="'+(urlFirst+1+urlEnd)+'">首页</a>&nbsp;&nbsp;&nbsp;<a href="'+(urlFirst+(currentPage-1)+urlEnd)+'">上一页</a>&nbsp;&nbsp;&nbsp;<a href="'+(urlFirst+(currentPage+1)+urlEnd)+'">下一页</a>&nbsp;&nbsp;&nbsp;<a href="'+(urlFirst+pagesCount+urlEnd)+'">末页</a>&nbsp;');		
	else
		document.write('&nbsp;<a href="'+(urlFirst+1+urlEnd)+'">首页</a>&nbsp;&nbsp;&nbsp;<a href="'+(urlFirst+(currentPage-1)+urlEnd)+'">上一页</a>&nbsp;&nbsp;&nbsp;下一页&nbsp;&nbsp;&nbsp;末页&nbsp;');		
	if (pagesCount < 1000) {
		document.write('当前在<select onChange="javascript:window.location.href = \''+urlFirst+'\'+this.value+\''+urlEnd+'\';">');	
		for (var i = 1; i <= pagesCount; i++) {
			if (i == currentPage)
				document.write('<option value="'+i+'" selected="selected">第'+i+'页</option>');
			else
				document.write('<option value="'+i+'">第'+i+'页</option>');
		}
		document.write('</select>');
	}
	else {
		document.write("当前在第<B>" + currentPage + "</B>页");
	}	
}

function selectCategory(category) {
	cancelActive(category);
	gift_category_active = category;
	setActive();
	getHtmlData("space.jsp?do=gift&view=list&inajax=1", "type="+category, "giftData");
}
function cancelActive() {
	if(gift_category_active != "defGift" && gift_category_active != "advGift") {
		$("feeGift").className = "";
		$(gift_category_active).className = "";
	} else {
		$(gift_category_active).className = "";
	}
	if(gift_category_active == "advGift") {
		$("advTips").style.display = "none";
	} else if(gift_category_active != "defGift") {
		$("feesAdd").style.display = "none";
		$("feesCategory").style.display = "none";
		$("feeOrder").style.display = "none";
		$("regular").style.display = "none";
	}
}
function setActive() {
	if(gift_category_active != "defGift" && gift_category_active != "advGift") {
		$("feeGift").className = "active";
	} else {
		$(gift_category_active).className = "active";
	}
	if(gift_category_active == "advGift") {
		getHtmlData("space.jsp?do=gift&view=list&inajax=1", "reqtype=tips", "advTips");
		$("advTips").style.display = "block";
	} else if(gift_category_active != "defGift") {
		getHtmlData("space.jsp?do=gift&view=list&inajax=1", "reqtype=balance", "virtualCurrency");
		$("feesAdd").style.display = "block";
		getHtmlData("space.jsp?do=gift&view=list&inajax=1", "type="+gift_category_active+"&reqtype=feescate", "feesCategory");
		$("feesCategory").style.display = "block";
		$("feeOrder").style.display = "block";
		$("regular").style.display = "block";
	}
}
function getHtmlData(url, sendString, divId) {
    if(url.indexOf("?") > 0) { 
       url += "&randnum="+Math.random(); 
    } else { 
       url += "?randnum="+Math.random(); 
    }
	var x = new Ajax();
	x.post(url, sendString, function(s) {
		$(divId).innerHTML = s;
	});
}

//--初始化变量--
var rT=true;//允许图像过渡
var bT=true;//允许图像淡入淡出
var tw_1=0;//提示框宽度
var endaction=false;//结束动画

var ns4 = document.layers;
var ns6 = document.getElementById && !document.all;
var ie4 = document.all;
offsetX = 20;
offsetY = 12;  
var toolTipSTYLE="";   
function initToolTips() {
	document.write('<div id="toolTipLayer" style="position:absolute; visibility: hidden"></div>');
	if(ns4||ns6||ie4) {
		if(ns4) toolTipSTYLE = document.toolTipLayer;
		else if(ns6) toolTipSTYLE = document.getElementById("toolTipLayer").style;
		else if(ie4) toolTipSTYLE = document.all.toolTipLayer.style;
		if (ns4) document.captureEvents(Event.MOUSEMOVE);
		else {
			toolTipSTYLE.visibility = "visible";
			toolTipSTYLE.display = "none";
		}
	}
}

function tooTipHidden(){
	toolTipSTYLE.display = "none";
}

function toolTip(msg, fg, bg) {
	moveToMouseLoc(this.event);
	if(toolTip.arguments.length < 1) {
		if(ns4) { 
			toolTipSTYLE.visibility = "hidden";
		} 
		else {
			//--图象过渡，淡出处理--
			var tempMsg1 = document.getElementById("msg1");
			if (!endaction) {toolTipSTYLE.display = "none";}
			
			if (rT) {
				if (tempMsg1.filters[1].status==1 || tempMsg1.filters[1].status==0) {
				toolTipSTYLE.display = "none";}
			}
			if (bT) {
				if (tempMsg1.filters[2].status==1 || tempMsg1.filters[2].status==0) {
					toolTipSTYLE.display = "none";
				}
			}
			if (!rT && !bT) toolTipSTYLE.display = "none";
		}
		document.onmousemove = null;
		document.onclick = null;
	}
	else {
		if(!fg) fg = "#777777";
		if(!bg) bg = "#eeeeee";
		var content = 
          '<table id="msg1" name="msg1" border="0" cellspacing="0" cellpadding="1" bgcolor="'+fg+'" class="trans_msg"><td>'+   
          '<table border="0" cellspacing="0" cellpadding="3" bgcolor="'+bg+     
          '"><td>'+msg+'</td></table></td></table>';
		if(ns4) {
			toolTipSTYLE.document.write(content);
			toolTipSTYLE.document.close();
			toolTipSTYLE.visibility = "visible";
		}
		if(ns6) {
			document.getElementById("toolTipLayer").innerHTML = content;
			toolTipSTYLE.display='block';
		}
		if(ie4) {
			document.all("toolTipLayer").innerHTML=content;
			toolTipSTYLE.display='block';
			//--图象过渡，淡入处理--
			var tempMsg1 = document.getElementById("msg1");
			//alert(tempMsg1.filters[0]);
		}
		document.onmousemove = moveToMouseLoc;
		document.onclick = function(){toolTip();};
		moveToMouseLoc(this.event);
		//var tempStr = ""+toolTipSTYLE.top;
		//tempStr = tempStr.indexOf("px") > 0 ? tempStr.substring(0,tempStr.lenght-2):tempStr;
		//alert(toolTipSTYLE.top);
		//toolTipSTYLE.top = toolTipSTYLE.top - document.getElementById("msg1").offsetHeight;
		//temp_ToolTipLayer.top = this.event.y + document.body.scrollTop - document.getElementById("msg1").offsetHeight;
		//temp_ToolTipLayer.left = this.event.x + document.body.scrollLeft;
		//alert(document.getElementById("toolTipLayer").innerHTML);
		//alert(document.getElementById("msg1").offsetHeight); - document.getElementById("msg1").offsetHeight
	}
}
function moveToMouseLoc(e) {
	if(ns4||ns6) {
		x = e.pageX;
		y = e.pageY;
	}
	else {
		x = event.x + document.body.scrollLeft;
		y = event.y + document.body.scrollTop;
	}
	x = mouseX(event);
	y = mouseY(event);
	toolTipSTYLE.left = x + offsetX;
	if (document.getElementById("msg1"))
		y = y - document.getElementById("msg1").offsetHeight;
	toolTipSTYLE.top = y + offsetY;
	//alert("left:"+toolTipSTYLE.left + " top:"+toolTipSTYLE.top);
}
initToolTips();
function mouseX(evt) {
	if (evt.pageX) return evt.pageX;
	else if (evt.clientX)
		return evt.clientX + (document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft);
	return 0;
}
function mouseY(evt) {
	if (evt.pageY) return evt.pageY;
	else if (evt.clientY)
		return evt.clientY + (document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop);
	return 0;
}
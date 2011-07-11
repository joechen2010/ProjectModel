window._MYCONTACTUserAgent = navigator.userAgent.toLowerCase();

var MYCONTACT_Config =
{
  callbackPage: 'contact.jsp',
  b_version: (_MYCONTACTUserAgent.match( /.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/ ) || [])[1],
  b_msie: /msie/.test(_MYCONTACTUserAgent) && !/opera/.test(_MYCONTACTUserAgent),
  b_webkit: /webkit/.test(_MYCONTACTUserAgent),
  b_opera: /opera/.test(_MYCONTACTUserAgent),
  css1Compat: (document.compatMode == "CSS1Compat")
};
  
window.$mycontact_popup_div = null;
window._MYCONTACTMiddleItv = null;
window._MYCONTACTMiddleFlag = 0;

var MYCONTACT$ = function(d, win)
{
  return (win||window).document.getElementById(d);
};

MYCONTACT = {};
MYCONTACT.util = {};

MYCONTACT.showChooser = function(textAreaID, useWinOpen, customStyleBaseName)
{
  if(useWinOpen===undefined){
  	useWinOpen = true;
  }
  var taObj = MYCONTACT$(textAreaID);
  if (!taObj)
  {
    alert("\u8bf7\u6307\u5b9a\u8054\u7cfb\u4eba\u9009\u62e9\u5668\u7684\u6587\u672c\u57df!");
    return;
  }
  var page=MYCONTACT_Config.callbackPage+"?ta="+escape(textAreaID);
  //use window.open
  if (useWinOpen)
  {
  	  var abWin = window.mycontactWindow;
	  if (!abWin || abWin.closed)
	  {
	    window.mycontactWindow = window.open(page, 'MYCONTACTChooser', 'toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no,width=460,height=460', false);
	  }
	  if (abWin)
	  {
	    try{
	      abWin.focus();
	    } catch(ex) {}
	  }
  }
  //use iframe
  else
  {
  	  MYCONTACT.util.showPopup(page, customStyleBaseName);
  }
};
MYCONTACT.onSubmit2 = function(form)
{
  var username = form.username.value;
  var password = form.password.value;
  if (username.length==0)
  {
    alert('\u8bf7\u586b\u5199\u5e10\u6237\u540d\u79f0');
    return false;
  }
  if (password.length==0)
  {
    alert('\u8bf7\u586b\u5199\u5bc6\u7801');
    return false;
  }
  form.style.display = 'none';
  MYCONTACT$('importing').style.display = '';
  var p=parent.parent.opener;
  if(p==null || p==undefined){
  	p=window.top.parent;
  }
  if (p) {
  	form.emails.value=MYCONTACT$(textareaID,p).value;
  }
  return true;
};
MYCONTACT.onSubmit4 = function()
{
  var listDOM = MYCONTACT$('contact_data_list');
  var items = listDOM.getElementsByTagName('input');
  var arr = [];
  for (var ii=0; ii<items.length; ii++) {
    var it = items[ii];
    if (it.type=='checkbox' && it.checked) {
      var val = it.value;
      var na = MYCONTACT$('my_list_name_'+val).innerHTML;
      var ad = MYCONTACT$('my_list_addr_'+val).innerHTML;
      arr.push({name:na, addr:ad});
    }
  }
	var p=parent.parent.opener;
	var useIframe=false;
	if(p==null || p==undefined){
		p=window.top.parent;
		useIframe=false;
	}else{
		useIframe=true;
	}
	if (p) {
		var email=MYCONTACT$(textareaID,p).value;
		var list=MYCONTACT.util.formatRecipientList(arr);
		if(email){
			if (email.substring(email.length-1,email.length)==","){
				email+=list;
			}else{
				email+=","+list;
			}
		}else{
			email=list;
		}
		MYCONTACT$(textareaID,p).value=email;
		if(useIframe){
			window.top.close();
		}else{
			p.MYCONTACT.util.closeMYCONTACTPopupWin();
		}
		return true;
	}
  return false;
};


MYCONTACT.util.doSwitch = function(imgObj)
{
  var isHide = (MYCONTACT$('contact_data_list').style.display == 'none');
  var oldSrc = imgObj.src;
  if(isHide){
    MYCONTACT$('contact_data_list').style.display = '';
    imgObj.src = oldSrc.replace('show', 'hide');
  }else{
    MYCONTACT$('contact_data_list').style.display = 'none';
    imgObj.src = oldSrc.replace('hide', 'show');
  }
};

MYCONTACT.onMYCONTACTFindAll = function(ifAll)
{
  var chbs = document.getElementsByName('my_list_chb');
  for (var ii=0; ii<chbs.length; ii++) {
    if(ifAll){
      chbs[ii].checked = 'checked';
    }else{
      chbs[ii].checked = false;
    }
  }
  chbs = null;
  delete chbs;
  return false;
};

MYCONTACT.util.showPopup = function(url, customStyleBaseName)
{
	if (!document.getElementById('mycontact_popup_div_id')) {
		var sname = customStyleBaseName||'mycontact';
		
		if (!customStyleBaseName) {
			MYCONTACT.util.writeMYCONTACTPopupStyle();
		}
  	$mycontact_popup_div = document.createElement('DIV');
  	document.getElementsByTagName('body')[0].appendChild($mycontact_popup_div);
  	$mycontact_popup_div.className = sname + '_popup_div';
	$mycontact_popup_div.id = 'mycontact_popup_div_id';
  	MYCONTACT.util.middleElem();
  	_MYCONTACTMiddleItv = window.setInterval(function(){
  		MYCONTACT.util.middleElem();
  		_MYCONTACTMiddleFlag++;
  		if (_MYCONTACTMiddleFlag>2) {
  			window.clearInterval(_MYCONTACTMiddleItv);
  			_MYCONTACTMiddleItv = null;
  			_MYCONTACTMiddleFlag = 0;
  		}
  	}, 50);
  	if (window.addEventListener) {
  		window.addEventListener('scroll',MYCONTACT.util.middleElem,false);
  		window.addEventListener('resize',MYCONTACT.util.middleElem,false);
  	}
  	if (window.attachEvent) {
  		window.attachEvent('onscroll',MYCONTACT.util.middleElem);
  		window.attachEvent('onresize',MYCONTACT.util.middleElem);
  	}
  	var content = '<div class="' + sname + '_popup_close"><a href="#" onclick="MYCONTACT.util.closeMYCONTACTPopupWin();return false;">[&#20851;&#38381;]</a></div>'; //关闭
  	content += '<iframe class="' + sname + '_popup_ifm" src="'+ url +'" frameborder="no" scrolling="no"></iframe>';

  	$mycontact_popup_div.innerHTML = content;
		
		return false;
	}
};
MYCONTACT.util.writeMYCONTACTPopupStyle = function()
{
	var oldSS = document.getElementById('mycontactPopupStylesheet');
	if (oldSS) {
		document.getElementsByTagName('head')[0].removeChild(oldSS);
	}
	oldSS = null;
	delete oldSS;
	var sContent = '.mycontact_popup_div {width:460px; height:460px; background-color:#fff; border:solid #bbb; border-width:2px 1px 1px 1px; position:absolute; top:0; left:0; z-index:333; overflow:hidden; margin:0; padding:0}.mycontact_popup_ifm {width:460px; height:460px; border:0;}.mycontact_popup_close {height:22px; margin:0; padding:0;}.mycontact_popup_close a {font-size:12px !important; text-decoration:none; color:#6CAAD9; display:block; padding:0; margin:7px 0 0 410px;}';
	var sObj = document.createElement('style');
	document.getElementsByTagName('head')[0].appendChild(sObj);
	sObj.setAttribute('type', 'text/css');
	sObj.setAttribute('rel', 'stylesheet');
	sObj.setAttribute('id', 'mycontactPopupStylesheet');
	if (sObj.styleSheet) {
		sObj.styleSheet.cssText = sContent;
	} else {
		sObj.appendChild(document.createTextNode(sContent));
	}
	sObj = null;
	delete sObj;
};

MYCONTACT.util.closeMYCONTACTPopupWin = function()
{
	if(!$mycontact_popup_div) return;
	window.clearInterval(_MYCONTACTMiddleItv);
	_MYCONTACTMiddleItv = null;
	
	if (window.removeEventListener) {
		window.removeEventListener('scroll',MYCONTACT.util.middleElem,false);
		window.removeEventListener('resize',MYCONTACT.util.middleElem,false);
	}
	if (window.detachEvent) {
		window.detachEvent('onscroll',MYCONTACT.util.middleElem);
		window.detachEvent('onresize',MYCONTACT.util.middleElem);
	}
	
	window.setTimeout(function(){
		document.getElementsByTagName('body')[0].removeChild( document.getElementById('mycontact_popup_div_id') );
	},20);
	
	$mycontact_popup_div = null;
};

MYCONTACT.util.middleElem = function()
{
	var obj = $mycontact_popup_div;
	if (!obj) return;
	window.setTimeout(function(){
		var ie = MYCONTACT_Config.b_msie;
		var ie6 = ( ie && MYCONTACT_Config.b_version<7 );
		
		var shouldFix = ie
						? MYCONTACT_Config.b_version<7
							? false
							: MYCONTACT_Config.css1Compat
								? true
								: false
						: true;
		
		obj.style.position = (shouldFix?'fixed':'absolute');
		
		var $base = document.createElement('div');
		document.getElementsByTagName('body')[0].appendChild($base);
		$base.style.position = (shouldFix?'fixed':'absolute');
		$base.style.left = '0';
		$base.style.top = '0';
		$base.style.zIndex = '567';
		$base.style.width = "100%";
		$base.style.height = ie6
								? (Math.min(document.documentElement.scrollHeight,document.documentElement.clientHeight)+'px')
								:"100%";
		$base.style.border = '2px dashed red';
		$base.innerHTML = "&nbsp;";
		
		var winW = Math.min($base.scrollWidth, $base.clientWidth);
		if (MYCONTACT_Config.b_opera) {
			winW = $base.clientWidth;
		}
		var divW = obj.clientWidth;
		try{
			var vl = (0.5*(winW-divW) + parseInt(ie6?document.documentElement.scrollLeft:0));
			vl = vl<0?0:vl;
			obj.style.left = vl + 'px';
		}catch(ex){}
		var winH = Math.max($base.scrollHeight, $base.clientHeight);
		var divH = obj.clientHeight;
		try{
			var vt = (0.5*(winH-divH) + parseInt(ie6?document.documentElement.scrollTop:0));
			vt = vt<0?0:vt;
			obj.style.top = vt + 'px';
		}catch(ex){}
		
		$base.parentNode.removeChild($base);
		$base = null;
		delete $base;
	}, 10);
};

MYCONTACT.util.formatRecipientList = function(data)
{
	var recips = [];
	for (var i = 0; i < data.length; i++) {
		var fmt = data[i].addr;
		recips.push(fmt);
	}
	return recips.join(', ');
};
MYCONTACT.util.getSelectedAB = function(form)
{
	var r = form.ab;
	for(var i=0;i<r.length;i++)
  {
		if(r[i].checked)
    {
			return r[i].value;
		}
	}
	return null;
};
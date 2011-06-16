//程序基本思路：通过扩展对象来实现，将String扩展 将默认的表单元素扩展 定义两个自定义对象。
//String.isEmail
//String.isUrl
//表单元素.required
//表单元素.isvalid
//表单元素.validate
//

//字符串验证扩展
//├电子邮件验证
String.prototype.isEmail = function(){
	var tmpStr = this;
	var email = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	return email.test(tmpStr)
}
//├http地址验证
String.prototype.isUrl = function(){
	var url = /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/;
	var tmpStr = this;
	return url.test(tmpStr);
}
//├日期验证（第一部分）
String.prototype.isDateTime = function(){
	if(Date.parse(this)||Date.parseDate(this))
	{
		return true;
	}
	else
	{
		return false;
	}
}
String.prototype.isInteger = function()
{
	var _i = /^[-\+]?\d+$/;
	var _s = this; 
	return _i.test(_s);
}
Date.prototype.toIsoDate = function()
{
	var _d = this;
	var _s;
	_Y =_d.getFullYear();
	_M = _d.getMonth() + 1;
	_D = _d.getDate();
	_H = _d.getHours();
	_I = _d.getMinutes();
	_S = _d.getSeconds();
	with(_d)
	{
		_s = [getMonth() + 1,getDate(),getHours(),getMinutes(),getSeconds()];
	}
	for(var i = 0; i < _s.length; i++)
	{
		if (_s[i].toString().length == 1)_s[i]= '0'+_s[i];
	}
		return (_Y + '-'+_s[0]+'-'+_s[1]+' '+_s[2]+':'+_s[3]+':'+_s[4])
}
//├日期验证（第二部分）
Date.parseDate = function(str, fmt) {
	fmt = fmt||"%Y-%m-%d %H:%M";
	var today = new Date();
	var y = 0;
	var m = -1;
	var d = 0;
	var a = str.split(/\W+/);
	var b = fmt.match(/%./g);
	var i = 0, j = 0;
	var hr = 0;
	var min = 0;
	for (i = 0; i < a.length; ++i) {
		if (!a[i])
			continue;
		switch (b[i]) {
		    case "%d":
		    case "%e":
			d = parseInt(a[i], 10);
			break;

		    case "%m":
			m = parseInt(a[i], 10) - 1;
			break;

		    case "%Y":
		    case "%y":
			y = parseInt(a[i], 10);
			(y < 100) && (y += (y > 29) ? 1900 : 2000);
			break;

		    case "%b":
		    case "%B":
			for (j = 0; j < 12; ++j) {
				if (Calendar._MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { m = j; break; }
			}
			break;

		    case "%H":
		    case "%I":
		    case "%k":
		    case "%l":
			hr = parseInt(a[i], 10);
			break;

		    case "%P":
		    case "%p":
			if (/pm/i.test(a[i]) && hr < 12)
				hr += 12;
			else if (/am/i.test(a[i]) && hr >= 12)
				hr -= 12;
			break;

		    case "%M":
			min = parseInt(a[i], 10);
			break;
		}
	}
	if (isNaN(y)) y = today.getFullYear();
	if (isNaN(m)) m = today.getMonth();
	if (isNaN(d)) d = today.getDate();
	if (isNaN(hr)) hr = today.getHours();
	if (isNaN(min)) min = today.getMinutes();
	if (y != 0 && m != -1 && d != 0)
		return new Date(y, m, d, hr, min, 0);
	y = 0; m = -1; d = 0;
	for (i = 0; i < a.length; ++i) {
		if (a[i].search(/[a-zA-Z]+/) != -1) {
			var t = -1;
			for (j = 0; j < 12; ++j) {
				if (Calendar._MN[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { t = j; break; }
			}
			if (t != -1) {
				if (m != -1) {
					d = m+1;
				}
				m = t;
			}
		} else if (parseInt(a[i], 10) <= 12 && m == -1) {
			m = a[i]-1;
		} else if (parseInt(a[i], 10) > 31 && y == 0) {
			y = parseInt(a[i], 10);
			(y < 100) && (y += (y > 29) ? 1900 : 2000);
		} else if (d == 0) {
			d = a[i];
		}
	}
	if (y == 0)
		y = today.getFullYear();
	if (m != -1 && d != 0)
		return new Date(y, m, d, hr, min, 0);
	return today;
};
//扩展完成

//对象定义

var vform = new Object;
//获取弹出提示的显示位置
vform.getAbsolutePos = function(el) {
	var _p = { x: 0, y: 0 };
	 do{
				_p.x += (el.offsetLeft - el.scrollLeft);
				_p.y += (el.offsetTop - el.scrollTop); 
		}
		 while(el=el.offsetParent)
     return _p;
      };
vform.toString = function()
{
	return("vForm表单验证程序\n版本：1.0beta\n作者：雷晓宝\n时间：2006-07-31\n网址：[url]http://lxbzj.com[/url]\n许可：LGPL");
}
vform.rules = new Array;
vform.rules.add = function(obj,minLength,dataType,errmsg,maxLength,rule,patams)
{
    var curlen = this.length;
        this[curlen] = [obj,minLength,dataType,errmsg,maxLength,rule,patams];
        //this[curlen] = [ 0 ,    1    ,    2   ,   3  ,   4  ,  5 ,   6  ];

    return this.length;
}
vform.init= function()
{
	if(document.getElementById(this.form_id))
	{
		//获取表单
		var o = document.getElementById(this.form_id);
		//遍历规则
		for(var i = 0 ;i< this.rules.length;i++)
		{
			_r = this.rules[i]
			//如果存在元素，则添加验证程序
			if(_o = o.elements[_r[0]])
			{
				//判断是是否必填,是否有最小长度
				if(_r[1] > 0 )
				{
					_o.required = true;//必填的含义和最小长度为1是一样的
					_o.minLength = parseInt(_r[1]);
				}
				else
				{
					_o.required = false;
					_o.minLength = 0;
				}
				//判断是否有最大长度;
				if(_r[4])
				{
					_o.maxLength = parseInt(_r[4]);
				}
				//添加长度验证函数
				_o.validLength = function ()
				{
					var b =true;
					if(this.minLength)
					{
						b = (this.minLength <= this.value.length);
					}
					if(this.type == 'textarea' && this.maxLength )
					{
						b = b && (this.maxLength >= this.value.length );
					}
					return (b);
				}
				//添加验证，进行格式验证
				switch(_r[2])
				{
					case 'e-mail':
						_o.validate = function()
						{
							this.isvalid = this.validLength() && this.value.isEmail();
							return (this.isvalid);
						};
						break;
					case 'url':
						_o.validate = function()
						{
							if (this.value.substring(0,7) != 'http://')this.value = 'http://' +this.value;
							this.isvalid = this.validLength() && this.value.isUrl();
							return (this.isvalid);
						}
						break;
					case 'date':
						_o.validate = function()
						{
							var _d = Date.parse(this.value)||Date.parseDate(this.value);
							this.value =  _d.toIsoDate();
							
							this.isvalid = this.validLength() && this.value.isDateTime();
							return (this.isvalid);
							a=a>b?1:1;
						}
						break;
					case 'number':
						_o.validate = function()
						{
							this.isvalid = this.validLength() && this.value.isInteger();
							return (this.isvalid);

						}
						break;
					case 'any':
						_o.validate = function()
						{
							this.isvalid = this.validLength();
							return  this.isvalid
						}
						break;
					default :
						var regexp = /^\\\w+$/;
						if ( regexp.test(_r[2]))//表示必须和同表单下的某个字段的值一样。用于重复输入的验证
						{
							_el = _r[2].substring(1);
							if (o.elements[_el]){
								_o.equal = _el;
								_o.validate = function()
								{
									if(_o = this.form.elements[this.equal])
									{
										if ( (_o.value == this.value) && this.validLength())
										{
											return true;
										}else {
										return false;
										}
									}else{
										alert('setup error');
									}
								
								}
							}else
							{
								alert(_el + 'is not a valid form element');
								_o.validate = function(){return true;}
							}
						}
						var regexp1 = /^\\(==|!=|>=|<=|>|<)/;
						if ( regexp1.test(_r[2]) )
						{
							_s0 = _r[2];
							_s1 = RegExp.$1;
							_s2 = _s0.replace(regexp1,'');
							_operator = _s1.substring(0);//比较操作符
							var regexp2 = /^\w+$/;
							if (regexp2.test(_s2))//是一个标志符，整数 或者变量
							{
								_o.operation = _operator+_s2;
								_o.validate = function()
								{
									_b = true;
									if (this.value.length !=0)
									{
										_b = eval(this.value+this.operation+';');
									}									
									_b = _b && this.validLength();
									return _b;
								}
							}
						};
						break;
						
				}
				//添加验证提示(div标签)并初始化
				var _p = vform.getAbsolutePos(_o);
				_o.tip = new tip(_r[3],vform.err_class,_p.x+_o.offsetWidth+3,_p.y);

				_o.tip.init();
				//失去焦点时，开始验证
				_o.onblur =function(e)
				{
					if(this.minLength || this.value.length >0) 
					{ 
						if( this.validate() )
						{
							this.tip.hide();
						}else
						{
							this.tip.show();//显示错误信息
							//this.focus(); 添加这句在ie里会导致死循环 :(
							return false;
						}
					}
				}
			}
		}
	//焦点验证可能会失败,所以最后需要表单提交前的验证作为最后的补充。
		document.getElementById(this.form_id).onsubmit = function()
		{
			var valid = true;
			for(i=0;i<this.elements.length;i++)
			{
				_o = this.elements[i];
				if(_o.minLength && !_o.isvalid)
				{
					_o.tip.show();
					valid = false;
				}
			}
			return valid;
		}
	}
}
//弹出提示定义
function tip(text,className,x,y)
{
	var o = document.createElement("div");
	o.style.display = "none";
	o.innerHTML = text;
	//var t = document.createTextNode(text);
	document.body.appendChild(o);
	//o.appendChild(t);
	
	this.init = function(dis)
	{
		o.className = "info";
		o.style.left = x+"px";
		o.style.top = y+"px";
		o.style.zindex = 100;
		if(dis)
		{
			o.style.display = "";
		}
		else
		{
			o.style.display = "none";
		}
	}
	this.show = function()
	{
		o.style.display = "";
	}
	this.hide = function()
	{
		o.style.display = "none";
	}
}


function start()
{
		vform.form_id = 'form1';//必须是表单的id
		vform.err_class = 'info';//出错提示的样式
	//验证规则，逐条填写
vform.rules.add('frm_name',1,'e-mail','请您按照 [email]user@domain.com[/email] 的格式输入电子邮件地址。
<span style="color:#f00">必填项目</span>');
		vform.rules.add('myweb',1,'url','请您按照 [url]http://www.domain.com[/url] 的格式输入您的网址。
<span style="color:#f00">必填项目</span>');
		vform.rules.add('dateinput',0,'date','请按2000-03-05 的格式输入日期。
<span style="color:#f00">必填项目</span>');
		vform.rules.add('qq',0,'number','这必须是一个整数');
		vform.rules.add('least10',10,'any','您必须至少填写10个
<span style="color:#f00">必填项目</span>');
		vform.rules.add('ok100',1,'any','这里被限制为100个字符
<span style="color:#f00">必填项目</span>',100);
		vform.rules.add('r_pass0',5,'any','密码最短5位最长20位
<span style="color:#f00">必填项目</span>',20);
		vform.rules.add('r_pass1',5,"\\r_pass0",'确认密码错误
<span style="color:#f00">必填项目</span>',20);
		vform.rules.add('frm_sel',1,"\\>2",'必须大于2000
<span style="color:#f00">必填项目</span>');
		vform.init();
	
}

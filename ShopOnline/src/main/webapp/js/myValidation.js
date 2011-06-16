//�������˼·��ͨ����չ������ʵ�֣���String��չ ��Ĭ�ϵı�Ԫ����չ ���������Զ������
//String.isEmail
//String.isUrl
//��Ԫ��.required
//��Ԫ��.isvalid
//��Ԫ��.validate
//

//�ַ�����֤��չ
//�������ʼ���֤
String.prototype.isEmail = function(){
	var tmpStr = this;
	var email = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	return email.test(tmpStr)
}
//��http��ַ��֤
String.prototype.isUrl = function(){
	var url = /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/;
	var tmpStr = this;
	return url.test(tmpStr);
}
//��������֤����һ���֣�
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
//��������֤���ڶ����֣�
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
//��չ���

//������

var vform = new Object;
//��ȡ������ʾ����ʾλ��
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
	return("vForm����֤����\n�汾��1.0beta\n���ߣ�������\nʱ�䣺2006-07-31\n��ַ��[url]http://lxbzj.com[/url]\n��ɣ�LGPL");
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
		//��ȡ��
		var o = document.getElementById(this.form_id);
		//��������
		for(var i = 0 ;i< this.rules.length;i++)
		{
			_r = this.rules[i]
			//�������Ԫ�أ��������֤����
			if(_o = o.elements[_r[0]])
			{
				//�ж����Ƿ����,�Ƿ�����С����
				if(_r[1] > 0 )
				{
					_o.required = true;//����ĺ������С����Ϊ1��һ����
					_o.minLength = parseInt(_r[1]);
				}
				else
				{
					_o.required = false;
					_o.minLength = 0;
				}
				//�ж��Ƿ�����󳤶�;
				if(_r[4])
				{
					_o.maxLength = parseInt(_r[4]);
				}
				//��ӳ�����֤����
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
				//�����֤�����и�ʽ��֤
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
						if ( regexp.test(_r[2]))//��ʾ�����ͬ���µ�ĳ���ֶε�ֵһ���������ظ��������֤
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
							_operator = _s1.substring(0);//�Ƚϲ�����
							var regexp2 = /^\w+$/;
							if (regexp2.test(_s2))//��һ����־�������� ���߱���
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
				//�����֤��ʾ(div��ǩ)����ʼ��
				var _p = vform.getAbsolutePos(_o);
				_o.tip = new tip(_r[3],vform.err_class,_p.x+_o.offsetWidth+3,_p.y);

				_o.tip.init();
				//ʧȥ����ʱ����ʼ��֤
				_o.onblur =function(e)
				{
					if(this.minLength || this.value.length >0) 
					{ 
						if( this.validate() )
						{
							this.tip.hide();
						}else
						{
							this.tip.show();//��ʾ������Ϣ
							//this.focus(); ��������ie��ᵼ����ѭ�� :(
							return false;
						}
					}
				}
			}
		}
	//������֤���ܻ�ʧ��,���������Ҫ���ύǰ����֤��Ϊ���Ĳ��䡣
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
//������ʾ����
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
		vform.form_id = 'form1';//�����Ǳ���id
		vform.err_class = 'info';//������ʾ����ʽ
	//��֤����������д
vform.rules.add('frm_name',1,'e-mail','�������� [email]user@domain.com[/email] �ĸ�ʽ��������ʼ���ַ��
<span style="color:#f00">������Ŀ</span>');
		vform.rules.add('myweb',1,'url','�������� [url]http://www.domain.com[/url] �ĸ�ʽ����������ַ��
<span style="color:#f00">������Ŀ</span>');
		vform.rules.add('dateinput',0,'date','�밴2000-03-05 �ĸ�ʽ�������ڡ�
<span style="color:#f00">������Ŀ</span>');
		vform.rules.add('qq',0,'number','�������һ������');
		vform.rules.add('least10',10,'any','������������д10��
<span style="color:#f00">������Ŀ</span>');
		vform.rules.add('ok100',1,'any','���ﱻ����Ϊ100���ַ�
<span style="color:#f00">������Ŀ</span>',100);
		vform.rules.add('r_pass0',5,'any','�������5λ�20λ
<span style="color:#f00">������Ŀ</span>',20);
		vform.rules.add('r_pass1',5,"\\r_pass0",'ȷ���������
<span style="color:#f00">������Ŀ</span>',20);
		vform.rules.add('frm_sel',1,"\\>2",'�������2000
<span style="color:#f00">������Ŀ</span>');
		vform.init();
	
}

function Ajax()
{
        this.Ajax = (function ()
        {
                var XmlHttp = false;
                if (window.XMLHttpRequest) {
                        XmlHttp = new XMLHttpRequest();
                        if (XmlHttp.overrideMimeType) 
                        	XmlHttp.overrideMimeType("text/xml");
                }
                else if (window.ActiveXObject) {
                        try {
                                XmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
                        } catch (e) {
                                try {
                                        XmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
                                } catch (e) {
                                        XmlHttp = false;
                                }
                        }
                }
                return XmlHttp;
        })();
        if (!this.Ajax) 
        	return false;
        this.state = 0;
        this.resText = null;
        this.dom = null;
        this.method = arguments.length >= 1 ? arguments[0] : this.method;
        this.url = arguments.length >= 2 ? arguments[1] : null;
        this.url += "&reqTime=" + new Date().getTime();
        this.resType = arguments.length >= 3 ? arguments[2] : "xml";
        this.async = arguments.length >= 4 ? arguments[3] : true;
        this.callback = function(){};
  
        var me = this;
        this.Ajax.onreadystatechange = function()
        {
                 me.state = me.Ajax.readyState + (typeof(me.Ajax.status) == "unknown" ? 0 : me.Ajax.status);
                 me.callback();
                 if (me.state == 204) {
                 	if (me.resType.toLowerCase == "xml")
                 		me.dom = me.Ajax.responseXML.getElementsByTagName("Child");
                 	else if (me.resType.toLowerCase == "text")
                 		me.resText = me.Ajax.responseText;
                 	me.Ajax = null;
                 	handStateStatusChange();
                 }
        };
        
        this.send = function(data)
        {
                this.Ajax.open(this.method, this.url, this.async);
                if (this.method.toUpperCase()=="POST") 
                	this.Ajax.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
                this.Ajax.send(data);
        };
        
        function handStateStatusChange() {
        	if (this.Ajax.resType.toLowerCase == "xml")
        		return this.dom;
        	else
        		return this.resText;
        }
        
}
Date.prototype.toText=function(){ 
	var m; 
	var d; 
	if(this.getMonth()<9){ 
	m="0"+(this.getMonth()+1); 
	}else{ 
	m=this.getMonth()+1; 
	} 
	if(this.getDate()<10){ 
	d="0"+this.getDate(); 
	}else{ 
	d=this.getDate(); 
	} 
	return this.getYear()+"-"+m+"-"+d; 
} 


Date.prototype.toLongintText=function(){ 
	var m; 
	var d; 
	if(this.getMonth()<9){ 
	m="0"+(this.getMonth()+1); 
	}else{ 
	m=this.getMonth()+1; 
	} 
	if(this.getDate()<10){ 
	d="0"+this.getDate(); 
	}else{ 
	d=this.getDate(); 
	} 
	return this.getYear()+m+d; 
} 


var getYesterday = function() { 
	var dateStr = arguments[0].split("-"); 
	return new Date(new Date(dateStr[1]+"-"+dateStr[2]+"-"+dateStr[0])-1000*60*60*24).toText(); 
} 

function StringToDate(DateStr)   
{    
   
    var converted = Date.parse(DateStr);   
    var myDate = new Date(converted);   
    if (isNaN(myDate))   
    {    
        //var delimCahar = DateStr.indexOf('/')!=-1?'/':'-';   
        var arys= DateStr.split('-');   
        myDate = new Date(arys[0],--arys[1],arys[2]);   
    }   
    return myDate;   
}   

function StringToStanderDate(str){
	return  new   Date(Date.parse(str.replace(/-/g,   "/"))); 
}

function uploadEdit(obj) {
	mainForm = obj.form;
	forms = $('attachbody').getElementsByTagName("FORM");
	albumid = $('uploadalbum').value;
	edit_save();
	upload();
}

function edit_save() {
	var p = window.frames['jchome-ifrHtmlEditor'];
	var obj = p.window.frames['HtmlEditor'];
	var status = p.document.getElementById('jchome-editstatus').value;
	if(status == 'code') {
		$('jchome-ttHtmlEditor').value = p.document.getElementById('sourceEditor').value;
	} else if(status == 'text') {
		if(is_ie) {
			obj.document.body.innerText = p.document.getElementById('dvtext').value;
			$('jchome-ttHtmlEditor').value = obj.document.body.innerHTML;
		} else {
			obj.document.body.textContent = p.document.getElementById('dvtext').value;
			var sOutText = obj.document.body.innerHTML;
			$('jchome-ttHtmlEditor').value = sOutText.replace(/\r\n|\n/g,"<br>");
		}
	} else {
		$('jchome-ttHtmlEditor').value = obj.document.body.innerHTML;
	}
	backupContent($('jchome-ttHtmlEditor').value);
}

function relatekw() {
	edit_save();
	var subject = $('subject').value;
	var x = new Ajax();
	subject=encodeURIComponent(encodeURIComponent(subject));
	x.get('cp.jsp?ac=relatekw&subjectenc=' + subject, function(s){
		$('tag').value = s;
	});
}
	
function backupContent(sHTML) {
	if (sHTML.length > 11) {
		var oArea = $('jchome-ttHtmlEditor');
		try {
			var xmlDoc = oArea.XMLDocument;
			var subNode = xmlDoc.createNode(1, 'subject', '');
			var subValueNode = xmlDoc.createNode(4, "subject", "");
			var msgNode = xmlDoc.createNode(1, 'message', '');
			var msgValueNode = xmlDoc.createNode(4, "message", "");
			var tagNode = xmlDoc.createNode(1, 'tag', '');
			var tagValueNode = xmlDoc.createNode(4, "tag", "");
				
			delmsg = xmlDoc.selectNodes("//subject");
   			delmsg.removeAll();
   			delmsg = xmlDoc.selectNodes("//message");
   			delmsg.removeAll();
   			delmsg = xmlDoc.selectNodes("//tag");
   			delmsg.removeAll();
			
			msgValueNode.nodeValue = sHTML;
			subValueNode.nodeValue = $('subject').value;
			tagValueNode.nodeValue = $('tag').value;
				
			subNode.appendChild(subValueNode);
			msgNode.appendChild(msgValueNode);
			tagNode.appendChild(tagValueNode);

			root = xmlDoc.documentElement;
				
			root.appendChild(msgNode);
			root.appendChild(subNode);
			root.appendChild(tagNode);
			oArea.save('JCHome');
		} catch (e) {
			if(window.sessionStorage) {
				try {
					sessionStorage.setItem('message', sHTML);
					if($('subject') !=null ) sessionStorage.setItem('subject', $('subject').value);
					if($('tag') !=null ) sessionStorage.setItem('tag', $('tag').value);
				} catch(e) {}
			}
				
		}
	}
}

function edit_insert(html) {
	var p = window.frames['jchome-ifrHtmlEditor'];
	var obj = p.window.frames['HtmlEditor'];
	var status = p.document.getElementById('jchome-editstatus').value;
	if(status != 'html') {
		alert('本操作只在多媒体编辑模式下才有效');
		return;
	}
	obj.focus();
	if(window.Event){
		obj.document.execCommand('insertHTML', false, html);
	} else {
		obj.focus();
		var f = obj.document.selection.createRange();
		f.pasteHTML(html);
		f.collapse(false);
		f.select();
	}
}

function insertImage(image) {
	edit_insert('<p><img src="'+image+'"></p>');
}
function login() {
	if (document.location.protocol != 'https:') {
		// alert('Please use https protocol');
		//  return;
	}
	var callback = document.location.protocol + '//' + document.location.host + document.location.pathname;
	document.location = 'https://id.myopenlink.net/ods/webid_verify.vsp?callback=' + encodeURIComponent(callback);
	
}

function getParam(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.href);
	if (results == null)
		return "";
	else
		return decodeURIComponent(results[1]);
}
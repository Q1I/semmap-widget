WIDGET = (function() {

	// WebID
	var webid = "";
	var error = "";

	function getUser() {
		webid = getParam("webid");
		error = getParam("error");
		if(webid !="")
			$("#user").val(webid);
	}

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

	function init() {
		initLogin();
		initStars();
		initUpload();
		// initDetails();
	}

	function initLogin() {
		// openid
		// var lib = '<script type="text/javascript" src="js/lib/jquery.openid.js"></script>';
		// $.get('html/login.html', function(data) {
		// $('head').append(lib);$('#login').html(data);
		//
		// $("form.openid:eq(0)").openid();
		// });

		// webid
		if (webid == "")
			$('#login').html("<button id='loginButton' onclick='WIDGET.login()'>WebID Login</button>");
		else
			$('#login').html("Logged in as <b>" + webid + "</b>");
	}

	function initStars() {
		var lib = '<script type="text/javascript" src="js/lib/jquery.rating.pack.js"></script>';
		$.get('html/stars.html', function(data) {
			$('#starRating').html(data);
			$('head').append(lib);
		});
	}

	function initUpload() {
		$.get('html/upload.html', function(data) {
			$('#upload').html(data);
		});
	}

	function initDetails() {
		$('#details').html();
	}

	function setRating() {
		var rat = $('input[type=radio].star:checked').rating().val();
		if (rat != undefined)
			$('#rating').val(rat);
	}

	function getResource() {
		alert(resourceUri);
		return resourceUri;
	}

	function test() {
		window.alert("test!");
		var url = "http://localhost:8080/semmap-server/Server?callback=bla";
		$.ajax({
			url : "http://localhost:8080/semmap-server/Server",
			dataType : 'jsonp',
			jsonp : 'jsonpCallback',
			beforeSend : function(xhr) {
				xhr.overrideMimeType("text/javascript; charset=x-user-defined");
			}
		}).done(function(data) {
			window.alert("done");
			// $('#semmap-widget').html(data.html);
		});
	}

	function jsonpCallback(data) {
		$('#semmap-widget').html(data.html);
	}

	function checkLib() {
		// Localize jQuery variable
		var jQuery;

		/******** Load jQuery if not present *********/
		if (window.jQuery === undefined || window.jQuery.fn.jquery !== '1.8.2') {
			var script_tag = document.createElement('script');
			script_tag.setAttribute("type", "text/javascript");
			script_tag.setAttribute("src", "http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js");
			if (script_tag.readyState) {
				script_tag.onreadystatechange = function() {// For old versions of IE
					if (this.readyState == 'complete' || this.readyState == 'loaded') {
						scriptLoadHandler();
					}
				};
			} else {
				script_tag.onload = scriptLoadHandler;
			}
			// Try to find the head, otherwise default to the documentElement
			(document.getElementsByTagName("head")[0] || document.documentElement).appendChild(script_tag);
		} else {
			// The jQuery version on the window is the one we want to use
			jQuery = window.jQuery;
			main();
		}
	}

	/******** Called once jQuery has loaded ******/
	function scriptLoadHandler() {
		// Restore $ and window.jQuery to their previous values and store the
		// new jQuery in our local jQuery variable
		jQuery = window.jQuery.noConflict(true);
		// Call our main function
		main();
	}

	/******** Our main function ********/
	function main() {
		jQuery(document).ready(function($) {
			/******* Load CSS *******/
			var css_link = $("<link>", {
				rel : "stylesheet",
				type : "text/css",
				href : "css/style.css"
			});
			css_link.appendTo('head');

			/******* If redirect -> Get PARAM *******/
			getUser();

			/******* Load HTML *******/
			//initWidget();
			init();
		});
	}

	function initWidget() {
		$.ajax({
			url : "http://localhost:8080/semmap-server/Server",
			dataType : 'jsonp',
			jsonp : 'jsonpCallback',
			beforeSend : function(xhr) {
				xhr.overrideMimeType("text/javascript; charset=x-user-defined");
			}
		}).done(function(data) {
			window.alert("done");
			// $('#semmap-widget').html(data.html);
		});
	}

	return {
		start : function() {
			// Get Widget
			checkLib();
		},
		login : function() {
			login();
		},
		setProperty : function() {
			var prop = $('#selectProperty').val();
			if (prop == 'other') {
				prop = '';
				$('#property').css('display', '');
			} else
				$('#property').css('display', 'none');
			$('#property').val(prop);
		},
		checkForm : function() {
			var okay = true;
			if (webid.length == 0) {
				okay = false;
				alert('You need to log in! No user specified!');
			}
			if ($('#resource').val().length == 0) {
				okay = false;
				alert('No resource selected!');
			}
			if ($('#property').val().length < 3) {
				okay = false;
				alert('Property field empty!');
			}
			if ($('#file').val().length == 0) {
				okay = false;
				alert('No file selected!');
			}
			setRating();
			return okay;
		}
	}

})();

window.onload = WIDGET.start();

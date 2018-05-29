jQuery.sap.declare("sap.ui.demo.wt.util.rester");

sap.ui.demo.wt.util.rester = function() {};

sap.ui.demo.wt.util.rester.read = function(params) {
	var headers = {
		"Accept": "application/json",
		"X-Requested-With": "XMLHttpRequest"

	};

	if (typeof params !== "object" || !params.hasOwnProperty("url") || !params.hasOwnProperty("callbackSuccess") || !params.hasOwnProperty(
			"callbackError") || typeof params.url !== "string") {
		return;
	}

	jQuery.ajax({
		timeout: 600000,
		url: params.url,
		type: "GET",
		headers: headers,
		contentType: "application/json; charset=utf-8",
		success: function(data) {
			if (typeof params === "object" && params.hasOwnProperty("callbackSuccess") && typeof params.callbackSuccess === "function") {
				return params.callbackSuccess(data);
			}
		},
		async: true,
		error: function(oEvent, sStatus, sError) {
			if (typeof params === "object" && params.hasOwnProperty("callbackError") && typeof params.callbackError === "function") {
				return params.callbackError(oEvent, sStatus, sError);
			}
		}
	});
};

sap.ui.demo.wt.util.rester.create = function(params) {
	if (typeof params !== "object" || !params.hasOwnProperty("url") || !params.hasOwnProperty("callbackSuccess") || !params.hasOwnProperty(
			"callbackError") || typeof params.url !== "string") {
		return;
	}
	jQuery.ajax({
		timeout: 600000,
		url: params.url,
		type: "POST",
		data: params.data,
		dataType: 'json',
		contentType: "application/json; charset=utf-8",
		success: function(data) {
			if (typeof params === "object" && params.hasOwnProperty("callbackSuccess") && typeof params.callbackSuccess === "function") {
				return params.callbackSuccess(data);
			}
		},
		async: true,
		error: function(oEvent, sStatus, sError) {
			if (typeof params === "object" && params.hasOwnProperty("callbackError") && typeof params.callbackError === "function") {
				return params.callbackError(oEvent, sStatus, sError);
			}
		}
	});
};
sap.ui.define([
	"sap/ui/core/mvc/Controller",
	], function (Controller) {
	"use strict";

	return Controller.extend("com.acme.controller.Map", {

		onInit: function () {
			var cusData = $.ajax({
				type : "GET",
				url : "./api/oneTimeRecords",
				async : true
			});
			var that = this;
			$.when(cusData).then(function(cusData) {
				if (!jQuery.isArray(cusData)) {
					alert('Error loading customer records data from the backend. Please check the browser logs for details.');
				};
				that.data = cusData;
			}, function(error) {
				alert('Error loading customer records data from the backend. Please check the browser logs for details.');
			});
		},

		onAfterRendering: function() {
			var geocoder = new google.maps.Geocoder();
			var mapOptions = {
				center : new google.maps.LatLng(12.977788, 77.714421),
				zoom : 3,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			};

			this.map = new google.maps.Map(this.oView.byId("mapContainer").getDomRef(), mapOptions);

			// Multiple markers on google map...
			var salesOrder = this.salesOrders.items;
			var marker, i, j;
			var map = this.map;
			var orderID;
			var shippingAddress;
			var matchedOrderIds = [];
			var matchedAddress = [];

			//Compare the sales order and prepare the sales order and shipping address arrays
			for (i = 0; i < this.data.length; i++) {
				var orderID = this.data[i].orderId;
				var shippingAddress = this.data[i].shippingAddress;
				for (j = 0; j < salesOrder.length; j++) {
					var count = 0;
					if ((salesOrder[j].salesOrder) === orderID) {
						matchedOrderIds.push(orderID);
						matchedAddress.push(shippingAddress);
						count++;
						break;
					}
				}
			}

			//call geoCodeFn with matchedAddress as param
	        //This method will get co-ordinates for all the addresses
			geoCodeFn(matchedAddress, function(cords) {
				for (var i = 0; i < cords.length; i++) {
					map.setCenter(cords[i]);
					marker = new google.maps.Marker({
						map : map,
						position : cords[i],
						title : matchedOrderIds[i]
					});
					var infoWindow = new google.maps.InfoWindow();
					var content   = '<div id="content"><div><label><b>Sales Order: </label>'+ matchedOrderIds[i]+'</b></div>'+
		             '<div><label><b>Shipping Address: </label>'+ matchedAddress[i]+'</b></div></div>';
					google.maps.event.addListener(marker, 'click',
						(function(marker, content, infowindow) {
							return function() {
								infowindow.setContent(content);
								infowindow.open(map, marker);
							}
					})(marker, content, infoWindow));
				}

			});
		},

		setSalesOrders: function (data) {
			this.salesOrders = data;
		}

	});
});

geocoder = new google.maps.Geocoder();
//This is a call back function and used to return the co-ordinates for the desired address
//This uses geocoder functionality
//input param : addresses
//returns coords
	function geoCodeFn(addresses, callback) {
		var coords = [];
		for (var i = 0; i < addresses.length; i++) {
			currAddress = addresses[i];
			var geocoder = new google.maps.Geocoder();
			if (geocoder) {
				geocoder.geocode({
				'address' : currAddress
				}, function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					coords.push(results[0].geometry.location);
					if (coords.length == addresses.length) {
						if (typeof callback == 'function') {
							callback(coords);
						}
					}
				} else {
					throw ('No results found: ' + status);
				}
				});
			}
		}
	}

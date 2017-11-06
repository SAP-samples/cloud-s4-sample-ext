sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"sap/ui/model/json/JSONModel"
	], function (Controller, JSONModel) {
	"use strict";

	return Controller.extend("com.acme.controller.Chart", {

		onInit: function () {
			var monthLabels = ["January", "Febraury", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

			var oSalesOrderModel = new JSONModel("./api/salesOrders");
			var oSalesOrderModelAggregated = new JSONModel({});

			// aggregate the sales orders in the UI layer
			oSalesOrderModel.attachRequestCompleted(function () {
				var agg = [];
				var data = this.getData();
				if (!jQuery.isArray(data)) {
					alert('Error loading sales order data from the backend. Please check the browser logs for details.');
				};
				for (var i=0; i<data.length; i++) {
					var salesOrder = data[i];
					var month = new Date(salesOrder.creationDate).getMonth();
					if (!agg[month]) {
						agg[month] = {"monthNo": month, "month": monthLabels[month], "count": 0, "totalAmount": 0, "items": []};
					}
					agg[month].count++;
					agg[month].totalAmount += parseFloat(salesOrder.totalNetAmount);
					agg[month].items.push(salesOrder);
				}
				var newArray = new Array();
				for (var j=0; j<agg.length; j++){
					if(agg[j]){
						newArray.push(agg[j]);
					}					
				}
				oSalesOrderModelAggregated.setData(newArray);
			});
			var oVizFrame = this.getView().byId("idVizFrame");
			oVizFrame.setModel(oSalesOrderModelAggregated);
			oVizFrame.setLegendVisible(false);

			oVizFrame.attachSelectData(function (e) {
				var monthLabelClicked = e.getParameter('data')[0].data.Month;
				var monthNoClicked = monthLabels.indexOf(monthLabelClicked);
				var oTemp = oSalesOrderModelAggregated.getData();
				for (var k=0; k<oTemp.length; k++){
					if(monthNoClicked == oTemp[k].monthNo){
						var salesOrders = oSalesOrderModelAggregated.getProperty("/")[k];
					}
				}
				sap.ui.getCore().byId("appView").getController().showMap(salesOrders);
			});

		}

	});

});
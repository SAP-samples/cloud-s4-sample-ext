sap.ui.define([
	"sap/ui/core/mvc/Controller"
	], function (Controller) {
	"use strict";

	var oHeaderChartLabelTitle;
	var oHeaderLabel;
	return Controller.extend("com.acme.controller.App", {
		onInit: function () {
			this._mainPanel =  this.getView().byId("mainPanel");

			this._chartView = sap.ui.view({id:"chartView", viewName:"com.acme.view.Chart", type: sap.ui.core.mvc.ViewType.XML});
			this._mapView = sap.ui.view({id:"mapView", viewName:"com.acme.view.Map", type: sap.ui.core.mvc.ViewType.XML});

			this._mainPanel.addContent(this._chartView);
			oHeaderLabel = this._mainPanel.getHeaderToolbar("__toolbar0").getContent();
			oHeaderChartLabelTitle = oHeaderLabel[3].getText();
		},

		showMap: function (salesOrders) {						
			oHeaderLabel[3].setText(oHeaderChartLabelTitle + " for the month of " + salesOrders.month);
			this._mapView.getController().setSalesOrders(salesOrders);
			this._mainPanel.removeAllContent();
			this._mainPanel.addContent(this._mapView);
			this.byId("backBtn").setVisible(true);
		},

		goBack: function () {
			oHeaderLabel[3].setText(oHeaderChartLabelTitle);
			this._mainPanel.removeAllContent();
			this._mainPanel.addContent(this._chartView);
			this.byId("backBtn").setVisible(false);
		},

		init: function (params) {
			this.getView().byId("userLbl").setText(params.user);

			this.getView().byId("homeBtn").attachPress(function () {
				window.top.location = params.s4HomeHref;
			});
		}
	});

});
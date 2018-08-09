jQuery.sap.require("com.sap.build.standard.timesheetExportFromScratch.util.rester");
sap.ui.define(["sap/ui/core/mvc/Controller",
	"sap/m/MessageBox",
	"./utilities",
	"sap/ui/core/routing/History",
	"sap/ui/model/json/JSONModel",
	"sap/ui/model/Filter"
], function(BaseController, MessageBox, Utilities, History, JSONModel, Filter) {
	"use strict";

	return BaseController.extend("com.sap.build.standard.timesheetExportFromScratch.controller.Page1", {
		handleRouteMatched: function(oEvent) {

			var oParams = {};

			if (oEvent.mParameters.data.context) {
				this.sContext = oEvent.mParameters.data.context;
				var oPath;
				if (this.sContext) {
					oPath = {
						path: "/" + this.sContext,
						parameters: oParams
					};
					this.getView().bindObject(oPath);
				}
			}
		},
		inputId: "",
		customerId: "",

		onInit: function() {
			this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
			this.oRouter.getTarget("Page1").attachDisplay(jQuery.proxy(this.handleRouteMatched, this));
			//date formatters
			this.oFormat = sap.ui.core.format.DateFormat.getInstance({
				pattern: "yyyy-MM-dd"
			});
			this.oFormat2 = sap.ui.core.format.DateFormat.getInstance({
				pattern: "EE dd.MM.yyyy"
			});
			//create and set the model for reading data to the view
			var oModel = new JSONModel();
			oModel.setData({
				"days": [],
				"customers": [],
				"projects": [],
				"workPackages": [],
				"imageBase64": ""
			});
			this.getView().setModel(oModel);
			//create and set the model for selected data
			var oPrepareModel = new JSONModel();
			oPrepareModel.setData({
				"format": "",
				"ids": [],
				"signature": ""
			});
			this.getView().setModel(oPrepareModel, "oPrepareModel");
			this._readProjects();
			this._readCustomers();
			this._readWorkPackage();
			this._readBackendForCurrentWeek();
		},

		/******************Signature Pad Draw************************/
		onSign: function(oEvent) {
			var canvas = document.getElementById("signature-pad");
			var context = canvas.getContext("2d");
			canvas.width = 280;
			canvas.height = 180;
			context.fillStyle = "#fff";
			context.strokeStyle = "#444";
			context.lineWidth = 2.5;
			context.lineCap = "round";
			context.fillRect(0, 0, canvas.width, canvas.height);
			var disableSave = true;
			var pixels = [];
			var cpixels = [];
			var xyLast = {};
			var xyAddLast = {};
			var calculate = false;
			//functions
			function remove_event_listeners() {
				canvas.removeEventListener('mousemove', on_mousemove, false);
				canvas.removeEventListener('mouseup', on_mouseup, false);
				canvas.removeEventListener('touchmove', on_mousemove, false);
				canvas.removeEventListener('touchend', on_mouseup, false);

				document.body.removeEventListener('mouseup', on_mouseup, false);
				document.body.removeEventListener('touchend', on_mouseup, false);
			}

			function get_coords(e) {
				var x, y;

				if (e.changedTouches && e.changedTouches[0]) {
					var offsety = canvas.offsetTop || 0;
					var offsetx = canvas.offsetLeft || 0;

					x = e.changedTouches[0].pageX - offsetx;
					y = e.changedTouches[0].pageY - offsety;
				} else if (e.layerX || 0 == e.layerX) {

					if (e.layerX < 0) {
						x = e.offsetX; //-e.layerX;
					} else {
						x = e.offsetX; //e.offsetX;e.layerX;
					}
					//x = -e.layerX;
					if (e.layerY < 0) {
						y = e.offsetY; //-e.layerY;
					} else {
						y = e.offsetY; //e.layerY;
					}
					//	y = -e.layerY;
				} else if (e.offsetX || 0 == e.offsetX) {
					x = e.offsetX;
					y = e.offsetY;
				}

				return {
					x: x,
					y: y
				};
			}

			function on_mousedown(e) {
				e.preventDefault();
				e.stopPropagation();
				canvas.addEventListener('mouseup', on_mouseup, false);
				canvas.addEventListener('mousemove', on_mousemove, false);
				canvas.addEventListener('touchend', on_mouseup, false);
				canvas.addEventListener('touchmove', on_mousemove, false);
				document.body.addEventListener('mouseup', on_mouseup, false);
				document.body.addEventListener('touchend', on_mouseup, false);

				//	empty = false;
				var xy = get_coords(e);
				context.beginPath();
				pixels.push('moveStart');
				context.moveTo(xy.x, xy.y);
				pixels.push(xy.x, xy.y);
				xyLast = xy;
			}

			function on_mousemove(e, finish) {
				e.preventDefault();
				e.stopPropagation();
				var xy = get_coords(e);
				var xyAdd = {
					x: (xyLast.x + xy.x) / 2,
					y: (xyLast.y + xy.y) / 2
				};

				if (calculate) {
					var xLast = (xyAddLast.x + xyLast.x + xyAdd.x) / 3;
					var yLast = (xyAddLast.y + xyLast.y + xyAdd.y) / 3;
					pixels.push(xLast, yLast);
				} else {
					calculate = true;
				}
				context.quadraticCurveTo(xyLast.x, xyLast.y, xyAdd.x, xyAdd.y);
				pixels.push(xyAdd.x, xyAdd.y);
				context.stroke();
				context.beginPath();
				context.moveTo(xyAdd.x, xyAdd.y);
				xyAddLast = xyAdd;
				xyLast = xy;
			}

			function on_mouseup(e) {
				remove_event_listeners();
				disableSave = false;
				context.stroke();
				pixels.push('e');
				calculate = false;
			}
			canvas.addEventListener('touchstart', on_mousedown, false);
			canvas.addEventListener('mousedown', on_mousedown, false);
		},

		//prepare model for sending the signature and selected rows to backend
		prepareModel: function() {
			var daysModel = this.getView().getModel().getData().days;
			var postModel = this.getView().getModel("oPrepareModel").getData().ids;
			var tbl = this.byId("mainTable");
			var selectedItems = tbl.getSelectedItems();
			jQuery.each(selectedItems, function(index, item) {
				var path = item.getBindingContextPath();
				var pathSubstr = path.substr(6);
				var ID = daysModel[pathSubstr].id;
				postModel.push(
					ID
				);
			});
		},

		_sendingModel: function() {
			var data = JSON.stringify(this.getView().getModel("oPrepareModel").getData());

			var request = new XMLHttpRequest();
			request.open("POST", "/rest/export", true);
			request.setRequestHeader("Accept", "application/json");
			request.setRequestHeader("Content-Type", "application/json; charset=UTF-8");
			request.responseType = "blob";

			request.onload = function() {
				// Only handle status code 200
				if (request.status === 200) {
					// Try to find out the filename from the content disposition `filename` value
					var disposition = request.getResponseHeader("content-disposition");
					var matches = /filename=(.*)$/.exec(disposition);
					var filename = (matches != null && matches[1] ? matches[1] : "file.pdf");

					// The actual download
					var blob = new Blob([request.response], {
						type: "application/pdf"
							// type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
					});
					var link = document.createElement("a");
					link.href = window.URL.createObjectURL(blob);
					link.download = filename;
					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
				}
			};
			request.send(data);

		},

		saveButton: function() {
			var canvas = document.getElementById("signature-pad");
			var link = document.createElement('a');
			//	link.href = canvas.toDataURL('image/jpeg');
			link = canvas.toDataURL('image/png');
			var base64 = link.replace(/^data:image\/png;base64,/, "");
			this.getView().getModel().updateBindings(true);
			this.getView().getModel("oPrepareModel").getData().signature = base64;
			this.getView().getModel("oPrepareModel").getData().format = "pdf";
			this.prepareModel();
			this._sendingModel();
			this.getView().getModel("oPrepareModel").getData().ids = [];
			this.getView().getModel("oPrepareModel").getData().signature = "";
			this.onCloseDialog();
		},

		printPDF: function() {
			this.prepareModel();
			this.getView().getModel("oPrepareModel").getData().format = "pdf";
			this._sendingModel();
			this.getView().getModel("oPrepareModel").getData().ids = [];
			this.getView().getModel("oPrepareModel").getData().signature = "";
		},

		exportToExcel: function() {
			this.prepareModel();
			this.getView().getModel("oPrepareModel").getData().format = "xlsx";
			this._sendingModel();
			this.getView().getModel("oPrepareModel").getData().ids = [];
			this.getView().getModel("oPrepareModel").getData().signature = "";
		},

		clearButton: function() {
			var canvas = document.getElementById("signature-pad");
			var context = canvas.getContext("2d");
			context.clearRect(0, 0, canvas.width, canvas.height);
		},

		onDialogWithSizePress: function() {
			var oView = this.getView();
			var oDialog = oView.byId("SignatureFragment");
			if (!oDialog) {
				//	this.fragment = sap.ui.xmlfragment("sap.ui.demo.wt.view.Signature", this);
				//	this.byId("btnOpenDialog").addDependent(this.fragment);
				oDialog = sap.ui.xmlfragment(oView.getId(), "com.sap.build.standard.timesheetExportFromScratch.view.Signature", this);
				//	oDialog = sap.ui.xmlfragment(oView.getId(), "sap.ui.demo.wt.view.Signature", this);
				oView.addDependent(oDialog);
				//	this.byId("SignatureFragment").addDependent(this.fragment);
			}
			//	this.fragment.open();
			oDialog.open();
			var htmlId = this.byId("SignatureFragment").getContent()[0].getItems()[0].getItems()[0].sId;
			sap.ui.getCore().byId(htmlId).setContent(
				"<canvas id='signature-pad' width='280' height='180' class='signature-pad'></canvas>");
		},

		onCloseDialog: function() {
			this.byId("SignatureFragment").close();
			this.getView().getModel("oPrepareModel").getData().ids = [];
			this.getView().getModel("oPrepareModel").getData().signature = "";
			//	sap.ui.getCore().byId("SignatureFragment").close();
			//sap.ui.getCore().byId("SignatureFragment").destroyContent();
			//	sap.ui.getCore().byId("sap.ui.demo.wt.view.Signature").destroy();
			//sap.ui.getCore().byId("SignatureFragment").remove();
		},
		///////////////////////////////////////END SIGNATURE//////////////////////////////////////////////////////////////////////////////////////////

		//date formatter
		dateFormatter: function(date) {
			var oDate = new Date(date);
			var oFormatX = sap.ui.core.format.DateFormat.getInstance({
				pattern: "EE dd.MM.yyyy"
			});
			return oFormatX.format(oDate);
		},

		//read customer data for ovs selector
		_readCustomers: function() {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/rest/workforces/filters/customers",
				"callbackSuccess": function(data) {
					this.getView().getModel().getData().customers = data;
				}.bind(this),
				"callbackError": function(error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		//read projects data for ovs selector
		_readProjects: function() {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/rest/workforces/filters/projects",
				"callbackSuccess": function(data) {
					this._createProjectsModel(data);
				}.bind(this),
				"callbackError": function(error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		//read work package for ovs selector
		_readWorkPackage: function() {
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/rest/workforces/filters/workPackages",
				"callbackSuccess": function(data) {
					this.getView().getModel().getData().workPackages = data;
				}.bind(this),
				"callbackError": function(error) {
					this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		_createProjectsModel: function(data) {
			var prModel = [];
			var Items = data;
			jQuery.each(Items, function(index, item) {
				prModel.push({
					"ProjectName": item
				});
			});
			this.getView().getModel().getData().projects = prModel;
		},

		//read the data with filters from the JAVA backend
		readBackendData: function() {
			var sFrom = this.byId("dRange").getDateValue();
			var sTo = this.byId("dRange").getSecondDateValue();
			var sProject = this.byId("productInputA").getValue();
			var sWorkPackage = this.byId("productInputD").getValue();
			var sCustomer = this.customerId;
			// this._showBusyIndicator();
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/rest/workforces?customer=" + sCustomer + "&workPackage=" + sWorkPackage + "&from=" + this.oFormat.format(sFrom) +
					"&project=" + sProject + "&to=" +
					this.oFormat.format(sTo),
				"callbackSuccess": function(data) {
					// this._hideBusyIndicator();
					this.getView().getModel().getData().days = data;
					this.byId("mainTable").getModel().updateBindings(true);
				}.bind(this),
				"callbackError": function(error) {
					// this._hideBusyIndicator();
					// this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		//read the data for the current week
		_readBackendForCurrentWeek: function() {
			var currDate = new Date();
			var first = currDate.getDate() - currDate.getDay() + 1;
			var last = first + 4;
			var firstday = new Date(currDate.setDate(first));
			this.byId("dRange").setDateValue(firstday);
			var lastday = new Date(currDate.setDate(last));
			this.byId("dRange").setSecondDateValue(lastday);
			jQuery.proxy(sap.ui.demo.wt.util.rester.read({
				"url": "/rest/workforces?from=" + this.oFormat.format(firstday) + "&to=" + this.oFormat.format(lastday),
				"callbackSuccess": function(data) {
					// this._hideBusyIndicator();
					this.getView().getModel().getData().days = data;
					this.byId("mainTable").getModel().updateBindings(true);
				}.bind(this),
				"callbackError": function(error) {
					// this._hideBusyIndicator();
					// this.handleErrorMessageBoxPress(error.responseJSON.message);
				}.bind(this)
			}), this);
		},

		//valueHelp for Project
		handleValueHelp: function(oEvent) {
			var sInputValue = oEvent.getSource().getValue();
			this.inputId = oEvent.getSource().getId();

			// create value help dialog
			var selDialogue = new sap.m.SelectDialog({
				multiSelect: false,
				confirm: this._handleValueHelpClose1.bind(this),
				search: this._handleValueHelpSearch1,
				cancel: this._handleValueHelpClose1.bind(this)
			});
			//set model to Dialog
			var oModel1 = this.getView().getModel();
			selDialogue.setModel(oModel1);
			//bind items to Dialog
			selDialogue.bindAggregation("items", {
				path: "/projects",
				template: new sap.m.StandardListItem({
					title: "{ProjectName}"
				})
			});
			selDialogue.open();
			// create a filter for the binding
			selDialogue.getBinding("items").filter([new Filter(
				"ProjectName",
				sap.ui.model.FilterOperator.Contains, sInputValue
			)]);
			// open value help dialog filtered by the input value
			selDialogue.open(sInputValue);
		},

		handleValueHelp2: function(oEvent) {
			var sInputValue = oEvent.getSource().getValue();
			this.inputId = oEvent.getSource().getId();

			// create value help dialog
			var selDialogue = new sap.m.SelectDialog({
				multiSelect: false,
				confirm: this._handleValueHelpClose2.bind(this),
				search: this._handleValueHelpSearch2,
				cancel: this._handleValueHelpClose2.bind(this)
			});
			//set model to Dialog
			var oModel2 = this.getView().getModel();
			selDialogue.setModel(oModel2);
			//bind items to Dialog
			selDialogue.bindAggregation("items", {
				path: "/customers",
				template: new sap.m.StandardListItem({
					title: "{customerFullName}",
					description: "{customerId}"
				})
			});
			selDialogue.open();
			// create a filter for the binding
			selDialogue.getBinding("items").filter([new Filter(
				"customerFullName",
				sap.ui.model.FilterOperator.Contains, sInputValue
			)]);
			// open value help dialog filtered by the input value
			selDialogue.open(sInputValue);
		},

		handleValueHelp3: function(oEvent) {
			var sInputValue = oEvent.getSource().getValue();
			this.inputId = oEvent.getSource().getId();

			// create value help dialog
			var selDialogue = new sap.m.SelectDialog({
				multiSelect: false,
				confirm: this._handleValueHelpClose3.bind(this),
				search: this._handleValueHelpSearch3,
				cancel: this._handleValueHelpClose3.bind(this)
			});
			//set model to Dialog
			var oModel2 = this.getView().getModel();
			selDialogue.setModel(oModel2);
			//bind items to Dialog
			selDialogue.bindAggregation("items", {
				path: "/workPackages",
				template: new sap.m.StandardListItem({
					title: "{workPackage}",
					description: "{workPackageName}"
				})
			});
			selDialogue.open();
			// create a filter for the binding
			selDialogue.getBinding("items").filter([new Filter(
				"workPackageName",
				sap.ui.model.FilterOperator.Contains, sInputValue
			)]);
			// open value help dialog filtered by the input value
			selDialogue.open(sInputValue);
		},

		_handleValueHelpSearch1: function(evt) {
			var sValue = evt.getParameter("value");
			var oFilter = new Filter(
				"ProjectName",
				sap.ui.model.FilterOperator.Contains, sValue
			);
			evt.getSource().getBinding("items").filter([oFilter]);
		},

		_handleValueHelpSearch2: function(evt) {
			var sValue = evt.getParameter("value");
			var oFilter = new Filter(
				"customerFullName",
				sap.ui.model.FilterOperator.Contains, sValue
			);
			evt.getSource().getBinding("items").filter([oFilter]);
		},

		_handleValueHelpSearch3: function(evt) {
			var sValue = evt.getParameter("value");
			var oFilter = new Filter(
				"workPackageName",
				sap.ui.model.FilterOperator.Contains, sValue
			);
			evt.getSource().getBinding("items").filter([oFilter]);
		},

		_handleValueHelpClose1: function(evt) {
			var oSelectedItem = evt.getParameter("selectedItem");
			var productInput = this.getView().byId(this.inputId);
			if (oSelectedItem) {
				productInput.setValue(oSelectedItem.getTitle());
			} else {
				productInput.setValue("");
			}
			evt.getSource().getBinding("items").filter([]);
		},

		_handleValueHelpClose2: function(evt) {
			var oSelectedItem = evt.getParameter("selectedItem");

			var productInput = this.getView().byId(this.inputId);
			if (oSelectedItem) {
				productInput.setValue(oSelectedItem.getTitle());
				this.customerId = oSelectedItem.getDescription();
			} else {
				productInput.setValue("");
				this.customerId = "";
			}
			evt.getSource().getBinding("items").filter([]);
		},

		_handleValueHelpClose3: function(evt) {
			var oSelectedItem = evt.getParameter("selectedItem");
			var productInput = this.getView().byId(this.inputId);
			if (oSelectedItem) {
				productInput.setValue(oSelectedItem.getTitle());
			} else {
				productInput.setValue("");
			}
			evt.getSource().getBinding("items").filter([]);
		}
	});
}, /* bExport= */ true);
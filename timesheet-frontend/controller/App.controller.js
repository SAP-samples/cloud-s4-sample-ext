jQuery.sap.require("sap.ui.demo.wt.util.rester");
//change the entire row color for weekend days
var myFormatter = {
	colorFormatter: function(date) {
		var oDate = new Date(date);
		var oFormatX = sap.ui.core.format.DateFormat.getInstance({
			pattern: "EE dd.MM.yyyy"
		});
		if (oDate.getDay() == 6 || oDate.getDay() == 0) {
			this.getParent().addStyleClass("yellow");
			return oFormatX.format(oDate);
		}
		return oFormatX.format(oDate);
	}
};
sap.ui.define([
		"sap/ui/core/mvc/Controller",
		"sap/m/MessageBox",
		"./utilities",
		"sap/ui/core/routing/History",
		"sap/ui/model/json/JSONModel",
		"sap/ui/core/format/DateFormat",
		"sap/m/Dialog",
		"sap/m/Button"
	], function(BaseController, MessageBox, Utilities, History, JSONModel, DateFormat, Dialog, Button) {
		"use strict";
		return BaseController.extend("sap.ui.demo.wt.controller.App", {
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
			_selectedPathForCopy: "",
			onInit: function() {
				this.mBindingOptions = {};
				this.oRouter = sap.ui.core.UIComponent.getRouterFor(this);
				this.oRouter.getTarget("App").attachDisplay(jQuery.proxy(this.handleRouteMatched, this));
				//format the date
				this.oFormat = sap.ui.core.format.DateFormat.getInstance({
					pattern: "dd-MM-yyyy"
				});
				this.oFormat2 = sap.ui.core.format.DateFormat.getInstance({
					pattern: "EE dd.MM.yyyy"
				});
				//create and set the model for the View
				var oModel = new JSONModel();
				oModel.setData({
					"days": [],
					"taskTypes": [],
					"buttonsVisible": {
						"edit": false,
						"save": false,
						"cancel": false,
						"copy": false,
						"paste": false,
						"clear": false
					},
					"buttonsEnabled": {
						"edit": true,
						"save": true,
						"cancel": true,
						"copy": true,
						"paste": true,
						"clear": true
					},
					"readOnly": {
						"setAll": false
					},
					"currentUser": ""
				});
				this.getView().setModel(oModel);
				this._readTaskTypes();
				this._readCurrentUser();
				this.getView().getModel().updateBindings(true);
			},
			//check if the column is selected
			columnSelectCheck: function() {
				var select = this.byId("columnList").getSelected();
				return select;
			},
			//set properties for buttons
			setButtonsForSelectionChange: function() {
				this.getView().getModel().getData().buttonsEnabled.paste = true;
				this.getView().getModel().getData().buttonsVisible.paste = true;
				this.getView().getModel().getData().buttonsVisible.clear = false;
				this.getView().getModel().getData().buttonsEnabled.clear = false;
			},

			setButtonsForSaveAndCancel: function() {
				this.setTableMode("None");
				this.getView().getModel().getData().readOnly.setAll = false;
				this.getView().getModel().getData().buttonsVisible.edit = true;
				this.getView().getModel().getData().buttonsVisible.cancel = false;
				this.getView().getModel().getData().buttonsVisible.save = false;
				this.getView().getModel().getData().buttonsVisible.clear = false;
				this.getView().getModel().getData().buttonsVisible.copy = false;
				this.getView().getModel().getData().buttonsVisible.paste = false;
				this.getView().getModel().updateBindings(true);
			},

			setButtonsForCopy: function() {
				this.getView().getModel().getData().buttonsVisible.edit = false;
				this.getView().getModel().getData().buttonsVisible.cancel = true;
				this.getView().getModel().getData().buttonsVisible.save = true;
				this.getView().getModel().getData().buttonsEnabled.save = false;
				this.getView().getModel().getData().buttonsVisible.copy = false;
				this.getView().getModel().getData().buttonsEnabled.copy = false;
				this.getView().getModel().getData().buttonsVisible.clear = false;
				this.getView().getModel().getData().buttonsEnabled.paste = false;
				this.getView().getModel().getData().buttonsVisible.paste = true;
			},

			setButtonsForPaste: function() {
				this.getView().getModel().getData().buttonsEnabled.save = true;
				this.getView().getModel().getData().buttonsVisible.paste = false;
				this.getView().getModel().getData().buttonsEnabled.copy = false;
				this.getView().getModel().getData().buttonsVisible.copy = true;
				this.getView().getModel().getData().buttonsEnabled.clear = false;
				this.getView().getModel().getData().buttonsVisible.clear = true;
			},

			setButtonsForEdit: function() {
				this.getView().getModel().getData().buttonsVisible.edit = false;
				this.getView().getModel().getData().buttonsVisible.cancel = true;
				this.getView().getModel().getData().buttonsVisible.save = true;
				this.getView().getModel().getData().buttonsEnabled.save = false;
				this.getView().getModel().getData().buttonsVisible.copy = true;
				this.getView().getModel().getData().buttonsEnabled.copy = false;
				this.getView().getModel().getData().buttonsEnabled.clear = false;
				this.getView().getModel().getData().buttonsVisible.clear = true;
			},

			//set buttons properties according to selection mode of the table
			onSelectionChange: function(oEvent) {
				var sel = oEvent.getParameter("selected");
				var tm = this.byId("daysTable").getMode();
				var oSelItems = this.byId("daysTable").getSelectedItems();
				var oSelItemsLength = oSelItems.length;
				if (tm === "MultiSelect" && sel && oSelItemsLength >= 1) {
					this.setButtonsForSelectionChange();
					this.getView().getModel().updateBindings(true);
				} else if (tm === "SingleSelectLeft") {
					this.getView().getModel().getData().buttonsEnabled.copy = true;
					this.getView().getModel().getData().buttonsEnabled.clear = true;
					this.getView().getModel().getData().buttonsEnabled.paste = false;
					this.getView().getModel().getData().buttonsVisible.paste = false;
					this.getView().getModel().updateBindings(true);
				} else if (tm === "MultiSelect" && !sel && oSelItemsLength < 1) {
					this.getView().getModel().getData().buttonsEnabled.paste = false;
					this.getView().getModel().getData().buttonsVisible.paste = true;
					this.getView().getModel().getData().buttonsVisible.clear = false;
					this.getView().getModel().getData().buttonsEnabled.clear = false;
					this.getView().getModel().updateBindings(true);
				} else if (tm === "MultiSelect" && !sel && oSelItemsLength >= 1) {
					this.setButtonsForSelectionChange();
					this.getView().getModel().updateBindings(true);
				}
			},
			//set the table mode
			setTableMode: function(data) {
				this.byId("daysTable").setMode(data);
			},
			//get the selected item(row) from the table
			getSelectionItem: function() {
				this._selectedPathForCopy = this.byId("daysTable").getSelectedItem().getBindingContextPath();
			},
			//event for Edit button
			onEdit: function() {
				this.setTableMode("SingleSelectLeft");
				this.getView().getModel().getData().readOnly.setAll = true;
				this.setButtonsForEdit();
				this.getView().getModel().updateBindings(true);
			},
			//read the data from the Java app for the Cancel and Save events
			readForCancelAndSave: function() {
				var sFrom = this.byId("dRange").getDateValue();
				var sTo = this.byId("dRange").getSecondDateValue();
				this._showBusyIndicator();
				jQuery.proxy(sap.ui.demo.wt.util.rester.read({
					"url": "/rest/timesheet?dateFrom=" + this.oFormat.format(sFrom) + "&dateTo=" + this.oFormat.format(sTo),
					"callbackSuccess": function(data) {
						this._hideBusyIndicator();
						this.enhanceModel(data);
						this.getView().getModel().getData().days = data;
						this.byId("daysTable").getModel().updateBindings(true);
						this.selectTaskTypes();
					}.bind(this),
					"callbackError": function(error) {
						//this.getView().setBusy(false);
						this._hideBusyIndicator();
						this.handleErrorMessageBoxPress(error.responseJSON.message);
					}.bind(this)
				}), this);
			},
			//event for Cancel button
			onCancel: function() {
				this.readForCancelAndSave();
				this.setHighlight();
				this.setButtonsForSaveAndCancel();
			},
			//event for Copy button
			onCopy: function() {
				this.setButtonsForCopy();
				this.getSelectionItem();
				this.byId("daysTable").getSelectedItem().setProperty("highlight", "Information");
				this.setTableMode("MultiSelect");
				this.byId("daysTable").removeSelections();
				this.getView().getModel().updateBindings(true);
			},
			//copy the data from one row and paste it to the selected rows
			pasteValues: function(toPath) {
				var tblModel = this.byId("daysTable").getModel();
				tblModel.getProperty(toPath).abbr = tblModel.getProperty(this._selectedPathForCopy).abbr;
				tblModel.getProperty(toPath).taskTime.first.startTime = tblModel.getProperty(this._selectedPathForCopy).taskTime.first.startTime;
				tblModel.getProperty(toPath).taskTime.second.endTime = tblModel.getProperty(this._selectedPathForCopy).taskTime.second.endTime;
				tblModel.getProperty(toPath).breakTime.startTime = tblModel.getProperty(this._selectedPathForCopy).breakTime.startTime;
				tblModel.getProperty(toPath).breakTime.duration = tblModel.getProperty(this._selectedPathForCopy).breakTime.duration;
				tblModel.getProperty(toPath).totalTravelTime = tblModel.getProperty(this._selectedPathForCopy).totalTravelTime;
			},
			//set the highlight property for every table item to none
			setHighlight: function() {
				var oItems = this.byId("daysTable").getItems();
				jQuery.each(oItems, function(cellIndex) {
					oItems[cellIndex].mProperties.highlight = "none";
				});
			},
			//event for Paste button
			onPaste: function() {
				var tbl = this.byId("daysTable");
				var that = this;
				$.each(tbl.getSelectedItems(), function(index, elm) {
					that.pasteValues(elm.getBindingContextPath());
				});
				this.setButtonsForPaste();
				this.byId("daysTable").removeSelections();
				this.setTableMode("SingleSelectLeft");
				this.setHighlight();
				this.getView().getModel().updateBindings(true);
			},
			//event for Clear button
			onClear: function() {
				this.getSelectionItem();
				var tblModel = this.byId("daysTable").getModel();
				tblModel.getProperty(this._selectedPathForCopy).abbr = "DFLT";
				tblModel.getProperty(this._selectedPathForCopy).taskTime.first.startTime = "";
				tblModel.getProperty(this._selectedPathForCopy).taskTime.second.endTime = "";
				tblModel.getProperty(this._selectedPathForCopy).breakTime.startTime = "";
				tblModel.getProperty(this._selectedPathForCopy).breakTime.duration = 0;
				tblModel.getProperty(this._selectedPathForCopy).totalTravelTime = 0;
				this.getView().getModel().getData().buttonsEnabled.save = true;
				this.getView().getModel().getData().buttonsEnabled.copy = false;
				this.getView().getModel().getData().buttonsEnabled.clear = false;
				this.getView().getModel().updateBindings(true);
			},
			//method for showing errors
			handleErrorMessageBoxPress: function(msg) {
				var bCompact = !!this.getView().$().closest(".sapUiSizeCompact").length;
				MessageBox.error(msg, {
					styleClass: bCompact ? "sapUiSizeCompact" : ""
				});
			},
			//if the task is changed the Save button is enabled
			onTaskChange: function() {
				this.checkTaskType();
				// this.getView().getModel().getData().buttonsEnabled.save = true;
			},
			//for each input change of every cell, we need to validate the data
			onInputChange: function(oEvent) {
				var tblModel = this.byId("daysTable").getModel();
				var a = oEvent.getSource().getBinding("value");
				var oTaskType = tblModel.getProperty(a.oContext.sPath).abbr;
				var oStTime = tblModel.getProperty(a.oContext.sPath).taskTime.first.startTime;
				var oEndTime = tblModel.getProperty(a.oContext.sPath).taskTime.second.endTime;
				var oBrkStart = tblModel.getProperty(a.oContext.sPath).breakTime.startTime;
				// var oTrvDur = tblModel.getProperty(a.oContext.sPath).totalTravelTime;    
				if (a.sPath === "taskTime/first/startTime" && (oEndTime < oStTime && oStTime > oBrkStart && (!oEndTime == "" || !oBrkStart == "") ||
						oEndTime == oStTime)) {
					tblModel.getProperty(a.oContext.sPath).taskTime.first.startTime = "";
					var msg1 = "Start Time greater or equal with End Time";
					this.handleErrorMessageBoxPress(msg1);
				} else if (a.sPath === "taskTime/second/endTime" && (oEndTime < oStTime || oEndTime == oStTime)) {
					tblModel.getProperty(a.oContext.sPath).taskTime.second.endTime = "";
					// oEvent.getSource().setValueState(sap.ui.core.ValueState.Warning);
					var msg2 = "End Time lower or equal with Start Time";
					this.handleErrorMessageBoxPress(msg2);
				} else if (a.sPath === "breakTime/startTime" && (oBrkStart < oStTime || oBrkStart > oEndTime) && (!oStTime == "" && !oEndTime == "")) {
					tblModel.getProperty(a.oContext.sPath).breakTime.startTime = "";
					var msg3 = "Break should starts between Start Time and End Time";
					this.handleErrorMessageBoxPress(msg3);
				} else if (a.sPath === "breakTime/startTime" && (oStTime == "" || oEndTime == "")) {
					tblModel.getProperty(a.oContext.sPath).breakTime.startTime = "";
					var msg4 = "Please insert Start time and End time";
					this.handleErrorMessageBoxPress(msg4);
				} else if (!(oTaskType == "DFLT") && a.sPath === "breakTime/duration") {
					this.getView().getModel().getData().buttonsEnabled.save = true;
					this.getView().getModel().updateBindings(true);
				} else if (!(oTaskType == "DFLT") && a.sPath === "totalTravelTime") {
					this.getView().getModel().getData().buttonsEnabled.save = true;
					this.getView().getModel().updateBindings(true);
				} else if (!(oTaskType == "DFLT") && a.sPath === "taskTime/first/startTime") {
					this.getView().getModel().getData().buttonsEnabled.save = true;
					this.getView().getModel().updateBindings(true);
				} else if (!(oTaskType == "DFLT") && a.sPath === "breakTime/startTime") {
					this.getView().getModel().getData().buttonsEnabled.save = true;
					this.getView().getModel().updateBindings(true);
				} else if (!(oTaskType == "DFLT") && a.sPath === "taskTime/second/endTime") {
					this.getView().getModel().getData().buttonsEnabled.save = true;
					this.getView().getModel().updateBindings(true);
				}
			},

			onInputSubmit: function(oEvent) {
				var tblModel2 = this.byId("daysTable").getModel();
				var p = oEvent.getSource().getBinding("value");
				var oBrkDur = tblModel2.getProperty(p.oContext.sPath).breakTime.duration;
				if (p.sPath === "breakTime/duration" && oBrkDur >= "30") {
					oEvent.getSource().setValueState("None");
					this.getView().getModel().updateBindings(true);
				} else {
					oEvent.getSource().setValueState("Warning");
					this.getView().getModel().updateBindings(true);
				}
			},

			handleValidationWarning: function(oEvent) {
				oEvent.getSource().setValueState(sap.ui.core.ValueState.Warning);
			},
			//get the task types from the Java app
			_readTaskTypes: function() {
				jQuery.proxy(sap.ui.demo.wt.util.rester.read({
					"url": "/rest/tasktype",
					"callbackSuccess": function(data) {
						this.getView().getModel().getData().taskTypes = data;
					}.bind(this),
					"callbackError": function(error) {
						this.handleErrorMessageBoxPress(error.responseJSON.message);
					}.bind(this)
				}), this);
			},
			enhanceModel: function(data) {
				var that = this;
				jQuery.each(data, function(index) {
					data[index].taskTypes = that.getView().getModel().getData().taskTypes;
				});
			},

			checkTaskType: function() {
				var tableModel = this.byId("daysTable").getModel().getData().days;
				var tableItems = this.byId("daysTable").getItems();
				var that = this;
				jQuery.each(tableItems, function(indexTable) {
					if (!(tableModel[indexTable].abbr == "DFLT")) {
						that.getView().getModel().getData().buttonsEnabled.save = true;
						that.getView().getModel().updateBindings(true);
					} else {
						that.getView().getModel().getData().buttonsEnabled.save = false;
						that.getView().getModel().updateBindings(true);
					}
				});
			},

			selectTaskTypes: function() {
				var tableModel = this.getView().getModel().getData().days;
				var tableItems = this.byId("daysTable").getItems();
				jQuery.each(tableItems, function(indexTable, item) {
					var tableCells = item.getCells();
					var comboTaskType = tableCells[1];
					comboTaskType.setSelectedKey(tableModel[indexTable].taskTime.first.taskType.abbr);
				});
			},
			prepareModelForSave: function() {
				var tableModel = this.getView().getModel().getData().days;
				var tableItems = this.byId("daysTable").getItems();
				jQuery.each(tableItems, function(indexTable, item) {
					var tableCells = item.getCells();
					var comboTaskType = tableCells[1];
					tableModel[indexTable].taskTime.first.taskType = comboTaskType.getSelectedKey();
					tableModel[indexTable].taskTime.second.taskType = comboTaskType.getSelectedKey();
					tableModel[indexTable].breakTime.taskType = tableModel[indexTable].breakTime.taskType.abbr;
					tableModel[indexTable].travelTime.first.taskType = tableModel[indexTable].travelTime.first.taskType.abbr;
					tableModel[indexTable].travelTime.second.taskType = tableModel[indexTable].travelTime.second.taskType.abbr;
					delete tableModel[indexTable].taskTypes;
				});
				return tableModel;
			},
			//event for calendar selection days
			handlerDateRangeSelection: function(oEvent) {
				var sFrom = oEvent.getParameter("from");
				var sTo = oEvent.getParameter("to");
				var diff = Math.abs(sFrom.getTime() - sTo.getTime());
				var diffD = Math.ceil(diff / (1000 * 60 * 60 * 24) + 1);
				if (diffD <= 7) {
					//this.getView().setBusy(true);
					this._showBusyIndicator();
					jQuery.proxy(sap.ui.demo.wt.util.rester.read({
						"url": "/rest/timesheet?dateFrom=" + this.oFormat.format(sFrom) + "&dateTo=" + this.oFormat.format(sTo),
						"callbackSuccess": function(data) {
							this.enhanceModel(data);
							this.getView().getModel().getData().days = data;
							this.byId("daysTable").getModel().updateBindings(true);
							this.selectTaskTypes();
							this._hideBusyIndicator();
							//this.getView().setBusy(false);
						}.bind(this),
						"callbackError": function(error) {
							this._hideBusyIndicator();
							this.handleErrorMessageBoxPress(error.responseJSON.message);
						}.bind(this)
					}), this);
					this.setButtonsForSaveAndCancel();
				} else {
					var msg5 = "You need to select max 7 days";
					this.handleErrorMessageBoxPress(msg5);
				}
			},
			//read the user who is logged in and update the model
			_readCurrentUser: function() {
				jQuery.proxy(sap.ui.demo.wt.util.rester.read({
					"url": "/rest/user",
					"callbackSuccess": function(data) {
						this.getView().getModel().getData().currentUser = data.id;
						this.getView().getModel().refresh(true);
					}.bind(this),
					"callbackError": function(err) {
						sap.m.MessageToast.show("Error reading user.", {
							duration: 3500
						});
					}.bind(this)
				}), this);
			},
			//event for Save button
			onSave: function() {
				var data = JSON.stringify(this.prepareModelForSave(this.getView().getModel().getData().days));
				//this.getView().setBusy(true);
				this._showBusyIndicator();
				jQuery.proxy(sap.ui.demo.wt.util.rester.create({
					"data": data,
					"url": "/rest/timesheet/",
					"callbackSuccess": function() {
						sap.m.MessageToast.show("Data has been saved ", {
							duration: 3500
						});
						//this.getView().setBusy(false);
						this._hideBusyIndicator();
						this.readForCancelAndSave();
						this.setButtonsForSaveAndCancel();
					}.bind(this),
					"callbackError": function(error) {
						//this.getView().setBusy(false);
						this._hideBusyIndicator();
						this.readForCancelAndSave();
						this.handleErrorMessageBoxPress(error.responseJSON.message);
						this.getView().getModel().getData().buttonsEnabled.save = false;
						this.getView().getModel().updateBindings(true);
					}.bind(this)
				}), this);
				// this.setButtonsForSaveAndCancel();
			},

			_handleLogout: function() {
				window.location.replace("logout.html");
			},
			_showBusyIndicator: function() {
				sap.ui.core.BusyIndicator.show(0);
			},
			_hideBusyIndicator: function() {
				sap.ui.core.BusyIndicator.hide();
			}
		});
	}, /* bExport= */
	true);
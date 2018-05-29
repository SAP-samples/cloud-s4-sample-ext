jQuery.sap.declare("sap.ui.demo.wt.util.Formatter");

sap.ui.demo.wt.util.Formatter = {

	formatDate: function(date) {
		var oDate = new Date(date);
		if (oDate.getDay() == 6 || oDate.getDay() == 0) {
			this.addStyleClass("yellow");
		}
		return oDate;
	}

};
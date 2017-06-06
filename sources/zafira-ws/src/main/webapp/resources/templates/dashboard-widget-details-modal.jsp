<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>Widget settings
	</h3>
</div>
<div class="modal-body">
	<form name="dashboardWidgetForm">
		<div class="form-group">
			<label>Widget</label> 
			<input type="text" class="form-control" data-ng-model="widget.title" required></input>
		</div>
		<div class="form-group">
			<label>Size</label> 
			<select class="form-control validation" data-ng-model="widget.size" required>
				<option data-ng-value=4>1/3 screen</option>
				<option data-ng-value=8>2/3 screen</option>
				<option data-ng-value=12>Full-screen</option>
			</select>
		</div>
		<div class="form-group">
			<label>Position</label> 
			<input type="text" class="form-control validation" data-ng-model="widget.position" required/>
		</div>
	</form>
</div>
<div class="modal-footer">
	<button data-ng-if="!isNew" class="btn btn-danger" data-ng-really-message="Do you really want to delete widget from dashboard?" data-ng-really-click="deleteDashboardWidget(widget)">Delete</button>
	<button data-ng-if="isNew" class="btn btn-success" data-ng-click="addDashboardWidget(widget)"  data-ng-disabled="dashboardWidgetForm.$invalid">
    	Add
    </button>
    <button data-ng-if="!isNew" class="btn btn-success" data-ng-click="updateDashboardWidget(widget)"  data-ng-disabled="dashboardWidgetForm.$invalid">
    	Save
    </button>
</div>
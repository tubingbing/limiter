window._originalAlert = window._originalAlert || window.alert;
window.alert = function(text, retFun) {
    var bootStrapAlert = function() {
        if(! $.fn.modal.Constructor)
            return false;
        if($("#windowAlertModal")[0]) {
        	$("#windowAlertModal").remove();
        	$(".modal-backdrop").remove();
        }
        $('body').append(
        	'<div id="windowAlertModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true"> ' +
        		'<div class="modal-dialog"> ' +
        			'<div class="modal-content"> ' +
        				'<div class="modal-body">' +
        	      			'<button type="button" class="close" onclick="$(\'#windowAlertModal\').modal(\'hide\');" data-dismiss="modal" aria-hidden="true">&times;</button>'+
        	        		'<p class="word-break-all"> alert text </p>' +
        	      		'</div>'+
        	      		'<div class="modal-footer">'+
        	        		'<button class="btn btn-xs btn-danger" onclick="$(\'#windowAlertModal\').modal(\'hide\');" style="border-width: 2px;" data-dismiss="modal" aria-hidden="true">关闭</button>'+
        	      		'</div>'+
        			'</div>'+
        		'</div>'+
        	'</div>');
        return true;
    };
    if ( bootStrapAlert() ){
        $('#windowAlertModal .modal-body p').html(text);
        $('#windowAlertModal').modal();
        if(retFun){
        	$("#windowAlertModal").on("hidden.bs.modal", retFun);
        } else {
        	$("#windowAlertModal").on("hidden.bs.modal", null);
        }
    }  else {
        console.log('bootstrap was not found');
        window._originalAlert(text);
    }
};
window._originalConfirm = window._originalConfirm || window.confirm;
window.confirm = function(text, cb, cl) {
    var initTemplate = function(){
      if($('#windowConfirmModal').length == 1)
        return true;
      $('body').append('<div id="windowConfirmModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true"> '+
    		  	'<div class="modal-dialog"> '+
    		  		'<div class="modal-content"> '+
    		  			'<div class="modal-body" style="padding:15px;"> '+
    		  				'<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button> '+
    		  				'<p class="word-break-all"> alert text </p> '+
    		  			'</div> '+
	    		  		'<div class="modal-footer"> '+
	    		  			'<button class="btn btn-xs btn-primary" id="btn_index_yes" style="border-width: 2px;" data-dismiss="modal" aria-hidden="true">确定</button> '+
	    		  			'<button class="btn btn-xs btn-danger" id="btn_index_no" style="border-width: 2px;" data-dismiss="modal" aria-hidden="true">取消</button> '+
	    		  		'</div> '+
    		  		'</div> '+
    		  	'</div> '+
    		  '</div>');
    };

    var bootStrapConfirm = function() {
      if(! $.fn.modal.Constructor)
          return false;

      $('body').off('click', '#btn_index_yes');
      $('body').off('click', '#btn_index_no');

      function confirm() { cb(true); }
      function deny() { if(cl){cl(false);}}

      $('body').on('click', '#btn_index_yes', confirm);
      $('body').on('click', '#btn_index_no', deny);

      return true;
    };

    initTemplate();

    if ( bootStrapConfirm() ){
        $('#windowConfirmModal .modal-body p').html(text);
        $('#windowConfirmModal').modal();
    }  else {
        console.log('bootstrap was not found');
        cb(window._originalConfirm(text));
    }
};
/**
 * 弹出框的放大还原JS对象
 */
var iMoveJS = {
	//原始的窗口宽度
	OLD_WIN_WIDTH : 400,
	//原始的窗口高度
	OLD_WIN_HEIGHT : 200,
	//原始窗口的大小
	OLD_DIALOG_WIDTH : 800,
	//原始BODY最大高度
	OLD_DIALOGBODY_WIDTH : "350px",
	
	//初始化可以变化的窗口
	createMoveWin : (function(winId){
		if(!$("#" + winId)[0]) {
			alert("创建可变窗口失败，无法找到窗口对象。");
			return false;
		}
		var winHeadObj = $("#" + winId + "  .close");
		if(!winHeadObj[0]) {
			alert("创建可变窗口失败，需要可变窗口无标题信息。");
			return false;
		}
		winHeadObj.before("<a href='javascript:;' title='还原' style='display:none;' id='a_iMove_resize' onclick='iMoveJS.moveResize(\""+ winId + "\");'><i class='icon-resize-small resize'></i></a>" +
				"<a href='javascript:;' onclick='iMoveJS.moveFullScreen(\""+ winId + "\");' title='全屏' id='a_iMove_fullscreen'><i class='icon-fullscreen fullscreen'></i></a>");
	}),
	
	//全屏
	moveFullScreen : (function(winId){
		var winObj = $("#" + winId + " .modal-content");
		var dialogObj = $("#" + winId + " .modal-dialog");
		var dialogBOdyObj = $("#" + winId + " .modal-body");
		iMoveJS.OLD_WIN_WIDTH = winObj.width();
		iMoveJS.OLD_WIN_HEIGHT = winObj.height();
		iMoveJS.OLD_DIALOG_WIDTH = dialogObj.width();
		dialogObj.css({"padding":"0px", "width":"auto"});
		winObj.width($(window).width()).height($(window).height());
		var footerHeight = $("#" + winId + " .modal-footer").outerHeight();
		iMoveJS.OLD_DIALOGBODY_WIDTH = dialogBOdyObj.css("max-height");
		//设置body的高度
		dialogBOdyObj.css("max-height", ($(window).height() - dialogBOdyObj.offset().top - footerHeight) + "px");
		$("#a_iMove_fullscreen").hide();
		$("#a_iMove_resize").show();
	}),
	
	//还原
	moveResize : (function(winId){
		var winObj = $("#" + winId + " .modal-content");
		var dialogObj = $("#" + winId + " .modal-dialog");
		dialogObj.css({"padding" : "30px 10px", "width" : (iMoveJS.OLD_DIALOG_WIDTH + "px")});
		winObj.width(iMoveJS.OLD_WIN_WIDTH).height(iMoveJS.OLD_WIN_HEIGHT);
		$("#" + winId + " .modal-body").css("max-height", iMoveJS.OLD_DIALOGBODY_WIDTH);
		$("#a_iMove_fullscreen").show();
		$("#a_iMove_resize").hide();
	})
};
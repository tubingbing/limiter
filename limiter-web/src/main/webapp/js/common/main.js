var mainJS = {
	letterInterval : null,
	timeout : null,
	toLy : (function(){

	}),
	toCi : (function(){
		$("#JONE-CXJC").click();
	}),
	toDownload : (function(){
		$("#JONE-KFGJ").click();
	}),
	toRb : (function(){
		$("#JONE-DMPS").click();
		$("#JONE-DMPS-WDGZT").click();
	}),
	toOnline : (function(){
		$("#JONE-SXGL").click();
		$("#JONE-SXGL-DBRW").click();
	}),
	toWorkspace : (function(){
		$("#JONE-WDGZQ").click();
	}),
	showImg:(function(){
		mainJS.cancelShowImg();
		mainJS.timeout = setTimeout(function(){
			$("#div_me").animate({right: "0"}, "normal", "linear", function(){
				$("#div_me").find("i").attr("class", "icon-chevron-right");
			});
		}, 100);
	}),
	cancelShowImg : (function(){
		if(mainJS.timeout){
			clearTimeout(mainJS.timeout);
			mainJS.timeout = null;
		}
	}),
	hiddenImg:(function(){
		$("#div_me").animate({right: "-133px"}, "normal", "linear", function(){
			$("#div_me").find("i").attr("class", "icon-chevron-left");
		});
	})
	
};

$(function() {
	// 异步获取待办上线任务数量
	$.ajax({
		url:contextPath + "/getOnlineTaskNum",
		datatype:"json",
		success:function(data){
			$("#onlineTaskNum").text(data.data);
		}
	});
	
    $('#nav > div').hover(
	    function () {
	        var $this = $(this);
	        $this.find('img').stop().animate({
	            'width'     :'199px',
	            'height'    :'199px',
	            'top'       :'-25px',
	            'left'      :'-25px',
	            'opacity'   :'1.0'
	        },500,'easeOutBack',function(){
	            $(this).parent().find('ul').fadeIn(700);
	        });
	
	        $this.find('a:first,h2').addClass('active');
	        $this.find('a:first').addClass('yellow');
	    },
	    function () {
	        var $this = $(this);
	        $this.find('ul').fadeOut(500);
	        $this.find('img').stop().animate({
	            'width'     :'52px',
	            'height'    :'52px',
	            'top'       :'0px',
	            'left'      :'0px',
	            'opacity'   :'0.1'
	        },5000,'easeOutBack');
	
	        $this.find('a:first,h2').removeClass('active');
	        $this.find('a:first').removeClass('yellow');
	    }
	);
    $(".toast").letterfx({
    	"fx":"fade",
    	"timing":100
    });
	mainJS.letterInterval = setInterval(function(){
		if($(".toast").length > 0){
			$(".toast").letterfx({
		    	"fx":"fade",
		    	"timing":100
		    });
		}else{
			clearInterval(mainJS.letterInterval);
		}
	}, 5000);
});
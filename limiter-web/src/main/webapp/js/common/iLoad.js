//定义定时器数组
intervalArray = []

var iLoadJS = iLoadJS || {

        //初始化Jquery的Ajax请求配置信息，可以让请求过的JS缓存到客户端
        initJqueryConfig: (function () {
            $.ajaxSetup({
                cache: true
            });
        })(),

        /*--------------------------------------------
         * Desc: 更新对应区域的内容;
         * arg: contentId		->需要加载的DIV区域ID
         * 		url				->需要引入的文件的URL
         * 		_paramData		->参数对象，可以封装到URL中
         * 		_callbackFun	->载入成功后的回调方法
         * ret: 无
         * Date:   2014/02/28;
         ---------------------------------------------*/
        upContent: (function (contentId, url, _paramData, _callbackFun) {
            console.log("fuck")
            console.log(intervalArray)
            for (var i = intervalArray.length - 1; i >= 0; i--) {
                    clearInterval(intervalArray[i]);
            }
            ;

            if (!contentId) {
                contentId = "main-content";
            }
            if (!document.getElementById(contentId)) {
                //alert("传递的容器ID在页面中无法找到！");
                return false;
            }
            if (!url) {

                alert("请传递需要加载的URL！");
                return false;
            }
            if (url.indexOf("?") != -1) {
                url += "&version=" + new Date().getTime();
            } else {
                url += "?version=" + new Date().getTime();
            }
            //跨域处理
            if (url.indexOf("otherdomain=") != -1) {
                iLoadJS.upOtherDomainUrl(contentId, url);
            } else {
                showWaiting();
                $("#" + contentId).load(url, _paramData || null, function (data) {
                    hideWaiting();
                    try {
                        if (_callbackFun && typeof(_callbackFun) == "function") {
                            _callbackFun(data);
                        }
                    }
                    catch (e) {
                        alert("调用回调方法异常！");
                    }
                });
            }
        }),

        /*--------------------------------------------
         * Desc: 更新对应子区域的内容;
         * arg: contentId		->需要加载的DIV区域ID
         * 		url				->需要引入的文件的URL
         * 		_paramData		->参数对象，可以封装到URL中
         * 		_callbackFun	->载入成功后的回调方法
         * ret: 无
         * Date:   2014/02/28;
         ---------------------------------------------*/
        upSubContent: (function (contentId, url, _paramData, _callbackFun) {
            if (!contentId) {
                alert("请输入容器ID！");
                return false;
            }
            if (!$("#" + contentId)[0]) {
                //alert("传递的容器ID在页面中无法找到！");
                return false;
            }
            if (!url) {
                alert("请传递需要加载的URL！");
                return false;
            }
            if (url.indexOf("?") != -1) {
                url += "&jone_req_version=" + new Date().getTime();
            } else {
                url += "?jone_req_version=" + new Date().getTime();
            }
            //跨域处理
            if (url.indexOf("otherdomain=") != -1) {
                iLoadJS.upOtherDomainUrl(contentId, url);
            } else {
                $("#" + contentId).load(url, _paramData || null, function (response, status, xhr) {
                    try {
                        if (_callbackFun && typeof(_callbackFun) == "function") {
                            _callbackFun(_paramData);
                        }
                    }
                    catch (e) {
                        alert("调用回调方法异常！");
                    }
                });
            }
        }),

        /*
         * 检查当前用户在编译系统中是否过期
         * 过期模拟登录操作
         * arg: contentId	->需要加载的DIV区域ID
         * 		url			->需要引入的文件的URL
         */
        upOtherDomainUrl: (function (contentId, url) {
            var uri = url;
//		$.ajax({
//			url : contextPath + "/userInfo",
//			type : "post",
//			datatype : "json",
//			success : function(data){
//				uri =uri+"&aca="+data.map.aca;
//				var iframStr = "<iframe style='width:100%;min-height:430px;' frameborder='0' src='" + uri + "' id='ifm_index_content'></iframe>";
//				$("#" + contentId).html(iframStr);
//				var intervalObj = setInterval(function(){
//					if($("#ifm_index_content")[0]){
//						var heightStr = $("#ifm_index_content").contents().find("body").height() + 80;
//						if(heightStr){
//							$("#ifm_index_content").height(heightStr);
//						}
//					} else {
//						clearInterval(intervalObj);
//					}
//				}, 1000);
//			}
//		});
            var iframStr = "<iframe style='width:100%;min-height:800px;' frameborder='0' src='" + uri + "' id='ifm_index_content'></iframe>";
            $("#" + contentId).html(iframStr);
        }),

        logout: (function () {
            window.location.href = contextPath + "/user/logout.xhtml";
            /*alert(contextPath);
             $.ajax({
             url : contextPath + "/user/logout.xhtml",
             type : "post",
             datatype : "json",
             success : function(){
             window.location.href = contextPath;
             }
             });*/
        })

    };

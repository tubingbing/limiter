$(function () {
})
var limiter = {
    delete: function (viewUrl,systemId,path) {
        confirm("确认是否删除？", function(){
            $.ajax({
                url: viewUrl,
                type: "post",
                data: {"path": path,"systemId": systemId},
                success: function (data) {
                    if (data == "1") {
                        alert("删除成功");
                        $("#limiterSearchButton").click();
                    } else {
                        alert("删除失败");
                        return false;
                    }
                }
            });
        });
    },
    //修改对话框，展示界面。标题。确认地址，界面form id,传入id
    update: function (viewUrl, title, targetUrl, systemId,path) {
        $.ajax({
            url: viewUrl,
            type: "post",
            data: {"path": path,"systemId": systemId},
            success: function (data) {
                d = dialog({
                    title: title,
                    modal:true,
                    content: data,
                    ok: function () {
                        var that = this;
                        var qps = $.trim($("#totalQps").val());
                        if(qps == null || qps == "") {
                            alert("QPS不能为空！");
                            return false;
                        }else if(!/^[0-9]*$/.test($("#totalQps").val())){
                            alert("QPS必须输入正整数!");
                            return false;
                        }
                        var openLimiter = $("input:radio[name='openLimiter']:checked").val();
                        if(openLimiter==null){
                            alert("请选择是否开启限流！");
                            return false;
                        }
                        var openBrush = $("input:radio[name='openBrush']:checked").val();
                        if(openBrush==null){
                            alert("请选择是否开启防刷！");
                            return false;
                        }
                        if (openBrush==1){
                            var requestQps = $.trim($("#requestQps").val());
                            if(requestQps == null || requestQps == "") {
                                alert("防刷次数不能为空！");
                                return false;
                            } else if(!/^[0-9]*$/.test($("#requestQps").val())){
                                alert("防刷次数必须输入正整数!");
                                return false;
                            }
                            var flag = 0;
                            $("input[name='requestType']:checkbox").each(function () {
                                if ($(this).attr("checked")) {
                                    flag += 1;
                                }
                            });
                            if (flag ==0){
                                alert("请选择请求类型！");
                                return false;
                            }
                            var time = $.trim($("#time").val());
                            if(time == null || time == "") {
                                alert("限制时长不能为空！");
                                return false;
                            }else if(!/^[0-9]*$/.test($("#time").val())){
                                alert("限制时长必须输入正整数!");
                                return false;
                            }
                        }
                        $.ajax({
                            url: targetUrl,
                            type: "post",
                            data: $("#updateForm").serialize(),
                            dataType: 'text',
                            success: function (data2) {
                                if (data2 == "1") {
                                    alert("修改成功");
                                    that.close().remove();
                                    d.close().remove();
                                    $("#limiterSearchButton").click();
                                } else {
                                    alert("修改失败");
                                    return false;
                                }
                            }
                        });
                    },
                    cancel: function () {
                        return true;
                    }
                });
                d.show();
            }
        });
    },
    //修改对话框，展示界面。标题。确认地址，界面form id,传入id
    add: function (viewUrl, title, targetUrl ) {
        $.ajax({
            url: viewUrl,
            type: "post",
            success: function (data) {
                d = dialog({
                    title: title,
                    modal:true,
                    content: data,
                    ok: function () {
                        var that = this;
                        this.title('正在提交..');
                        var systemId = $.trim($("#systemId").val());
                        if(systemId == null || systemId == "") {
                            alert("系统id不能为空！");
                            return false;
                        }
                        var path = $.trim($("#path").val());
                        if(path == null || path == "") {
                            alert("限流接口路径不能为空！");
                            return false;
                        }
                        var qps = $.trim($("#totalQps").val());
                        if(qps == null || qps == "") {
                            alert("QPS不能为空！");
                            return false;
                        }else if(!/^[0-9]*$/.test($("#totalQps").val())){
                            alert("QPS必须输入正整数!");
                            return false;
                        }

                        var openLimiter = $("input:radio[name='openLimiter']:checked").val();
                        if(openLimiter==null){
                            alert("请选择是否开启限流！");
                            return false;
                        }
                        var openBrush = $("input:radio[name='openBrush']:checked").val();
                        if(openBrush==null){
                            alert("请选择是否开启防刷！");
                            return false;
                        }
                        if(openBrush==1) {
                            var requestQps = $.trim($("#requestQps").val());
                            if(requestQps == null || requestQps == "") {
                                alert("防刷次数不能为空！");
                                return false;
                            } else if(!/^[0-9]*$/.test($("#requestQps").val())){
                                alert("防刷次数必须输入正整数!");
                                return false;
                            }
                            var flag = 0;
                            $("input[name='requestType']:checkbox").each(function () {
                                if ($(this).attr("checked")) {
                                    flag += 1;
                                }
                            });
                            if (flag == 0) {
                                alert("请选择请求类型！");
                                return false;
                            }
                            var time = $.trim($("#time").val());
                            if (time == null || time == "") {
                                alert("限制时长不能为空！");
                                return false;
                            } else if (!/^[0-9]*$/.test($("#time").val())) {
                                alert("限制时长必须输入正整数!");
                                return false;
                            }
                        }
                        
                        $.ajax({
                            url: targetUrl,
                            type: "post",
                            data: $("#addForm").serialize(),
                            dataType: 'text',
                            success: function (data2) {
                                if (data2 == "1") {
                                    alert("添加成功");
                                    that.close().remove();
                                    d.close().remove();
                                    $("#limiterSearchButton").click();
                                } else if (data2 == "2") {
                                    alert("该限流接口已存在");
                                    return false;
                                } else {
                                    alert("添加失败");
                                    return false;
                                }
                            }
                        });
                    },
                    cancel: function () {
                        return true;
                    }
                });
                d.show();
            }
        });
    },
    batchAdd: function (viewUrl, title, targetUrl ) {
        $.ajax({
            url: viewUrl,
            type: "post",
            success: function (data) {
                d = dialog({
                    title: title,
                    modal:true,
                    content: data,
                    ok: function () {
                        var that = this;
                        this.title('正在提交..');
                        var systemId = $.trim($("#systemId").val());
                        if(systemId == null || systemId == "") {
                            alert("系统id不能为空！");
                            return false;
                        }
                        var paths = $.trim($("#paths").val());
                        if(paths == null || paths == "") {
                            alert("限流接口路径不能为空！");
                            return false;
                        }
                        var qps = $.trim($("#totalQps").val());
                        if(qps == null || qps == "") {
                            alert("QPS不能为空！");
                            return false;
                        }else if(!/^[0-9]*$/.test($("#totalQps").val())){
                            alert("QPS必须输入正整数!");
                            return false;
                        }

                        var openLimiter = $("input:radio[name='openLimiter']:checked").val();
                        if(openLimiter==null){
                            alert("请选择是否开启限流！");
                            return false;
                        }
                        var openBrush = $("input:radio[name='openBrush']:checked").val();
                        if(openBrush==null){
                            alert("请选择是否开启防刷！");
                            return false;
                        }
                        if(openBrush==1) {
                            var requestQps = $.trim($("#requestQps").val());
                            if(requestQps == null || requestQps == "") {
                                alert("防刷次数不能为空！");
                                return false;
                            } else if(!/^[0-9]*$/.test($("#requestQps").val())){
                                alert("防刷次数必须输入正整数!");
                                return false;
                            }
                            var flag = 0;
                            $("input[name='requestType']:checkbox").each(function () {
                                if ($(this).attr("checked")) {
                                    flag += 1;
                                }
                            });
                            if (flag == 0) {
                                alert("请选择请求类型！");
                                return false;
                            }
                            var time = $.trim($("#time").val());
                            if (time == null || time == "") {
                                alert("限制时长不能为空！");
                                return false;
                            } else if (!/^[0-9]*$/.test($("#time").val())) {
                                alert("限制时长必须输入正整数!");
                                return false;
                            }
                        }

                        $.ajax({
                            url: targetUrl,
                            type: "post",
                            data: $("#batchAddForm").serialize(),
                            dataType: 'text',
                            success: function (data2) {
                                if (data2 == "1") {
                                    alert("添加成功");
                                    that.close().remove();
                                    d.close().remove();
                                    $("#limiterSearchButton").click();
                                } else if (data2 == "-1") {
                                    alert("添加失败");
                                    $("#limiterSearchButton").click();
                                    return false;
                                } else {
                                    alert("添加失败，"+data2+"已存在,其他添加成功！");
                                    that.close().remove();
                                    d.close().remove();
                                    $("#limiterSearchButton").click();
                                    return false;
                                }
                            }
                        });
                    },
                    cancel: function () {
                        return true;
                    }
                });
                d.show();
            }
        });
    },
    delAll:function(url){
        confirm("确认是否删除？", function(){
            $.ajax({
                url: url,
                type: "post",
                data: {},
                success: function (data) {
                    if (data == "1") {
                        alert("删除成功");
                        $("#limiterSearchButton").click();
                    } else {
                        alert("删除失败");
                        return false;
                    }
                }
            });
        });
    },
    batchDelete:function(url){
        if($("input[type='checkbox'][name='checkList']:checked").length==0){
            alert("至少选择一条记录！");
            return;
        }
        var paths="";
        var systemId;
        $("input[type='checkbox'][name='checkList']:checked").each(function(i){
            paths = paths+$(this).val()+",";
            systemId = $(this).next().val();
        });
        confirm("确认是否删除已选择的记录？", function(){
            $.ajax({
                url: url,
                type: "post",
                data: {"paths": paths,"systemId": systemId},
                success: function (data) {
                    if (data == "1") {
                        alert("删除成功");
                        $("#limiterSearchButton").click();
                    } else {
                        alert("删除失败");
                        return false;
                    }
                }
            });
        });
    },
    batchUpdate: function (viewUrl, title, targetUrl ) {
        if($("input[type='checkbox'][name='checkList']:checked").length==0){
           alert("至少选择一条记录！");
           return;
        }
        var paths="";
        var systemId;
        $("input[type='checkbox'][name='checkList']:checked").each(function(i){
            paths = paths+$(this).val()+",";
            systemId = $(this).next().val();
        });
        $.ajax({
            url: viewUrl,
            type: "post",
            data: {"paths": paths,"systemId": systemId},
            success: function (data) {
                d = dialog({
                    title: title,
                    modal:true,
                    content: data,
                    ok: function () {
                        var that = this;
                        this.title('正在提交..');
                        var qps = $.trim($("#totalQps").val());
                        if(qps == null || qps == "") {
                            alert("QPS不能为空！");
                            return false;
                        }else if(!/^[0-9]*$/.test($("#totalQps").val())){
                            alert("QPS必须输入正整数!");
                            return false;
                        }

                        var openLimiter = $("input:radio[name='openLimiter']:checked").val();
                        if(openLimiter==null){
                            alert("请选择是否开启限流！");
                            return false;
                        }
                        var openBrush = $("input:radio[name='openBrush']:checked").val();
                        if(openBrush==null){
                            alert("请选择是否开启防刷！");
                            return false;
                        }
                        if(openBrush==1) {
                            var requestQps = $.trim($("#requestQps").val());
                            if(requestQps == null || requestQps == "") {
                                alert("防刷次数不能为空！");
                                return false;
                            } else if(!/^[0-9]*$/.test($("#requestQps").val())){
                                alert("防刷次数必须输入正整数!");
                                return false;
                            }
                            var flag = 0;
                            $("input[name='requestType']:checkbox").each(function () {
                                if ($(this).attr("checked")) {
                                    flag += 1;
                                }
                            });
                            if (flag == 0) {
                                alert("请选择请求类型！");
                                return false;
                            }
                            var time = $.trim($("#time").val());
                            if (time == null || time == "") {
                                alert("限制时长不能为空！");
                                return false;
                            } else if (!/^[0-9]*$/.test($("#time").val())) {
                                alert("限制时长必须输入正整数!");
                                return false;
                            }
                        }

                        $.ajax({
                            url: targetUrl,
                            type: "post",
                            data: $("#batchUpdateForm").serialize(),
                            dataType: 'text',
                            success: function (data2) {
                                if (data2 == "1") {
                                    alert("修改成功");
                                    that.close().remove();
                                    d.close().remove();
                                    $("#limiterSearchButton").click();
                                } else {
                                    alert("批量修改失败");
                                    return false;
                                }
                            }
                        });
                    },
                    cancel: function () {
                        return true;
                    }
                });
                d.show();
            }
        });
    },
    updateSystemId: function (viewUrl, title, targetUrl, systemId) {
        $.ajax({
            url: viewUrl,
            type: "post",
            data: {"systemId": systemId},
            success: function (data) {
                d = dialog({
                    title: title,
                    modal:true,
                    content: data,
                    ok: function () {
                        var that = this;
                        var systemid = $.trim($("#systemId").val());
                        if(systemid==null || systemid==""){
                            alert("系统id不能为空");
                            return false;
                        }
                        var systemname = $.trim($("#systemName").val());
                        if(systemname==null || systemname==""){
                            alert("系统名称不能为空！");
                            return false;
                        }
                        var auth = $.trim($("#auth").val());
                        if(auth==null || auth==""){
                            alert("权限erp不能为空！");
                            return false ;
                        }
                        $.ajax({
                            url: targetUrl,
                            type: "post",
                            data: $("#updateForm").serialize(),
                            dataType: 'text',
                            success: function (data2) {
                                if (data2 == "1") {
                                    alert("操作成功！");
                                    that.close().remove();
                                    d.close().remove();
                                    $.ajax({
                                        url : "/limiter/querySystemIdList.xhtml",
                                        type : "post",
                                        success : function(data){
                                            $("#limiterTbody").html(data);
                                        }
                                    });
                                } else {
                                    alert("操作失败！");
                                    return false;
                                }
                            }
                        });
                    },
                    cancel: function () {
                        return true;
                    }
                });
                d.show();
            }
        });
    },
    showBrushData:function(id){
      if (id==1){
          $(".brushTrClass").show();
      }else{
          $(".brushTrClass").hide();
      }
    }
}
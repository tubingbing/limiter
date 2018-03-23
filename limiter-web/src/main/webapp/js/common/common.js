String.prototype.endWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substring(this.length - str.length) == str)
        return true;
    else
        return false;
    return true;
};

String.prototype.replaceAll = function (reallyDo, replaceWith, ignoreCase) {
    if (!RegExp.prototype.isPrototypeOf(reallyDo)) {
        return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi" : "g")), replaceWith);
    } else {
        return this.replace(reallyDo, replaceWith);
    }
};
Array.prototype.clone = function () {
    var a = [];
    for (var i = 0, l = this.length; i < l; i++) {
        a.push(this[i]);
    }
    return a;
};
//---------------------------------------------------  
//日期格式化  
//格式 YYYY/yyyy/YY/yy 表示年份  
//MM/M 月份  
//W/w 星期  
//dd/DD/d/D 日期  
//hh/HH/h/H 时间  
//mm/m 分钟  
//ss/SS/s/S 秒  
//---------------------------------------------------  
Date.prototype.Format = function (formatStr) {
    var str = formatStr;
    var Week = ['日', '一', '二', '三', '四', '五', '六'];

    str = str.replace(/yyyy|YYYY/, this.getFullYear());
    str = str.replace(/yy|YY/, (this.getYear() % 100) > 9 ? (this.getYear() % 100).toString() : '0' + (this.getYear() % 100));
    var month = this.getMonth() + 1;
    str = str.replace(/MM/, month > 9 ? month.toString() : '0' + month);
    str = str.replace(/M/g, month + 1);

    str = str.replace(/w|W/g, Week[this.getDay()]);

    str = str.replace(/dd|DD/, this.getDate() > 9 ? this.getDate().toString() : '0' + this.getDate());
    str = str.replace(/d|D/g, this.getDate());

    str = str.replace(/hh|HH/, this.getHours() > 9 ? this.getHours().toString() : '0' + this.getHours());
    str = str.replace(/h|H/g, this.getHours());
    str = str.replace(/mm/, this.getMinutes() > 9 ? this.getMinutes().toString() : '0' + this.getMinutes());
    str = str.replace(/m/g, this.getMinutes());

    str = str.replace(/ss|SS/, this.getSeconds() > 9 ? this.getSeconds().toString() : '0' + this.getSeconds());
    str = str.replace(/s|S/g, this.getSeconds());

    return str;
};

var iTime;
$(function () {
    /**
     * 全输入框trim
     */
    $("input[type='text'], input[type='password'], textarea").live("blur", function () {
        $(this).val($.trim($(this).val()));
    });

    /**
     * 全密码框禁止粘贴
     */
    $("input[type='password']").live("paste", function () {
        return false;
    });

    /**
     * 全选/取消全选
     */
    $("#checkAll").live("click", function () {
        var name = "id";
        if ($(this).attr("checkedBy")) {
            name = $(this).attr("checkedBy");
        }
        if ($(this).attr("checked") == "checked") {
            $("input[name='" + name + "']").attr("checked", "checked");
        } else {
            $("input[name='" + name + "']").removeAttr("checked");
        }
    });

    /**
     * 查询按钮响应
     */
    $("#searchButton").live("click", function () {
        $("#keyword").val($.trim($("#keyword").val()));
        $("#currPage").val(1);
        var formObj = $(this).closest("form");
        iLoadJS.upContent("main-content", formObj.attr("action"), formObj.serializeArray());
    });

    /**
     * 验证信息点击隐藏
     */
    $(".msg-box").live("click", function () {
        $(this).hide();
    });

    /**
     * 查询按钮响应
     */
    $("#toggleSearch").live("click", function () {
        if ($("#showPage").val() == "1") {
            $("#showPage").val("0");
        } else {
            $("#showPage").val("1");
        }
    });
});

/**
 * 分页跳转
 * @param currPage 跳转页数
 * @param prePageSize 每页条数
 */
function skipTo(currPage, prePageSize, srcElement) {
    $("#currPage").val(currPage);
    if (prePageSize) {
        $("#pageSize").val(prePageSize);
    }
    var form = srcElement ? $(srcElement).closest("form") : $("form:first");
    var url = form.attr("action");
    var param = form.serializeArray();
    var contentId = form.attr("contentId") || "main-content";
    if (!url) {
        url = $("form:eq(1)").attr("action");
        param = $("form:eq(1)").serializeArray();
        contentId = $("form:eq(1)").attr("contentId");
    }
    iLoadJS.upContent(contentId, url, param, null);
}

/**
 * 模态窗口分页跳转
 * @param obj 点击的对象
 * @param currPage 跳转页数
 * @param prePageSize 每页条数
 */
function modalSkipTo(contentId, currPage, prePageSize) {
    $("#" + contentId + " #currPage").val(currPage);
    if (prePageSize) {
        $("#" + contentId + " #pageSize").val(prePageSize);
    }
    var form = $("#" + contentId + " form");
    iLoadJS.upContent(contentId, form.attr("action"), form.serializeArray(), null);
}


/**
 * 复选框选中个数
 * @param checkBoxName 复选框名称
 */
function checkedNum(checkBoxName, propName, propValue) {
    var selector = "";
    if (typeof checkBoxName == "undefined") {
        checkBoxName = "id";
    }
    selector = 'input[name="' + checkBoxName + '"]';
    if (typeof propName != "undefined") {
        selector += '[' + propName + '="' + propValue + '"]';
    }
    selector += ':checked';
    return $(selector).length;
}

/**
 * 显示提示
 * @param content 提示内容
 * @param level 提示级别 error:错误 warning:警告 success:成功 info:信息
 */
function showAlert(content, level) {
    clearTimeout(iTime);
    $(".alert").remove();
    var alertType = "";
    if (level == "error") {
        alertType = "alert-error";
    } else if (level == "success") {
        alertType = "alert-success";
    } else if (level == "info") {
        alertType = "alert-info";
    }
    var msgDiv = '<div class=\"alert ' + alertType + ' fade in hide\"><button data-dismiss=\"alert\" class=\"close\" type=\"button\">×</button><strong>' + content + '</strong></div>';
    if ($("#message_info").size() > 0) {
        $("#message_info").prepend(msgDiv);
    } else {
        $("body").prepend(msgDiv);
    }
    $(".alert").slideDown();
    iTime = setTimeout("hideAlert()", 3000);
}

/**
 * 隐藏提示
 */
function hideAlert() {
    $(".alert").slideUp();
}

/**
 * 显示的一个提示
 *
 * @param element
 *            提示的元素 如果需要自动隐藏，该元素需要有ID属性
 * @param title
 *            提示的标题
 * @param content
 *            提示的内容
 * @param placement
 *            提示的显示位置 top | bottom | left | right
 * @param trigger
 *            显示方式 click | hover | focus | manual | autohide
 */
function showPopover(element, title, content, placement, trigger) {
    if (!placement) {
        placement = "top";
    }
    if (!trigger) {
        trigger = "autohide";
    }
    $(element).popover({
        title: title,
        content: content,
        placement: placement,
        trigger: trigger,
        html: true
    }).popover("show");
    var elementId = $(element).attr("id");
    if (elementId) {
        setTimeout("hidePopover('#" + elementId + "')", 2000);
    }
}

/**
 * 隐藏一个提示
 * @param elementId
 */
function hidePopover(elementId) {
    $(elementId).popover("destroy");
}

function showWaiting() {
    if ($("#winModal")[0]) {
        $("#winModal").addClass("in");
    } else {
        $("body").append("<div id='winModal' name='div_index_winModal' class='modal-backdrop fade in' style='z-index:9999'></div><div id='loadInfo' name='div_index_winModal'>请稍候...</div>");
    }
}

function hideWaiting() {
    $("div[name='div_index_winModal']").each(function () {
        $(this).remove();
    });
}

/**
 * 特殊字符校验
 */
function checkCommonCharacter(value) {
    var tmp = "`，~，#，$，%，^，&，*，'，\"，<，>，|";
    var paras = tmp.replace(/[,|，]/g, "").replace("\$", "\\$").replace("\^", "\\^").replace("\*", "\\*");
    var exp = new RegExp("[" + paras + "]+");
    if (exp.test(value)) {
        return "不能录入特殊字符 " + tmp;
    } else {
        return true;
    }
}

/*--------------------------------------------
 * Desc: RSA加密方法;本方法依赖于Barrett.js、BigInt.js、RSA.js
 * arg: paramStr	->需要加密的字符串
 * 		rsaKey		->加密的KEY
 * ret: resultStr	->加密后的字符串
 * Date:   2013/11/18;
 ---------------------------------------------*/
function encryptedString(paramStr, rsaKey) {
    if (!rsaKey) {
        alert("RSA加密KEY获取失败，无法加密！");
        return false;
    }
    return RSAUtils.encryptedString(RSAUtils.getKeyPair("10001", "", rsaKey), paramStr);
}

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || "");
        } else {
            o[this.name] = this.value || "";
        }
    });
    return o;
};
//判断数组中是否包含某个元素
Array.prototype.inArr = function (val) {
    for (var flag = 0, len = this.length; flag < len; flag++) {
        if (this[flag] == val) {
            return true;
        }
    }
    return false;
};
Array.prototype.clone = function () {
    var a = [];
    for (var i = 0, l = this.length; i < l; i++) {
        a.push(this[i]);
    }
    return a;
};
/*
 * 在需要自动补全的input框上面加上auto-erp='true'属性便可
 * 另可对input框设置一个auto-erp-arg熟悉值用"|"分隔
 * 目前接受2个值
 * 第一个为选择时需要填充到输入框的类型：erp-只是erp，name-只是姓名，label-姓名（erp），append-erp-递增逗号分隔都现有erp后面，默认只有erp
 * 第二个为选择是需要筛选用户的组别
 * 格式：auto-erp-arg='erp|FUNCTIONTEST'
 */
$("#main-content").delegate("input[auto-erp='true']", "focus", function () {
    function split(val) {
        return val.split(/,\s*/);
    }

    function extractLast(term) {
        return split(term).pop();
    }

    $(this).autocomplete({
        source: function (request, response) {
            var arg = $($(this)[0].bindings[0]).attr("auto-erp-arg");
            if (arg && arg.split("|").length > 1) {
                $.getJSON(contextPath + "/authgroup/autoComplete", {
                    term: escape(extractLast(request.term)),
                    groupCode: (arg.split("|")[1] || "")
                }, response);
            } else {
                $.getJSON(contextPath + "/user/autoComplete", {
                    term: escape(extractLast(request.term))
                }, response);
            }
        },
        search: function () {
            var term = extractLast(this.value);
            if (term.length < 1) {
                return false;
            }
        },
        focus: function () {
            return false;
        },
        select: function (event, ui) {
            var value = ui.item.value;
            var label = ui.item.label;
            if (value) {
                var arg = $(this).attr("auto-erp-arg");
                //返回值类型
                if (arg) {
                    var args = arg.split("|");
                    switch (args[0]) {
                        case "erp": //只是erp
                            this.value = value;
                            break;
                        case "name": //只是姓名
                            this.value = label ? label.substring(label.indexOf("(") + 1, label.indexOf(")")) : "";
                            break;
                        case "label": //姓名（erp）
                            this.value = label || "";
                            break;
                        case "append-erp": //递增erp
                            var curErp = split(this.value);
                            // 去除当前输入值
                            curErp.pop();
                            // 添加不重复选项
                            if (this.value.indexOf(value + ",") == -1) {
                                curErp.push(value);
                            }
                            curErp.push("");
                            this.value = curErp.join(",");
                            break;
                        default:
                            this.value = value;
                            break;
                    }
                } else {//默认返回填充erp
                    this.value = ui.item.value;
                }
            }
            return false;
        }
    });
    return false;
});
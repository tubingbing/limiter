/*********************************
 * Themes, rules, and i18n support
 * Locale: Chinese; 中文
 *********************************/
(function ($) {
    /* Global configuration
     */
    $.validator.config({
        //stopOnError: false,
        theme: 'yellow_right',
        defaultMsg: "{0}格式不正确",
        loadingMsg: "正在验证...",
        
        // Custom rules
        rules: {
            digits: [/^\d*$/, "请输入数字"]
            ,decimal: [/^[-+]?[0-9]{0,}(\.?[0-9]{1,2})?$/, "请输入整数或两位以内小数"]
    		,decimal1: [/^[-+]?[0-9]{0,}(\.?[0-9]{1})?$/, "请输入整数或一位小数"]
    		,buildTimeFrequency:[/((^[1][0-9]?)|(^[2][0-3]?)|(^[3-9]))$/,"只能输入1-23的整数"]
            ,letters: [/^[a-z]*$/i, "{0}只能输入字母"]
            ,tel: [/^(?:(?:0\d{2,3}[- ]?[1-9]\d{6,7})|(?:[48]00[- ]?[1-9]\d{6}))$/, "电话格式不正确"]
            ,mobile: [/^1[3-9]\d{9}$/, "手机号格式不正确"]
            ,email: [/^(?:[a-z0-9]+[_\-+.]?)*[a-z0-9]+@(?:([a-z0-9]+-?)*[a-z0-9]+.)+([a-z]{2,})+$/i, "邮箱格式不正确"]
            ,qq: [/^[1-9]\d{4,}$/, "QQ号格式不正确"]
            ,date: [/^\d{4}-\d{1,2}-\d{1,2}$/, "请输入正确的日期,例:yyyy-mm-dd"]
            ,time: [/^([01]\d|2[0-3])(:[0-5]\d){1,2}$/, "请输入正确的时间,例:14:30或14:30:00"]
            ,ID_card: [/^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[A-Z])$/, "请输入正确的身份证号码"]
            ,url: [/^(https?|ftp):\/\/[^\s]*$/i, "网址格式不正确"]
            ,postcode: [/^[1-9]\d{5}$/, "邮政编码格式不正确"]
            ,chinese: [/^[\u0391-\uFFE5]+$/, "请输入中文"]
            ,username: [/^\w{3,12}$/, "请输入3-12位数字、字母、下划线"]
            ,password: function (element){
            	var value = element.value;
            	var ALL_CHARACTERS_REG = /^[\x00-\xff]*$/;
            	var SPECIAL_CHARACTERS_REG = "<>/";
            	if(value){
            		for (var i = 0; i < value.length; i++) {
    					if (SPECIAL_CHARACTERS_REG.indexOf(value.charAt(i)) != -1) {
    						return "不能包含<>/";
    					}
    				}
            		if(!ALL_CHARACTERS_REG.test(value)){
                		return "密码格式错误";
                	}
            		return true;
            	}
            }
            ,accept: function (element, params){
                if (!params) return true;
                var ext = params[0];
                return (ext === '*') ||
                       (new RegExp("\^\\S+\\.+(" + (ext || "png|jpg|jpeg|gif") + ")$", "i")).test(element.value) ||
                       this.renderMsg("只接受{1}文件", ext.replace('|', ','));
            }
    		,specialName: [/^[a-zA-Z0-9_\-]{1,}$/, "只能输入字母、数字、中划线以及下划线"]
    		,appName:[/^[a-zA-Z0-9._\-]{1,}$/, "只能输入字母、数字、点、中划线以及下划线"]
    		,deployAppName:[/^[a-zA-Z0-9\-._\-,_]+$/,"只能输入字母、数字、点、逗号、中划线以及下划线"]
    		,appCNName:[/^((?!\,|\，).)*$/, "不能包含逗号"]
    		,factoryClass:[/^[a-zA-Z]{1,}[a-zA-Z.]{0,}[a-zA-Z]{1,}$/, "只能输入字母、点,不能以.开头和结尾"]
    		,domain:[/^([0-9]|[a-z]|[A-Z]|\-){1,62}(\.([0-9]|[a-z]|[A-Z]|\-){1,62})+$/, "域名格式不正确"]
    		,outNetDomain:[/^(([0-9]|[a-z]|[A-Z]){1,}(\.([0-9]|[a-z]|[A-Z]){1,})|(([0-9]|[a-z]|[A-Z]|\-){1,}))(.jd.com|.360buy.com|.3.cn|.paipai.com|.51buy.com|.yixun.com|.icson.com|.jd.hk|.jcloud.com|.360buyimg.com)$/, "由字母、数字、点组成，以.jd.com|.360buy.com|.3.cn|.paipai.com|.51buy.com|.yixun.com|.icson.com|.jd.hk|.jcloud.com|.360buyimg.com结尾"]
    		,innerNetDomain:[/^(([0-9]|[a-z]|[A-Z]|\-|\_){1,}\.){1,}(jd.local|360buy.local|3.local|jd.care|jcloud.com|360buyimg.com)$/, "机房内部域名格式只能字母、数字、点、横线、下划线组成，请以.jd.local或.360buy.local或.3.local或jd.care或jcloud.com或360buyimg.com结尾"]
    		,netDomainLength: function(element){
    			var value = element.value;
    			if(value){
    				var domainArr = value.split(".");
    				for(var i=0, length=domainArr.length; i<length; i++){
    					var len = 0;
    					if(length == 4){
    						len = domainArr[0].length+domainArr[1].length;
    					}else if(length == 3){
    						len = domainArr[0].length;
    					}else{
    						return "域名格式与规则不一致";
    					}
    					if(len > 10){
    						return "前两段域名总长度不能超过10个字符";
    					}
    				}
    				return true;
    			}
    		}
    		,multiDomain: function(element){
    			var value = element.value;
    			if(value){
    				var domainReg = /^([0-9]|[a-z]|[A-Z]|\-){1,62}(\.([0-9]|[a-z]|[A-Z]|\-){1,62})+$/;
    				var domainArr = value.split(",");
    				for(var i=0, length=domainArr.length; i<length; i++){
    					if(!domainReg.test(domainArr[i])) {
    						return "第" + (i + 1) + "个域名格式错误";
    					}
    				}
    				return true;
    			}
    		}
    		,buildCmd:[/^(?!-P|-p).*$/, "不能以-P或者-p开头"]
    		,ip:[/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/, "IP格式不正确"]
    		,htmlSpecial: function (element){
    			var SPECIAL_HTML_CHARACTERS = "@#$%^&\'|\"<>~";
    			var value = element.value;
    			if(value){
    				for (var i = 0; i < value.length; i++) {
    					if (SPECIAL_HTML_CHARACTERS.indexOf(value.charAt(i)) != -1) {
    						return "不能包含html特殊字符";
    					}
    				}
    				return true;
    			}
    		}
    		//rpm正则
    		,key:[/[\w\d\-_]+$/, "环境变量名称格式不正确"]
    		,dir:[/[\w\d\-_\\/:]+$/, "路径格式不正确"]
    		,outputName:[/[\w\d\-_\\/: ]+$/, "输出目录格式不正确"]
    		,fileName:[/[\w\d\-_\.]+$/, "文件名格式不正确"]
    		,value:[/[^!]+$/, "环境变量值不正确"]
    		,gitAddr: [/^.+\.git$/, "git地址请以.git结尾"]

    		,svnTrunk:[/^((?!branch).)*$/, "主干地址中不能包含branch关键字"]
    		
    		,path:[/(\/([0-9a-zA-Z]+))+\/$/, "路径格式不正确"]
    		,deployPath:[/(\/([0-9a-zA-Z._]+))+\/$/, "路径格式不正确"]
    		,version:[/^V\d{1,2}.\d{1,2}.\d{1,2}$/, "版本号格式不正确"]
    		,androidVersion:[/^\d{1,2}(.\d{1,9}){0,9}$/, "版本号格式不正确"]
    		,hosts: function (element){
    			var value = element.value;
    			if(value){
    				// 外网IP段数组
    				var externalIp = [
    				    {
    				    	start:[172, 16, 0, 1],
    				    	end:[172, 24, 6, 255]
    				    },
    				    {
    				    	start:[172, 24, 8, 0],
    				    	end:[172, 31, 255, 254]
    				    },
    				    {
    				    	start:[10, 16, 0, 1],
    				    	end:[10, 28, 167, 255]
    				    },
    				    {
    				    	start:[10, 28, 175, 255],
    				    	end:[10, 31, 255, 254]
    				    },
    				    {
    				    	start:[192, 168, 128, 1],
    				    	end:[192, 168, 143, 254]
    				    }
    				   
    				];
    				
    				var localIpPrefix = "127.";
    				var ipReg = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
    				var domainReg = /[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\.?$/;
    				var hostArr = value.split("|");
    				for(var i=0, iLength=hostArr.length; i<iLength; i++){
    					var ipDomain = $.trim(hostArr[i]);
    					if(!ipDomain) {
    						return "Host信息格式错误";
    					}
    					
    					var ipDomainArr = ipDomain.replace(/\s+/g, " ").split(" ");
    					if(ipDomainArr.length != 2){
    						return "第" + (i + 1) + "组Host格式错误";
    					}
    					
    					if(!ipReg.test(ipDomainArr[0])) {
    						return "第" + (i + 1) + "组Host的IP格式错误";
    					}
    					
    					if(ipDomainArr[0].indexOf(localIpPrefix) == 0) {
    						return "第" + (i + 1) + "组Host的IP不能为127.X.X.X";
    					}
    					
    					// ip段校验
    					var ipSection = ipDomainArr[0].split(".");
    					var ip2Num = parseInt(ipSection[0], 10) * 256 * 256 * 256 + parseInt(ipSection[1], 10) * 256 * 256 + parseInt(ipSection[2], 10) * 256 + parseInt(ipSection[3], 10);
    					for(var j=0, jLength=externalIp.length; j<jLength; j++) {
    						var start = externalIp[j].start;
    						var end = externalIp[j].end;
    						var start2Num = start[0] * 256 * 256 * 256 + start[1] * 256 * 256 + start[2] * 256 + start[3];
    						var end2Num = end[0] * 256 * 256 * 256 + end[1] * 256 * 256 + end[2] * 256 + end[3];
    						if(ip2Num >= start2Num && ip2Num <= end2Num) {
    							return "<b style='color:red;font-weight:bold'>第" + (i + 1) + "组Host的IP属于线上环境地址，可能对线上数据造成影响。<br/>为了保证数据安全请提供测试环境地址。有问题请联系安全测试人员<b/>";
    						}
    					}
    					
						if(!domainReg.test(ipDomainArr[1])) {
							return "第" + (i + 1) + "组Host的域名格式错误";
						}
    				}
    				return true;
    			}
    		} ,
    		jenkinsCmd : (function(element){
    			var regStr = "rm ";
    			if(element && element.value && element.value.toLowerCase().indexOf(regStr)!=-1) {
    				return "不能出现【rm 】关键字";
    			} else {
    				return true;
    			}
    		}),
    		indexSearchContent : (function(element){
    			if(element && element.value && element.value.length<3) {
    				return "关键字不能小于3个字符";
    			} else {
    				return true;
    			}
    		})
        }
    });

    /* Default error messages
     */
    $.validator.config({
        messages: {
            required: "{0}不能为空",
            remote: "{0}已被使用",
            integer: {
                '*': "请输入整数",
                '+': "请输入正整数",
                '+0': "请输入正整数或0",
                '-': "请输入负整数",
                '-0': "请输入负整数或0"
            },
            match: {
                eq: "{0}与{1}不一致",
                lt: "{0}必须小于{1}",
                gt: "{0}必须大于{1}",
                lte: "{0}必须小于或等于{1}",
                gte: "{0}必须大于或等于{1}"
            },
            range: {
                rg: "请输入{1}到{2}的数",
                gt: "请输入大于或等于{1}的数",
                lt: "请输入小于或等于{1}的数"
            },
            checked: {
                eq: "请选择{1}项",
                rg: "请选择{1}到{2}项",
                gt: "请至少选择{1}项",
                lt: "请最多选择{1}项"
            },
            length: {
                eq: "请输入{1}个字符",
                rg: "请输入{1}到{2}个字符",
                gt: "请输入大于{1}个字符",
                lt: "请输入小于{1}个字符",
                eq_2: "",
                rg_2: "",
                gt_2: "",
                lt_2: ""
            }
        }
    });
    
    /* Themes
     */
    var TPL_ARROW = '<span class="n-arrow"><b>◆</b><i>◆</i></span>';
    $.validator.setTheme({
        'simple_right': {
            formClass: 'n-simple',
            msgClass: 'n-right'
        },
        'simple_bottom': {
            formClass: 'n-simple',
            msgClass: 'n-bottom'
        },
        'yellow_top': {
            formClass: 'n-yellow',
            msgClass: 'n-top',
            msgArrow: TPL_ARROW
        },
        'yellow_right': {
            formClass: 'n-yellow',
            msgClass: 'n-right',
            msgArrow: TPL_ARROW
        },
        'yellow_right_effect': {
            formClass: 'n-yellow',
            msgClass: 'n-right',
            msgArrow: TPL_ARROW,
            msgShow: function($msgbox, type){
                var $el = $msgbox.children();
                if ($el.is(':animated')) return;
                if (type === 'error') {
                    $el.css({
                        left: '20px',
                        opacity: 0
                    }).delay(100).show().stop().animate({
                        left: '-4px',
                        opacity: 1
                    }, 150).animate({
                        left: '3px'
                    }, 80).animate({
                        left: 0
                    }, 80);
                } else {
                    $el.css({
                        left: 0,
                        opacity: 1
                    }).fadeIn(200);
                }
            },
            msgHide: function($msgbox, type){
                var $el = $msgbox.children();
                $el.stop().delay(100).show().animate({
                    left: '20px',
                    opacity: 0
                }, 300, function(){
                    $msgbox.hide();
                });
            }
        }
    });
})(jQuery);
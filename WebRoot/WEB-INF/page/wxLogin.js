function initWXBind() {
	var wx = api.require('wx');
	var code = '';
	wx.isInstalled(function(ret, err) {
		if (!ret.installed) {
			alert("当前设备未安装微信客户端");
		} else {
			wx.auth({
//				apiKey : 'wxd0d84bbf23b4a0e4'
			}, function(ret, err) {
				if (ret.status) {
					wx.getToken({
//						apiKey : 'wxd0d84bbf23b4a0e4',
//						apiSecret : 'a354f72aa1b4c2b8eaad137ac81434cd',
						code : ret.code
					}, function(ret, err) {
						if (ret.status) {
							api.showProgress({
								style : 'default',
								animationType : 'fade',
								title : '授权成功',
								text : '绑定处理中...',
								modal : true
							});
							wx.getUserInfo({
								accessToken : ret.accessToken,
								openId : ret.openId
							}, function(ret, err) {
								if (ret.status) {
									// 绑定成功 
									WXbindAccount(ret.openid);
									api.hideProgress();


								}
							});
						} else {
							alert(err.code);
						}
					});
				}
			});
		}
	});
}

function WXbindAccount(openId) {
	api.ajax({
		url : '接口',
		method : 'get',
		data : {
			values : {
				openid : openId,
			}
		}
	}, function(ret, err) {
		if (ret.retCode == 200) {
			$api.setStorage("保存当前user", ret.data);
			api.closeWin({
				name : "login"
			});
			return true;
		}
	});
}
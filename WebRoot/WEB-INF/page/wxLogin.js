//微信登录
function wxLogin() {
	var wx = api.require('wx');
	wx.isInstalled(function(ret, err) {
		if (!ret.installed) {
			alert("当前设备未安装微信客户端");
		} else {
			if ($api.getStorage("code")) { //已经授权
				if ($api.getStorage("accessToken")) {
					wx.getUserInfo({
						accessToken : $api.getStorage("accessToken"),
						openId : $api.getStorage("openId")
					}, function(ret, err) {
						if (err.code == 1) { //token过期
							wx.refreshToken({
								dynamicToken : $api.getStorage("dynamicToken")
							}, function(ret, err) {
								if (ret.status) {
									$api.setStorage("accessToken", ret.accessToken); //保存accessToken
									$api.setStorage("openId", ret.openId); //保存openId
									$api.setStorage("dynamicToken", ret.dynamicToken); //保存dynamicToken

									api.showProgress({
										style : 'default',
										animationType : 'fade',
										title : '',
										text : '登录中...',
										modal : true
									});

									getUserInfo(wx,ret.accessToken,ret.openId);									

									api.hideProgress();
								} else {
									//重新授权
									wxAuth(wx)
								}
							});
						} else {
							if (ret.status) {
								api.showProgress({
									style : 'default',
									animationType : 'fade',
									title : '',
									text : '登录中...',
									modal : true
								});

								// 绑定成功 
								WXbindAccount(ret);
								
								api.hideProgress();
							} else {
								if (err.code == -1) {
									alert("未知错误");
								} else if (err.code == 2) {
									alert("openId非法");
								} else if (err.code == 3) {
									alert("openId值为空");
								} else if (err.code == 4) {
									alert("accessToken值为空");
								} else if (err.code == 5) {
									alert("accessToken非法");
								} else if (err.code == 6) {
									alert("网络超时");
								}
							}
						}
					});
				} else {
					getToken(wx,$api.getStorage("code"));
				}
			} else {
				wxAuth(wx);
			}
		}
	});
}

//微信授权
function wxAuth(wx) {
	wx.auth({

	}, function(ret, err) {
		if (ret.status) {
			$api.setStorage("code", ret.code); //保存授权码
			getToken(wx,ret.code);
		} else {
			if (err.code == -1) {
				alert("未知错误");
			} else if (err.code == 1) {
				alert("用户取消");
			} else if (err.code == 2) {
				alert("用户拒绝授权");
			} else if (err.code == 3) {
				alert("当前设备未安装微信客户端");
			}
		}
	});
}

//获取token
function getToken(wx,code){
	wx.getToken({
		code : code
	}, function(ret, err) {
		if (ret.status) {
			$api.setStorage("accessToken", ret.accessToken); //保存accessToken
			$api.setStorage("openId", ret.openId); //保存openId
			$api.setStorage("dynamicToken", ret.dynamicToken); //保存dynamicToken

			api.showProgress({
				style : 'default',
				animationType : 'fade',
				title : '授权成功',
				text : '登录中...',
				modal : true
			});

			getUserInfo(wx,ret.accessToken,ret.openId);			

			api.hideProgress();
		} else {
			if (err.code == -1) {
				alert("未知错误");
			} else if (err.code == 1) {
				alert("apiKey值为空或非");
			} else if (err.code == 2) {
				alert("apiSecret值为空或非法");
			} else if (err.code == 3) {
				alert("code值为空或非法");
			} else if (err.code == 4) {
				alert("网络超时");
			}
		}
	});
}

//正常--获得微信用户信息
function getUserInfo(wx,accessToken,openId){
	wx.getUserInfo({
		accessToken : accessToken,
		openId : openId
	}, function(ret, err) {
		if (ret.status) {
			// 绑定登录 
			WXbindAccount(ret);
		} else {
			if (err.code == -1) {
				alert("未知错误");
			} else if (err.code == 1) {
				alert("accessToken 过期");
			} else if (err.code == 2) {
				alert("openId非法");
			} else if (err.code == 3) {
				alert("openId值为空");
			} else if (err.code == 4) {
				alert("accessToken值为空");
			} else if (err.code == 5) {
				alert("accessToken非法");
			} else if (err.code == 6) {
				alert("网络超时");
			}
		}
	});
}

//绑定登陆
function WXbindAccount(ret) {
	var nickname = ret.nickname;
	var sex = ret.sex;
	var headimgurl = ret.headimgurl;
	var openid = ret.openid;
	
	api.ajax({
		url : '本系统后端登陆接口',
		method : 'post',
		data : {
			values : {
				openid : openid,
			}
		}
	}, function(ret, err) {
		if (ret.retCode == 200) {
			$api.setStorage("user", ret.data);
			api.closeWin({
				name : "login"
			});
			return true;
		}
	});
}
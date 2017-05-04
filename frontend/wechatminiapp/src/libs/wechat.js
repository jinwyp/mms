var Promise = require('./bluebird')



function request(url, data, method){
  	return new Promise(function(resolve,reject){
  		wx.request({
	 		  url,
			  data: data || {},
				method : method || 'GET',
			  header: { 'Content-Type': 'application/json', 'X-SID' :wx.getStorageSync('accessToken111') },
			  success (res) {
					console.log('成功',res)

					if (res.data.error && res.data.error.code === 401){
						console.log("请登录")
					}
			    resolve(res.data)
			  },
			  fail (e) {
					console.log('fail',e)
			    reject(e)
			  }
	  	})
  	})
  }


function login () {
  return new Promise((resolve, reject) => {
    wx.login({ success: resolve, fail: reject })
  })
}

function getUserInfo () {
  return new Promise((resolve, reject) => {
    wx.getUserInfo({ success: resolve, fail: reject })
  })
}



module.exports = { 
    request : request,
    login : login, 
    getUserInfo : getUserInfo
}
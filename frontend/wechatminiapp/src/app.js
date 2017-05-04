//app.js

var UserService = require('./service/user.js');

App({
  onLaunch: function () {

    var that = this;

    //调用API从本地缓存中获取数据
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)


    UserService.getWXUserInfo().then(function(result){

        if(!that.globalData.userInfo){
          that.globalData.userInfo = result
        }

        return UserService.signUp(result)

    }).then(function(resultUserToken){

      console.log("Token : ", resultUserToken)

      that.globalData.accessToken = resultUserToken.sid
      // that.globalData.userId = resultUserToken._id

       wx.setStorageSync('accessToken', resultUserToken.sid)
    })
  },
  
  globalData:{
    userInfo : null,
    userId : null,
    accessToken : ''
  }
})

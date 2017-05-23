//app.js

var UserService = require('./service/user.js');
var Error = require('./service/error.js');


App({
  onLaunch: function () {

    var that = this;

    //调用API从本地缓存中获取数据
    // var logs = wx.getStorageSync('logs') || []
    // logs.unshift(Date.now())
    // wx.setStorageSync('logs', logs)


  //   var accessToken = wx.getStorageSync('accessToken')
  //   console.log("accessToken",accessToken)

  //   if(!accessToken){

  //     UserService.getWXUserInfo().then(function(result){

  //         if(!that.globalData.userInfo){
  //           that.globalData.userInfo = result
  //         }

  //         return UserService.signUp(result)

  //     }).then(function(resultUserToken){
  //       console.log(resultUserToken+"@")
  //       if(typeof resultUserToken.error === 'undefined'){
  //            that.globalData.accessToken = resultUserToken.data.openid
  //           // that.globalData.userId = resultUserToken._id

  //           wx.setStorageSync('accessToken', resultUserToken.data.openid)
  //       }else{
  //         wx.clearStorageSync()
  //       }
       
  //     }).catch(Error.PromiseError)
  //   }
  //   console.log(accessToken+"@@")
  }
  
  // globalData:{
  //   userInfo : null,
  //   userId : null,
  //   accessToken : ''
  // }
})

//app.js

var UserService = require('./service/user.js');
var Error = require('./service/error.js');


App({
  onLaunch: function () {

    var that = this;
     var accessToken = wx.getStorageSync('accessToken')
     console.log("accessToken",accessToken)

     if(!accessToken){

       UserService.getWXUserInfo().then(function(result){

           if(!that.globalData.userInfo){
             that.globalData.userInfo = result
           }

           return UserService.signUp(result)

       }).then(function(resultUserToken){
         if(typeof resultUserToken.error === 'undefined'){
              that.globalData.accessToken = resultUserToken.data.openid
             wx.setStorageSync('accessToken', resultUserToken.data.openid)
         }else{
           wx.clearStorageSync()
         }
       
       }).catch(Error.PromiseError)
     }
  },
   globalData:{
     userInfo : null,
     userId : null,
     accessToken : ''
   }
})

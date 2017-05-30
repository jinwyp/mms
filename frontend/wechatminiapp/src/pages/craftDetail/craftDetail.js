var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');

Page({
  data:{
    day:[
      {
      name:'周一',
      checked:''
      },
      {
        name: '周二',
        checked: ''
      },
      {
        name: '周三',
        checked: ''
      }, {
        name: '周四',
        checked: ''
      },
      {
       name:'周五',
        checked: ''
      }, {
        name: '周六',
        checked: ''
      },
      {
        name: '周日',
        checked: ''
      }
      ]
  },
  onLoad:function(options){
    var openid = options.openid;
    var that = this;
    var accessToken = wx.getStorageSync('accessToken')
    CategoryService.addProcess(openid).then(function (res) {
      that.setData({
        userName:res.userName,
        categories: res.categories,
        wxNum:res.wxNum,
        shopName: res.shopName,
        phone: res.phone,
        workAddress: res.workAddress,
        workBeg: res.workBeg,
        workEnd: res.workEnd,
        workDay:res.workDay,
        avatarUrl: res.avatarUrl,
        openId:res.openid,
        loginOpenId: accessToken
      })
      
     for(var i =0; i<res.workDay.length; i++){
       res.workDay[i] = res.workDay[i]-1
       that.setData({
        ['day[' + res.workDay[i] + '].checked']:true
       })
      }
     }).catch(Error.PromiseError)
    
  },
  editBtn:function(){
    wx.navigateTo({
      url: "/pages/personInfo/personInfo?craftId=" + openid
    })
  }
})
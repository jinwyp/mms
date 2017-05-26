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
    var that = this
    CategoryService.addProcess('', openid).then(function (res) {
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
        avatarUrl: res.avatarUrl
      })
      
     for(var i =0; i<res.workDay.length; i++){
       res.workDay[i] = res.workDay[i]-1
       that.setData({
        ['day[' + res.workDay[i] + '].checked']:true
       })
      }
     }).catch(Error.PromiseError)
    
  },
  onReady:function(){
    // 页面渲染完成
  },
  onShow:function(){
    // 页面显示
  },
  onHide:function(){
    // 页面隐藏
  },
  onUnload:function(){
    // 页面关闭
  }, 
  editBtn:function(){
    wx.navigateTo({
      url: "/pages/personInfo/personInfo"
    })
  }
})
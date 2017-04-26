// pages/mine/mine.js

var app = getApp() 
Page({
  data: {
   nickName:'',
   userInfoAvatar:'',
   tempFilePaths:'../../images/star.png',
   picker1Value:0,
   picker1Range:['造型、插花','上海','广州','深圳'],
   dateValue:new Date().getFullYear()+"-"+new Date().getMonth()+"-"+new Date().getDate()
  }, 
   normalPickerBindchange:function(e){
    this.setData({
      picker1Value:e.detail.value
    })
  },
  datePickerBindchange:function(e){
    this.setData({
      dateValue:e.detail.value
    })
  },
  chooseimage: function () { 
  var _this = this; 
  wx.chooseImage({ 
   count: 1, // 默认9 
   sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有 
   sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有 
   success: function (res) { 
    // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片 
    _this.setData({ 
     tempFilePaths:res.tempFilePaths
    }) 
    wx.uploadFile({
       url: 'https://String',
       filePath:'filePath',
       name:'name',
       // header: {}, // 设置请求的 header
       // formData: {}, // HTTP 请求中其他额外的 form data
       success: function(res){
         // success
       },
       fail: function() {
         // fail
       },
       complete: function() {
         // complete
       }
     })

   } 
  }) 
 } ,
 formSubmit: function(e) {
    console.log('form发生了submit事件，携带数据为：', e.detail.value)
  },
  formReset: function() {
    console.log('form发生了reset事件')
  },
  onLoad: function () {
    var that=this;    
    wx.getUserInfo({
      success: function(res){
        // success
        that.setData({
          nickName:res.userInfo.nickName,
          userInfoAvatar:res.userInfo.avatarUrl
        })
        
      },
      fail: function() {
        // fail
        console.log("获取失败！")
      },
      complete: function() {
        // complete
        console.log("获取用户信息完成！")
      }
    }),
    wx.setNavigationBarTitle({
      title: '个人信息设定'
    })
    
       
  }
})

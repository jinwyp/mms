// pages/mine/mine.js

var app = getApp() 
Page({
  data: {
   nickName:'',
   userInfoAvatar:'',
   tempFilePaths:'../../images/star.png',
   allValue:'',
   markers: [{
      latitude: 23.099994,
      longitude: 113.324520,
      name: 'T.I.T 创意园',
      desc: '我现在的位置'
    }],
    covers: [{
      latitude: 23.099794,
      longitude: 113.324520,
      iconPath: '../../images/wechart.png',
      rotate: 10
    }, {
      latitude: 23.099298,
      longitude: 113.324129,
      iconPath: '../../images/wechart.png',
      rotate: 90
    }]
  }, 
   
  getLocation:function() {
    console.log('地图定位！')
    var that = this
    wx.chooseLocation({
        type: 'gcj02', //返回可以用于wx.openLocation的经纬度
        success: function (res) {
          console.log(res)
            var latitude = res.latitude; 
            var longitude = res.longitude; 
            wx.openLocation({
              latitude:latitude,
              longitude:longitude,
              scale:1
            })
        }
    });
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
        url: 'https://wd.gongshijia.com/upload',
        filePath:tempFilePaths[0],
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
 chooseWXcode: function () { 
    var _this = this; 
    wx.chooseImage({ 
    count: 1, // 默认9 
    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有 
    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有 
    success: function (res) { 
      // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片 
      _this.setData({ 
      userInfoAvatar:res.tempFilePaths
      }) 
      wx.uploadFile({
        url: 'https://wd.gongshijia.com/upload',
        filePath:userInfoAvatar[0],
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
    console.log('form发生了submit事件，携带数据为：', e.detail.value);
    this.setData({
      allValue:e.detail.value
      })
  },
  formReset: function() {
    console.log('form发生了reset事件');
    this.setData({
   allValue:''
  })
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

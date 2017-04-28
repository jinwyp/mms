//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
   interest:['造型','美甲','服装搭配','茶艺','造型','美甲'],
   currentItemIndex:[]
  },
  chooseItem: function(e){
    console.log(e.target.dataset.interest)
    this.setData({
      currentItemIndex:e.target.dataset.interest
      // interestItem = e.target.dataset.interest,
    })
  },
  onLoad: function () {
    console.log('onLoad')
    var that = this
    //调用应用实例的方法获取全局数据
    app.getUserInfo(function(userInfo){
      //更新数据
      that.setData({
        userInfo:userInfo
      })
    }),
    wx.setNavigationBarTitle({
      title: '工时家',
      success: function(res) {
        // success
      }
    })
    // wx.request({
    //   url: 'https://wd.gongshijia.com', //仅为示例，并非真实的接口地址
    //   data:{},
    //   header: {
    //       'content-type': 'application/json'
    //   },
    //   success: function(res) {
    //     console.log(res.data.data);
    //     that.setData({
    //       array: res.data.data,
          
    //     })
        
        
    //   }
    // })
  }
})

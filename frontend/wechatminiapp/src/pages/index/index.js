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

    wx.setNavigationBarTitle({
      title: '工时家',
      success: function(res) {
        // success
      }
    })

  }
})

// pages/timeSetting/timeSetting.js
Page({
  data:{
    date: '2016-09-01',
    time: '09:00',
    timeDown: '18:00'
  },

  bindTimeChange: function(e) {
    this.setData({
      time: e.detail.value
    })
  },
  bindTimeChange2: function(e) {
    this.setData({
      time: e.detail.value
    })
  },
  onLoad:function(options){
    // 页面初始化 options为页面跳转所带来的参数
    wx.setNavigationBarTitle({
      title: '工作时间设定',
      success: function(res) {
        // success
      }
    })
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
  }
})
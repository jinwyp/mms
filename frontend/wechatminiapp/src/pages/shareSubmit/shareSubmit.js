// pages/shareSubmit/shareSubmit.js
Page({
  data:{},
  
  onShareAppMessage: function () {
    return {
      title: 'mms',
      desc:"mms",
      path: '/page/index/index',
      success: function(res) {
        // 分享成功
      },
      fail: function(res) {
        // 分享失败
      }
    }
  },
  onLoad:function(options){
    // 页面初始化 options为页面跳转所带来的参数
  
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
// pages/shareSubmit/shareSubmit.js
var shareHrefId, shareHrefFrom
Page({
  data:{},
  
  onShareAppMessage: function () {
    return {
      title: '工时家',
      desc:"工时家",
      path: '/pages/friendsExp/friendsExpDetail/friendsExpDetail?id=' + shareHrefId + '&from=' + shareHrefFrom,
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
    shareHrefId=options.id;
    shareHrefFrom=options.from;
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
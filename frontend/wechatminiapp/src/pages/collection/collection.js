// pages/collection/collection.js
Page({
  data:{
    interest:[{
      name:"造型",
      number:2
    },
    {
      name:"美甲",
      number:0
    },
    {
      name:"服装搭配",
      number:0
    },
    {
      name:"茶艺",
      number:0
    },
    {
      name:"造型",
      number:0
    },
        {
      name:"美甲",
      number:0
    }],
    experience: [
      {
        category:'造型',
        name:'刘杰',
        img:'../../images/star.png',
        text:'此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点'
      },
      {
        category:'造型',
        name:'刘杰2',
        img:'../../images/star.png',
        text:'此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点'
      }
      
    ],
    autoplay:false,//是否自动播放
    indicatorDots: false,//指示点
    interval: 2000,//图片切换间隔时间
    duration: 500,//每个图片滑动速度,
    circular:true,//是否采用衔接滑动
  },
  onLoad:function(options){
    // 页面初始化 options为页面跳转所带来的参数
    wx.setNavigationBarTitle({
      title: '工时家',
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
// pages/collection/collection.js
Page({
  data:{
    slideshow: false,
    currentId:0,
    peopleid:'',
    type:'造型',
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
    }],
    experience: [
      {
        id:'111',
        category:'造型',
        name:'刘杰',
        headImg:'../../images/star.png',
        img:['../../images/02.jpg','../../images/02.jpg','../../images/02.jpg','../../images/02.jpg','../../images/02.jpg','../../images/02.jpg','../../images/02.jpg','../../images/02.jpg'],
        text:'此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点'
      },
      {
        id:'222',
        category:'造型',
        name:'刘杰2',
        headImg:'../../images/star.png',
        video:'../../images/star.png',
        text:'此处为体验者个人观点此处为体验者个人观点此处为体验者个人观点'
      }
      
    ],
    autoplay:false,//是否自动播放
    indicatorDots: false,//指示点
    interval: 2000,//图片切换间隔时间
    duration: 500,//每个图片滑动速度,
    circular:true,//是否采用衔接滑动
  },
  show: function (e) {
    var that = this;
    var id = e.target.dataset.id;
     
    if (that.data.interest.length) {
      that.setData({
        slideshow: true,
        currentId:id,
        peopleid:e.target.dataset.peopleid
      })
    }    
  },
  
  hide:function(){
    this.setData({
      slideshow:false
    })
  },
  getList:function(e) {
    var val = e.target.dataset.value
    console.log(val)
    wx.request({
      url: 'https://baz.ngrok.io/api/shops', //仅为示例，并非真实的接口地址
      data:{
        val:val
      },
      header: {
          'content-type': 'application/json'
      },
      success: function(res) {
        console.log(res.data);
        that.setData({
          type:res.data.type,
          interest: res.data.data,
          
        })
        
        
      }
    })
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
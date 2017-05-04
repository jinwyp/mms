Page({
  data: {
    slideshow: false,
    currentId:0,
    expLists: [
      {
        ifvideo:false,
        message:'没想到简简单单的一些造型能让这个形象有如此大的改变，这是我相信美丽是需要后天打造的，感谢发型师鼓励噶，这次体验非常棒。',
        imgs:[
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg'      
        ],
        myself:false
      },
      {
        message:'你真的好棒棒！',
        ifvideo:true,
        videoUrl:'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400',
        myself:false
      },
      {
        ifvideo:false,
        message:'没想到简简单单的一些造型能让这个形象有如此大的改变，这是我相信美丽是需要后天打造的，感谢发型师鼓励噶，这次体验非常棒。',
        imgs:[
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg'
        ],
        myself:true

      }
    ]
  },
  show: function (e) {
    var that = this;
    var id = e.target.dataset.id;
    if (that.data.expLists.length) {
      that.setData({
        slideshow: true,
        currentId:id
      })
    }
  },
  hide:function(){
    this.setData({
      slideshow:false
    })
  },
  swiperChange:function(e){
    var current=e.detail.current,
        listsLen=this.data.expLists.length
        // 判断是否滑到最后一个
        if(current==listsLen-1){
          console.log("已经滑到最后一个")
        }
  },
  jumpToDetail:function(){
    // wx.navigateTo({
    //   url: 'test?id=1'
    // })
    console.log("Asd")
  }
})
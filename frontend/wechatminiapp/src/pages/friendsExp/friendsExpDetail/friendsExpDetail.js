Page({
  data: {
    slideshow: false,
    isClick:false,
    currentId:0,
    collectText:"+ 收藏",
    personName:"刘婕",
    releaseDate:"2017-02-22 08:47:23",
    shopName:"工时期王靓店",
    shopAddress:"上海市彭浦新村",
    hasCollect:false,
    slideorder:true,
    order:"1",
    inputVal:"",
    shopPics:[
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg',
        '../../../images/meinv.jpg'
      ],
      material:[
        '欧莱雅染膏X1',
        '欧莱雅染膏X2',
        '欧莱雅染膏X1'
      ],
      costTime:'40',
      gyprocess:'流程十分复杂流程十分复杂流程十分复杂流程十分复杂流程十分复杂流程十分复杂',
      comments:[
          {
            commentsName:"COCO",
            time:'2013-1-21 18:00',
            message:'真的很漂亮~~~'
          },
          {
            commentsName:"Lu",
            time:'2014-2-4 18:00',
            message:'时代变了，曾经打一局电子竞技游戏需要端坐在电脑前，一局游戏没有半个小时不会完结。随着 4G 网络和智能手机的普及，以及手游的快节奏，现在随时随地都可以呼朋唤友来一局，尤其是在吃海底捞排队，或者飞机延误这种等待时间里，手游，尤其是竞技性更强手游，成为了 Kill Time 的更佳选择。'
          }
        ],
    expLists:{
        ifvideo:false,
        message:'没想到简简单单的一些造型能让这个形象有如此大的改变，这是我相信美丽是需要后天打造的，感谢发型师鼓励噶，这次体验非常棒。',
        imgs:[
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg',
          '../../../images/meinv.jpg'
        ],
        videoUrl:'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400'
      }
      
  },
  onLoad:function(options){
    wx.setNavigationBarTitle({
      title: '好友体验详情'
    })
  },
  oncollect:function(){
    if(!this.data.hasCollect){
      this.setData({
          collectText:'已收藏',
          hasCollect:true
        })
        wx.showToast({
          title: '已收藏',
          icon: 'success',
          duration: 2000,
          mask:true
        })
    }else{
      this.setData({
          collectText:'+ 收藏',
          hasCollect:false
        })
        wx.showToast({
          title: '已取消',
          icon: 'loading',
          duration: 2000,
          mask:true
        })
    }
  },
  show: function (e) {
    var id = e.target.dataset.id,
        order=e.target.dataset.order;
        this.setData({
          slideshow: true,
          currentId:id,
          order:order
        })
  },
  hide:function(){
    this.setData({
      slideshow:false
    })
  },
  submitMessage:function(){
    var that=this;
    var commentInfo= [{
          commentsName:"匿名",
          time:"xxxx-xx-xx",
          message:this.data.inputVal
        }]
        if(this.data.inputVal.length){
          var newArray=this.data.comments.concat(commentInfo);
          this.setData({
            comments:newArray
          })
          wx.showToast({
            title: '已提交',
            icon: 'success',
            duration: 2000,
            mask:true
            })
          }else{
            wx.showToast({
              title: '内容不能为空',
              icon: 'loading',
              duration: 2000,
              mask:true
            })
          }
        this.setData({
            inputVal:''
        })
  },
  inputFun:function(e){
    var that=this;
    var input=e.detail.value;
    this.setData({
        inputVal:input
    })
  },
  tapSwitch : function(){
    console.log(this.data.isClick)
    this.setData({
        isClick: !this.data.isClick
    });
  }
})
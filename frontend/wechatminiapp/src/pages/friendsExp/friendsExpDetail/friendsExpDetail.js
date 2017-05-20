var UserService = require('../../../service/user.js');
var CategoryService = require('../../../service/category.js');
var Error = require('../../../service/error.js');

Page({
  data: {
    avatarUrl:'',
    nickName:'',
    expTime:'',
    slideshow: false,
    isClick:false,
    pictures:[],
    videos:'',
    feeling:'',
    signInfo:[],
    shopName:'',
    currentId:0,
    collectText:"+ 收藏",
    hasCollect:false,
    slideorder:true,
    hrefId:'',
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
          // {
          //   commentsName:"COCO",
          //   time:'2013-1-21 18:00',
          //   message:'真的很漂亮~~~'
          // },
          // {
          //   commentsName:"Lu",
          //   time:'2014-2-4 18:00',
          //   message:'时代变了，曾经打一局电子竞技游戏需要端坐在电脑前，一局游戏没有半个小时不会完结。随着 4G 网络和智能手机的普及，以及手游的快节奏，现在随时随地都可以呼朋唤友来一局，尤其是在吃海底捞排队，或者飞机延误这种等待时间里，手游，尤其是竞技性更强手游，成为了 Kill Time 的更佳选择。'
          // }
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

    var id = options.id,pid = options.pid;
    var that = this;
    var openId = wx.getStorageSync('accessToken')

    console.log(options.id + '@@')
    console.log(options.pid + '@@')
  
    if (openId && id) {
        CategoryService.friendDetail('',id).then(function(res){
          console.log(res.data.avatarUrl)
          that.setData({
            videos: res.data.videos,
            avatarUrl: res.data.avatarUrl,
            nickName: res.data.nickName,
            expTime: res.data.expTime,
            pictures: res.data.pictures,
            feeling: res.data.feeling,
            signInfo: res.data.signInfo,
            hrefId:res.data._id,
            comments: res.data.comments,
            checked:res.data.checked,
            shopName: res.data.shopName,
            locationName: res.data.locationName,
            signId:res.data.signInfo[0]._id
          })
        }).catch(Error.PromiseError)
    }

    if (openId && pid) {
      var mkdata={
        reportId: id,
        signInfoId: pid
      }
      CategoryService.makeSureReport(mkdata).then(function (res) {
        console.log(res.data.avatarUrl)
        that.setData({
          videos: res.data.videos,
          avatarUrl: res.data.avatarUrl,
          nickName: res.data.nickName,
          expTime: res.data.expTime,
          pictures: res.data.pictures,
          feeling: res.data.feeling,
          signInfo: res.data.signInfo,
          hrefId: res.data._id,
          comments: res.data.comments,
          checked: res.data.checked,
          shopName: res.data.shopName,
          locationName: res.data.locationName,
          signId: res.data.signInfo[0]._id
        })
      }).catch(Error.PromiseError)
    }
  },
  oncollect:function(){
    if(!this.data.hasCollect){
      this.setData({
          collectText:'已收藏',
          hasCollect:true
        })
  
      var data={
        reportId: this.data.hrefId
      }
        CategoryService.addReportToCollect(data).then(function (res) {
          console.log(res);
        }).catch(Error.PromiseError)
    }else{
      this.setData({
          collectText:'+ 收藏',
          hasCollect:false
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
      content:this.data.inputVal
    }]
    var data={
      "reportid": this.data.hrefId,
      "openid": wx.getStorageSync('accessToken'),
      "content": this.data.inputVal
    }
    if(this.data.inputVal.length){
      var newArray = this.data.comments.concat(commentInfo);
      CategoryService.addComment(data).then(function (res) {
        wx.showToast({
          title: '提交中',
          icon: 'loading',
          duration: 5000,
          mask: true
        })
        if(res.success){
          wx.hideToast();
          that.setData({
            comments: newArray
          })
        }else{
          wx.showModal({
            title: '提示',
            content: '评论失败',
            showCancel: false,
            success: function (res) {

            }
          })
        }
      }).catch(Error.PromiseError)
    }else{
      wx.showModal({
        title: '提示',
        content: '评论内容不能空',
        showCancel:false,
        success: function (res) {

        }
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
    this.setData({
        isClick: !this.data.isClick
    });
    var isClick = this.data.isClick,that = this
    if (isClick) {
      wx.showModal({
        title: '提示',
        content: '您确认对他进行署名确认？',
        success: function (res) {
          if (res.confirm) {

              var data = {
                reportId: that.data.hrefId,
                signInfoId: that.data.signId
              }
              CategoryService.handlerMakeSureReport(data).then(function (res) {
                console.log(res)
              }).catch(Error.PromiseError)

          } else if (res.cancel) {
            that.setData({
              isClick: false
            });
          }
        }
      })
    }
   
  },
  addProcess : function(){
    var that = this 
    CategoryService.addProcess().then(function (res) {
      console.log("res._id+"+res._id)
      if(res.shopName == undefined){
        wx.navigateTo({
          url: '/pages/personInfo/personInfo?reportId=' + that.data.hrefId
        })
      }else{
        wx.navigateTo({
          url: '/pages/craft/craftTitle?reportId=' + that.data.hrefId
        })
      }
    }).catch(Error.PromiseError)
  }
})
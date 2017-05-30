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
    checked:'',
    order:"1",
    inputVal:"",
    shopPics:[],
    material:[],
    costTime:'40',
    gyprocess:'',
    comments:[],
    expLists:[]
    
  },
  onLoad:function(options){

    var id = options.id,pid = options.pid,fromdata = options.from;
    var that = this;
    var openId = wx.getStorageSync('accessToken')

    if (openId && fromdata) {
      CategoryService.friendShareDetail(id + '/' + fromdata).then(function (res) {
        console.log(id,'id')
        console.log(formdata,'formdata')
        console.log(res.data,'fenxiang data')
      }).catch(Error.PromiseError)
    }

    if (openId && id) {
        CategoryService.friendDetail('',id).then(function(res){
          console.log(res.data)
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
            signId: res.data.signInfo[0]
          })
        }).catch(Error.PromiseError)
    }

    if (openId && pid) {
      var mkdata={
        reportId: id,
        signInfoId: pid
      }
      CategoryService.makeSureReport(mkdata).then(function (res) {
        console.log(res.data)
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
          signId: res.data.signInfo[0]
        })
      }).catch(Error.PromiseError)
    }
  },
  oncollect:function(){
    var data = {
      reportId: this.data.hrefId
    }
    if(!this.data.hasCollect){
      this.setData({
        collectText:'已收藏',
        hasCollect:true
      })
      CategoryService.addReportToCollect(data).then(function (res) {
        console.log(res);
      }).catch(Error.PromiseError)
    }else{

      this.setData({
          collectText:'+ 收藏',
          hasCollect:false
        });
      CategoryService.removeCollectReport(data).then(function (res) {
        console.log(res);
      }).catch(Error.PromiseError)
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
   
    var data={
      "reportid": this.data.hrefId,
      "openid": wx.getStorageSync('accessToken'),
      "content": this.data.inputVal
    }
    if(this.data.inputVal.length){
      
      CategoryService.addComment(data).then(function (res) {
        wx.showToast({
          title: '提交中',
          icon: 'loading',
          duration: 1000,
          mask: true
        })
        if(res.success){
          var commentInfo = [{
            nickName: res.data.nickName,
            createDate: res.data.createDate,
            content: res.data.content,
            avatarUrl: res.data.avatarUrl
          }]
          var newArray = that.data.comments.concat(commentInfo);
          wx.hideToast();
          that.setData({
            comments: newArray
          })
          console.log(that.data.comments)
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
                signInfoId: that.data.signId._id
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
  },
  craftDetail : function(e){
    var openid = e.currentTarget.dataset.openid;
    wx.navigateTo({
      url: '/pages/craftDetail/craftDetail?openid=' + openid
    })
  }
})
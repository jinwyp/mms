var UserService = require('../../../service/user.js');
var CategoryService = require('../../../service/category.js');
var Error = require('../../../service/error.js');
var imgIndex=2;

Page({
  data: {
    slideshow: false,
    currentId:0,
    expLists: [],
    current:0
  },
  onLoad : function(){
    var that = this
    var openId = wx.getStorageSync('accessToken')
        console.log('openId',openId);
   

    if (openId) {
        CategoryService.friendList('',1).then(function(res){
          console.log(res.data.length)

          
          
          for(var i = 0; i<res.data.length; i++){
            var friendList={
              category:res.data[i].category,
              imgs:res.data[i].pictures,
              message:res.data[i].feeling,
              hrefId:res.data[i]._id,
              nickName: res.data[i].nickName,
              avatarUrl: res.data[i].avatarUrl,
              videos:res.data[i].videos,
              signInfo: res.data[i].signInfo,
              current: that.data.current
            };
            that.data.expLists.push(friendList);
            that.setData({
              expLists:that.data.expLists
            })
            
          }

        }).catch(Error.PromiseError)
    }
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
        listsLen=this.data.expLists.length,
        that=this;
        this.setData({
          current: current
        })
        // 判断是否滑到最后一个
        if(current==listsLen-1){
          CategoryService.friendList('', imgIndex).then(function (res) {
          if(res.data==''){
            wx.showToast({
              title: '已经是最后一条',
              icon: '',
              duration: 2000
            })
          }else{
              for (var i = 0; i < res.data.length; i++) {
                var newobj = {
                  category: res.data[i].category,
                  imgs: res.data[i].pictures,
                  message: res.data[i].feeling,
                  hrefId: res.data[i]._id,
                  nickName: res.data[i].nickName,
                  avatarUrl: res.data[i].avatarUrl,
                  videos: res.data[i].videos,
                  signInfo: res.data[i].signInfo
                };
                that.data.expLists.push(newobj);
                that.setData({
                  expLists: that.data.expLists,
                  current:that.data.current
                })
              }
              imgIndex = imgIndex+1;
            }
          }).catch(Error.PromiseError)
        }
  },
  named:function(e){
   var url=e.target.dataset.url,that=this;
    wx.navigateTo({
      url: url
    })
  }
})
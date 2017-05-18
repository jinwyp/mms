var UserService = require('../../../service/user.js');
var CategoryService = require('../../../service/category.js');
var Error = require('../../../service/error.js');
var imgIndex=2;

Page({
  data: {
    slideshow: false,
    currentId:0,
    expLists: [
      // {
      //   category:'',
      //   releaseName:'',
      //   ifvideo:false,
      //   message:'',
      //   imgs:[],
      //   myself:false
      // }
      // {
      //   category:'',
      //   message:'你真的好棒棒！',
      //   ifvideo:true,
      //   videoUrl:'http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400',
      //   myself:false
      // },
      // {
      //   category:'',
      //   ifvideo:false,
      //   message:'没想到简简单单的一些造型能让这个形象有如此大的改变，这是我相信美丽是需要后天打造的，感谢发型师鼓励噶，这次体验非常棒。',
      //   imgs:[
      //     '../../../images/meinv.jpg',
      //     '../../../images/meinv.jpg',
      //     '../../../images/meinv.jpg'
      //   ],
      //   myself:true

      // }
    ]
  },
  onLoad : function(){
    var that = this
    var openId = wx.getStorageSync('accessToken')
        console.log('openId1',openId);
   

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
              videos:res.data[i].videos
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
                  videos: res.data[i].videos
                };
                that.data.expLists.push(newobj);
                that.setData({
                  expLists: that.data.expLists
                })
              }
              imgIndex = imgIndex+1;
            }
          }).catch(Error.PromiseError)
        }
  }
})
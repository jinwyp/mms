// pages/collection/collection.js
var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');
var apiPath = require("../../service/apiPath.js");
Page({
  data:{
    collect:true,
    slideshow: false,
    currentId:0,
    peopleid:'',
    interest:[],
    category:'',
    index:0,
    experience: [],
    autoplay:false,//是否自动播放
    indicatorDots: false,//指示点
    interval: 2000,//图片切换间隔时间
    duration: 500,//每个图片滑动速度,
    circular:false,//是否采用衔接滑动
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
    var that = this;
    var getCategory = e.target.dataset.value;
    // var getIdx = e.target.dataset.idx;
    that.setData({
      index: e.target.dataset.idx
    })
    // console.log(getCategory)
    // console.log(that.data.index)
    wx.request({
      url: apiPath.getCollectReport + getCategory, 
      method:'GET',
      header: {
          'content-type': 'application/json',
          'X-OPENID': wx.getStorageSync('accessToken'),
      },
      success: function(res) {
        // console.log(res.data.data);
        // console.log(res.data.data[0].category);
        that.setData({
          experience: res.data.data,
          category: res.data.data[0].category,
        
        })
        
        
      }
    })
  },
  deleteCollect:function(e){
    var that = this;
    var id = e.target.dataset.value;
    // console.log(id)
    wx.showModal({
      title: '提示',
      content: '确定要取消收藏吗？',
      success: function (res) {
        if (res.confirm) {
          wx.request({
            url: apiPath.removeCollectReport + '?reportId=' + id,
            method: 'GET',
            header: {
              'content-type': 'application/json',
              'X-OPENID': wx.getStorageSync('accessToken'),
            },
            success: function (res) {
              that.setData({
                collect: false
              })
              console.log(res);
              // console.log(res.data.data[0].category);

            }
          })
        } else if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
   
  },
  addCollect: function (e) {
    var that = this;
    var id = e.target.dataset.value;
    // console.log(id)
    wx.showModal({
      title: '提示',
      content: '确定要添加收藏吗？',
      success: function (res) {
        if (res.confirm) {
          wx.request({
            url: apiPath.addReportToCollect + '?reportId=' + id,
            method: 'GET',
            header: {
              'content-type': 'application/json',
              'X-OPENID': wx.getStorageSync('accessToken'),
            },
            success: function (res) {
              console.log(res);
              that.setData({
                collect:true
              })
              // console.log(res.data.data[0].category);

            }
          })
        } else if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })

  },
  onLoad:function(options){
     
    var that = this
    var openId = wx.getStorageSync('accessToken')
    if (openId) {
      CategoryService.getIndexList().then(function (res) {
        console.log('getIndexList', res)
        that.setData({
          interest: res.data.categories
        })

      }).catch(Error.PromiseError)
      CategoryService.loadCollectReport().then(function (res) {
        console.log('res', res.data)
        that.setData({
          experience:res.data,
          category: res.data[0].category
          // 'experience.id': res.data._id,
          // 'experience.category': res.data.category,
          // 'experience.name': res.data.nickName,
          // 'experience.headImg': res.data.avatarUrl,
          // 'experience.img': res.data.pictures,
          // 'experience.video': res.data.videos,
          // 'experience.text': res.data.feeling,
        })

      }).catch(Error.PromiseError)
    }
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
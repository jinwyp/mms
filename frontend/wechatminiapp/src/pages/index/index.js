//index.js
//获取应用实例
var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');

var app = getApp()
Page({
  data: {
   interest:[
    //  {name:'造型',checked: false},
    //  {name:'美甲',checked: false},
    //  {name:'服装',checked: false},
    //  {name:'茶艺',checked: false},
    //  {name:'美容',checked: false},
    //  {name:'美发',checked: false} 
    ],
  
  },

  checkItem: function(e) {
    var index=e.target.dataset.index;
    var a = !this.data.interest[index].checked
    this.setData({
      ['interest['+index+'].checked']:a
    })
  },
  
  
  interestPost:function(e) {
    var that = this
    console.log('interestPost事件，携带数据为：', this.data.interest);
    var categories = [];
    var interest = this.data.interest;
    for(var i=0;i<interest.length;i++){
      if(interest[i].checked === true){
        categories.push(interest[i].name)
      }
      
    }
   
    console.log('categories',categories);

    CategoryService.pushIndexList({  categories:categories }).then(function(res){
      // console.log('res', res.data.redirect)
          if(res.data.redirect === 0){
            wx.navigateTo({
              url: '../collection/collection',
              // success: function(res){
              //   // success
              // },
              // fail: function(res) {
              //   // fail
              // },
              // complete: function(res) {
              //   // complete
              // }
            })
          }else if(res.data.redirect === 1){
            wx.navigateTo({
              url: '../friendsExp/friendsList/friendsList',
            })
          }else{
            console.log('categories',res)
          }

        }).catch(Error.PromiseError)
  },
  
  test :function(){
    UserService.addComment({ content: "hello world" }).then(function(res){
      console.log('test',res)
    })

  },
  clear :function(){
    wx.clearStorageSync();

  },
  onLoad: function () {
    var that = this
    var openId = wx.getStorageSync('accessToken')
        console.log('openId',openId)
    if (openId) {
        CategoryService.getIndexList().then(function(res){
          console.log('getIndexList',res)
          that.setData({
            interest:res.data.categories
          })

        }).catch(Error.PromiseError)
    }
   
  }
})

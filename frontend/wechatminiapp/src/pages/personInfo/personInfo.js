// pages/mine/mine.js
var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');
var apiPath = require("../../service/apiPath.js");

var app = getApp() 
Page({
  data: {
   nickName:'',
   WXcode:'../../images/upload.png',
   upWXcode:'',
   head:'',
   week:'',
   time: '09:00',
   time2: '18:00',
   latitude:'',
   longitude:'',
   tel:'',
   address:'请选择',
   submitAll:[{
      userName:'',
      phone:'',
      shopName:'',
      workAddress:'',
      workLat:'',
      workLon:'',
      wxNum:'',
      wxQrCode:'',
      workDay:[],
      workBeg:'',
      workEndl:''

  }],
   ifSubmit: false
  }, 
  //  时间选择
  //时间选择
  bindTimeChange: function(e) {
    this.setData({
      time: e.detail.value
    })
  },
  bindTimeChange2: function(e) {
    this.setData({
      time2: e.detail.value
    })
  },
  // checkbox
  checkboxChange: function(e) {
    var aa = e.detail.value;
    var bb = aa.map(function(value){
      return parseInt(value,10)
    })
      this.setData({
        week: bb
      })
  },
  getLocation:function() {
    var that = this
    wx.chooseLocation({
        type: 'gcj02', //返回可以用于wx.openLocation的经纬度
        success: function (res) {
          that.setData({
            latitude : res.latitude,
            longitude : res.longitude,
            address : res.address
          })
            
        }
    });
  },
  chooseimage: function () { 
    var that = this; 
    // var that = this;
    var policy, callback, signature, OssAccessKeyId;
    var suffix = ''
    function generateUUID() {
      var d = new Date().getTime();
      var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x7 | 0x8)).toString(16);
      });
      return uuid;
    };
    wx.request({
      url: 'http://zxy.gongshijia.com/asset/policy',
      method: 'GET',
      header: {
        'content-type': 'application/json',
        'X-OPENID': wx.getStorageSync('accessToken'),
      },
      success: function (res) {
        policy = res.data.data.policy;
        callback = res.data.data.callback;
        signature = res.data.data.signature;
        OssAccessKeyId = res.data.data.ossAccessId;
      }
    })

    wx.chooseImage({
      count: 1,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: function (res) {
        that.setData({
          WXcode: res.tempFilePaths
        })
      },
      complete: function (res) {
            var tempFilePaths = res.tempFilePaths;
            suffix = res.tempFilePaths[0].split('.')[1];
            var fd = {
              key: generateUUID() + '.' + suffix,
              policy: policy,
              success_action_status: '200',
              callback: callback,
              signature: signature,
              OSSAccessKeyId: OssAccessKeyId,
              file: tempFilePaths[0]
            }
            wx.uploadFile({
              url: apiPath.ossUrl,
              filePath: tempFilePaths[0],
              name: 'file',
              header: { "content-Type": "multipart/form-data" },
              formData: fd,
              success: function (res) {
                var callBackName = JSON.parse(res.data).filename;
                that.setData({
                  upWXcode: apiPath.ossUrl + callBackName
                })
                console.log(that.data.upWXcode)
              },
              fail: function (res) {
                // console.log(res)
              }
            })
         
        
      }
    })
 } ,

  checkTel:function(e) {
    var tel = e.detail.value;
    var mobile = /^0?(13[0-9]|17[0-9]|15[0-9]|18[0-9]|14[57])[0-9]{8}$/;
    if(!mobile.test(tel)){
      wx.showModal({
        title: '提示',
        content: '请正确填写您的电话号码',
        success: function(res) {
          if (res.confirm) {
          } else if (res.cancel) {
          }
        }
      })
    }
  },
  formSubmit: function(e) {
     var that = this;
     that.setData({
         'submitAll.userName':e.detail.value.name,
         'submitAll.phone':e.detail.value.tel,
         'submitAll.shopName':e.detail.value.shopName,
         'submitAll.workAddress':that.data.address,
         'submitAll.workLat':that.data.latitude,
         'submitAll.workLon':that.data.longitude,
         'submitAll.wxNum':e.detail.value.wx,
         'submitAll.wxQrCode': that.data.upWXcode,
         'submitAll.workDay':that.data.week,
         'submitAll.workBeg':that.data.time,
         'submitAll.workEnd':that.data.time2
     })
     console.log(that.data.submitAll)
     var all = that.data.submitAll;

     if (all.userName === '') {
       wx.showModal({
         title: '提示',
         content: '请输入姓名',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else if (all.phone === ''){
       wx.showModal({
         title: '提示',
         content: '请输入手机号',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else if (all.shopName === '') {
       wx.showModal({
         title: '提示',
         content: '请输入商号名称',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else if (all.workAddress === '请选择') {
       wx.showModal({
         title: '提示',
         content: '请选择工作地址',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else if (all.wxNum === '') {
       wx.showModal({
         title: '提示',
         content: '请输入个人微信号',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else if (all.wxQrCode === '') {
       wx.showModal({
         title: '提示',
         content: '请上传个人微信二维码图片',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else if (all.workDay === '') {
       wx.showModal({
         title: '提示',
         content: '请选择工作日',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else {
       that.setData({
         ifSubmit: true
       });

     }
     

     if (that.data.ifSubmit === true){
      CategoryService.improveInfo(that.data.submitAll).then(function (res) {
        if (reportId!=undefined){
          wx.navigateTo({
            url: '/pages/craft/craftTitle?reportId=' + reportId,
          })
      
        }
         

      }).catch(Error.PromiseError)
    }
  },

  onLoad: function (option) {
    var that = this;
    if (option.reportId!=undefined){
      reportId = option.reportId
    }
       
    var openId = wx.getStorageSync('accessToken')
    if (openId) {
      UserService.getWXUserInfo().then(function (res) {
        console.log(res)
        that.setData({
          head: res.avatarUrl
        })
        
      }).catch(Error.PromiseError)
      wx.request({
        url: apiPath.addProcess + openId,
        header: {
          'content-type': 'application/json'
        }, 'X-OPENID': wx.getStorageSync('accessToken'),
        success: function (res) {
          console.log(res)
        }
      })
    }
       
  }
})

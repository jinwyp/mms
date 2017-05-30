// pages/mine/mine.js
var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');
var apiPath = require("../../service/apiPath.js");
var app = getApp() 
var reportJumpId, craftJumpId;
Page({
  data: {
  //  nickName:'',
   wxQrCode: '../../images/upload.png',
   head:'',
   week:'',
   ischeck1:false,
   ischeck2: false,
   ischeck3:false,
   ischeck4:false,
   ischeck5: false,
   ischeck6: false,
   ischeck7: false,
  //  address:'请选择',
   submitAll:{
      userName:'',
      phone:'',
      shopName:'',
      workAddress:'请选择',
      workLat:'',
      workLon:'',
      wxNum:'',
      wxQrCode:'../../images/upload.png',
      workDay:[],
      workBeg: '09:00',
      workEnd: '18:00'

  },
   ifSubmit: false
  }, 
  //  时间选择
  //时间选择
  bindTimeChange: function(e) {
    this.setData({
      'submitAll.workBeg': e.detail.value
    })
  },
  bindTimeChange2: function(e) {
    this.setData({
      'submitAll.workEnd': e.detail.value
    })
  },
  // checkbox
  checkboxChange: function(e) {
    var aa = e.detail.value;
    var bb = aa.map(function(value){
      return parseInt(value,10)
    })
      this.setData({
        'submitAll.workDay': bb
      })
  },
  getLocation:function() {
    var that = this
    wx.chooseLocation({
        type: 'gcj02', //返回可以用于wx.openLocation的经纬度
        success: function (res) {
          that.setData({
            'submitAll.workLat' : res.latitude*1,
            'submitAll.workLon' : res.longitude*1,
            'submitAll.workAddress' : res.address
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
    // wx.request({
    //   url: 'http://zxy.gongshijia.com/asset/policy',
    //   method: 'GET',
    //   header: {
    //     'content-type': 'application/json',
    //     'X-OPENID': wx.getStorageSync('accessToken'),
    //   },
    //   success: function (res) {
    //     policy = res.data.data.policy;
    //     callback = res.data.data.callback;
    //     signature = res.data.data.signature;
    //     OssAccessKeyId = res.data.data.ossAccessId;
    //   }
    // })

    wx.chooseImage({
      count: 1,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success: function (res) {
        console.log('res', res.tempFilePaths[0])
        that.setData({
          wxQrCode: res.tempFilePaths[0]
        })
      },
      complete: function (res) {
            wx.showLoading({
              title: '上传中',
            })
            var tempFilePaths = res.tempFilePaths[0];
            // suffix = res.tempFilePaths[0].split('.')[1];
            // var fd = {
            //   key: generateUUID() + '.' + suffix,
            //   policy: policy,
            //   success_action_status: '200',
            //   callback: callback,
            //   signature: signature,
            //   OSSAccessKeyId: OssAccessKeyId,
            //   file: tempFilePaths[0]
            // }
            console.log('tempFilePaths', tempFilePaths)
            wx.uploadFile({
              url: apiPath.ossUrl,
              filePath: tempFilePaths,
              name: 'file',
              header: { "content-Type": "multipart/form-data" },
              // formData: fd,
              success: function (res) {
                console.log('111', res)
                // var format = JSON.parse(res.data).data.split(".")[1];
                // var callBackName = generateUUID(JSON.parse(res.data).data) + '.' + format;
                var callBackName = JSON.parse(res.data).data;
                console.log('0000', callBackName)
                wx.showToast({
                  title: '上传成功',
                  icon:'success',
                  duration:1000
                })
                that.setData({
                  'submitAll.wxQrCode': callBackName
                })
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
         'submitAll.wxNum':e.detail.value.wx,
        //  'submitAll.workDay':that.data.week,
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
     } else if (all.wxQrCode === '../../images/upload.png') {
       wx.showModal({
         title: '提示',
         content: '请上传个人微信二维码图片',
         success: function (res) {
           if (res.confirm) {
           } else if (res.cancel) {
           }
         }
       })
     } else if (all.workDay === null) {
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
       wx.request({
         url: apiPath.improveInfo,
        method:'POST',
        header: {
          'content-type': 'application/json',
          'X-OPENID': wx.getStorageSync('accessToken'),
        }, 
        data: that.data.submitAll,
        success: function (res) {
          console.log(res)
          that.setData({
            // head: res.data.avatarUrl,
            // nickName: res.data.nickName,
            // address: res.data.nickName,
          })
          if(res.data){
            // 从署名页面来
            if (reportJumpId != '') {
              wx.redirectTo({
                url: '/pages/craft/craftTitle?reportId=' + reportJumpId
              })
            }
            //从手艺人信息页面来
            if (craftJumpId != ''){
            
              wx.redirectTo({
                url: '/pages/craftDetail/craftDetail?openid=' + craftJumpId
              })
              console.log('ssss')
            }
          }
        }
      })
    }
  },

  onLoad: function (option) {
    var that = this; 
    
    reportJumpId = option.reportId || '';
    craftJumpId = option.craftId || '';
    console.log(craftJumpId,'craftJumpId!')

    var openId = wx.getStorageSync('accessToken')
    UserService.getWXUserInfo().then(function (res) {
      console.log(res)
      that.setData({
        head: res.avatarUrl
      })
    }).catch(Error.PromiseError)

    if (openId) { 
      wx.request({
        url: apiPath.addProcess,
        method:'GET',
        header: {
          'content-type': 'application/json',
          'X-OPENID': wx.getStorageSync('accessToken'),
        }, 
        success: function (res) {
          // console.log('aaa',res.data)
          var workDay = res.data.workDay;
          if (workDay){
            for (var i = 0; i < workDay.length;i++){
              if (workDay[i] == 1){
                that.setData({
                  ischeck1: true,
                })
              } else if (workDay[i] == 2){
                that.setData({
                  ischeck2: true,
                })
              }else if (workDay[i] == 3) {
                that.setData({
                  ischeck3: true,
                })
              } else if (workDay[i] == 4) {
                that.setData({
                  ischeck4: true,
                })
              } else if (workDay[i] == 5) {
                that.setData({
                  ischeck5: true,
                })
              } else if (workDay[i] == 6) {
                that.setData({
                  ischeck6: true,
                })
              } else if (workDay[i] == 7) {
                that.setData({
                  ischeck7: true,
                })
              }
            }
            var myfilepath = res.data.wxQrCode.split('static/')[1]
            // console.log('myfilepath',myfilepath)
            that.setData({
              submitAll: res.data,
              wxQrCode: res.data.wxQrCode,
              
              'submitAll.wxQrCode': myfilepath
            })
          }
          
          console.log(that.data.submitAll)
        }
      })
    }
       
  }
})

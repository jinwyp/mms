// pages/mine/mine.js
var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');
var apiPath = require("../../service/apiPath.js");


var app = getApp() 
Page({
  data: {
    interest:[],
    whoCanSee:['私密','好友','公开'],
    ifChoose:'造型',
    // isClick:false,
    pricePrivacy:'0',
    index:0,
    dateValue:new Date().getFullYear()+"-"+(new Date().getMonth()+1)+"-"+new Date().getDate(), 
   tempFilePaths:'../../images/upload.png',
   uploadImg:[],
   backImg:[],
   uploadVideo:'',
   backVideo: '',
   slideshow: false,
   address:'未选择',
   latitude:'',
   longitude:'',
   price:'',
   submitAll:[{
     category:'',
     pictures:[],
     videos:'',
     feeling:'',
     locationName:'',
     lat:'',
     lon:'',
     shopName:'',
     expTime:'',
     expPrice:0,
     pricePrivacy:'',
     privacy:''

   }],
   ifSubmit:false
  
  }, 
  
  // 上传
  uploadfiles:function(){
   var that = this;
   var policy, callback, signature, OssAccessKeyId;
   var suffix = ''
    wx.showActionSheet({
      itemList: ['上传照片', '上传视频'],
      success: function(res) {  
           
        if(res.tapIndex === 0){
               
          function generateUUID(){
              var d = new Date().getTime();
              var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                  var r = (d + Math.random()*16)%16 | 0;
                  d = Math.floor(d/16);
                  return (c=='x' ? r : (r&0x7|0x8)).toString(16);
              });
              return uuid;
          };
          // wx.request({
          //   url: 'http://zxy.gongshijia.com/asset/policy',
          //   method:'GET',
          //   header: {
          //     'content-type': 'application/json',
          //     'X-OPENID':wx.getStorageSync('accessToken'),
          //   },
          //   success: function(res) {
          //     policy=res.data.data.policy;
          //     callback = res.data.data.callback;
          //     signature = res.data.data.signature;
          //     OssAccessKeyId = res.data.data.ossAccessId;
          //   }
          // })
          wx.chooseImage({
            count: 9, // 默认9
            sizeType: ['original', 'compressed'], 
            sourceType: ['album', 'camera'], 
            success: function (res) {               
              that.setData({
                uploadImg:res.tempFilePaths.concat(that.data.uploadImg)
              })
              console.log('sss',that.data.uploadImg)
              
            },
            complete:function(res){
              var tempFilePaths = res.tempFilePaths;
              console.log('sss1', tempFilePaths)
              for (var i = 0; i < tempFilePaths.length; i++) {
                // console.log('tempFilePaths[i]', tempFilePaths[i])
                // var format = tempFilePaths[i].split(".")[1];
                // var callBackName = generateUUID(tempFilePaths[i]) + '.' + format;
                // console.log('sss2', format)
                // console.log('sss2', callBackName)
                wx.uploadFile({
                  url: apiPath.ossUrl,
                  filePath: tempFilePaths[i],
                  name: 'file',
                  header: { "content-Type": "multipart/form-data" },
                  success: function (res) {
                    console.log('res',res)
                    var callBackName = JSON.parse(res.data).data;
                    // var format = JSON.parse(res.data).data.split(".")[1];
                    // var callBackName = generateUUID(JSON.parse(res.data).data) + '.' + format;
                    that.data.backImg.push(callBackName)
                    console.log('0000',that.data.backImg)
                    console.log('@@@@@', callBackName)
                  },
                  fail: function (res) {
                    console.log('err',res)
                  }
                })
              }
            }
     
          })
        }else if(res.tapIndex === 1){
          // var that=this,policy,callback,signature,OssAccessKeyId;
          function generateUUID(){
              var d = new Date().getTime();
              var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                  var r = (d + Math.random()*16)%16 | 0;
                  d = Math.floor(d/16);
                  return (c=='x' ? r : (r&0x7|0x8)).toString(16);
              });
              return uuid;
          };
          // wx.request({
          //   url: 'http://zxy.gongshijia.com/asset/policy',
          //   method:'GET',
          //   header: {
          //     'content-type': 'application/json',
          //     'X-OPENID':wx.getStorageSync('accessToken'),
          //   },
          //   success: function(res) {
          //     policy = res.data.data.policy;
          //     callback = res.data.data.callback;
          //     signature = res.data.data.signature;
          //     OssAccessKeyId = res.data.data.ossAccessId;
          //   }
          // })
          wx.chooseVideo({
            sourceType: ['album', 'camera'], 
            maxDuration: 60, 
            camera: ['front', 'back'],
            success: function(res){  
              that.setData({
                  uploadVideo:res.tempFilePath
              })
            },
            fail: function(res) {
              // fail
            },
            complete: function(res) {
              var tempFilePaths = res.tempFilePath;
              console.log('v', tempFilePaths);
              // suffix = res.tempFilePath.split('.')[1];
              //   var fd = {
              //     key: generateUUID() + '.' + suffix,
              //     policy: policy,
              //     success_action_status: '200',
              //     callback: callback,
              //     signature: signature,
              //     OSSAccessKeyId: OssAccessKeyId,
              //     file: tempFilePaths
              //   }
                wx.uploadFile({
                  url: apiPath.ossUrl,
                  filePath: tempFilePaths,
                  name: 'file',
                  header: { 
                    "content-Type": "multipart/form-data"
                     },
                  // formData: fd,
                  success: function (res) {
                    // console.log('vvv', res)
                    var callBackName = JSON.parse(res.data+"").data;
                    // var format = JSON.parse(res.data).data.split(".")[1];
                    // var callBackName = generateUUID(JSON.parse(res.data).data) + '.' + format;
                    that.setData({
                      backVideo:  callBackName
                    })

                  },
                  fail: function (res) {
                    console.log(res)
                  }
                })
              
            }
          })
          
        }
      },
      fail: function(res) {
        console.log(res.errMsg)
      }
    })
    
  },
  // 上传图片
  uploadImg:function(){ 
    var that=this;
    if(that.data.uploadImg.length>=9){
        wx.showModal({
          title: '提示',
          content: '店内实景最多上传9张',
          showCancel:false,
          success: function(res) {
            if (res.confirm) {
              // console.log('用户点击确定')
            } else if (res.cancel) {
              // console.log('用户点击取消')
            }
          }
        })
    }else{
      var that=this
      var policy,callback,signature,OssAccessKeyId;
      var suffix = ''
      function generateUUID(){
          var d = new Date().getTime();
          var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
              var r = (d + Math.random()*16)%16 | 0;
              d = Math.floor(d/16);
              return (c=='x' ? r : (r&0x7|0x8)).toString(16);
          });
          return uuid;
      };
      // wx.request({
      //   url: 'http://zxy.gongshijia.com/asset/policy',
      //   method:'GET',
      //   header: {
      //     'content-type': 'application/json',
      //     'X-OPENID':wx.getStorageSync('accessToken'),
      //   },
      //   success: function(res) {
      //     policy = res.data.data.policy;
      //     callback = res.data.data.callback;
      //     signature = res.data.data.signature;
      //     OssAccessKeyId = res.data.data.ossAccessId;
      //   }
      // })

      wx.chooseImage({
        count: 9, 
        sizeType: ['original', 'compressed'], 
        sourceType: ['album', 'camera'], 
        success: function (res) {
          var tempFilePaths = res.tempFilePaths;
          that.setData({
            uploadImg:tempFilePaths.concat(that.data.uploadImg)
          })
        },
        complete:function(res){
          var tempFilePaths = res.tempFilePaths;
          if(that.data.uploadImg.length>9){
              wx.showModal({
              title: '提示',
              content: '店内实景最多上传9张',
              showCancel:false,
              success: function(res) {
                if (res.confirm) {
                  that.data.uploadImg.length=9;
                    that.setData({
                        uploadImg:that.data.uploadImg
                      })
                }
              }
            })
          }else{
            //999
            var tempFilePaths = res.tempFilePaths;
            for (var i = 0; i < tempFilePaths.length; i++) {
            //   suffix = res.tempFilePaths[i].split('.')[1];
            // var fd = {
            //   key: generateUUID() + '.' + suffix,
            //   policy: policy,
            //   success_action_status: '200',
            //   callback: callback,
            //   signature: signature,
            //   OSSAccessKeyId: OssAccessKeyId,
            //   file: tempFilePaths[i]
            // }
            wx.uploadFile({
              url: apiPath.ossUrl,
              filePath: tempFilePaths[i],
              name: 'file',
              header: { "content-Type": "multipart/form-data" },
              // formData: fd,
              success: function (res) {
                console.log('res',res)
                var callBackName = JSON.parse(res.data).data;
                // var format = JSON.parse(res.data).data.split(".")[1];
                // var callBackName = generateUUID(JSON.parse(res.data).data) + '.' + format;
                // console.log('res2', callBackName)
                that.data.backImg.push(callBackName)
              },
              fail: function (res) {
                // console.log(res)
              }
            })
            }
          }
        }
      })
    }
  },

  // 图片放大查看
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
  // 删除图片
  delImg:function(e){
    var that=this;
     wx.showModal({
        title: '提示',
        content: '您确认删除该张照片吗',
        showCancel:true,
        success: function(res) {
          if (res.confirm) {
            var index=e.target.dataset.id;
            that.data.uploadImg.splice(index,1)
            that.setData({
                uploadImg:that.data.uploadImg
            });
          } else if (res.cancel) {
            // console.log('用户点击取消')
          }
        }
      })
  },
  // 删除视频
  delVideo:function(){
    var that=this;
     wx.showModal({
        title: '提示',
        content: '您确认删除该视频吗',
        showCancel:true,
        success: function(res) {
          if (res.confirm) {
            // var index=e.target.dataset.id;
            // that.data.uploadVideo.splice(index,1)
            that.setData({
                uploadVideo:''
            });
          } else if (res.cancel) {
            console.log('用户点击取消')
          }
        }
      })
  },
  // 选择时间
  datePickerBindchange:function(e){
    this.setData({
      dateValue:e.detail.value
    })
  },
  bindPickerChange: function(e) {
    this.setData({
      index: e.detail.value
    })
  },
  //  调用地图
  getLocation:function() {
    // console.log('地图定位！')
    var that = this
    wx.chooseLocation({
        type: 'gcj02', //返回可以用于wx.openLocation的经纬度
        success: function (res) {
          // console.log(res) 
            that.setData({
              // address : res.address.slice(0,13)+'...',
              address : res.address,
              latitude : res.latitude,
              longitude : res.longitude
            })
            // wx.openLocation({
            //   latitude:latitude,
            //   longitude:longitude,
            //   scale:1
            // })
        }
    });
  },

 radioChange: function(e) {
   var that = this;
    // console.log('radio发生change事件，携带value值为：', e.detail.value)
    that.setData({
      ifChoose:e.detail.value,
    })    
  },

  // tapSwitch : function(){
  //   // console.log(this.data.isClick)
  //   this.setData({
  //       isClick: !this.data.isClick
        
  //   });
  //   if(this.data.isClick == true){
  //     this.setData({
  //       pricePrivacy : '1'
  //     });
  //   }else{
  //     this.setData({
  //       pricePrivacy : '0'
  //   });
  //   }
  // },
  
 formSubmit: function(e) {
    var that = this;
    var openId = wx.getStorageSync('accessToken')
    that.setData({   
      'submitAll.category':that.data.ifChoose,
      'submitAll.pictures': that.data.backImg,
      'submitAll.videos': that.data.backVideo,
      'submitAll.feeling':e.detail.value.feeling,
      'submitAll.locationName':that.data.address,
      'submitAll.lat':that.data.latitude,
      'submitAll.lon':that.data.longitude,
      'submitAll.shopName':e.detail.value.shop,
      'submitAll.expTime':e.detail.value.day,
      'submitAll.expPrice':Number(e.detail.value.price),
      'submitAll.pricePrivacy':Number(that.data.pricePrivacy),
      'submitAll.privacy':Number(e.detail.value.whoCanSee),    
    })
    console.log(that.data.submitAll)
    
      if(that.data.submitAll.expPrice === 0){
          wx.showModal({
          title: '提示',
          content: '请输入体验价格',
          success: function(res) {
            if (res.confirm) {
            } else if (res.cancel) {
            }
          }
        })
       
      }else if(that.data.submitAll.shopName === ''){
          wx.showModal({
          title: '提示',
          content: '请输入体验门店（店铺）',
          success: function(res) {
            if (res.confirm) {
            } else if (res.cancel) {
            }
          }
        })
        
      }else if(that.data.submitAll.locationName === '未选择'){
          wx.showModal({
          title: '提示',
          content: '请选择体验区域',
          success: function(res) {
            if (res.confirm) {
            } else if (res.cancel) {
            }
          }
        })
        
      }else if(that.data.submitAll.feeling === ''){
          wx.showModal({
          title: '提示',
          content: '请输入个人体验感受',
          success: function(res) {
            if (res.confirm) {
            } else if (res.cancel) {
            }
          }
        })
        
      }else if(that.data.submitAll.pictures.length === 0 && that.data.submitAll.videos === ''){
          wx.showModal({
          title: '提示',
          content: '请上传相关图片或者视频',
          success: function(res) {
            if (res.confirm) {
            } else if (res.cancel) {
            }
          }
        })
      
      }else{
        that.setData({
          ifSubmit : true
        });
              
      }
      
      if(that.data.ifSubmit === true){
        var accessToken = wx.getStorageSync('accessToken')
        CategoryService.releaseReport(that.data.submitAll).then(function(res){
          wx.navigateTo({
<<<<<<< HEAD
            url: '../shareSubmit/shareSubmit?id=' + res.data + '&from=' + accessToken,
=======
            url: 'pages/shareSubmit/shareSubmit?id=' + res.data,
>>>>>>> efd514e186cf2530fcc37d1d9a1b3242ad3089a0
          })
          
        }).catch(Error.PromiseError)
      }
      
    
  },



  // input价格校验
  inputReg:function(e) {
    var price = e.detail.value
    var sign = ''
    var cents=''
    price = price.toString().replace(/\$|\,/g,'');  
    if(isNaN(price)){ 
      price = 0; 
      wx.showModal({
        title: '提示',
        content: '请输入金额',
        success: function(res) {
          if (res.confirm) {
          } else if (res.cancel) {
          }
        }
      })
    } 
    sign = (price == (price = Math.abs(price)));  
    price = Math.floor(price*100+0.50000000001);  
    cents = price%100;  
    price = Math.floor(price/100).toString();  
    if(cents<10)  
    cents = "0" + cents;  
    for (var i = 0; i < Math.floor((price.length-(1+i))/3); i++)  
    price = price.substring(0,price.length-(4*i+3))+','+  
    price.substring(price.length-(4*i+3)); 
    // console.log(((sign)?'':'-') + price + '.' + cents)
    this.setData({
      price:(((sign)?'':'-') + price + '.' + cents)
    })
  },
  onLoad: function () {
    var that = this
    var openId = wx.getStorageSync('accessToken')
    console.log('openId',openId)
    if (openId) {
      CategoryService.getIndexList().then(function (res) {
        console.log('getIndexList', res)
        that.setData({
          interest: res.data.categories
        })

      }).catch(Error.PromiseError)
    }
  }
})

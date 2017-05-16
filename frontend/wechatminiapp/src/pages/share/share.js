// pages/mine/mine.js
var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');


var app = getApp() 
Page({
  data: {
    interest:[{
      name:"造型",
      number:2
    },
    {
      name:"美甲",
      number:0
    },
    {
      name:"服装搭配",
      number:0
    },
    {
      name:"茶艺",
      number:0
    }],
    whoCanSee:['私密','好友','公开'],
    ifChoose:'造型',
    isClick:false,
    pricePrivacy:'0',
    index:0,
    dateValue:new Date().getFullYear()+"-"+new Date().getMonth()+"-"+new Date().getDate(), 
   tempFilePaths:'../../images/upload.png',
   uploadImg:[],
   uploadVideo:'',
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
    var that=this;
    wx.showActionSheet({
      itemList: ['上传照片', '上传视频'],
      success: function(res) {  
        // that.setData({
        //   tapIndex: res.tapIndex
        // })
        // console.log(that.data.tapIndex)
        if(res.tapIndex === 0){
          wx.chooseImage({
            count: 9, // 默认9
            sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
            success: function (res) {
              var tempFilePaths = res.tempFilePaths;
              that.setData({
                uploadImg:tempFilePaths.concat(that.data.uploadImg)
              })
              // console.log('1',that.data.uploadImg)
            }
     
          })
        }else if(res.tapIndex === 1){
          wx.chooseVideo({
            sourceType: ['album', 'camera'], // album 从相册选视频，camera 使用相机拍摄
            maxDuration: 60, // 拍摄视频最长拍摄时间，单位秒。最长支持60秒
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
              // complete
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
              console.log('用户点击确定')
            } else if (res.cancel) {
              console.log('用户点击取消')
            }
          }
        })
    }else{
      var that=this;
      wx.chooseImage({
        count: 9, // 默认9
        sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
        success: function (res) {
          var tempFilePaths = res.tempFilePaths;
          that.setData({
            uploadImg:tempFilePaths.concat(that.data.uploadImg)
          })
          console.log('3',that.data.uploadImg)
        },
        complete:function(){
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
                } else if (res.cancel) {
                  console.log('用户点击取消')
                }
              }
            })
          }
        }
      })
    }
    // console.log('2',that.data.uploadImg)  
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
            console.log('用户点击取消')
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

  tapSwitch : function(){
    // console.log(this.data.isClick)
    this.setData({
        isClick: !this.data.isClick
        
    });
    if(this.data.isClick == true){
      this.setData({
        pricePrivacy : '1'
      });
    }else{
      this.setData({
        pricePrivacy : '0'
    });
    }
  },
  
 formSubmit: function(e) {
    var that = this;
    var openId = wx.getStorageSync('accessToken')
    that.setData({   
      'submitAll.category':that.data.ifChoose,
      'submitAll.pictures':that.data.uploadImg,
      'submitAll.videos':that.data.uploadVideo,
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
    // console.log(that.data.submitAll)
    
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
          content: '请输入体验区域',
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
        CategoryService.releaseReport(that.data.submitAll).then(function(res){
          console.log('releaseReport',res)
          
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
    console.log(((sign)?'':'-') + price + '.' + cents)
    this.setData({
      price:(((sign)?'':'-') + price + '.' + cents)
    })
  },
  onLoad: function () {
       
  }
})

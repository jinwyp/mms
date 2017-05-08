// pages/mine/mine.js

var app = getApp() 
Page({
  data: {
   nickName:'',
   WXcode:'../../images/upload.png',
   tempFilePaths:'../../images/star.png',
   week:'',
   time: '09:00',
   time2: '18:00',
   latitude:'',
   longitude:'',
   tel:'',
   address:'请选择',
   submitAll:[{
      a:'',
      b:[],
      c:'',
      d:'',
      e:'',
      f:'',
      g:'',
      h:'',
      i:'',
      j:'',
      k:'',
      l:''

  }]
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
    console.log('checkbox发生change事件，携带value值为：', e.detail.value)
      this.setData({
          week: e.detail.value
      })
  },
  getLocation:function() {
    console.log('地图定位！')
    var that = this
    wx.chooseLocation({
        type: 'gcj02', //返回可以用于wx.openLocation的经纬度
        success: function (res) {
          console.log(res)
          that.setData({
            latitude : res.latitude,
            longitude : res.longitude,
            address : res.address
          })
            // var latitude = res.latitude; 
            // var longitude = res.longitude; 
            // wx.openLocation({
            //   latitude:latitude,
            //   longitude:longitude,
            //   scale:1
            // })
        }
    });
  },
  chooseimage: function () { 
    var _this = this; 
    wx.chooseImage({ 
    count: 1, // 默认9 
    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有 
    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有 
    success: function (res) { 
      // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片 
      _this.setData({ 
      tempFilePaths:res.tempFilePaths
      }) 
      wx.uploadFile({
        url: 'https://wd.gongshijia.com/upload',
        filePath:tempFilePaths[0],
        name:'name',
        // header: {}, // 设置请求的 header
        // formData: {}, // HTTP 请求中其他额外的 form data
        success: function(res){
          // success
        },
        fail: function() {
          // fail
        },
        complete: function() {
          // complete
        }
      })

    } 
    }) 
 } ,
 chooseWXcode: function () { 
    var _this = this; 
    wx.chooseImage({ 
    count: 1, // 默认9 
    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有 
    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有 
    success: function (res) { 
      // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片 
      _this.setData({ 
      WXcode:res.tempFilePaths
      }) 
      wx.uploadFile({
        url: 'https://wd.gongshijia.com/upload',
        filePath:WXcode[0],
        name:'name',
        // header: {}, // 设置请求的 header
        // formData: {}, // HTTP 请求中其他额外的 form data
        success: function(res){
          // success
        },
        fail: function() {
          // fail
        },
        complete: function() {
          // complete
        }
      })

    } 
    }) 
 } ,
checkTel:function(e) {
  var tel = e.detail.value;
  console.log(tel)
  var mobile = /^0?(13[0-9]|17[0-9]|15[0-9]|18[0-9]|14[57])[0-9]{8}$/;
  if(!mobile.test(tel)){
    wx.showModal({
      title: '提示',
      content: '请正确填写您的电话号码',
      success: function(res) {
        if (res.confirm) {
          console.log('用户点击确定')
        } else if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  }
},
 formSubmit: function(e) {
    // console.log('form发生了submit事件，携带数据为：', e.detail.value);

     var that = this;
     that.setData({
         'submitAll.a':that.data.tempFilePaths,
         'submitAll.b':e.detail.value.name,
         'submitAll.c':e.detail.value.tel,
         'submitAll.d':e.detail.value.shopName,
         'submitAll.e':that.data.address,
         'submitAll.f':that.data.latitude,
         'submitAll.g':that.data.longitude,
         'submitAll.h':e.detail.value.wx,
         'submitAll.i':that.data.WXcode,
         'submitAll.j':that.data.week,
         'submitAll.k':that.data.time,
         'submitAll.l':that.data.time2
     })
     console.log(that.data.submitAll)
  },

  onLoad: function () {
    
       
  }
})

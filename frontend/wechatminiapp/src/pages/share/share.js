// pages/mine/mine.js

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
    index:0,
    dateValue:new Date().getFullYear()+"-"+new Date().getMonth()+"-"+new Date().getDate(),
   
   tempFilePaths:'../../images/upload.png',
   uploadImg:[],
   slideshow: false,
   allValue:'',
   markers: [{
      latitude: 23.099994,
      longitude: 113.324520,
      name: 'T.I.T 创意园',
      desc: '我现在的位置'
    }],
    covers: [{
      latitude: 23.099794,
      longitude: 113.324520,
      iconPath: '../../images/wechart.png',
      rotate: 10
    }, {
      latitude: 23.099298,
      longitude: 113.324129,
      iconPath: '../../images/wechart.png',
      rotate: 90
    }]
  }, 
  
  // 上传图片
  uploadImg:function(){
    if(this.data.uploadImg.length>=9){
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
    console.log(this.data.uploadImg)
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
    console.log('地图定位！')
    var that = this
    wx.chooseLocation({
        type: 'gcj02', //返回可以用于wx.openLocation的经纬度
        success: function (res) {
          console.log(res)
            var latitude = res.latitude; 
            var longitude = res.longitude; 
            wx.openLocation({
              latitude:latitude,
              longitude:longitude,
              scale:1
            })
        }
    });
  },
//   chooseimage: function () { 
//     var _this = this; 
//     wx.chooseImage({ 
//     count: 1, // 默认9 
//     sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有 
//     sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有 
//     success: function (res) { 
//       // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片 
//       _this.setData({ 
//       tempFilePaths:res.tempFilePaths
//       }) 
//       wx.uploadFile({
//         url: 'https://wd.gongshijia.com/upload',
//         filePath:tempFilePaths[0],
//         name:'name',
//         // header: {}, // 设置请求的 header
//         // formData: {}, // HTTP 请求中其他额外的 form data
//         success: function(res){
//           // success
//         },
//         fail: function() {
//           // fail
//         },
//         complete: function() {
//           // complete
//         }
//       })

//     } 
//     }) 
//  } ,
 radioChange: function(e) {
    console.log('radio发生change事件，携带value值为：', e.detail.value)
    this.setData({
      ifChoose:e.detail.value
    })
    
  },
  tapSwitch : function(){
    console.log(this.data.isClick)
    this.setData({
        isClick: !this.data.isClick
    });
  },
  
 
 formSubmit: function(e) {
    console.log('form发生了submit事件，携带数据为：', e.detail.value);
    this.setData({
      allValue:e.detail.value
      })
  },
  formReset: function() {
    console.log('form发生了reset事件');
    this.setData({
   allValue:''
  })
  },
  onLoad: function () {
       
  }
})

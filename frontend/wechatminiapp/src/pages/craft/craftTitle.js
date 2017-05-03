// pages/craft/craftTitle.js
Page({
  data:{
      name:'张三丰',
      mobile:'13045105222',
      shopAddress:'哈尔滨市道里区',
      uploadImg:[],
      costList:[{costname:''}],
      range:['5','10','15','20','25','30','35','40','45','50','55','60','65','70','75','80','85','90','95','100','105','110','115','120'],
      gyprocessList:[{gyInfo:'',costTime:'0'}],
      slideshow: false,
      currentId:0,
      modalHidden:true
    },
  onLoad:function(options){
      wx.setNavigationBarTitle({
        title: '手艺署名'
    })
  },
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
      var that=this,policy,callback,signature,OssAccessKeyId
      wx.request({
        url: 'http://zxy.gongshijia.com/getOssPolicy',
        method:'GET',
        header: {
          'content-type': 'application/json'
        },
        success: function(res) {
          policy=res.data.policy;
          callback=res.data.callback;
          signature=res.data.signature;
          OssAccessKeyId=res.data.accessid;
          console.log(res)
        }
      })

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
        complete:function(res){
           var tempFilePaths = res.tempFilePaths;
           wx.uploadFile({
                url: 'https://gsjtest.oss-cn-shanghai.aliyuncs.com/',
                header: {
                  'content-type': 'multipart/form-data'
                },
                filePath:tempFilePaths[0],
                name: 'file',
                formData: {
                  key:'${filename}',
                  policy: policy ,
                  success_action_status:200,
                  callback: callback,
                  signature:signature,
                  OSSAccessKeyId:OssAccessKeyId,
                  file:that.data.uploadImg
                },
                success: function(res) {
                  // console.log(res)
                },
                complete:function(res){
                  console.log(res)
                }
              })
          // if(that.data.uploadImg.length>9){
          //     wx.showModal({
          //     title: '提示',
          //     content: '店内实景最多上传9张',
          //     showCancel:false,
          //     success: function(res) {
          //       if (res.confirm) {
          //         that.data.uploadImg.length=9;
          //           that.setData({
          //               uploadImg:that.data.uploadImg
          //             })

                  

          //       } else if (res.cancel) {
          //         console.log('用户点击取消')
          //       }
          //     }
          //   })
          // }else{
          //   wx.uploadFile({
          //     url: 'http://gsjtest.oss-cn-shanghai.aliyuncs.com/',
          //     method:'POST',
          //     header: {
          //       'content-type': 'multipart/form-data'
          //     },
          //     filePath:that.data.uploadImg,
          //     name: 'file',
          //     formData: {
          //       key:'${filename}',
          //       policy: policy ,
          //       success_action_status:200,
          //       callback: callback,
          //       signature:signature,
          //       OssAccessKeyId:OssAccessKeyId,
          //       file:that.data.uploadImg
          //     },
          //     success: function(res) {
          //       console.log(res)
          //     }
          //   })
          // }
        }
      })
    }
  },
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
  bindblurFun:function(e){
    var idx = e.target.dataset.idx,
        obj = this.data.costList
        this.setData({
          ['costList['+idx+'].costname']:e.detail.value
        })
  },
  textareaVal:function(e){
    var idx = e.target.dataset.idx,
            obj = this.data.gyprocessList
            this.setData({
              ['gyprocessList['+idx+'].gyInfo']:e.detail.value
            })
  },
  addCost:function(){
    if(this.data.costList.length>9){
          wx.showModal({
            title: '提示',
            content: '耗材信息不能超过10条',
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
        var obj={costname:''}
        this.setData({
            costList:this.data.costList.concat(obj)
        })
    }
    
  },
  subCost:function(e){
    var idx = e.target.dataset.idx;
    if(this.data.costList.length<=1){
        wx.showModal({
          title: '提示',
          content: '耗材不可以为空',
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
        this.data.costList.splice(idx,1)
        this.setData({
            costList:this.data.costList
        });
    }
  },
  costTimeChange:function(e){
        var idx = e.target.dataset.idx;
        this.setData({
          ['gyprocessList['+idx+'].costTime']:this.data.range[e.detail.value]
        })
    
  },
  addgy:function(){
    if(this.data.gyprocessList.length>9){
      wx.showModal({
          title: '提示',
          content: '工艺流程不能超过10条',
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
        var obj={gyInfo:'',costTime:'0'}
        this.setData({
            gyprocessList:this.data.gyprocessList.concat(obj)
        })
    }
  },
  subgy:function(e){
    var idx = e.target.dataset.idx;
    if(this.data.gyprocessList.length<=1){
        wx.showModal({
          title: '提示',
          content: '工艺流程不可以为空',
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
        this.data.gyprocessList.splice(idx,1)
        this.setData({
            gyprocessList:this.data.gyprocessList
        });
        console.log(this.data.gyprocessList)
    }
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
  uploadBtn:function(){
    this.setData({
        modalHidden:false
    });
  },
  onShareAppMessage:function(){
    return {
      title: '自定义分享标题',
      path: '/page/craft/craftTitle',
      success: function(res) {
        console.log(res)
      },
      fail: function(res) {
        // 分享失败
      }
    }
  }
})
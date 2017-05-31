// pages/craft/craftTitle.js
var UserService = require('../../service/user.js');
var CategoryService = require('../../service/category.js');
var Error = require('../../service/error.js');
var apiPath = require("../../service/apiPath.js");
var totalVerify = false;
var rpiId = ''
Page({
  data:{
      nickName:'',
      mobile:'',
      shopName:'',
      uploadImg:[],
      avatarUrl:'',
      costList:[{name:'',count:''}],
      range:['5','10','15','20','25','30','35','40','45','50','55','60','65','70','75','80','85','90','95','100','105','110','115','120'],
      gyprocessList:[{flow:'',duration:0}],
      slideshow: false,
      currentId:0,
      modalHidden:true,
      uploadPath:[],
      introd:''
    },
  onLoad:function(options){
      
    rpiId = options.reportId ;
    console.log('reportid' + rpiId)
    var that=this;
    CategoryService.addProcess('').then(function (res) {
      that.setData({
        nickName: res.nickName,
        phone: res.phone,
        shopName: res.shopName,
        avatarUrl: res.avatarUrl
      })
    }).catch(Error.PromiseError)
    
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
      var that=this,policy,callback,signature,OssAccessKeyId;
      var suffix=''
      function generateUUID(){
          var d = new Date().getTime();
          var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
              var r = (d + Math.random()*16)%16 | 0;
              d = Math.floor(d/16);
              return (c=='x' ? r : (r&0x7|0x8)).toString(16);
          });
          return uuid;
      };
  
      wx.chooseImage({
        count: 9, // 默认9
        sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
        sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
        success: function (res) {
          that.setData({
            uploadImg:res.tempFilePaths.concat(that.data.uploadImg)
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
            for (var i = 0; i < tempFilePaths.length; i++){
              wx.uploadFile({
                url: apiPath.ossUrl,
                filePath: tempFilePaths[i],
                name: 'file',
                header: { "content-Type": "multipart/form-data" },
                success: function (res) {
                  var myFileName = JSON.parse(res.data).data
                  that.data.uploadPath.push(myFileName)
                },
                fail: function (res) {
                  console.log(res)
                }
              })
            }
          }
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
          ['costList['+idx+'].name']:e.detail.value,
          ['costList['+idx+'].count']:idx
        })
  },
  textareaVal:function(e){
    var idx = e.target.dataset.idx,
            obj = this.data.gyprocessList
            this.setData({
              ['gyprocessList['+idx+'].flow']:e.detail.value
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
        var obj={name:'',count:''}
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
          ['gyprocessList['+idx+'].duration']:this.data.range[e.detail.value]*1
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
        var obj={flow:'',duration:0}
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
  introChange:function(e){
    this.setData({
        introd:e.detail.value
    });
  },
  verify : function(){
    var uploadImgStatus , costListStatus , gyprocessListStatus , introdStatus
    if(this.data.uploadImg.length < 1){
      wx.showModal({
        title: '提示',
        content: '店内实景不能为空',
        showCancel:false,
        success: function(res) {
          uploadImgStatus = false;
        }
      })
      return totalVerify = false;
    }
    for(var j=0;j<this.data.costList.length;j++){
      if(this.data.costList[j].name==''){
          wx.showModal({
          title: '提示',
          content: '耗材不能为空',
          showCancel:false,
          success: function(res) {
            costListStatus = false;
          }
        })
          return totalVerify = false;
      }
       
    }

    for(var i=0;i<this.data.gyprocessList.length;i++){
      if(this.data.gyprocessList[i].flow==''){
          wx.showModal({
            title: '提示',
            content: '工艺流程不能为空',
            showCancel:false,
            success: function(res) {
              gyprocessListStatus = false;
            }
        })
          return totalVerify = false;
      }
      
    }
    
      if(this.data.introd==''){
          wx.showModal({
          title: '提示',
          content: '工艺说明不能为空',
          showCancel:false,
          success: function(res) {
            introdStatus = false;
          }
        })
          return totalVerify=false;
      }
      totalVerify = true
  },
  uploadBtn:function(){
    this.verify()
   
    if(totalVerify){
      this.setData({
          modalHidden:false
      });

        var obj = {
          'reportid': rpiId,
          'realPicture': this.data.uploadPath,
          'material' : this.data.costList,
          'flows': this.data.gyprocessList,
          'checked' : false,
          'introd' : this.data.introd
        }

      var openId = wx.getStorageSync('accessToken')
      if (openId) {
          CategoryService.craftTitleUpload(obj).then(function(res){

          }).catch(Error.PromiseError)
      }
    }

    


  },
  onShareAppMessage:function(){
    var openId = wx.getStorageSync('accessToken')
    return {
      title: '工时家',
      // path: '/report/shareReport/' + rpiId+ '/'+openId,
      path:'/pages/friendsExp/friendsList/friendsList',
      success: function(res) {
        wx.redirectTo({
          url: '/pages/friendsExp/friendsList/friendsList'
        })
      },
      fail: function(res) {
      }
    }
  }
})
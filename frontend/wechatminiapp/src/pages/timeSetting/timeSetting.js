// pages/timeSetting/timeSetting.js
Page({
  data:{
    date: '2016-09-01',
    time: '09:00',
    time2: '18:00',
    timeDown: '18:00',
    mon:'',
    tues:'',
    wed:'',
    Thur:'',
    Fri:'',
    Sat:'',
    Sun:''
    
  },
  //星期选择
  checkboxChange1: function(e) { 
    if(e.target.dataset.mon === ''){
      this.setData({
        mon: 'true'
      })
    }else{
      this.setData({
        mon: ''
      })
    }
  },
  checkboxChange2: function(e) {    
    if(e.target.dataset.tues === ''){
      this.setData({
        tues: 'true'
      })
    }else{
      this.setData({
        tues: ''
      })
    }
  },
  checkboxChange3: function(e) {    
    if(e.target.dataset.wed === ''){
      this.setData({
        wed: 'true'
      })
    }else{
      this.setData({
        wed: ''
      })
    }
  },
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
  //表单提交
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
  onLoad:function(options){
    // 页面初始化 options为页面跳转所带来的参数
    wx.setNavigationBarTitle({
      title: '工作时间设定',
      success: function(res) {
        // success
      }
    })
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
// pages/timeSetting/timeSetting.js
Page({
  data:{
    date: '2016-09-01',
    time: '09:00',
    time2: '18:00',
    timeDown: '18:00',
    mon:false,
    tues:false,
    wed:false,
    thur:false,
    fri:false,
    sat:false,
    sun:false
    
  },
  //星期选择
  checkboxChange1: function(e) { 
    this.setData({
      mon: !e.target.dataset.mon
    })
  },
  checkboxChange2: function(e) {    
    this.setData({
      tues: !e.target.dataset.tues
    })
  },
  checkboxChange3: function(e) {    
    this.setData({
      wed: !e.target.dataset.wed
    })
  },
  checkboxChange4: function(e) {    
    this.setData({
      thur: !e.target.dataset.thur
    })
  },
  checkboxChange5: function(e) {    
    this.setData({
      fri: !e.target.dataset.fri
    })
  },
  checkboxChange6: function(e) {    
    this.setData({
      sat: !e.target.dataset.sat
    })
  },
  checkboxChange7: function(e) {    
    this.setData({
      sun: !e.target.dataset.sun
    })
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
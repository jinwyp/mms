//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
   interest:[
     {name:'造型',value:'造型',checked: 'true'},
     {name:'美甲',value:'美甲',},
     {name:'服装',value:'服装',},
     {name:'茶艺',value:'茶艺',},
     {name:'美容',value:'美容',},
     {name:'美发',value:'美发',} 
    ]
  //  checkItems:[],
  //  currentInterest:{},
  },
  
  checkboxChange: function(e) {  
    console.log('checkbox发生change事件，携带value值为：', e.detail.value) 
     
    // this.setData({
    //   checkItems: e.detail.value
    // }) 

    // console.log('checkbox发生change事件，携带checkItems值为：', this.data.checkItems)  
    
  }, 
  checkItem: function(e) {
    console.log('checkbox发生checkItem事件，携带value值为：', e.target.dataset.value) 
    interest["checked"] = "true";  
  },
  
  //表单提交
  formSubmit: function(e) {
    console.log('form发生了submit事件，携带数据为：', e.detail.value);
  },
  formReset: function() {
    console.log('form发生了reset事件');
  },
  onLoad: function () {
    // console.log('onLoad')
  }
})

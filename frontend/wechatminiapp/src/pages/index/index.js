//index.js
//获取应用实例
var app = getApp()
Page({
  data: {
   interest:[
     {name:'造型',value:'造型',checked: false},
     {name:'美甲',value:'美甲',checked: false},
     {name:'服装',value:'服装',checked: false},
     {name:'茶艺',value:'茶艺',checked: false},
     {name:'美容',value:'美容',checked: false},
     {name:'美发',value:'美发',checked: false} 
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
    // console.log('checkbox发生checkItem事件，携带value值为：', e.target.dataset.value) 
    // interest["checked"] = "true";  
     var index=e.target.dataset.index;
    var a = !this.data.interest[index].checked
    console.log(a)
    this.setData({
      ['interest['+index+'].checked']:a
    })
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

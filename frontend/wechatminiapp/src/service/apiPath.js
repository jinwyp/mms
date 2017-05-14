
//var apiPrefix = 'http://zxy.gongshijia.com'
var apiPrefix = 'http://gongshijia.com.ngrok.io'

var apiPath = {
    signUpWechat : apiPrefix + '/user/login/login',
    test : apiPrefix +'/misc/comment',
    getIndexList: apiPrefix +'/category/category',
    pushIndexList: apiPrefix +'/category/saveCategories'
}

module.exports = apiPath;
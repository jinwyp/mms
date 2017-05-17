
var apiPrefix = 'http://zxy.gongshijia.com'
// var apiPrefix = 'http://gongshijia.com.ngrok.io'

var apiPath = {
    signUpWechat : apiPrefix + '/user/login/login',
    test : apiPrefix +'/misc/comment',
    getIndexList: apiPrefix +'/category/category',
    pushIndexList: apiPrefix +'/category/saveCategories',
    releaseReport: apiPrefix +'/report/releaseReport',
    ossUrl: 'https://gsjtest.oss-cn-shanghai.aliyuncs.com/'
}

module.exports = apiPath;
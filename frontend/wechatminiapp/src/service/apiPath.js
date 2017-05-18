
var apiPrefix = 'http://zxy.gongshijia.com'
// var apiPrefix = 'http://gongshijia.com.ngrok.io'

var apiPath = {
    ossUrl: 'https://gsjtest.oss-cn-shanghai.aliyuncs.com/',
    signUpWechat : apiPrefix + '/user/login/login',
    test : apiPrefix +'/misc/comment',
    getIndexList: apiPrefix +'/category/category',
    pushIndexList: apiPrefix +'/category/saveCategories',
    releaseReport: apiPrefix +'/report/releaseReport',
    friendList:  apiPrefix +'/report/loadFriendReport?pageSize=5&page=',
    friendDetail:  apiPrefix +'/report',
    craftTitleUpload: apiPrefix + '/report/signReport',
    improveInfo: apiPrefix +'/user/improveInfo',
    
}

module.exports = apiPath;
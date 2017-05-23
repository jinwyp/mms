
var apiPrefix = 'http://zxy.gongshijia.com'
//  var apiPrefix = 'http://gongshijia.com.ngrok.io'

var apiPath = {
    ossUrl: 'https://gsjtest.oss-cn-shanghai.aliyuncs.com/',
    signUpWechat : apiPrefix + '/user/login/login',
    test : apiPrefix +'/misc/comment',
    getIndexList: apiPrefix +'/category/category',
    pushIndexList: apiPrefix +'/category/saveCategories',
    releaseReport: apiPrefix +'/report/releaseReport',
    friendList:  apiPrefix +'/report/loadFriendReport?pageSize=5&page=',
    friendDetail:  apiPrefix +'/report/report',
    craftTitleUpload: apiPrefix + '/report/signReport',
    improveInfo: apiPrefix +'/user/improveInfo',
    addProcess: apiPrefix +'/user/loadUserInfo',
    loadCollectReport: apiPrefix + '/report/loadCollectReport?category=茶艺',
    getCollectReport: apiPrefix + '/report/loadCollectReport?category=',
    removeCollectReport: apiPrefix + '/report/removeCollectReport',
    addReportToCollect: apiPrefix + '/report/addReportToCollect',
    handlerMakeSureReport: apiPrefix + '/report/handlerMakeSureReport',
    addComment: apiPrefix+ '/report/addComment',
    makeSureReport: apiPrefix + '/report/makeSureReport'
}

module.exports = apiPath;

var apiPrefix = 'http://zxy.gongshijia.com'
// var apiPrefix = 'http://gongshijia.com.ngrok.io'

var apiPath = {
    signUpWechat : apiPrefix + '/user/login/login',
    test : apiPrefix +'/misc/comment',
    getIndexList: apiPrefix +'/category/category',
    pushIndexList: apiPrefix +'/category/saveCategories',
    releaseReport: apiPrefix +'/report/releaseReport',
    friendList:  apiPrefix +'/report/loadFriendReport?pageSize=5&page=1',
    friendDetail:  apiPrefix +'/report',
}

module.exports = apiPath;
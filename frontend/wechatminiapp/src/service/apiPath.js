
var apiPrefix = 'https://zxy.gongshijia.com'

var apiPath = {
    ossUrl: apiPrefix+'/asset/upload',
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
    craftDetail: apiPrefix + '/user/loadUserInfoByOpenId?openid=',
    loadCollectReport: apiPrefix + '/report/loadCollectReport?category=造型',
    getCollectReport: apiPrefix + '/report/loadCollectReport?category=',
    removeCollectReport: apiPrefix + '/report/removeCollectReport',
    addReportToCollect: apiPrefix + '/report/addReportToCollect',
    handlerMakeSureReport: apiPrefix + '/report/handlerMakeSureReport',
    addComment: apiPrefix+ '/report/addComment',
    makeSureReport: apiPrefix + '/report/makeSureReport',
    friendShareDetail: apiPrefix + '/report/createSharedRelationShip/'
}

module.exports = apiPath;
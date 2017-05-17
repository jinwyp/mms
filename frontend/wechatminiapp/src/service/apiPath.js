
// var apiPrefix = 'http://hary.gongshijia.com'

var apiPrefix = 'http://zxy.gongshijia.com'

var apiPath = {
    signUpWechat : apiPrefix + '/login/login',
    test : apiPrefix +'/misc/comment',
    getIndexList: apiPrefix +'/misc/arts',
    pushIndexList: apiPrefix +'/misc/saveCategories'
}

module.exports = apiPath;
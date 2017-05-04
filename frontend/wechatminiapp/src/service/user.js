var wechat = require("../libs/wechat.js");
var apiPath = require("./apiPath.js");



function getWXUserInfo(){

    return wechat.login().then(function (res) {

        console.log('login: ', res);

        var resSimple = {
            code : "051J1y551mgHmN1kio651kGU551J1y58",
            errMsg : "login:ok"
        }

        if (res.code && res.errMsg === 'login:ok') {

            return wechat.getUserInfo().then(function (result) {

                console.log('getUserInfo: ', result)
                console.log(result.userInfo)

                var resultSimple = {
                    avatarUrl : "http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83epemhhMHVDahoKunWpAicYNibAz2joznAUeeMYER10ux3z1ibOWEXvuIuVfV062tYc7CZbsItZmCV49g/0",
                    city      : "",
                    country   : "CN",
                    gender    : 1,
                    language  : "zh_CN",
                    nickName  : "王宇鹏",
                    province  : "Shanghai"
                }


                var currentUser = {
                    code : res.code,
                    avatarUrl : result.userInfo.avatarUrl,
                    country : result.userInfo.country,
                    province : result.userInfo.province,
                    city : result.userInfo.city,
                    gender : result.userInfo.gender, //性别 0：未知、1：男、2：女 
                    language : result.userInfo.language,
                    nickName : result.userInfo.nickName,

                    rawData :result.rawData,
                    signature : result.signature,

                    encryptedData : result.encryptedData,
                    iv : result.iv
                }

                return (currentUser);
                
            }).catch(console.log)

        
        } else {
            console.log('获取用户登录态失败！' + res.errMsg)
            return res.errMsg
        }
    })
    
}

function signUp (data){
    return wechat.request(apiPath.signUpWechat, data, 'POST')
}


function addComment (data){
    return wechat.request(apiPath.test, data, 'POST')
}



module.exports = {
    getWXUserInfo : getWXUserInfo,
    signUp : signUp,
    addComment: addComment
}








var wechat = require("../libs/wechat.js");
var apiPath = require("./apiPath.js");


function getIndexList (data){
    return wechat.request(apiPath.getIndexList, data, 'GET')
}
function pushIndexList (data){
    return wechat.request(apiPath.pushIndexList, data, 'POST')
}
function releaseReport (data){
    return wechat.request(apiPath.releaseReport, data, 'POST')
}
function friendList (data){
    return wechat.request(apiPath.friendList, data, 'GET')
}
function friendDetail (data,id){
    return wechat.request(apiPath.friendDetail +'/'+id, data, 'GET')
}
function craftTitleUpload (data){
    return wechat.request(apiPath.craftTitleUpload, data, 'POST')
}
module.exports = {
    getIndexList : getIndexList,
    pushIndexList: pushIndexList,
    releaseReport: releaseReport,
    friendList: friendList,
    friendDetail: friendDetail,
    craftTitleUpload: craftTitleUpload,
}
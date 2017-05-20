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
function friendList (data,id){
    return wechat.request(apiPath.friendList + id, data, 'GET')
}
function friendDetail (data,id){
    return wechat.request(apiPath.friendDetail +'/'+id, data, 'GET')
}
function craftTitleUpload (data){
    return wechat.request(apiPath.craftTitleUpload, data, 'POST')
}
function improveInfo(data) {
  return wechat.request(apiPath.improveInfo, data, 'POST')
}
function addProcess(data) {
  return wechat.request(apiPath.addProcess, data, 'GET')
}
function loadCollectReport(data) {
  return wechat.request(apiPath.loadCollectReport, data, 'GET')
}
function handlerMakeSureReport(data) {
  return wechat.request(apiPath.handlerMakeSureReport, data, 'GET')
}
function addReportToCollect(data) {
  return wechat.request(apiPath.addReportToCollect, data, 'GET')
}
function addComment(data) {
  return wechat.request(apiPath.addComment, data, 'POST')
}
function makeSureReport(data) {
  return wechat.request(apiPath.makeSureReport, data, 'GET')
}

module.exports = {
    getIndexList : getIndexList,
    pushIndexList: pushIndexList,
    releaseReport: releaseReport,
    friendList: friendList,
    friendDetail: friendDetail,
    craftTitleUpload: craftTitleUpload,
    improveInfo: improveInfo,
    addProcess: addProcess,
    loadCollectReport: loadCollectReport,
    handlerMakeSureReport: handlerMakeSureReport,
    addReportToCollect: addReportToCollect,
    makeSureReport: makeSureReport,
    addComment: addComment
}
var wechat = require("../libs/wechat.js");
var apiPath = require("./apiPath.js");


function getIndexList (data){
    return wechat.request(apiPath.getIndexList, data, 'GET')
}
function pushIndexList (data){
    return wechat.request(apiPath.pushIndexList, data, 'POST')
}


module.exports = {
    getIndexList : getIndexList,
    pushIndexList: pushIndexList
    
}
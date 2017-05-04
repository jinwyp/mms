var wechat = require("../libs/wechat.js");
var apiPath = require("./apiPath.js");


function getIndexList (data){
    return wechat.request(apiPath.getIndexList, data, 'GET')
}


module.exports = {
    getIndexList : getIndexList
    
}
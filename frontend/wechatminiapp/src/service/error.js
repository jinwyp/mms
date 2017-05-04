function PromiseError(error){
    console.log(error)
    wx.clearStorageSync()

}




module.exports = {
    PromiseError : PromiseError
}

package com.gongshijia.mms.core

import redis.RedisClient

/**
  * Created by hary on 2017/5/12.
  */
// redis support
trait RedisSupport extends Core {

  val redisClient = RedisClient(coreConfig.getString("redis.host"), coreConfig.getInt("redis.port"))

}

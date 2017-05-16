package com.gongshijia.mms.core

import org.anormcypher.{Cypher, CypherResultRow, Neo4jREST}
import play.api.libs.ws.ning

import scala.concurrent.Future


/**
  * Created by hary on 2017/5/12.
  */
trait Neo4jSupport extends Core {
  // Provide an instance of WSClient
  val wsclient = ning.NingWSClient()
  implicit val connection: Neo4jREST = Neo4jREST(
    coreConfig.getString("neo4j.host"),
    coreConfig.getInt("neo4j.port"),
    coreConfig.getString("neo4j.username"),
    coreConfig.getString("neo4j.password"))(wsclient)

    implicit val ec = scala.concurrent.ExecutionContext.global

  /**
    * 1.创建node
    * 2.建立好友关系
    *
    * @param fromOpenid
    * @param toOpenid
    * @param expid
    * @return
    */

  def createFriendRelationShip(fromOpenid: String, toOpenid: String, expid: String): Future[Boolean] = {
    Cypher(
      """merge (a:User{ openid:{fromOpenid}}) ON   CREATE SET  (u:User{openid:{fromOpenid},createDate:timestamp() })
        |merge (b:User{ openid:{toOpenid}})   ON   CREATE SET  (b:User{openid:{toOpenid},createDate:timestamp() }
        | match (a:User),(b:User) where a.openid={fromOpenid} and b.openid={toOpenid}
        |       create (a)-[r:IS_FRIEND {expid:{expid}}]->(b)
        |       return r
        |""").on("fromOpenid" -> fromOpenid, "toOpenid" -> toOpenid, "expid" -> expid).executeAsync()
  }

  //  //加载我的好友
  def loadMyFriend(openid: String): Future[Seq[CypherResultRow]] = {
    Cypher("""MATCH (u:User{ openid:{fromOpenid} })-[r:IS_FRIEND]->(f:User) RETURN f.openid""")
      .on("openid" -> openid).async()
  }
}

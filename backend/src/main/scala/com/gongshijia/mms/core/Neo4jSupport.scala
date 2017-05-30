package com.gongshijia.mms.core

import org.anormcypher._
import play.api.libs.ws.ahc

import scala.concurrent.Future


/**
  * Created by hary on 2017/5/12.
  */
trait Neo4jSupport extends Core {
  // Provide an instance of WSClient
  implicit val wsclient = ahc.AhcWSClient()
  implicit val neo4jRest: Neo4jREST = Neo4jREST(
    coreConfig.getString("neo4j.host"),
    coreConfig.getInt("neo4j.port"),
    coreConfig.getString("neo4j.username"),
    coreConfig.getString("neo4j.password"))

    implicit val ec = scala.concurrent.ExecutionContext.global

  //创建好友关系
    def createFriendRelationShip(fromOpenId: String, toOpenId: String, expId: String): Future[Boolean] = {
      Cypher(s"""merge (a:User {openid: "${fromOpenId}" })  ON  CREATE SET  a.openid="${fromOpenId}", a.createDate=timestamp()
                 merge (b:User {openid: "${toOpenId} "  })  ON  CREATE SET  b.openid="${toOpenId}",   b.createDate=timestamp()
                 create (a)-[r:SHARED {expId:"${expId}",createDate:timestamp()}] ->(b) """ ).executeAsync()

      }

  //加载我的好友
    def loadMyFriend(openid: String): Seq[String] = {
      val req= Cypher(s"""MATCH (u:User{ openid:"${openid}" })-[r:SHARED]-(f:User) RETURN distinct f.openid""")
      val stream: Seq[CypherResultRow] = req()
      stream.map(row => { row[String]("f.openid") })
    }
}

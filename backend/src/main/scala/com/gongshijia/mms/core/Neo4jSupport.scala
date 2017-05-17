package com.gongshijia.mms.core

import org.anormcypher.{Neo4jConnection, Neo4jREST}
import play.api.libs.ws.ning

/**
  * Created by hary on 2017/5/12.
  */
trait Neo4jSupport extends Core {
  // Provide an instance of WSClient
  val wsclient = ning.NingWSClient()
  implicit val connection: Neo4jConnection = Neo4jREST(
    coreConfig.getString("neo4j.host"),
    coreConfig.getInt("neo4j.port"),
    coreConfig.getString("neo4j.username"),
    coreConfig.getString("neo4j.password"))(wsclient)
}

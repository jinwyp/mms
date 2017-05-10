package com.gongshijia.mms.neo4j



import org.anormcypher._
import play.api.libs.ws._

import scala.concurrent.{Await, Future}



object neo4jTest extends App {

  // Provide an instance of WSClient
  val wsclient = ning.NingWSClient()

  // Setup the Rest Client
  // Need to add the Neo4jConnection type annotation so that the default
  // Neo4jConnection -> Neo4jTransaction conversion is in the implicit scope
  // Provide an ExecutionContext


  // 隐式
  implicit val connection: Neo4jConnection = Neo4jREST("localhost", 7474, "neo4j", "jessie")(wsclient)
  implicit val ec = scala.concurrent.ExecutionContext.global


  // create some test nodes
  Cypher("""create (anorm:anormcyphertest {name:"AnormCypher"}), (test:anormcyphertest {name:"Test"})""").execute()

  // a simple query
  val req = Cypher("match (n:anormcyphertest) return n.name")

  // get a stream of results back
  val stream = req()

  // get the results and put them into a list
  stream.map(row => {
    row[String]("n.name")
  }).toList.map(println)

  // remove the test nodes
  Cypher("match (n:anormcyphertest) delete n")()

  // shut down WSClient
  wsclient.close()



}

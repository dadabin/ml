package com.ml

import org.apache.spark.SparkContext
import scala.collection.mutable._
import  scala.runtime.RichDouble
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

/**
 * Created by rimi on 2015/2/13.
 */
object DocKMeans {

  def main(args: Array[String]): Unit ={

    val conf=new SparkConf().setAppName("DocKMeans").setSparkHome("D:/scala/spark-1.0.0-bin-hadoop2").setMaster("local[*]")

    val sc=new SparkContext(conf)
    //读取文件
    // val data=sc.textFile("hdfs://192.168.7.12:9000/input/out2.txt")
    val data = sc.textFile("D:/out_tf_5/part-00000")
    val data2 = sc.textFile("D:/out_tf_5/part-00001")

    val parsedData=data.++(data2).map(s=>{
      val str=s.split(",")(1).replace(")","").trim()
      //Vectors.dense(str.split(' ').map(_.toDouble))
      val arry:Array[Double]=str.split(' ').map(_.toDouble)
      Vectors.dense(arry)
    }).cache()

    // Cluster the data into two classes using KMeans
    val numClusters = 4
    val numIterations = 100
    val runs=6000
    val clusters = KMeans.train(parsedData, numClusters, numIterations,runs)
    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)

    println("Within Set Sum of Squared Errors = " + WSSSE)


    clusters.clusterCenters.foreach((a)=>{
      println(a)
    })

    println(clusters.clusterCenters.length)

    data.++(data2).map(s => {
      val str = s.split(",")(1).replace(")", "").trim()
      ( clusters.predict(Vectors.dense(str.split(' ').map(_.toDouble))),s.split(",")(0).replace(")",""))

    }).sortByKey(true, 1).saveAsTextFile("D:/out_kmeans_17")
  }

}

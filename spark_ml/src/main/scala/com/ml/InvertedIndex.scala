package com.ml

import org.apache.spark.SparkContext
import scala.collection.mutable._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

/**
 * Created by rimi on 2015/2/13.
 */
object InvertedIndex {

  def main(args:Array[String]){
    val conf = new SparkConf().setAppName("InvertedIndex").setSparkHome("D:/scala/spark-1.0.0-bin-hadoop2")
    conf.setMaster("local[*]")
    val sc = new SparkContext(conf)
    //读取文件
    // val data=sc.textFile("hdfs://192.168.7.12:9000/input/out2.txt")
    val words=sc.textFile("D:/input.txt").map(file=>file.split(",")).map(item=>{
      (item(0),item(1))
    }).flatMap(file=>{
      var list=new LinkedList[(String,String)]
      val words=file._2.split(" ").iterator
      while(words.hasNext){
        list = (file._1,words.next)+:list
      }
      list
    }).distinct()
    words.map(word =>{
      (word._2,word._1)
    }).reduceByKey(_+" "+_).saveAsTextFile("D:/out_index")
  }

}

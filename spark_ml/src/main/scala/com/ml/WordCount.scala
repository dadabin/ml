package com.ml

import org.apache.spark._
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.SparkContext._

/**
 * Created by rimi on 2015/2/13.
 */
object WordCount {

  def main(args:Array[String]): Unit ={
    val conf= new SparkConf().setAppName("word count").setSparkHome("D:/scala/spark-1.0.0-bin-hadoop2")
    conf.setMaster("local[*]")

    val sc= new SparkContext(conf)

    //读取文件
    //val data = sc.textFile("hdfs://192.168.7.12:9000/input/out2.txt")
    val data= sc.textFile("D:/message/input_keyword.txt")
    val counts=data.flatMap(_.split(" ")).map(x => (x,1)).reduceByKey(_+_)
    counts.saveAsTextFile("D:/out1")
  }

}

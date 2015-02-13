package com.ml

import org.apache.spark.SparkContext
import scala.collection.mutable._
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf


import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

/**
 * Created by rimi on 2015/2/13.
 */
object Tf {

  def main(args:Array[String]){
    val conf = new SparkConf().setAppName("tf").setSparkHome("D:/scala/spark-1.0.0-bin-hadoop2")
    conf.setMaster("local[*]")
    val sc = new SparkContext(conf)

    //得到 维度表
    val words2=sc.textFile("D:/input.txt").map(file=>file.split(",")).map(item=>{
      (item(0),item(1))
    }).flatMap(file=>{
      var list=new LinkedList[(String,String)]
      file._2.split(" ").iterator.toList
    }).distinct.toArray

    //更改维度表
    val words3=sc.textFile("D:/out1/part_001").flatMap(file=>file.split(" ")).map(item=>{
      item
    }).distinct.toArray


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
    }).map(a=>{
      (a._1+"="+a._2,1)
    }).reduceByKey(_+_).map(a=>{
      val v= a._1.split("=")
      // println(a._1)
      (v(0),v(1)+"="+a._2)
    }).groupByKey.map(a=>{
      var m=Map[String, String]()
      a._2.toIterable.foreach(str=>{
        val kv= str.split("=")
        // println(kv.length)
        if(kv.length==2) {
          m.put(kv(0).trim(), kv(1).trim())
          //   println(kv(0)+" ="+kv(1))
        }
      })

      //映射成矩阵

      var str=""
      //      println(words2.length)
      words3.foreach((s)=>{
        if(m.contains(s.trim())){
          str+= m.get(s.trim()).toString()+" "
        }else{
          str+="0"+" "
        }
      })

      //  println(str)
      // System.out.println(a._2)
      (a._1,str.trim().replace("Some(", "").replace(")", ""))
    }).saveAsTextFile("D:/out_tf_5")




    //val wordCountRDD= sc.parallelize(words,1)

    //       words.map(a=>{
    //         (a._1+"="+a._2,1)
    //       }).reduceByKey(_+_).map(a=>{
    //         val v= a._1.split("=")
    //         println(a._1)
    //         (v(0),v(1)+"="+a._2)
    //       }).groupByKey.map(a=>{
    //         var str=""
    //
    //         a._1.foreach(s=>{
    //           str=str+s+" "
    //         })
    //         //System.out.println(str)
    //         (a._1,str)
    //       })//.saveAsTextFile("D:/out_tf_1")

    //       wordCountRDD.map((a)=>{
    //         val m=Map[String, Int]()
    //        // System.out.println(a._2)
    //         a._1.trim().split(" ").foreach(str=>{
    //          val kv= str.split("=")
    //         // println(kv.length)
    //          if(kv.length==2) m.put(kv(0), kv(1).trim().toInt)
    //         })
    //         //映射成矩阵
    //
    //         var str=a._1+","
    //        /* words2.foreach((s)=>{
    //           str+= m.get(s)+" "
    //         })*/
    //         str.trim()
    //       }).collect().foreach((s)=>{
    //         println(s)
    //       })
    /* words.map(word =>{
       (word._2,word._1)
     }).reduceByKey(_+" "+_).saveAsTextFile("D:/out_tf")*/
  }

}

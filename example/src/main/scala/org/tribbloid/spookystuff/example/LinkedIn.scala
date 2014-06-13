package org.tribbloid.spookystuff.example

import org.apache.spark.{SparkContext, SparkConf}
import org.tribbloid.spookystuff.entity._
import org.tribbloid.spookystuff.SpookyContext._

/**
 * This job will find and printout urls of all Sanjay Gupta in your local area
 */
object LinkedIn {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("LinkedIn")
    conf.setMaster("local[*]")
    conf.setSparkHome(System.getenv("SPARK_HOME"))
    conf.setJars(SparkContext.jarOfClass(this.getClass).toList)
    val sc = new SparkContext(conf)

    val actions = Seq[Interaction](
      Visit("https://www.linkedin.com/"),
      TextInput("input#first","Sanjay"),
      TextInput("input#last","Gupta"),
      Submit("input[name=\"search\"]")
    )
    val actionsRDD = sc.parallelize(Seq(actions))
    val pageRDD = actionsRDD.map {
      actions => {
        PageBuilder.resolveFinal(actions: _*)
      }
    }

    val linkRDD = pageRDD.flatMap(_.allLinks("ol#result-set h2 a"))
    val results = linkRDD.collect()
    results.foreach {
      println(_)
    }

    sc.stop()

  }
}

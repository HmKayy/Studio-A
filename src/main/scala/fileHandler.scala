import scala.collection.immutable.Seq
import java.io.*
import java.util.Scanner
import scala.io.Source
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.BufferedReader
import java.io.IOException
import java.io.FileWriter
import java.io.BufferedWriter
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.parser.decode

import scala.collection.immutable.Seq
class data() {
  var source = "/data/vgsales.csv"
  var header: Array[String] = Array[String]()
  var output: Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]] = Seq()
  var colCount = 0

  private def handleCSV(buff: BufferedReader): Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]] = {
    var result = Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]]()
    var row = 0
    var oneLine: String = buff.readLine()
    println(oneLine)
    if (oneLine != null) {
      header = oneLine.split(',')
      colCount = header.length
      println(colCount)
    }
    while ( {
      oneLine = buff.readLine(); oneLine != null
    }) {
      val currentLine = oneLine.trim.split(',')
      if (currentLine.length != colCount) new Error("Invalid file")
      else {
        try {
          result = result.appended(currentLine.map(a => a.toFloatOption match {
            case None => 999999
            case Some(a: Float) => a
          }))
          row += 1
        } catch {
          case _: NumberFormatException => new Error("Invalid data")
        }
      }
    }
    result
  }

  /** JSON file handler. */
  private def handleJSON: Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]] = {
    var result = Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]]
    val fileSource = util.Try(scala.io.Source.fromFile(source))

    def readText(source: scala.io.Source) = source.getLines().mkString("\n")

    def readJson(text: String) = decode[Seq[Seq[Float]]](text).toTry

    for {
      source1 <- fileSource // Get succesful source result
      text = readText(source1) // Get text from source
      list <- readJson(text) // Parse JSON into list
      entry <- list
    } result = result.
    result
  }

  /** File handler. */
  def importing() = source.split('.').last.toLowerCase() match {
    case "csv" => try {
      println("importing")
      val fileIn = new FileReader(source)
      val linesIn = new BufferedReader(fileIn)
      try {
        output = handleCSV(linesIn)
      } finally {
        println(output.size, output(1).size)
        fileIn.close()
        linesIn.close()
      }
    }
    case "js" => try {
      println("importing")
      try {
        output = handleJSON
      } finally {
        println(output.size, output(1).size)
      }

    }
    case _ => new Error()
  }

  /** Manual input handler. Assume input follows required structure */
  def importFromManualInput(dataManual: Seq[String], columns: Int): Unit = {
    numberOfColumn = dataManual.size
    for (i <- dataManual) {
      val headerAndData = i.split(":")
      header = header.appended(headerAndData.head)
      if (headerAndData.last.split(",").length != columns) new Error("Invalid Data")
      else {
        output = output.appended(headerAndData.last.split(",").map(_.toFloat))
      }
    }
    output = output.transpose
  }

  /** Helper function to get data by column by index */
  def getColumn(col: Int): Seq[Float] = output.map(a => a(col))
}


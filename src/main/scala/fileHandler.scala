import scala.collection.Seq
import java.io._
import java.util.Scanner
import scala.io.Source
import java.io.FileReader
import java.io.FileNotFoundException
import java.io.BufferedReader
import java.io.IOException
import java.io.FileWriter
import java.io.BufferedWriter

class data() {
  var source = "/data/vgsales.csv"
  var header: Array[String] = Array[String]()
  var output: Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]] = Seq()
  var colCount = 0

  def readCSV(buff: BufferedReader): Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]] =
    var result = Seq[Tuple11[Int, String, String, Int, String, String, Double, Double, Double, Double, Double]]()
    var row = 0
    var oneLine: String = buff.readLine()
    if (oneLine != null) then
      header = oneLine.split(',')
      colCount = header.length
    while {oneLine = buff.readLine();oneLine != null} do
      val currentLine = oneLine.trim.split(',')
      if (currentLine.length != colCount) new Error("Invalid file")
    result



  end readCSV

  def importCSV() =
    println("importing")
    val fileIn = new FileReader(source)
    val linesIn = new BufferedReader(fileIn)
    try output = readCSV(linesIn)
    finally
      fileIn.close()
      linesIn.close()
  end importCSV
}

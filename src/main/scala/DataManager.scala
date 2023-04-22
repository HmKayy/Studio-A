import scala.io.Source
import java.io.PrintWriter
import java.io.File
import java.io.{BufferedWriter,BufferedReader,FileReader, File, FileWriter}

object DataManager {

  // Function to load Data
  def loadData(filePath: String): (List[VideoGame], Boolean) = {
    if (filePath.isEmpty | filePath.isBlank) {
      // return dummy data if filePath is empty
      val data = List(
          VideoGame("Game 1", 2000, "Action", 11.0, 25.0, 33.0),
          VideoGame("Game 2", 2000, "Sports", 16.0, 22.0, 34.0),
          VideoGame("Game 3", 2000, "Platform", 12.0, 24.0, 35.0),
          VideoGame("Game 4", 2000, "Racing", 17.0, 28.0, 34.0),
          VideoGame("Game 5", 2000, "Role-Playing", 15.0, 26.0, 3.20),
          VideoGame("Game 6", 2000, "Puzzle", 1.30, 26.0, 37.0),
          VideoGame("Game 7", 2000, "Misc", 12.0, 2.0, 3.0),
          VideoGame("Game 8", 2000, "Shooter", 13.0, 26.0, 37.0),
          VideoGame("Game 9", 2000, "Simulation", 112.0, 25.0, 31.0),
          VideoGame("Game 10", 2000, "Adventure", 13.0, 24.0, 39.0),
          VideoGame("Game 11", 2000, "Fighting", 12.0, 22.0, 38.0),
          VideoGame("Game 12", 2000, "Strategy", 15.0, 26.0, 73.0)
      )
      (data, true)
    }
    else {
      val bufferedSource = Source.fromFile(filePath)
      val lines = bufferedSource.getLines.toList
      val header = lines.head.split(",").map(_.trim)
      val correctFormat = header.sameElements(Array("Name", "Year", "Genre", "NASales", "EUSales", "GlobalSales"))
      if (!correctFormat) {
        bufferedSource.close()
        return (List.empty[VideoGame], false)
      }
      val data = (for (line <- lines.tail) yield {
        val cols = line.split(",").map(_.trim)
        if (!cols.contains("N/A") && cols(1).matches("\\d+")) {
          VideoGame(cols(0).trim, cols(1).toInt, cols(2), cols(3).toDouble, cols(4).toDouble, cols(5).toDouble)
        } else {
          null // return null for records with "N/A" values or non-numeric year
        }
      }).filter(_ != null).toList

      bufferedSource.close()
      (data, true)
    }
  }

  // Function to save Data
  def saveData(data: List[VideoGame], filePath: String): Boolean = {
    val file = new File(filePath)
    val writer = new BufferedWriter(new FileWriter(file))
    val header = "Name,Year,Genre,NASales,EUSales,GlobalSales"
    writer.write(header + "\n")
    data.foreach(game => {
      val line = s"${game.name},${game.year},${game.genre},${game.naSales},${game.euSales},${game.globalSales}"
      writer.write(line + "\n")
    })
    writer.close()
    true
  }
  // Function to load config from config file
  def loadConfig(filePath: String): ((String, String, String, String), Boolean) = {
    if (filePath.isEmpty | filePath.isBlank)
      (("NA", "EU", "Global", "Global"),false)
    else {
      val file = new File(filePath)
      val reader = new BufferedReader(new FileReader(file))
      val lines = reader.lines.toArray.map(_.toString)
      var checker = true
      if (lines.length != 4) {
        // Invalid format, return default values
        (("NA", "EU", "Global", "Global"),false)
      } else {
        val region1 = if (lines(0).startsWith("Region 1: ")) {
          val value = lines(0).substring(10)
          if (value != "NA" && value != "EU" && value != "Global") checker = false
          value
        } else "NA"
        val region2 = if (lines(1).startsWith("Region 2: ")) {
          val value = lines(1).substring(10)
          if (value != "NA" && value != "EU" && value != "Global") checker = false
          value
        } else "EU"
        val region = if (lines(2).startsWith("Region : ")) {
          val value = lines(2).substring(10)
          if (value != "NA" && value != "EU" && value != "Global") checker = false
          value
        } else "Global"
        val regionStat = if (lines(3).startsWith("Region Stat: ")) {
          val value = lines(3).substring(13)
          if (value != "NA" && value != "EU" && value != "Global") checker = false
          value
        } else "Global"
        ((region1, region2, region, regionStat), checker)
      }
    }
  }
  // function to save the config of the current Dashboard
  def saveConfig(config: (String,String,String,String), filePath: String): Boolean = {
    val file = new File(filePath)
    val writer = new BufferedWriter(new FileWriter(file))

    try {
      writer.write(s"Region 1: ${config(0)}\n")
      writer.write(s"Region 2: ${config(1)}\n")
      writer.write(s"Region 3: ${config(2)}\n")
      writer.write(s"Region Stat: ${config(3)}\n")
      true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        false
    } finally {
      writer.close()
    }
  }
}

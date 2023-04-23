import scalafx.scene.layout._
import scalafx.scene.control.Label
import scalafx.scene.paint.Color

object DataProcessor {
  // Define functions to calculate statistics from the data
  def genres(data: List[VideoGame]): List[String] = {
    data.map(_.genre).distinct
  }

  def salesByGenre(data: List[VideoGame], region: String): Map[String, Double] = {
    data.groupBy(_.genre).map { case (genre, genreGames) =>
      genre -> (region match {
        case "NA" => genreGames.map(_.naSales).sum
        case "EU" => genreGames.map(_.euSales).sum
        case "Global" => genreGames.map(_.globalSales).sum
      })
    }
  }

  def minSales(data: List[VideoGame], region: String): Double = {
    data.map(game => region match {
      case "NA" => game.naSales
      case "EU" => game.euSales
      case "Global" => game.globalSales
    }).min
  }

  def maxSales(data: List[VideoGame], region: String): Double = {
    data.map(game => region match {
      case "NA" => game.naSales
      case "EU" => game.euSales
      case "Global" => game.globalSales
    }).max
  }

  def avgSales(data: List[VideoGame], region: String): Double = {
    val totalSales = data.map(game => region match {
      case "NA" => game.naSales
      case "EU" => game.euSales
      case "Global" => game.globalSales
    }).sum
    totalSales / data.length
  }

  def totalSales(data: List[VideoGame], region: String): Double = {
    data.map(game => region match {
      case "NA" => game.naSales
      case "EU" => game.euSales
      case "Global" => game.globalSales
    }).sum
  }

  def stdDevSales(data: List[VideoGame], region: String): Double = {
    val mean = avgSales(data, region)
    val squaredDiffs = data.map(game => region match {
      case "NA" => (game.naSales - mean) * (game.naSales - mean)
      case "EU" => (game.euSales - mean) * (game.euSales - mean)
      case "Global" => (game.globalSales - mean) * (game.globalSales - mean)
    })
    Math.sqrt(squaredDiffs.sum / data.length)
  }

  def bestSellingGenres(data: List[VideoGame], region: String): String = {
    val salesByGenreMap = salesByGenre(data, region)
    val topSellingGenres = salesByGenreMap.toList.sortBy(-_._2).take(5).map(_._1)
    topSellingGenres.mkString(", ")
  }
  // Create a custom card that shows the top 5 genres in a region
  def createTopGenresCard(title: String, genres: String): VBox = {
    val titleLabel = new Label(title) {
      style = "-fx-font-weight: bold; -fx-font-size: 16pt"
    }
    val genreLabels = genres.split(",").map(_.trim)
    val rankedGenreLabels = genreLabels.zipWithIndex.map { case (genre, index) =>
      new Label(s"${index + 1}. $genre") {
        style = "-fx-font-size: 14pt;"
      }
    }
    val genresBox = new VBox(rankedGenreLabels: _*)
    new VBox(titleLabel, genresBox) {
      style = "-fx-padding: 10px; -fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 10px;"
    }
  }

  // Create a card with the given title and value
  def createCard(title: String, value: Double, color: Color): VBox = {
    val titleLabel = new Label(title) {
      style = "-fx-font-weight: bold; -fx-font-size: 16pt"
    }
    val valueLabel = value match {
      case d: Double => new Label(f"$d%.2f") {
        style = "-fx-font-weight: bold; -fx-font-size: 24pt"
        textFill = color
      }
      case _ => throw new IllegalArgumentException("Value must be a Double")
    }
    new VBox(titleLabel, valueLabel) {
      style = "-fx-padding: 10px; -fx-background-color: white; -fx-border-color: gray; -fx-border-radius: 10px;"
    }
  }
}

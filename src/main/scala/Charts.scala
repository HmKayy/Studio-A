import scalafx.collections.ObservableBuffer
import javafx.collections.ObservableList
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.collections.ObservableBuffer
import scalafx.scene.control._
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, HBox, VBox}
import scalafx.scene.paint.Color._
import scalafx.scene.text.{Font, FontWeight}
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.Screen
import scalafx.scene.chart._
import scalafx.util.Duration
import scalafx.scene.control.Tooltip
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.scene.Node

class barPlot(data: List[VideoGame], region1: String, region2: String) extends VBox {
  val sum1 = DataProcessor.salesByGenre(data,region1)
  val sum2 = DataProcessor.salesByGenre(data,region2)
  val genres = ObservableBuffer() ++ DataProcessor.genres(data)

  val xAxis = new CategoryAxis()
  xAxis.setCategories(genres)

  val yAxis = new NumberAxis()
  yAxis.setLabel("Total Sales")

  val series1 = new XYChart.Series[String, Number]()
  series1.setName(region1 + " Sales")
  for i <- genres.indices do
  {
    series1.getData.add(XYChart.Data(genres(i), sum1(genres(i))))
  }


  val series2 = new XYChart.Series[String, Number]()
  series2.setName(region2 +" Sales")
  for i <- genres.indices do {
    series2.getData.add(XYChart.Data(genres(i), sum2(genres(i))))
  }

  val barChart = new BarChart[String, Number](xAxis, yAxis)
  this.children = barChart
  xAxis.setTickLabelRotation(-45)
  barChart.getData.addAll(series2, series1)
  barChart.setTitle(region1+ " vs "+ region2 + " bar plot")
  barChart.getData.foreach(
    d => {
      d.getData.foreach(
        i => {
          val barNode:Node = i.getNode
          var barValue:Double = i.getYValue.toString.toDouble
          val msg = "%s: %.2f".format(d.getName, barValue)
          // Tooltip installing
          val tt = new Tooltip {
            text = msg
            showDelay = Duration(0)
          }
          Tooltip.install(barNode, tt)
        }
      )
    })

  def updateData(region1: String, region2: String): Unit = {
    series1.setName(region1 + " Sales")
    for (i <- genres.indices) {
      series1.getData.get(i).setYValue(DataProcessor.salesByGenre(data, region1)(genres(i)))
    }

    series2.setName(region2 + " Sales")
    for (i <- genres.indices) {
      series2.getData.get(i).setYValue(DataProcessor.salesByGenre(data, region2)(genres(i)))
    }
  }


  barChart.setPrefSize(800, 400)
  barChart.setMinSize(800, 400)
  barChart.setMaxSize(800, 400)
}
class scatterPlot(data: List[VideoGame],region1:String,region2:String) extends VBox {
  val sum1 = DataProcessor.salesByGenre(data, region1)
  val sum2 = DataProcessor.salesByGenre(data, region2)
  val genres = ObservableBuffer() ++ DataProcessor.genres(data)

  val xAxis = new CategoryAxis()
  xAxis.setCategories(genres)

  val yAxis = new NumberAxis()
  yAxis.setLabel("Total Sales")

  val series1 = new XYChart.Series[String, Number]()
  series1.setName(region1+" Sales")
  for i <- genres.indices do {
    series1.getData.add(XYChart.Data(genres(i), sum1(genres(i))))
  }

  val series2 = new XYChart.Series[String, Number]()
  series2.setName(region2+" Sales")
  for i <- genres.indices do {
    series2.getData.add(XYChart.Data(genres(i), sum2(genres(i))))
  }

  val scatterChart = new ScatterChart[String, Number](xAxis, yAxis)
  this.children = scatterChart
  scatterChart.getData.addAll(series2, series1)
  scatterChart.setTitle(region1+ " vs "+ region2 + " scatter plot")
  scatterChart.getData.foreach(
    d => {
      d.getData.foreach(
        i => {
          val barNode:Node = i.getNode
          var barValue:Double = i.getYValue.toString.toDouble
          val msg = "%s: %.2f".format(d.getName, barValue)
          // Tooltip installing
          val tt = new Tooltip {
            text = msg
            showDelay = Duration(0)
          }
          Tooltip.install(barNode, tt)
        }
      )
    })

  def updateData(region1: String, region2: String): Unit = {
    series1.setName(region1 + " Sales")
    for (i <- genres.indices) {
      series1.getData.get(i).setYValue(DataProcessor.salesByGenre(data, region1)(genres(i)))
    }

    series2.setName(region2 + " Sales")
    for (i <- genres.indices) {
      series2.getData.get(i).setYValue(DataProcessor.salesByGenre(data, region2)(genres(i)))
    }
  }


  scatterChart.setPrefSize(800, 400)
  scatterChart.setMinSize(800, 400)
  scatterChart.setMaxSize(800, 400)
}
class pieChart(data: List[VideoGame],region:String) extends VBox {
  val sumregion = DataProcessor.salesByGenre(data, region)
  val genres = ObservableBuffer() ++ DataProcessor.genres(data)
  var note = new Label("")
  val pieChartData = ObservableBuffer(
    PieChart.Data(genres(0), sumregion(genres(0))),
    PieChart.Data(genres(1), sumregion(genres(1))),
    PieChart.Data(genres(2), sumregion(genres(2))),
    PieChart.Data(genres(3), sumregion(genres(3))),
    PieChart.Data(genres(4), sumregion(genres(4))),
    PieChart.Data(genres(5), sumregion(genres(5))),
    PieChart.Data(genres(6), sumregion(genres(6))),
    PieChart.Data(genres(7), sumregion(genres(7))),
    PieChart.Data(genres(8), sumregion(genres(8))),
    PieChart.Data(genres(9), sumregion(genres(9))),
    PieChart.Data(genres(10), sumregion(genres(10))),
    PieChart.Data(genres(11), sumregion(genres(11)))
  )
  var total: Double = _
  val pie: PieChart = new PieChart()
  this.children = pie

  pie.setTitle(region+" pie chart")
  pie.setData(pieChartData)
  pie.setLabelsVisible(false)
  pie.getData.foreach(total += _.getPieValue)
  pie.setPrefSize(800, 400)
  pie.getData.foreach(
    d => {
      val sliceNode: Node = d.getNode
      var pieValue = d.getPieValue
      val percent = (pieValue / total) * 100.0
      val msg = "%s: %.2f (%.2f%%)".format(d.getName, pieValue, percent)
      // Show tooltip when hovered
      val tt = new Tooltip {
        text = msg
        showDelay = Duration(0)
      }
      Tooltip.install(sliceNode, tt)
    })

  val colors = List(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Orange, Color.Purple,
    Color.Cyan, Color.Magenta, Color.Gray, Color.DarkGray, Color.Pink, Color.Brown)

  for ((data, color) <- pieChartData.zip(colors)) {
    data.getNode.setStyle("-fx-pie-color: " + toRgbCode(color) + ";")

    def toRgbCode(color: Color): String = {
      val r = (color.getRed() * 255).toInt
      val g = (color.getGreen() * 255).toInt
      val b = (color.getBlue() * 255).toInt
      String.format("#%02x%02x%02x", r.asInstanceOf[AnyRef], g.asInstanceOf[AnyRef], b.asInstanceOf[AnyRef])
    }
  }
}
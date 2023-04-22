import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.geometry.Orientation
import scalafx.scene.Scene
import scalafx.scene.chart.{CategoryAxis, NumberAxis, PieChart, ScatterChart, XYChart}
import scalafx.scene.control.{Alert, Button, ButtonType, CheckBox, Label, ScrollPane, SplitPane, TextArea, TextField}
import scalafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, HBox, VBox}
import scalafx.scene.control.Alert.AlertType
import scalafx.geometry.Insets
import scalafx.scene.paint.Color
import scalafx.geometry.Pos
import scalafx.stage.FileChooser
import scalafx.scene.input.KeyEvent
import scalafx.beans.property.BooleanProperty
import scalafx.scene.layout.Region
import scalafx.util.Duration
import scalafx.beans.binding.Bindings
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Region
import scalafx.Includes._
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.scene.text.Text

object Main extends JFXApp3 {

  override def start(): Unit = {
    // Annoucement when the program is opened
    // Create a new alert dialog
    val announcement = new Alert(AlertType.Information)
    announcement.setTitle("Welcome!")
    announcement.setHeaderText("Welcome to My Dashboard")
    announcement.setContentText("Everything has been set to default and a mock database is loaded")
    announcement.showAndWait()
    // filepath and data
    var dataStats = DataManager.loadData("")._1
    var currentConfig = DataManager.loadConfig("")._1
    // create a text box with instructions
    val instructionsBox = new Text("" +
      "Here is how you use this dashboard" +
      "\nAt the top left, you can see a check box that let you select which plot to be shown" +
      "\nUnder it, you can see some basic calculation of a region that you select")
    // Regions for charts and cards
    var region1: String = currentConfig._1
    var region2: String = currentConfig._2
    var region: String = currentConfig._3
    var regionStat: String = currentConfig._4
    // Default charts
    var columnPlot = new barPlot(dataStats, region1, region2)
    var scatterPlot = new scatterPlot(dataStats, region1, region2)
    var piePlot = new pieChart(dataStats, region)
    // Default cards
    val cardsLabel = new Label(regionStat+" statistics")
    val totalSalesCard = DataProcessor.createCard("Total Sales", DataProcessor.totalSales(dataStats, regionStat), Color.Blue)
    val avgSalesCard = DataProcessor.createCard("Average Sales", DataProcessor.avgSales(dataStats, regionStat), Color.Purple)
    val maxSalesCard = DataProcessor.createCard("Max Sales", DataProcessor.maxSales(dataStats, regionStat), Color.Green)
    val minSalesCard = DataProcessor.createCard("Min Sales", DataProcessor.minSales(dataStats, regionStat), Color.Red)
    val stdSalesCard = DataProcessor.createCard("Standard deviation of Sales", DataProcessor.stdDevSales(dataStats, regionStat), Color.Gray)
    val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat), Color.Black)
    // Create buttons
    val helpButton = new Button("Help")
    val loadButton = new Button("Load Data")
    val saveButton = new Button("Save Data")
    val loadConfigButton = new Button("Load Config")
    val saveConfigButton = new Button("Save Config")
    val updateButton = new Button("Update")
    // Create Labels
    val chartCheckBox = new Label("Choose which charts to show")
    val region1Label = new Label("The first region of the scatter/bar plot:")
    val region2Label = new Label("The second region of the scatter/bar plot:")
    val regionLabel = new Label("Region for the Pie chart:")
    val regionStatLabel = new Label("The region you want to know the basic statistics:")
    val updateButtonLabel = new Label("Click update to update the Dashboard | Help if you need help with what to do")
    val saveConfigTextFieldLabel = new Label("Name of the config file (only 0-9,a-z characters are allowed)")
    val saveTextFieldLabel = new Label("Name of the data file (only 0-9,a-z characters are allowed)")
    // Text fields
    val fileNameField = new TextField() {
      onKeyTyped = { (event: KeyEvent) =>
        val c = event.character
        if (c.matches("[a-zA-Z0-9]")) {
          event.consume()
        }
      }
      tooltip = "Please enter only letters and numbers"
    }
    val configNameField = new TextField() {
      onKeyTyped = { (event: KeyEvent) =>
        val c = event.character
        if (c.matches("[a-zA-Z0-9]")) {
          event.consume()
        }
      }
      tooltip = "Please enter only letters and numbers"
    }
    val region1TextField = new TextField {
      text = region1
      promptText = "NA, EU, or Global"
      prefWidth = 50
      tooltip = "Enter NA, EU, or Global"
    }
    val region2TextField = new TextField {
      text = region2
      promptText = "NA, EU, or Global"
      prefWidth = 50
      tooltip = "Enter NA, EU, or Global"
    }
    val regionTextField = new TextField {
      text = region
      promptText = "NA, EU, or Global"
      prefWidth = 50
      tooltip = "Enter NA, EU, or Global"
    }
    val regionStatTextField = new TextField {
      text = regionStat
      promptText = "NA, EU, or Global"
      prefWidth = 50
      tooltip = "Enter NA, EU, or Global"
    }
    // Scroll Panes for charts
    val columnScrollPane = new ScrollPane() {
      content = columnPlot
      fitToWidth = true
      fitToHeight = true
    }
    val scatterScrollPane = new ScrollPane() {
      content = scatterPlot
      fitToWidth = true
      fitToHeight = true
    }
    val pieScrollPane = new ScrollPane() {
      content = piePlot
      fitToWidth = true
      fitToHeight = true
    }
    // Check box for chart
    val columnCheckBox = new CheckBox("Bar Plot") {
      selected = true
    }
    val scatterCheckBox = new CheckBox("Scatter Plot") {
      selected = true
    }
    val pieCheckBox = new CheckBox("Pie Chart") {
      selected = true
    }
    columnCheckBox.selected.onChange { (_, _, selected) =>
      columnScrollPane.managed = selected
      columnScrollPane.visible = selected
    }
    scatterCheckBox.selected.onChange { (_, _, selected) =>
      scatterScrollPane.managed = selected
      scatterScrollPane.visible = selected
    }
    pieCheckBox.selected.onChange { (_, _, selected) =>
      pieScrollPane.managed = selected
      pieScrollPane.visible = selected
    }
    // Containers
    val cardsContainer = new VBox(cardsLabel, totalSalesCard, avgSalesCard, maxSalesCard, minSalesCard, stdSalesCard, bestGenreCard) {
      style = "-fx-spacing: 10px; -fx-padding: 10px;"
    }
    val regionsContainer = new VBox(region1Label,
      region1TextField,
      region2Label,
      region2TextField,
      regionLabel,
      regionTextField,
      regionStatLabel,
      regionStatTextField)
    val saveloadcontainer = new HBox(30,saveButton, loadButton)
    val saveloadConfigcontainer = new HBox(30,saveConfigButton, loadConfigButton)
    val updateContainer = new VBox(5,updateButtonLabel, new HBox(150,updateButton,helpButton))
    val chartCheckBoxes = new VBox(5, chartCheckBox, new HBox(50,columnCheckBox, scatterCheckBox, pieCheckBox))
    val chartScrollPanes = new VBox(columnScrollPane, scatterScrollPane, pieScrollPane)
    // Scroll Pane for Cards
    val cardsScrollPane = new ScrollPane() {
      content = cardsContainer
      fitToWidth = true
      fitToHeight = true
    }
    // Buttons' actions
    helpButton.setOnAction(_ => {
      // create a dialog box
      val dialog = new Dialog[Unit] {
        title = "Instructions"
        headerText = "How to use the Data Dashboard"
        resizable = false
        dialogPane().buttonTypes = Seq(ButtonType.OK)
        dialogPane().content = new VBox {
          children = Seq(
            instructionsBox
          )
        }
      }
      // show the dialog box
      val result = dialog.showAndWait()
    })
    // Load Data
    loadButton.setOnAction(_ => {
      val fileChooser = new FileChooser()
      fileChooser.setTitle("Open CSV File")
      fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("CSV Files", "*.csv")
      )
      val selectedFile = fileChooser.showOpenDialog(null)
      if (selectedFile != null) {
        val filePath = selectedFile.getAbsolutePath()
        if (filePath.endsWith(".csv")) {
          val (data, correctFormat) = DataManager.loadData(filePath)
          if (correctFormat) {
            dataStats = data
            // update plots
            columnPlot = new barPlot(dataStats, region1, region2)
            scatterPlot = new scatterPlot(dataStats, region1, region2)
            piePlot = new pieChart(dataStats, region)
            pieScrollPane.content = piePlot
            // update Cards
            cardsContainer.children.clear()
            val cardsLabel = new Label(s"Some statistics of $regionStat")
            val totalSalesCard = DataProcessor.createCard("Total Sales", DataProcessor.totalSales(dataStats, regionStat), Color.Blue)
            val avgSalesCard = DataProcessor.createCard("Average Sales", DataProcessor.avgSales(dataStats, regionStat), Color.Purple)
            val maxSalesCard = DataProcessor.createCard("Max Sales", DataProcessor.maxSales(dataStats, regionStat), Color.Green)
            val minSalesCard = DataProcessor.createCard("Min Sales", DataProcessor.minSales(dataStats, regionStat), Color.Red)
            val stdSalesCard = DataProcessor.createCard("Standard deviation of Sales", DataProcessor.stdDevSales(dataStats, regionStat), Color.Gray)
            val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat), Color.Black)
            cardsContainer.children.addAll(cardsLabel, totalSalesCard, avgSalesCard, maxSalesCard, minSalesCard, stdSalesCard, bestGenreCard)
          } else {
            val alert = new Alert(AlertType.Error)
            alert.setTitle("File Format Error")
            alert.setHeaderText("The CSV file does not have the correct format.")
            alert.setContentText("Please select a CSV file with the following headers: Name, Year, Genre, NASales, EUSales, GlobalSales.")
            alert.showAndWait()
          }
        } else {
          val alert = new Alert(AlertType.Error)
          alert.setTitle("File Format Error")
          alert.setHeaderText("Selected file is not a CSV file.")
          alert.setContentText("Please select a file with a .csv extension.")
          alert.showAndWait()
        }
      }
    })
    // Save Data
    saveButton.onAction = () => {
      val filename = fileNameField.text.value.trim
      if (!filename.matches("[a-zA-Z0-9]+") || filename.isEmpty) {
        val alert = new Alert(AlertType.Warning) {
          title = "Invalid filename"
          headerText = "Filename contains invalid characters"
          contentText = "Please enter a filename that only contains letters and numbers."
        }
        alert.showAndWait()
      } else {
        val fileChooser = new FileChooser()
        fileChooser.title = "Save Data"
        fileChooser.initialFileName = filename + ".csv"
        val file = fileChooser.showSaveDialog(null)
        if (file != null) {
          val success = DataManager.saveData(dataStats, file.getPath)
          if (success) {
            val alert = new Alert(AlertType.Confirmation) {
              title = "Success"
              headerText = "Data has been saved "
              contentText = "The data is saved to "+file.getPath
            }
            alert.showAndWait()
          } else {
            val alert = new Alert(AlertType.Warning) {
              title = "Invalid filename"
              headerText = "Filename contains invalid characters"
              contentText = "Please enter a filename that only contains letters and numbers."
            }
            alert.showAndWait()
          }
        }
      }
    }
    // Update
    updateButton.onAction = () => {
      // update text field values
      region1 = region1TextField.text.value
      region2 = region2TextField.text.value
      region = regionTextField.text.value
      regionStat = regionStatTextField.text.value
      // validate inputs
      if (!List("NA", "EU", "Global").contains(region1) ||
        !List("NA", "EU", "Global").contains(region2) ||
        !List("NA", "EU", "Global").contains(regionStat) ||
        !List("NA", "EU", "Global").contains(region)) {
        new Alert(Alert.AlertType.Warning, "Invalid input. Region can only be NA, EU, or Global.", ButtonType.OK).showAndWait()
      } else {
        // update plots
        columnPlot.updateData(region1, region2)
        scatterPlot.updateData(region1, region2)
        piePlot = new pieChart(dataStats, region)
        pieScrollPane.content = piePlot
        // update Cards
        cardsContainer.children.clear()
        val cardsLabel = new Label(s"Some statistics of $regionStat")
        val totalSalesCard = DataProcessor.createCard("Total Sales", DataProcessor.totalSales(dataStats, regionStat), Color.Blue)
        val avgSalesCard = DataProcessor.createCard("Average Sales", DataProcessor.avgSales(dataStats, regionStat), Color.Purple)
        val maxSalesCard = DataProcessor.createCard("Max Sales", DataProcessor.maxSales(dataStats, regionStat), Color.Green)
        val minSalesCard = DataProcessor.createCard("Min Sales", DataProcessor.minSales(dataStats, regionStat), Color.Red)
        val stdSalesCard = DataProcessor.createCard("Standard deviation of Sales", DataProcessor.stdDevSales(dataStats, regionStat), Color.Gray)
        val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat), Color.Black)
        cardsContainer.children.addAll(cardsLabel, totalSalesCard, avgSalesCard, maxSalesCard, minSalesCard, stdSalesCard, bestGenreCard)
      }
    }
    // Current state
    // Load Config
    loadConfigButton.onAction = () => {
      val fileChooser = new FileChooser()
      fileChooser.title = "Load Data"
      val file = fileChooser.showOpenDialog(null)
      if (file != null && file.getName.endsWith(".idk")) {
        val (newConfig, checker) = DataManager.loadConfig(file.getPath)
        if (checker) {
          currentConfig = newConfig
          region1 = newConfig._1
          region2 = newConfig._2
          region = newConfig._3
          regionStat = newConfig._4
          region1TextField.text = newConfig._1
          region2TextField.text = newConfig._2
          regionTextField.text = newConfig._3
          regionStatTextField.text = newConfig._4
          // update plots
          columnPlot.updateData(region1, region2)
          scatterPlot.updateData(region1, region2)
          piePlot = new pieChart(dataStats, region)
          pieScrollPane.content = piePlot
          // update Cards
          cardsContainer.children.clear()
          val cardsLabel = new Label(s"Some statistics of $regionStat")
          val totalSalesCard = DataProcessor.createCard("Total Sales", DataProcessor.totalSales(dataStats, regionStat), Color.Blue)
          val avgSalesCard = DataProcessor.createCard("Average Sales", DataProcessor.avgSales(dataStats, regionStat), Color.Purple)
          val maxSalesCard = DataProcessor.createCard("Max Sales", DataProcessor.maxSales(dataStats, regionStat), Color.Green)
          val minSalesCard = DataProcessor.createCard("Min Sales", DataProcessor.minSales(dataStats, regionStat), Color.Red)
          val stdSalesCard = DataProcessor.createCard("Standard deviation of Sales", DataProcessor.stdDevSales(dataStats, regionStat), Color.Gray)
          val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat), Color.Black)
          cardsContainer.children.addAll(cardsLabel, totalSalesCard, avgSalesCard, maxSalesCard, minSalesCard, stdSalesCard, bestGenreCard)
          val alert = new Alert(AlertType.Confirmation) {
            title = "Success"
            headerText = "Config is loaded"
            contentText = "Config is successfully read and loaded to the Dashboard"
          }
          alert.showAndWait()
        } else {
          currentConfig = newConfig
          val alert = new Alert(AlertType.Warning) {
            title = "Invalid file format"
            headerText = "The file format is invalid"
            contentText = "The config file is corrupted or has an invalid format. Loading default configuration."
          }
          alert.showAndWait()
        }
      } else {
        val alert = new Alert(AlertType.Warning) {
          title = "Invalid file type"
          headerText = "The file type is invalid"
          contentText = "Please select a valid .idk config file."
        }
        alert.showAndWait()
      }
    }
    // Save Config
    saveConfigButton.onAction = () => {
      val filename = configNameField.text.value.trim
      if (!filename.matches("[a-zA-Z0-9]+") || filename.isEmpty) {
        val alert = new Alert(AlertType.Warning) {
          title = "Invalid filename"
          headerText = "Filename contains invalid characters"
          contentText = "Please enter a filename that only contains letters and numbers."
        }
        alert.showAndWait()
      } else {
        val fileChooser = new FileChooser()
        fileChooser.title = "Save Data"
        fileChooser.initialFileName = filename + ".idk"
        val file = fileChooser.showSaveDialog(null)
        if (file != null) {
          val success = DataManager.saveConfig(currentConfig, file.getPath)
          if (success) {
            val alert = new Alert(AlertType.Confirmation) {
              title = "Success"
              headerText = "Config has been saved"
              contentText = "The config is saved to" + file.getPath
            }
            alert.showAndWait()
          } else {
            val alert = new Alert(AlertType.Warning) {
              title = "Failed"
              headerText = "Saving config has failed"
              contentText = "Please go over everything again to make sure they are correct!"
            }
            alert.showAndWait()
          }
        }
      }
    }
    // Area that contains function of duplicate
    // Initialize Boolean variables to track whether each chart type has been duplicated
    var columnChartDuplicated = false
    var scatterChartDuplicated = false
    var pieChartDuplicated = false
    // Create a new check box to let the user pick which chart to duplicate
    val columnDupeCheckBox = new CheckBox("Bar Plot")
    val scatterDupeCheckBox = new CheckBox("Scatter Plot")
    val pieDupeCheckBox = new CheckBox("Pie Chart")
    // Create a button to duplicate the charts
    val dupesScrollPanes = new VBox()
    val duplicateButton = new Button("Duplicate Charts")
    duplicateButton.setOnAction(_ => {
      if (columnDupeCheckBox.isSelected && !columnChartDuplicated) {
        // Duplicate column chart
        val duplicatedColumnPlot = new barPlot(dataStats, region1, region2)
        val duplicatedColumnScrollPane = new ScrollPane() {
          content = duplicatedColumnPlot
          fitToWidth = true
          fitToHeight = true
        }
        dupesScrollPanes.getChildren.add(duplicatedColumnScrollPane)
        columnChartDuplicated = true
      }

      if (scatterDupeCheckBox.isSelected && !scatterChartDuplicated) {
        // Duplicate scatter plot
        val duplicatedScatterPlot = new scatterPlot(dataStats, region1, region2)
        val duplicatedScatterScrollPane = new ScrollPane() {
          content = duplicatedScatterPlot
          fitToWidth = true
          fitToHeight = true
        }
        dupesScrollPanes.getChildren.add(duplicatedScatterScrollPane)
        scatterChartDuplicated = true
      }

      if (pieDupeCheckBox.isSelected && !pieChartDuplicated) {
        // Duplicate pie chart
        val duplicatedPiePlot = new pieChart(dataStats, region)
        val duplicatedPieScrollPane = new ScrollPane() {
          content = duplicatedPiePlot
          fitToWidth = true
          fitToHeight = true
        }
        dupesScrollPanes.getChildren.add(duplicatedPieScrollPane)
        pieChartDuplicated = true
      }
    })

    // Create a button to remove duplicates
    val deleteButton = new Button("Remove duplicates")
    deleteButton.setOnAction(_ => {
      dupesScrollPanes.getChildren.clear()
      columnChartDuplicated = false
      scatterChartDuplicated = false
      pieChartDuplicated = false
    })
    // container for the duplicate check box and buttons
    val controlsVBox = new VBox(5,new Label("Choose which chart to duplicate"),new HBox(50,columnDupeCheckBox, scatterDupeCheckBox, pieDupeCheckBox), new HBox(30,duplicateButton, deleteButton))
    // Stage
    stage = new JFXApp3.PrimaryStage {
      title = "Data Dashboard"
      scene = new Scene {
        root = new SplitPane {
          dividerPositions = 0.4 // set initial divider position
          items.addAll(
            new VBox {
              children = Seq(
                new VBox(5,chartCheckBoxes,controlsVBox,regionsContainer,
                  updateContainer, cardsScrollPane),
                new VBox {
                  alignment = Pos.CenterLeft
                  children = Seq(
                    saveTextFieldLabel,
                    fileNameField,
                    saveloadcontainer,
                    saveConfigTextFieldLabel,
                    configNameField,
                    saveloadConfigcontainer
                  )
                  padding = Insets(10)
                  spacing = 10
                }
              )
              padding = Insets(10)
            },
            new HBox(chartScrollPanes,dupesScrollPanes)
          )
          minHeight = 800
          minWidth = 800
        }
      }
      minWidth = 800
      minHeight = 800
      sizeToScene()
    }
  }

    override def stopApp(): Unit = {
    // Perform any required cleanup here
  }
}
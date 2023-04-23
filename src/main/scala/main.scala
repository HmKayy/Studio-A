import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.geometry.Orientation
import scalafx.scene.Scene
import scalafx.scene.chart.{CategoryAxis, NumberAxis, PieChart, ScatterChart, XYChart}
import scalafx.scene.control.{Alert, Button, ButtonType, CheckBox, Label, ScrollPane, SplitPane, TextArea, TextField}
import scalafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, HBox, Region, VBox, *}
import scalafx.scene.control.Alert.AlertType
import scalafx.geometry.Insets
import scalafx.scene.paint.Color
import scalafx.geometry.Pos
import scalafx.stage.FileChooser
import scalafx.scene.input.KeyEvent
import scalafx.beans.property.BooleanProperty
import scalafx.beans.binding.Bindings
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Region
import scalafx.Includes.*
import scalafx.scene.control.*
import scalafx.scene.text.Text

object main extends JFXApp3 {

  override def start(): Unit = {
    // Annoucement when the program is opened
    // Create a new alert dialog
    val announcement = new Alert(AlertType.Information)
    announcement.setTitle("Welcome!")
    announcement.setHeaderText("Welcome to My Dashboard")
    announcement.setContentText("Everything is set to default and a mock data has been loaded" +
      "\nClick help to bring up the general instruction of the dashboard")
    announcement.showAndWait()
    // filepath and data
    var dataStats = DataManager.loadData("")._1
    var currentConfig = DataManager.loadConfig("")._1
    // create a text box with instructions
    val instructionsBox = new Text(
      "This is a general instruction for this data dashboard" +
      "\n\nAt the top left, you can see 2 check boxes: One let you decide which chart to show, one let you decide which chart to duplicate" +
      "\n\nRight below them are 2 button, duplicate and remove duplicates (which would remove all duplicated charts)" +
      "\nNote that only 1 chart of each type can be duplicated" +
      "\n\nUnder it, you can see 4 text fields that let you decide which region you want to appear in the charts" +
      "\nNote that the 4 text fields here only accept string \"NA\",\"EU\",\"Global\" and that the first and second region for the scatter/bar plot need to be different" +
      "\n\nBelow the text fields are 2 buttons 'update' and 'help', after you've filled in the text field, clicked on update to show the latest information in the dashboard" +
      "\nUpdating the dashboard only affect the original charts, not the duplicated ones" +
      "\n\nAt the bottom left, you can see 2 text fields and their corresponding buttons that allow you to save and load the data/state of the dashboard" +
      "\n\nSave/Load Data: you can only save and load data to a .csv file." +
      "\n+ Save: you need to write the name you wanted the data to be saved as in the text field above the button" +
      "\n+ Load: the data can only be loaded from a .csv file with the correct header of :" +
      "\n \"Name\", \"Year\", \"Genre\", \"NASales\", \"EUSales\", \"GlobalSales\"" +
      "\n\nSave/load Dashboard: you can only save and load data to a .idk file." +
      "\n+ Save: you need to write the name you wanted the dashboard to be saved as in the text field above the button" +
      "\n+ Load: the data can only be loaded from a .idk file with the correct format :" +
      "\nRegion 1: x \nRegion 2: y \nRegion: z \nRegion Stat: t" +
      "\nWhere x,y,z,t can only be NA or EU or Global" +
      "\n\n*Do note that all text fields only accept letters and numbers*")
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
    val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat))
    // Create buttons
    val helpButton = new Button("Help")
    val loadButton = new Button("Load Data")
    val saveButton = new Button("Save Data")
    val loadConfigButton = new Button("Load Dashboard")
    val saveConfigButton = new Button("Save Dashboard")
    val updateButton = new Button("Update")
    // Create Labels
    val chartCheckBox = new Label("Choose which charts to show")
    val region1Label = new Label("The first region of the scatter/bar plot:")
    val region2Label = new Label("The second region of the scatter/bar plot:")
    val regionLabel = new Label("Region for the Pie chart:")
    val regionStatLabel = new Label("The region you want to know the basic statistics:")
    val updateButtonLabel = new Label("Click update to update the Dashboard | Click help for more information on what to do")
    val saveConfigTextFieldLabel = new Label("Name of the dashboard file")
    val saveTextFieldLabel = new Label("Name of the data file")
    // Text fields
    val fileNameField = new TextField() {
      onKeyTyped = { (event: KeyEvent) =>
        val c = event.character
        if (c.matches("[a-zA-Z0-9]")) {
          event.consume()
        }
      }
      tooltip = "Please enter only letters and numbers (only 0-9,a-z,A-Z characters are allowed)"
    }
    val configNameField = new TextField() {
      onKeyTyped = { (event: KeyEvent) =>
        val c = event.character
        if (c.matches("[a-zA-Z0-9]")) {
          event.consume()
        }
      }
      tooltip = "Please enter only letters and numbers (only 0-9,a-z,A-Z characters are allowed)"
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
            columnScrollPane.content = columnPlot
            scatterPlot = new scatterPlot(dataStats, region1, region2)
            columnScrollPane.content = columnPlot
            piePlot = new pieChart(dataStats, region)
            pieScrollPane.content = piePlot
            // update Cards
            cardsContainer.children.clear()
            val cardsLabel = new Label(regionStat + " statistics")
            val totalSalesCard = DataProcessor.createCard("Total Sales", DataProcessor.totalSales(dataStats, regionStat), Color.Blue)
            val avgSalesCard = DataProcessor.createCard("Average Sales", DataProcessor.avgSales(dataStats, regionStat), Color.Purple)
            val maxSalesCard = DataProcessor.createCard("Max Sales", DataProcessor.maxSales(dataStats, regionStat), Color.Green)
            val minSalesCard = DataProcessor.createCard("Min Sales", DataProcessor.minSales(dataStats, regionStat), Color.Red)
            val stdSalesCard = DataProcessor.createCard("Standard deviation of Sales", DataProcessor.stdDevSales(dataStats, regionStat), Color.Gray)
            val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat))
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
          alert.setHeaderText("Selected file is not a CSV file. or you have not selected anything")
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
    updateButton.setOnAction(_ => {
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
        new Alert(Alert.AlertType.Warning, "Invalid input. Region can only be NA, EU, or Global.\nOr your region1 and region2 is the same", ButtonType.OK).showAndWait()
      } else if (region1.equals(region2))
        new Alert(Alert.AlertType.Warning, "The first and second regions are the same, they need to be different", ButtonType.OK).showAndWait()
      else
      {
        // update plots
        columnPlot = new barPlot(dataStats, region1, region2)
        columnScrollPane.content = columnPlot
        scatterPlot = new scatterPlot(dataStats, region1, region2)
        scatterScrollPane.content = scatterPlot
        piePlot = new pieChart(dataStats, region)
        pieScrollPane.content = piePlot
        // update Cards
        cardsContainer.children.clear()
        val cardsLabel = new Label(regionStat + " statistics")
        val totalSalesCard = DataProcessor.createCard("Total Sales", DataProcessor.totalSales(dataStats, regionStat), Color.Blue)
        val avgSalesCard = DataProcessor.createCard("Average Sales", DataProcessor.avgSales(dataStats, regionStat), Color.Purple)
        val maxSalesCard = DataProcessor.createCard("Max Sales", DataProcessor.maxSales(dataStats, regionStat), Color.Green)
        val minSalesCard = DataProcessor.createCard("Min Sales", DataProcessor.minSales(dataStats, regionStat), Color.Red)
        val stdSalesCard = DataProcessor.createCard("Standard deviation of Sales", DataProcessor.stdDevSales(dataStats, regionStat), Color.Gray)
        val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat))
        cardsContainer.children.addAll(cardsLabel, totalSalesCard, avgSalesCard, maxSalesCard, minSalesCard, stdSalesCard, bestGenreCard)
      }
    })
    // Current state
    // Load Dashboard
    loadConfigButton.onAction = () => {
      val fileChooser = new FileChooser()
      fileChooser.title = "Load Data"
      val file = fileChooser.showOpenDialog(null)
      if (file != null && file.getName.endsWith(".idk")) {
        val (newConfig, checker) = DataManager.loadConfig(file.getPath)
        if (checker) {
          currentConfig = newConfig
          region1 = currentConfig._1
          region2 = currentConfig._2
          region = currentConfig._3
          regionStat = currentConfig._4
          region1TextField.text = currentConfig._1
          region2TextField.text = currentConfig._2
          regionTextField.text = currentConfig._3
          regionStatTextField.text = currentConfig._4
          // update plots
          columnPlot = new barPlot(dataStats,region1, region2)
          columnScrollPane.content = columnPlot
          scatterPlot = new scatterPlot(dataStats, region1, region2)
          scatterScrollPane.content = scatterPlot
          piePlot = new pieChart(dataStats, region)
          pieScrollPane.content = piePlot
          // update Cards
          cardsContainer.children.clear()
          val cardsLabel = new Label(regionStat + " statistics")
          val totalSalesCard = DataProcessor.createCard("Total Sales", DataProcessor.totalSales(dataStats, regionStat), Color.Blue)
          val avgSalesCard = DataProcessor.createCard("Average Sales", DataProcessor.avgSales(dataStats, regionStat), Color.Purple)
          val maxSalesCard = DataProcessor.createCard("Max Sales", DataProcessor.maxSales(dataStats, regionStat), Color.Green)
          val minSalesCard = DataProcessor.createCard("Min Sales", DataProcessor.minSales(dataStats, regionStat), Color.Red)
          val stdSalesCard = DataProcessor.createCard("Standard deviation of Sales", DataProcessor.stdDevSales(dataStats, regionStat), Color.Gray)
          val bestGenreCard = DataProcessor.createTopGenresCard("Top 5 Genres", DataProcessor.bestSellingGenres(dataStats, regionStat))
          cardsContainer.children.addAll(cardsLabel, totalSalesCard, avgSalesCard, maxSalesCard, minSalesCard, stdSalesCard, bestGenreCard)
          val alert = new Alert(AlertType.Confirmation) {
            title = "Success"
            headerText = "Dashboard has been loaded"
            contentText = "Dashboard is successfully read and loaded"
          }
          alert.showAndWait()
        } else {
          currentConfig = newConfig
          val alert = new Alert(AlertType.Warning) {
            title = "Invalid file format"
            headerText = "The file format is invalid"
            contentText = "The dashboard file is corrupted or has an invalid format. Loading default configuration."
          }
          alert.showAndWait()
        }
      } else {
        val alert = new Alert(AlertType.Warning) {
          title = "Invalid file type"
          headerText = "The file type is invalid or you have not picked anything"
          contentText = "Please select a valid .idk dashboard file."
        }
        alert.showAndWait()
      }
    }
    // Save Dashboard
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
              headerText = "Dashboard has been saved"
              contentText = "The dashboard is saved to" + file.getPath
            }
            alert.showAndWait()
          } else {
            val alert = new Alert(AlertType.Warning) {
              title = "Failed"
              headerText = "Saving dashboard has failed"
              contentText = "Make sure that the dashboard's name only has letters and numbers!"
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
    // Buttons for cards
    val showButton: Button = new Button("Show Cards") {
      onAction = () => cardsContainer.visible = true
    }

    val hideButton: Button = new Button("Hide Cards") {
      onAction = () => cardsContainer.visible = false
    }

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
                    new HBox(200, saveTextFieldLabel,new HBox(30,showButton,hideButton)),
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
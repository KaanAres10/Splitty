package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.List;


public class StatisticCtrl {

    @FXML
    PieChart pieChart;
    Stage stage;
    public ServerUtils server = new ServerUtils();
    public Event event;


    static URL getLocation(String path) {
        return client.scenes.StatisticCtrl.class.getClassLoader().getResource(path);
    }

    public void initialize() {
        if (event != null) {
            populatePieChartData();
            addTotalCostLabel();
        }

        // Apply
        applyModernAnimation();
        initializeShortcuts();


    }

    public void updateUIWithBundle(ResourceBundle bundle) {
        String titleL = (bundle.getString("pieChart"));
    }

    private String getLocalizedString(String s){
        String l = String.valueOf(LanguageManager.getInstance().getCurrentLocale());
        switch (l) {
            case "en":
                return s;
            case "fr":
                return "Coût total";
            case "de":
                return "Gesamtkosten";
            case "nl":
                return "Totale kosten";
        }
        return s;
    }

    private void addTotalCostLabel() {
        Label totalCostLabel = new Label();
        totalCostLabel.setText(getLocalizedString("Total Cost") + ": " + calculateTotalCost());
        totalCostLabel.setStyle("-fx-font-size: 16px;");

        // Position the label just below the PieChart title
        double titleHeight = 5;
        totalCostLabel.setLayoutY(titleHeight + 10);

        pieChart.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                ((StackPane) pieChart.getParent()).
                        getChildren().add(totalCostLabel);
            }
        });
    }
    BigDecimal calculateTotalCost() {
        BigDecimal totalCost = BigDecimal.ZERO;
        List<Expense> participants = server.
                getExpensesFromEvent(event.getId());
        for (Expense expense : participants) {
            totalCost = totalCost.add(expense.getAmount());
        }
        return totalCost;
    }


    private void initializeShortcuts() {
        pieChart.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE ||
                    event.getCode() == KeyCode.BACK_SPACE) {
                stage.close();
            }
        });
    }

    void applyModernAnimation() {
        // Fade transition
        FadeTransition fadeTransition = new
                FadeTransition(Duration.seconds(2), pieChart);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(3);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
        fadeTransition.play();
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public void populatePieChartData() {
        List<Expense> participants = server.getExpensesFromEvent(event.getId());
        HashMap<Tag, Double> tags = new HashMap<>();
        participants.forEach(expense -> {
            List<Tag> tagList = expense.getTags();
            BigDecimal amount = expense.getAmount();
            tagList.forEach(tag -> {
                if (tags.containsKey(tag)) {
                    tags.put(tag, tags.get(tag) + amount.doubleValue());
                } else {
                    tags.put(tag, amount.doubleValue());
                }
            });
        });

        List<PieChart.Data> pieChartData = new ArrayList<>();// Assuming you have a method to get tag colors
        List<PieChart.Data> datas = new ArrayList<>();
        List<String> colors = new ArrayList<>();

        for (Map.Entry<Tag, Double> entry : tags.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey().
                    getName(), entry.getValue());

            pieChartData.add(data);
            datas.add(data);
            // Add a ChangeListener to the Node property of the PieChart.Data object
            data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                            String color = entry.getKey().getColor();
                            String color1 = Color.web(color).toString();
                            if (color1.startsWith("0x")) {
                                color1 = "#" + color1.substring(2);
                            }
                            colors.add(color1);
                            newValue.setStyle("-fx-pie-color: " + color1 + ";");

                }
            });
        }



        pieChart.setTitle(getLocalizedString("Total Cost") + ": "
                + calculateTotalCost() + " ");
        // Set the data to the PieChart
        pieChart.setData(FXCollections.
                observableArrayList(pieChartData));
        pieChart.requestLayout();
        pieChart.applyCss();


        for (int i = 0 ; i < datas.size() ; i++) {
            String colorClass = "" ;
            for (String cls : datas.get(i).getNode().getStyleClass()) {
                if (cls.startsWith("default-color")) {
                    colorClass = cls ;
                    break ;
                }
            }
            for (Node n : pieChart.lookupAll("."+colorClass)) {
                n.setStyle("-fx-pie-color: "+colors.get(i) + ";");
            }
        }

        pieChart.getData().forEach(data -> {
            String percentage = String.format("%.2f%%", (data.getPieValue() / calculateTotalCost().doubleValue()) * 100);
            Tooltip toolTip = new Tooltip(percentage);
            Tooltip.install(data.getNode(), toolTip);
        });

    }

}
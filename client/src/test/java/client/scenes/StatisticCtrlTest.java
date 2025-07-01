package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static client.scenes.StatisticCtrl.getLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticCtrlTest extends ApplicationTest {

    private static StatisticCtrl statisticCtrl;
    private static ServerUtils server;
    private static Event eventMock;


    @BeforeAll
    public static void setupSpec() throws Exception {
        System.setProperty("testfx.robot.move_max_count", "1");
        System.setProperty("testfx.robot.write_sleep", "1");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
        server = mock(ServerUtils.class);
        eventMock = mock(Event.class);

        // Mocking server behavior
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(eventMock, "Expense 1", new BigDecimal("50.00"), null, null, "$", new ArrayList<>(), new ArrayList<>()));
        expenses.add(new Expense(eventMock, "Expense 2", new BigDecimal("75.00"), null, null, "$", new ArrayList<>(), new ArrayList<>()));

        when(server.getExpensesFromEvent(eventMock.getId())).thenReturn(expenses);
    }

    @Override
    public void start(Stage stage) throws Exception {
        var fxml = new FXMLLoader();
        fxml.setLocation(getLocation("client/scenes/TotalCostChart.fxml"));
        var scene = new Scene(fxml.load());
        stage.setScene(scene);
        statisticCtrl = fxml.getController();
        statisticCtrl.server = server;
        statisticCtrl.setEvent(eventMock);
    }

    @Test
    void testTotalCostCalculation() {
        BigDecimal totalCost = statisticCtrl.calculateTotalCost();
        assertEquals(new BigDecimal("125.00"), totalCost);
    }

    @Test
    void testTotalCostLabel() {
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
            statisticCtrl.initialize();
            Scene scene = statisticCtrl.pieChart.getScene();
            PieChart pieChart = (PieChart) scene.getRoot(); // Get the PieChart directly from the scene root
            assertEquals("Total Cost: 125.00 ", pieChart.getTitle()); // Assuming the title is set correctly
    }
    @Test
    void testPopulatePieChartData() {
        LanguageManager.getInstance().setCurrentLocale(new Locale("en"));
        // Mocking expenses
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(eventMock, "Expense 1", new BigDecimal("50.00"), null, null, "$", new ArrayList<>(), new ArrayList<>()));
        expenses.add(new Expense(eventMock, "Expense 2", new BigDecimal("75.00"), null, null, "$", new ArrayList<>(), new ArrayList<>()));

        // Mocking server behavior
        when(server.getExpensesFromEvent(eventMock.getId())).thenReturn(expenses);

        // Mocking tags
        Tag tag1 = new Tag("Food", "RED");
        Tag tag2 = new Tag("Transport", "BLUE");

        List<Tag> tags1 = new ArrayList<>();
        tags1.add(tag1);

        List<Tag> tags2 = new ArrayList<>();
        tags2.add(tag2);

        expenses.get(0).setTags(tags1);
        expenses.get(1).setTags(tags2);

        statisticCtrl.populatePieChartData();

        // Verify
        PieChart pieChart = statisticCtrl.pieChart;
        ObservableList<PieChart.Data> pieChartData = pieChart.getData();

        // first expense is Food and the second expense is Transport
        assertEquals("Food", pieChartData.get(0).getName());
        assertEquals("Transport", pieChartData.get(1).getName());


        assertEquals("Total Cost: 125.00 ", pieChart.getTitle());


        assertEquals("-fx-pie-color: #ff0000ff;", pieChartData.get(0).getNode().getStyle());
        assertEquals("-fx-pie-color: #0000ffff;", pieChartData.get(1).getNode().getStyle());
    }

}

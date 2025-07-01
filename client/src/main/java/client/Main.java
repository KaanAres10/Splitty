/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.scenes.*;
import client.utils.LanguageManager;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var startScreen = FXML.load(StartScreenCtrl.class, "client", "scenes", "StartScreen.fxml");
        var eventOverview = FXML.load(EventOverviewCtrl.class, "client", "scenes", "EventOverview.fxml");
        var inviteOverview = FXML.load(InviteCtrl.class, "client", "scenes", "Invite.fxml");
        var expenseOverview = FXML.load(ExpenseCtrl.class, "client", "scenes", "Expense.fxml");
        var currentParticipantsOverview = FXML.load(CurrentParticipantsCtrl.class, "client", "scenes", "CurrentParticipants.fxml");
        var contactOverview = FXML.load(ContactDetailsCtrl.class, "client", "scenes", "ContactDetails.fxml");
        var debtOverview2 = FXML.load(DebtOverviewCtrl.class, "client", "scenes", "DebtOverview.fxml");
        var settingsCtrl = FXML.load(SettingsCtrl.class, "client", "scenes", "Settings.fxml");
        var statisticsOverview = FXML.load(StatisticCtrl.class, "client", "scenes", "TotalCostChart.fxml");


        var adminLogInOverview = FXML.load(AdminLogInCtrl.class, "client", "scenes", "AdminLogIn.fxml");
        var adminCurrentEventsOverview = FXML.load(AdminCurrentEventsCtrl.class, "client", "scenes", "AdminCurrentEvents.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();

        mainCtrl.initialize(primaryStage, startScreen, eventOverview, inviteOverview, expenseOverview,
                currentParticipantsOverview, contactOverview, debtOverview2, settingsCtrl,
                adminLogInOverview, adminCurrentEventsOverview, statisticsOverview);
        mainCtrl.updateLanguage(bundle);

        primaryStage.setOnCloseRequest(e->{
            eventOverview.getKey().stop();
        });
    }
}
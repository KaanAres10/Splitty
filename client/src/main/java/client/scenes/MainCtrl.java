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
package client.scenes;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.User;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ResourceBundle;
import java.util.Stack;


public class MainCtrl {

    private final Stack<Scene> stack = new Stack<>();
    private Stage primaryStage;
    private EventOverviewCtrl eventOverviewCtrl;
    private Scene eventOverviewScene;
    private InviteCtrl inviteCtrl;
    private Scene inviteScene;
    private CurrentParticipantsCtrl currentParticipantsCtrl;
    private Scene currentParticipantsScene;
    private ContactDetailsCtrl contactDetailsCtrl;
    private Scene contactDetailsScene;
    private ExpenseCtrl changeExpenseCtrl;
    private Scene changeExpenseScene;
    private DebtOverviewCtrl debtOverviewCtrl;
    private Scene debtOverviewScene;
    private StartScreenCtrl startScreenCtrl;
    private Scene startScreenScene;
    private SettingsCtrl settingsCtrl;
    private Scene settingsScene;
    private AdminLogInCtrl adminLogInCtrl;
    private Scene adminLogInScene;
    private AdminCurrentEventsCtrl adminCurrentEventsCtrl;
    private Scene adminCurrentEventsScene;
    private Scene statisticsScene;
    private StatisticCtrl statisticCtrl;
    public void initialize(Stage primaryStage, Pair<StartScreenCtrl, Parent> startScreen,
                           Pair<EventOverviewCtrl, Parent> overview,
                           Pair<InviteCtrl, Parent> invite,
                           Pair<ExpenseCtrl, Parent> changeExpense,
                           Pair<CurrentParticipantsCtrl, Parent> currentParticipantsDetails,
                           Pair<ContactDetailsCtrl, Parent> participantDetails,
                           Pair<DebtOverviewCtrl, Parent> debt2,
                           Pair<SettingsCtrl, Parent> settings,
                           Pair<AdminLogInCtrl, Parent> adminLogIn,
                           Pair<AdminCurrentEventsCtrl, Parent> adminCurrentEvents, Pair<StatisticCtrl, Parent> statistics) {
        this.primaryStage = primaryStage;
        this.eventOverviewCtrl = overview.getKey();
        this.eventOverviewScene = new Scene(overview.getValue());

        this.inviteCtrl = invite.getKey();
        this.inviteScene = new Scene(invite.getValue());

        this.changeExpenseCtrl = changeExpense.getKey();
        this.changeExpenseScene = new Scene(changeExpense.getValue());

        this.currentParticipantsCtrl = currentParticipantsDetails.getKey();
        this.currentParticipantsScene = new Scene(currentParticipantsDetails.getValue());

        this.contactDetailsCtrl = participantDetails.getKey();
        this.contactDetailsScene = new Scene(participantDetails.getValue());

        this.debtOverviewCtrl = debt2.getKey();
        this.debtOverviewScene = new Scene(debt2.getValue());

        this.startScreenCtrl = startScreen.getKey();
        this.startScreenScene = new Scene(startScreen.getValue());

        this.settingsCtrl = settings.getKey();
        this.settingsScene = new Scene(settings.getValue());

        this.adminLogInCtrl = adminLogIn.getKey();
        this.adminLogInScene = new Scene(adminLogIn.getValue());

        this.adminCurrentEventsCtrl = adminCurrentEvents.getKey();
        this.adminCurrentEventsScene = new Scene(adminCurrentEvents.getValue());

        this.statisticCtrl = statistics.getKey();
        this.statisticsScene = new Scene(statistics.getValue());

        showStartScreen();
        primaryStage.show();
    }

    public void showStartScreen() {
        startScreenCtrl.refresh();
        primaryStage.setTitle("Splitty");
        primaryStage.setScene(startScreenScene);
    }

    public void showStatistics(Event event) {
        Stage statisticStage = new Stage();
        statisticStage.setTitle("Statistics");
        statisticCtrl.setEvent(event);
        statisticCtrl.initialize();
        statisticStage.setScene(statisticsScene);
        statisticStage.initOwner(primaryStage);
        statisticStage.initModality(Modality.WINDOW_MODAL);
        statisticStage.showAndWait();
    }

    public void showInvite(Event event) {
        Stage inviteStage = new Stage();
        inviteStage.setTitle("Invite");
        inviteCtrl.setEvent(event);
        inviteCtrl.initialize();
        inviteStage.setScene(inviteScene);
        inviteStage.initOwner(primaryStage);
        inviteStage.initModality(Modality.WINDOW_MODAL);
        inviteStage.showAndWait();
    }

    public void showEventOverview(User user, Event event) {
        primaryStage.setTitle("Event Overview");
        eventOverviewCtrl.refresh(user, event);
        primaryStage.setScene(eventOverviewScene);
    }

    public void showCurrentParticipants(User user, Event event) {
        primaryStage.setTitle("Current Participants");
        currentParticipantsCtrl.refresh(user, event);
        primaryStage.setScene(currentParticipantsScene);
    }

    // for Add button on Current Participants Page
    public void showContactDetails(User user, Event event) {
        contactDetailsCtrl.refresh(user, event);
        primaryStage.setTitle("Contact Details");
        primaryStage.setScene(contactDetailsScene);
    }

    // for Edit button on Current Participants Page
    public void showContactDetails(Participant participant) {
        contactDetailsCtrl.refresh(participant);
        primaryStage.setTitle("Contact Details");
        primaryStage.setScene(contactDetailsScene);
    }

    // for create button on the Start Screen Page
    public void showContactDetails(User user, Event event, boolean flagIsCreate) {
        contactDetailsCtrl.refresh(user, event, flagIsCreate);
        primaryStage.setTitle("Contact Details");
        primaryStage.setScene(contactDetailsScene);
    }

    public void showAddEdit(User user, Event event, Expense expense) {
        primaryStage.setTitle("Edit expense");
        changeExpenseCtrl.loadExpensePage(user, event, expense);
        primaryStage.setScene(changeExpenseScene);
    }

    public void showDebts(User user, Event event) {
        debtOverviewCtrl.refresh(user, event);
        primaryStage.setTitle("Settle debts");
        primaryStage.setScene(debtOverviewScene);
    }

    public void showSettings() {
        primaryStage.setTitle("Settings");
        stack.push(primaryStage.getScene());
        primaryStage.setScene(settingsScene);
    }

    public void updateLanguage(ResourceBundle bundle) {
        settingsCtrl.updateUIWithBundle(bundle);
        changeExpenseCtrl.updateUIWithBundle(bundle);
        startScreenCtrl.updateUIWithBundle(bundle);
        eventOverviewCtrl.updateUIWithBundle(bundle);
        inviteCtrl.updateUIWithBundle(bundle);
        debtOverviewCtrl.updateUIWithBundle(bundle);
        currentParticipantsCtrl.updateUIWithBundle(bundle);
        contactDetailsCtrl.updateUIWithBundle(bundle);
        adminLogInCtrl.updateUIWithBundle(bundle);
        adminCurrentEventsCtrl.updateUIWithBundle(bundle);
    }

    public void showAdminLogIn() {
        adminLogInCtrl.refresh();
        primaryStage.setTitle("Admin Log in");
        primaryStage.setScene(adminLogInScene);
    }

    public void showAdminCurrentEvents() {
        adminCurrentEventsCtrl.refresh();
        primaryStage.setTitle("Admin Current Events");
        primaryStage.setScene(adminCurrentEventsScene);
    }

    public void updateUserInStartScreen(User user) {
        startScreenCtrl.setUser(user);
    }

}

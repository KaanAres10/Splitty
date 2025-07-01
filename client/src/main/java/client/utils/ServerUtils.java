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
package client.utils;

import commons.*;
import dto.EventUserDTO;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static final String SERVER_DEFAULT = "localhost:8080";
    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();
    private static String SERVER = "http://" + SERVER_DEFAULT + "/";
    private static String WEBSOCKET_ADDRESS = "ws://" + SERVER_DEFAULT + "/websocket";
    private StompSession stompSession = connect(WEBSOCKET_ADDRESS);

    public static void setServer(String server) {
        SERVER = "http://" + server + "/";
        WEBSOCKET_ADDRESS = "ws://" + server + "/websocket";

        LanguageManager.getInstance().setServer(server);
    }

    public static String getCurrentServer() {
        return LanguageManager.getInstance().getServer();
    }

    public static boolean testServerConnection(String server) {
        System.out.println("Testing connection to server: " + server);
        String serverUrl = "http://" + server + "/";
        try {
            // Attempt to connect to the server (example using a simple GET request or WebSocket connection)
            // For demonstration, let's assume a simple REST endpoint that always exists
            Response response = ClientBuilder.newClient(new ClientConfig())
                    .target(serverUrl).path("api/handshake")
                    .request(APPLICATION_JSON)
                    .get();

            // Check if response is successful (HTTP status 204)
            return response.getStatus() == 204;
        } catch (Exception e) {
            // Connection attempt failed
            return false;
        }
    }


    private StompSession connect(String url) {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
                // should probably overwrite some of the default implementations

            }).get();
        } catch (Exception e) {
//            throw new RuntimeException(e);
            return null;
        }
    }

    public <T> void registerForDataWebSocket(String destination, Class<T> type, Consumer<T> consumer) {
        stompSession.subscribe(destination, new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    public List<Expense> getExpensesFromEvent(Long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + eventId + "/expenses")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public User createUser(User user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/users/create")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), User.class);
    }

    public User updateUser(User user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/users/update")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), User.class);
    }

    public Response leaveEvent(Long eventId, Long userId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/" + eventId + "/participants/users/" + userId + "/delete") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete();
    }

    public Event getEventByInviteCode(String inviteCode) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/invite/" + inviteCode) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(Event.class);
    }

    public Event addEvent(Event event, User user) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/create") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(new EventUserDTO(event, user), APPLICATION_JSON), Event.class);
    }

    public Participant addParticipant(Participant participant) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/events/" + participant.getEvent().getId() + "/participants/addparticipant") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(participant, APPLICATION_JSON), Participant.class);
    }

    public Participant updateParticipant(Participant participant) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/events/" + participant.getEvent().getId() + "/participants/" + participant.getId()) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .put(Entity.entity(participant, APPLICATION_JSON), Participant.class);
    }

    public Response deleteParticipant(Participant participant) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/events/" + participant.getEvent().getId()
                        + "/participants/" + participant.getId() + "/delete") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete();
    }

    public Response deleteEvent(Event event) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("/api/events/" + event.getId() + "/delete")//
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete();
    }

    public List<Debt> getEventDebt(Event event) {
        return ClientBuilder.newClient()
                .target(SERVER).path("api/events/" + event.getId() + "/debts")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Debt>>() {
                });
    }

    public Debt markDebtAs(Debt debt, Boolean paid) {
        return ClientBuilder.newClient()
                .target(SERVER).path(
                        "api/events/" + debt.getExpense().getEvent().getId()
                                + "/debts/" + debt.getId()
                ).queryParam("paid", paid)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(debt, APPLICATION_JSON), Debt.class);
    }

    public Participant getParticipantByName(Long eventId, String name) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + eventId + "/participants/" + name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<Debt> getDebtsByExpense(Long eventId, Long expenseId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + eventId + "/debts/" + expenseId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<Participant> getParticipantsFromEvent(Long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/events/" + eventId + "/participants")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public void getParticipantsUpdate(Long eventId, Consumer<Participant> consumer) {
        EXEC.submit(() -> {
            while (!Thread.interrupted()) {
                var result = ClientBuilder.newClient(new ClientConfig())
                        .target(SERVER).path("api/events/" + eventId + "/participants/updates")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);

                if (result.getStatus() == 204) continue;
                var participant = result.readEntity(Participant.class);
                consumer.accept(participant);
            }
        });
    }

    public void stop() {
        EXEC.shutdown();
    }

    public Expense addExpense(Expense expense, Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + event.getId() + "/expenses/create")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    public Expense addExpenseWithoutDebts(Expense expense, Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + event.getId() + "/expenses/create/nodebts")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    public Expense editExpense(Expense expense, Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + event.getId() + "/expenses/edit")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    public Event editEventTitle(Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + event.getId() + "/title")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    public List<Event> getEventsByUser(User user) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/users/" + user.getId() + "/events")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<Event> getAllEvents() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public Boolean checkPassword(String enteredString) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/admin/password/" + enteredString)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<Transfer> getAllTransfersFromEvent(Long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + eventId + "/transfers/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<Debt> getAllDebtsFromEvent(Long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + eventId + "/debts/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<Tag> getAllTagsFromEvent(Long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + eventId + "/tags/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public List<Expense> getAllExpensesFromEvent(Long eventId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/events/" + eventId + "/expenses/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<>() {
                });
    }

    public Debt addDebt(Long eventId, Debt debt) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/" + eventId + "/debts/add") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(debt, APPLICATION_JSON), Debt.class);
    }

    public Transfer addTransfer(Long eventId, Long debtId, Transfer transfer) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/" + eventId + "/" + debtId + "/transfers/create") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(transfer, APPLICATION_JSON), Transfer.class);
    }

    public Tag addTag(Long eventId, Tag tag) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/" + eventId + "/tags/add") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    public void removeTag(Long eventId, Long tagId) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/" + eventId + "/tags/remove/" + tagId) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete();
    }

    public Response deleteExpense(Long eventId, Long expenseId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/events/" + eventId + "/expenses/delete/" + expenseId) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .delete();
    }


}
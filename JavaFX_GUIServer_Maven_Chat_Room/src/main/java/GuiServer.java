// CS 342 Project 4, Spring 202
//
// Joseph Esquivel and Asif Rifat (Details in Collaboration Doc)
// NETID: Jesqui20
// Email: Jesqui20@uic.edu
//
// This file contains the main method to launch the GUI Server for a
// chat application for any number of clients.
// Supported features include personal message, group chat, or chatting with the entire room.
// Data on the users is stored and maintained, updating as the clients come/leave
//
import javafx.application.Application;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import javafx.scene.text.Text;
import java.util.ArrayList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.HashMap;

public class GuiServer extends Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	// server and client connection
	Server serverConnection;
	Client clientConnection;

	// list view for client
	ListView < String > ActiveClients = new ListView < String > ();
	ListView < String > Current_InGroup = new ListView < String > ();
	ListView < String > viewMessages = new ListView < String > ();

	//server list view
	ListView < MoreInfo > serverInfo = new ListView < MoreInfo > ();

	// list view for server connection
	ListView < String > ServerList = new ListView < String > ();

	// list view for client Connection
	ListView < MoreInfo > ClientList = new ListView < MoreInfo > ();

	HashMap < String, Scene > sceneMap;

	String[] values;

	int counter = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// primary stage title
		primaryStage.setTitle("Client-Server GUI");

		// server button
		Button serverButton;
		serverButton = new Button("Server");
		serverButton.setStyle("-fx-pref-width: 500px");
		serverButton.setStyle("-fx-pref-height: 500px");

		serverButton.setLayoutX(50);
		serverButton.setLayoutY(50);

		serverButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("server"));
			primaryStage.setTitle("This is the Server");
			serverConnection = new Server(data -> {
				Platform.runLater(() -> {
					ServerList.getItems().add(data.toString());
				});

			});

		}); // end of server

		// client button
		Button clientButton = new Button("Client");
		clientButton.setStyle("-fx-pref-width: 500px");
		clientButton.setStyle("-fx-pref-height: 500px");

		clientButton.setLayoutX(200);
		clientButton.setLayoutY(50);

		clientButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("client"));
			primaryStage.setTitle("This is a client");

			clientConnection = new Client(data -> {
				Platform.runLater(() -> {
					serverInfo.getItems().add((MoreInfo) data);
					ClientList.getItems().add((MoreInfo) data);

					MoreInfo clientsInformation = (MoreInfo) data;

					ArrayList < String > clients = new ArrayList < String > ();

					viewMessages.getItems().add(clientsInformation.message);

					if (clientsInformation.isNewClient == true) {
						ActiveClients.getItems().clear();

						for (int i = 0; i < clientsInformation.ActiveClients.size(); i++) {
							if (clients.contains(clientsInformation.ActiveClients.get(i).toString()) == false) {
								clients.add(clientsInformation.ActiveClients.get(i).toString());
							}
							ActiveClients.getItems().add("Clients #" + clients.get(i));

						}
						counter++;

					}
				});
			});
			clientConnection.start();

		}); // end of client

		// primary stage background
		Pane startPane = new Pane();
		startPane.setStyle("-fx-background-color: LIGHTSTEELBLUE");

		// add serverButton and clientButton
		startPane.getChildren().addAll(serverButton, clientButton);

		Scene startScene = new Scene(startPane, 300, 600);

		// this is the start scene
		sceneMap = new HashMap < String, Scene > ();
		sceneMap.put("client", CreateClientGUI());
		sceneMap.put("server", createServerGui());

		primaryStage.setOnCloseRequest(new EventHandler < WindowEvent > () {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(startScene);
		primaryStage.show();

	}

	// this is the server gui
	public Scene createServerGui() {

		BorderPane pane = new BorderPane();

		ServerList.setStyle("-fx-font: 12px Verdana;");
		pane.setCenter(ServerList);

		return new Scene(pane, 400, 600);

	}

	//this is the client gui
	public Scene CreateClientGUI() {
		// messages text
		Text yourMessages = new Text(" Messages ");
		yourMessages.setStyle("-fx-font: 12px Verdana; ");
		yourMessages.setLayoutX(100);
		yourMessages.setLayoutY(45);

		// view messages listview
		viewMessages.setStyle("-fx-font: 12px Verdana; ");
		viewMessages.setPrefSize(257, 410);
		viewMessages.setLayoutY(60);
		viewMessages.setLayoutX(30);

		// client in server
		Text activeClientsText = new Text("Clients in server");
		activeClientsText.setStyle("-fx-font: 12px Verdana; ");
		activeClientsText.setLayoutX(360);
		activeClientsText.setLayoutY(45);

		// clients in server listview
		ActiveClients.setStyle("-fx-font: 12px Verdana; ");
		ActiveClients.setPrefSize(200, 200);
		ActiveClients.setLayoutX(300);
		ActiveClients.setLayoutY(60);


		// send text field
		TextField sendText = new TextField();
		sendText.setStyle("-fx-font: 12px Verdana; ");
		sendText.setLayoutX(30);
		sendText.setLayoutY(480);
		sendText.setPrefSize(200, 50);

		// send button
		Button sendButton = new Button("Send");
		sendButton.setStyle("-fx-font: 12px Verdana; -fx-base: POWDERBLUE;");
		sendButton.setLayoutX(240);
		sendButton.setLayoutY(480);
		sendButton.setPrefSize(50, 50);
		sendButton.setOnAction(e -> {
			MoreInfo info = new MoreInfo();
			info.message = sendText.getText();

			if (values == null) {
				synchronized(clientConnection) {
					clientConnection.send(info);
				}
				sendText.clear();

			} else {
				info.SendingTo = values;
				synchronized(clientConnection) {
					clientConnection.send(info);
				}

				sendText.clear();
			}
		});


		// listview for current member in group
		Current_InGroup.setStyle("-fx-font: 12px Verdana; ");
		Current_InGroup.setPrefSize(200, 190);
		Current_InGroup.setLayoutX(300);
		Current_InGroup.setLayoutY(300);

		// button to add client(s) to group
		Button AddClient = new Button("Add Client");
		AddClient.setStyle("-fx-font: 11px Verdana; -fx-base: POWDERBLUE;");
		AddClient.setPrefSize(80, 25);
		AddClient.setLayoutX(420);
		AddClient.setLayoutY(500);

		// textfield for client(s) in group
		TextField ClientTextField = new TextField();
		ClientTextField.setStyle("-fx-font: 12px Verdana; ");
		ClientTextField.setPrefSize(110, 30);
		ClientTextField.setLayoutX(300);
		ClientTextField.setLayoutY(500);

		// refresh Button
		Button refresh = new Button("    Refresh/Send to all Client(s)   ");
		refresh.setLayoutX(300);
		refresh.setLayoutY(535);
		refresh.setStyle("-fx-font: 11px Verdana; -fx-base: POWDERBLUE;");
		refresh.setOnAction(R -> {
			Current_InGroup.getItems().clear();
			values = null;
		});

		// instruction for add client
		Text AddClientInfo = new Text("  Choose client(s) (text ex: 1,2)");
		AddClientInfo.setStyle("-fx-font: 12px Verdana; ");
		AddClientInfo.setLayoutX(300);
		AddClientInfo.setLayoutY(282);

		// AddClient in action
		AddClient.setOnAction(R -> {
			String temp = ClientTextField.getText();

			values = temp.split(",");
			ClientTextField.clear();

			int i;
			int length = values.length;

			for (i = 0; i < length; i++) {
				Current_InGroup.getItems().add("Client #" + values[i]);
			}
		});

		// scene
		Pane clientPane = new Pane();
		clientPane.setStyle("-fx-background-color: ALICEBLUE");
		clientPane.getChildren().addAll(sendText, sendButton, viewMessages, ActiveClients, Current_InGroup,
				ClientTextField, AddClient, AddClientInfo, yourMessages, activeClientsText, refresh);

		return new Scene(clientPane, 535, 600);
	}

}
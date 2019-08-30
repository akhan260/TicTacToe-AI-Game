import com.sun.corba.se.spi.activation.Server;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.List;


public class RPSLSClient extends Application{


    //Setting fields and buttons and images
    TextField tf_port,tf_ip,s3,s4;
    Button clientChoice,PlayAgain, Quit, submitPort, submitIP;
    HashMap<String, Scene> sceneMap;
    GridPane grid;
    HBox line1, line2;
    VBox stackup;
    VBox clientBox;
    Scene startScene;
    BorderPane startPane;
    Client clientConnection;
    Server serverConnection;
    Label lb_port, lb_ip, lb_score;

    ArrayList<String> clientsArray = new ArrayList<String>();
    String check = "";
    String check2 = "";
    List<String> result;
    List<String> scoreChart;

    Label displayWinner = new Label();
    GameInfo gameInfo = new GameInfo();

    Label lb_wins = new Label();

    ListView<String> highestScore = new ListView();

    private double sceneWidth = 500;
    private double sceneHeight = 500;

    Button playAgain = new Button("Play Again");
    Button quit = new Button("Quit");


    private int n = 3;
    private int m = 3;
    private int numOfwins = 0;

    double gridWidth = sceneWidth / n;
    double gridHeight = sceneHeight / m;

    MyNode[][] playfield = new MyNode[n][m];
    String[] data = new String[9];
    //GameInfo gameInfo = new GameInfo();

    ArrayList<Rectangle> square = new ArrayList<>();
    ArrayList<Label> mark = new ArrayList<>();
    ArrayList<String> board = new ArrayList<>();


    //list view
    ListView<String> listItems;

     int portNum;
     String ip;
     String name;

     int serverPick = -1;




    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub

        //initalizing logging in port
        lb_port = new Label("Enter Port");
        tf_port = new TextField("5555");
        submitPort = new Button("Submit");

        //initializing logging in ip address
        lb_ip = new Label("Enter IP Address");
        tf_ip = new TextField("127.0.0.1");
        submitIP = new Button("Submit");


        primaryStage.setTitle("This is the Client");



        this.clientChoice = new Button("Enter Game"); //button to start the game

        //submitIP is used so the program can get the IP
        submitIP.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                ip = tf_ip.getText();
                submitIP.setText("Submitted");
            }
        });
        //submitPort is used to get the port
        submitPort.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent event){
                portNum = Integer.parseInt(tf_port.getText());
                submitPort.setText("Submitted");
            }
        });
        //ListView is used to so that any client that's online can challenged another
        ListView test = new ListView();
        test.getItems().add("Players Online: to challenge these players click on the cell and then the Challenge Button");
        //clientChoice forgot to change it but
        this.clientChoice.setOnAction(e-> {primaryStage.setScene(sceneMap.get("client"));
            clientConnection = new Client(
                    data -> {Platform.runLater(()->{
                        listItems.getItems().add(data.toString());//data in getItems is convert in a string

                        if(data.toString().startsWith("Top Scorer "))
                        {
                            String temp8a = data.toString().replace("Top Scorer [", "");
                            String temp8b = temp8a.substring(0, temp8a.length()-1);
                            scoreChart = Arrays.asList(temp8b.toString().split("\\s*,\\s*"));
                            highestScore.getItems().clear();
                            for(int i = 0; i < scoreChart.size(); i++)
                            {
                                highestScore.getItems().add(scoreChart.get(i));
                                System.out.print("Debug 3");
                                System.out.print("Debug 4");

                            }

                            System.out.print(data);
                        }
                        if(data.toString().startsWith("Server picks "))
                        {
                            String temp3a = data.toString().replace("Server picks ", "");
                            serverPick = Integer.parseInt(temp3a);
                            System.out.print("--->" + serverPick + "\n");
                            //mark.set(serverPick, "X");
                            mark.get(serverPick).setText("X");
                            board.set(serverPick, "X");
                            System.out.print(board + "\n");
                         // these are the computer moves in the tic tac toe board game
                            if((mark.get(0).getText().equals("X") && mark.get(1).getText().equals("X") &&  mark.get(2).getText().equals("X")) ||
                                    (mark.get(3).getText().equals("X") && mark.get(4).getText().equals("X") &&  mark.get(5).getText().equals("X")) ||
                                    (mark.get(6).getText().equals("X") && mark.get(7).getText().equals("X") &&  mark.get(8).getText().equals("X")) ||
                                    (mark.get(0).getText().equals("X") && mark.get(3).getText().equals("X") &&  mark.get(6).getText().equals("X")) ||
                                    (mark.get(1).getText().equals("X") && mark.get(4).getText().equals("X") &&  mark.get(7).getText().equals("X")) ||
                                    (mark.get(2).getText().equals("X") && mark.get(5).getText().equals("X") &&  mark.get(8).getText().equals("X")) ||
                                    (mark.get(0).getText().equals("X") && mark.get(4).getText().equals("X") &&  mark.get(8).getText().equals("X")) ||
                                    (mark.get(2).getText().equals("X") && mark.get(4).getText().equals("X") &&  mark.get(6).getText().equals("X")))
                            {
                                System.out.print("Server Won");// this is printed out if the computer win

                                for(int i = 0; i < square.size(); i++)// this goes though the arraylist
                                {
                                    square.get(i).setDisable(true);// the board game is off
                                }
                                displayWinner.setText("Result: Server Won");// the server won
                                clientConnection.send("Server Won");

                            }// these are the moves that get will get the player and computer a tie
                            else if(!board.get(0).equals("b") && !board.get(1).equals("b") && !board.get(2).equals("b") &&
                               !board.get(3).equals("b") && !board.get(4).equals("b") && !board.get(5).equals("b") &&
                               !board.get(6).equals("b") && !board.get(7).equals("b") && !board.get(8).equals("b"))
                            {
                                System.out.print("Game Tied");// the game is display a text game tied

                                for(int i = 0; i < square.size(); i++)//  this goes though the arraylist
                                {
                                    square.get(i).setDisable(true);// turn the board game off
                                }
                                displayWinner.setText("Result: Game Tied");//text is display game tied
                                clientConnection.send("Game Tied");

                            }

                        }
                        if(data.toString().endsWith("is online"))
                        {
                            test.getItems().add(data.toString());
                        }
                        if(data.toString().endsWith("online]"))
                        {
                            check = data.toString().replace("[", "");
                            check2 = check.substring(0, check.length()-1);

                            test.getItems().clear();// remove everything in the items of test
                            test.getItems().add("Players Online: to challenge these players click on the cell and then the Challenge Button");
                            System.out.print("Debug 1");
                            result = Arrays.asList(check2.toString().split("\\s*,\\s*"));
                            System.out.print("Debug 2");
                            for(int i = 0; i < result.size(); i++)
                            {
                                System.out.print("Debug 3");
                                test.getItems().add(result.get(i));
                                System.out.print("Debug 4");

                            }
                        }
//
                    });

                    }, portNum, ip);
            clientConnection.start();

        });



        //setting up the design
        line1 = new HBox(5, lb_port, tf_port, submitPort);
        line2 = new HBox(5, lb_ip, tf_ip, submitIP);
        stackup = new VBox(5, line1,line2, clientChoice);
        startPane = new BorderPane();
        startPane.setPadding(new Insets(70));
        startPane.setCenter(stackup);

        startScene = new Scene(startPane, 550, 200);



        sceneMap = new HashMap<String, Scene>();

        sceneMap.put("client",  createClientGui(primaryStage, test));





        primaryStage.setScene(startScene);
        primaryStage.show();

    }

    public Scene createClientGui(Stage primaryStage, ListView test) throws IOException, ClassNotFoundException {
    	// The display of the client board game
        Label testing = new Label("Top 3 Highest Score");// label keeps the 3 highest score
        highestScore.setPrefSize(175, 80);
        highestScore.getItems().addAll("", "", "");
        VBox temp2 = new VBox(testing, highestScore);

        lb_wins.setText("Numbers of wins: " + numOfwins);

        VBox temp3 = new VBox(50, temp2, lb_wins, displayWinner); // display where the quit and playAgain buttons are

        HBox temp = new HBox(10,playAgain, quit);
        Group root = new Group();
        BorderPane pane = new BorderPane();


        //O X O
        //X b b
        //X b O
        //X O X O b b O b X
        //O X O X b b X b O
        int counter = 0;
        //ArrayList<String> puzzle = new ArrayList<>();
//        data[counter] = Integer.toString(counter);
        Random rand = new Random();
        int randomInt = rand.nextInt(9);
        System.out.print("Random Int " + randomInt + "\n");
        // initialize playfield
        MyNode node;
        for( int i=0; i < n; i++) {
            for( int j=0; j < m; j++) {

                //puzzle.add("b");
                board.add("b");
                // create node
                if(counter == randomInt)
                {
                    node = new MyNode( " " , j * gridHeight,i * gridWidth, gridHeight, gridWidth,counter, board, true);
                }
                else
                {
                    node = new MyNode( " " , j * gridHeight,i * gridWidth, gridHeight, gridWidth,counter, board, false);
                }


                // add node to group
                root.getChildren().add( node);

                // add to playfield for further reference using an array
                playfield[i][j] = node;

                counter++;
            }
        }

        pane.setLeft(temp3);
        pane.setCenter(root);
        pane.setTop(temp);






        //setting up the listview to know the game information
        listItems = new ListView<String>();
        listItems.setCellFactory(lst ->
                new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        //when it is a players turn the other player images will be disable
                        //System.out.print("Checking here: " + clientConnection.temp);

//                        if(clientConnection.info.data.startsWith("Server picks"))
//                        {
//                            System.out.print("--->" + clientConnection.info.stateGame + "\n");
//                        }
                        super.updateItem(item, empty);
                        if (empty) {
                            setPrefHeight(45.0);
                            setText(null);
                        } else {
                            setPrefHeight(Region.USE_COMPUTED_SIZE);
                            setText(item);
                        }
                    }
                });


        //quit the game
        quit.setOnAction(e -> {

                clientConnection.send(" wants to quit");
                primaryStage.close();


        });
        //play the game again
        playAgain.setOnAction(event -> {
            for(int i = 0; i < square.size(); i++)
            {
                square.get(i).setDisable(false);
                mark.get(i).setText("");
                board.set(i, "b");
            }
            Random newrand = new Random();
            int index = newrand.nextInt(9);
            mark.get(index).setText("X");
            board.set(index, "X");
            clientConnection.info.data = board.toString();
            displayWinner.setText("");
            clientConnection.send("Play Again");

            // clientConnection.send(clientConnection.info.data);


                //clientConnection.send(" wants to play again");
        });


        //setting up the design
        return new Scene(pane);

    }
  //The changes that were made in this program that was in program3 is the added listView test. 
 // The added listView is here to so client can challenged one another.
 //When a client want to play against another player the client just click on the client the player want to play against.
 //There are two added button for the players to challenge and accept.
 // Program4 which displays when clients are connected to the server.
 //Program4 has also include more if statements in it updateItem() function, since clients are able to challenged one another.



    public class MyNode extends StackPane {

        int serverChoice = -1;
        public MyNode( String name, double x, double y, double width, double height, int counter, ArrayList<String> data, Boolean startX) {

//			setStyle("-fx-border-color: black");
//			this.setPrefSize(2000, 2000);
//			this.setOnMouseClicked(e -> handleMouseClick());


            // create rectangle
            javafx.scene.shape.Rectangle rectangle = new Rectangle( width, height);
            square.add(rectangle);
            rectangle.setStroke(javafx.scene.paint.Color.BLACK);
            rectangle.setFill(Color.WHITE);

//                mark.get(serverPick).setText("X");
//                serverPick = -1;

            Label label = new Label( name);
            mark.add(label);

            if(startX)
            {
                mark.get(counter).setText("X");
                data.set(counter, "X");
                startX = false;
            }


//            if(clientConnection.temp.startsWith("Server picks "))
//            {
//                System.out.print(clientConnection.temp + "<----");
//            }

            if(serverPick > -1)
            {
                System.out.print("yesyesyesyesyes>>>\n");
            }
            rectangle.setOnMouseClicked(event -> {
                clientConnection.info.clickedOn = Integer.toString(counter);
                System.out.print("Clicked on: " + counter + "\n");
                label.setText("O");
                data.set(counter, "O");
                clientConnection.info.data = data.toString();

                System.out.print(clientConnection.info.data + "\n");
                if((mark.get(0).getText().equals("O") && mark.get(1).getText().equals("O") &&  mark.get(2).getText().equals("O")) ||
                (mark.get(3).getText().equals("O") && mark.get(4).getText().equals("O") &&  mark.get(5).getText().equals("O")) ||
                (mark.get(6).getText().equals("O") && mark.get(7).getText().equals("O") &&  mark.get(8).getText().equals("O")) ||
                (mark.get(0).getText().equals("O") && mark.get(3).getText().equals("O") &&  mark.get(6).getText().equals("O")) ||
                (mark.get(1).getText().equals("O") && mark.get(4).getText().equals("O") &&  mark.get(7).getText().equals("O")) ||
                (mark.get(2).getText().equals("O") && mark.get(5).getText().equals("O") &&  mark.get(8).getText().equals("O")) ||
                (mark.get(0).getText().equals("O") && mark.get(4).getText().equals("O") &&  mark.get(8).getText().equals("O")) ||
                (mark.get(2).getText().equals("O") && mark.get(4).getText().equals("O") &&  mark.get(6).getText().equals("O")))
                {
                    System.out.print("Client Won");
                    numOfwins++;
                    lb_wins.setText("Number wins " + numOfwins);
                    for(int i = 0; i < square.size(); i++)
                    {
                        square.get(i).setDisable(true);
                    }
                    displayWinner.setText("Result: Client Won");
                    clientConnection.send("Client Won");
                }
                else {
                    clientConnection.send(clientConnection.info.data.toString());

                }

//                clientConnection.send(clientConnection.info.state.toString());
                // data.getClass().getName();
            });

//            if(clientConnection.info.bestPick.startsWith("Server picks"))
//            {
//            }




            // set position
            setTranslateX( x);
            setTranslateY( y);

//            if(rectangle.is)
//            {
//
//            }
            getChildren().addAll( rectangle, label);

        }


    }
}

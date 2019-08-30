import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server{

	//initializing
	private int count = 1;
	private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private ArrayList<Integer> clientList = new ArrayList<>();
	private TheServer server;
	private Consumer<Serializable> callback;
	private int port = 0;
	private GameInfo info;
	boolean check;
	private String play = "Play";
	private String wait = "Wait";
	ArrayList<String> displayOnline = new ArrayList<>();
	String clientName;
	ArrayList<ClientThread> pair = new ArrayList<>();
	ArrayList<ArrayList<ClientThread>> allPairs = new ArrayList<>();
	GameInfo ji = new GameInfo();
	ArrayList<String> highScorer = new ArrayList<>();
	ArrayList<tempObject> objects2 = new ArrayList<>();




	//Initializing in constructor
	Server(Consumer<Serializable> call, int portNum){
	    port = portNum;
		callback = call;
		server = new TheServer();
		server.start();
		info = new GameInfo();
		clientName = "";
	}


	//Class thread
	public class TheServer extends Thread{

		public void run() {
			//waiting for the client
			callback.accept("Server is waiting for a client!\n");

			//checking socket
			try(ServerSocket mysocket = new ServerSocket(port);){
				//logged in now waiiting or a client

                while(true) {
                	//setting up clientName
					clientName = ("client #" + count);

					//Accepting the socket
                    ClientThread c = new ClientThread(mysocket.accept(), clientName, info);
                    //callback to server that client has been connected to it
					GameInfo temp = new GameInfo();
                    callback.accept("client has connected to server: " + "client #" + count);
//

					//c.out.writeObject();
                    //sending message to client that about the name
					temp.data = "You are client#" + count;
                    c.out.writeObject(temp); //this is also clientName
                    //add client to an arraylist of client thread
					clients.add(c);
					// starting a thread(multiple threads will be created)
                    c.start();
					//incrementing the count
                    count++;

                }
			}//end of try
			catch(Exception e) {
				//if server socket doesn't launch
				callback.accept("Server socket did not launch");
			}
		}//end of while
	}



	class tempObject
	{
		public String name;
		public int score;

		public tempObject(String Name, int Score)
		{
			name = Name;
			score = Score;
		}
	}// // this function sort the highest score of player 
	public String sortHighScorer(ArrayList<Server.ClientThread> clients)
	{
		ArrayList<String> scoresBoard = new ArrayList<>(); // the arraylist of scoresBoard
		ArrayList<tempObject> objects = new ArrayList<>();// the arraylist of objects

		for(int i = 0; i < clients.size(); i++)
		{
			objects.add(new tempObject(clients.get(i).clientName,clients.get(i).score));
		}
		Collections.sort(objects, new Comparator<tempObject>() {
			@Override
			public int compare(tempObject o1, tempObject o2) {
				return Integer.valueOf(o2.score).compareTo(Integer.valueOf(o1.score));
			}
		});

		for(int i = 0; i < objects.size(); i++)
		{
			if(i == 3)
			{
				break;
			}
			scoresBoard.add(objects.get(i).name + ": " + objects.get(i).score);
			callback.accept("#" + (i+1) + " position is " + objects.get(i).name + " with " +  objects.get(i).score);
		}

		return scoresBoard.toString();


	}

	//starting the clientThread
	class ClientThread extends Thread{


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;
		private String clientName;
		String extract = "";
		String extract2 = "";
		String reciever = "";
		String move;
		GameInfo info;
		Boolean plays;
		GameInfo gameInfo;
		ArrayList<String> pr_state;
		private ArrayList<Node> movesList;
		private  MinMax sendIn_InitState;
		String delim;
		ArrayList<Integer> bestMove;
		int pickedbestMove;
		ArrayList<Integer> worstMove;
		Boolean addOne;
		int score;
		String topScorer;





		//Client Thread initalizer
		ClientThread(Socket s, String name, GameInfo gameInfo) throws IOException {
			this.connection = s; //socket
			//reading the data from client
			in = new ObjectInputStream(connection.getInputStream());
			//sending the data from client
			out = new ObjectOutputStream(connection.getOutputStream());
			//clientName
			clientName = name;
			//move
			move = "";
			//GameInfo
			info = gameInfo;
			plays = false;
			gameInfo = new GameInfo();
			delim = "[ ]+";
			bestMove = new ArrayList<>();
			worstMove = new ArrayList<>();
			addOne = false;
			score = 0;
			topScorer = "";

		}

		private void printBestMoves() // this prints the moves of the player and computer 
		{
			System.out.print("\n\nThe moves list is: < ");

			for(int x = 0; x < movesList.size(); x++)
			{
				Node temp = movesList.get(x);

				if(temp.getMinMax() == 10 || temp.getMinMax() == 0)
				{
					System.out.print(temp.getMovedTo()-1 + " ");
					bestMove.add(temp.getMovedTo()-1);
				}
				if(temp.getMinMax() == -10)
				{
					worstMove.add(temp.getMovedTo()-1);
				}
			}
			System.out.print(">\n");
			System.out.print("worstMove: " + worstMove + "\n"); // print worstMove of the game
			System.out.print("bestMove: " + bestMove + "\n"); // print the bestMove of the game

			if(worstMove.size() > 0 && bestMove.size() < 2)
			{
				Random rand = new Random();
				int index = rand.nextInt(worstMove.size());
				bestMove.add(worstMove.get(index)); // add the best move to the worst move
				System.out.print("Adding one worst Move to best move: " + bestMove.get(bestMove.size() - 1) + "\n");
				System.out.print("the random number " + index + "\n"); // display a random number before and during the game 
				worstMove.remove(index);
			}
//			worstMove.remove(rand.nextInt(worstMove.size()));
		}
		//updating each client

		public synchronized void run(){

			try {
				connection.setTcpNoDelay(true);
				this.out.writeObject(ji);
//				highScorer.add(this.clientName);

				topScorer = sortHighScorer(clients); // the topscore of the client
				GameInfo obj3 = new GameInfo();
				obj3.data = "Top Scorer " + topScorer;
				for(int i = 0; i < clients.size(); i++)
				{
					clients.get(i).out.writeObject(obj3); 
				}
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			//After the first client is added whenever any other client comes
			// it will print all the clients that are there before it got connected
//			GameInfo temp2 = new GameInfo();
			//it is added to the list


			 while(true) {
				try {
					//reading the clients moves

					String check =  in.readObject().toString();

					if(check.equals("wants to quit ")) // if plaer clicks the quit button
					{
						callback.accept(this.clientName + " has left the server"); // this text is display in the server
						clients.remove(this); // server remove client
						break; //end
					}
					if(check.startsWith("[") && check.endsWith("]")) { // the moves of the player and the computer
						String temp = check.toString().replace("[", "");
						String temp2 = temp.replace("]", " ");
						String temp3 = temp2.toString().replace(",", "");

						System.out.print(temp3 + "\n");

						sendIn_InitState = new MinMax(temp3.split(delim));

						movesList = sendIn_InitState.findMoves();

						printBestMoves();

						System.out.print("worstMove: " + worstMove + "\n");
						System.out.print("bestMove: " + bestMove + "\n");

						if (bestMove.size() == 0) { // if there's no best moves
							Random rand = new Random();
							pickedbestMove = rand.nextInt(worstMove.size());
							GameInfo temp10a = new GameInfo();
							temp10a.data = "Server picks " + worstMove.get(pickedbestMove);
							System.out.print(worstMove.get(pickedbestMove) + "\n");
							this.out.writeObject(temp10a);
							worstMove.clear();
							bestMove.clear();
						}
						if (bestMove.size() > 0) {

							Random rand = new Random();
							pickedbestMove = rand.nextInt(bestMove.size());//best moves are being random
							GameInfo temp9a = new GameInfo(); //new object
							temp9a.data = "Server picks " + bestMove.get(pickedbestMove); // display the best moves the server pick
							System.out.print(bestMove.get(pickedbestMove) + "\n");//display the best moves
							this.out.writeObject(temp9a);
							bestMove.clear(); // clear best moves
							worstMove.clear();//clear worst moves

						}


						bestMove.clear();
						System.out.print("Best Move Clear: " + bestMove + "\n"); //display that the best move was clear
						worstMove.clear();
						System.out.print("Worst Move Clear: " + bestMove + "\n");// clear worst moves
					}
					if(check.endsWith(" Won"))
					{
						if(check.startsWith("Server"))
						{
							score = score - 10; // if server win
							callback.accept(this.clientName + " is defeated by the server.. -10 points");// display server winning and player getting -10
							callback.accept(this.clientName + " score is " + score);// display client recent score
							topScorer = sortHighScorer(clients); //client's highest score
						}
						if(check.startsWith("Client"))
						{
							score = score + 10;// if client win
							callback.accept(this.clientName + " has beaten the server in the game.. +10 points");// display server losing and player getting +10
							callback.accept(this.clientName + " score is " + score);// display client recent score
							topScorer = sortHighScorer(clients);//client's highest score
						}
						GameInfo obj = new GameInfo();
						obj.data = "Top Scorer " + topScorer;
						for(int i = 0; i < clients.size(); i++)
						{
							clients.get(i).out.writeObject(obj);
						}
					}
					else if(check.equals("Game Tied")) // if no one win
					{

						callback.accept(this.clientName + " game has ended in a tie"); //this text is display on the server
						callback.accept(this.clientName + " score is " + score);
						topScorer = sortHighScorer(clients); // display client high score
						GameInfo obj2 = new GameInfo();
						obj2.data = "Top Scorer " + topScorer; // display the highest score
						for(int i = 0; i < clients.size(); i++)
						{
							clients.get(i).out.writeObject(obj2); // read clients moves
						}

					}

					if(check.equals("Play Again")) // if player clicks play again button
					{
						callback.accept(this.clientName + " chooses to play another round"); // new game start
					}

//					for (int i=0; i<clients.size(); i++)
//						System.out.println(clients.get(i) + " " + clients.get(i).score);
//						clients.sort();


				}
				catch(Exception e) {

					callback.accept("OOOOPPs...Something wrong with the socket from client: " + clientName + "....closing down!");
					break;
				}
			}
		}//end of run

//		public void disconnect() {
//
//				connection.setTcpNoDelay(false);
//
//		}
	}//end of client thread


}
//The changes that were made in this program that was in program3 is that a function called updateClientsOnline(), which updates every client that 
//sign in the server and the client and it's number is display.
//Reading clients moves had to be change, because the preview program was hard coded so that only two players could play.
//In the preview's program as soon client 2 log in the server the game would start automatically. Now the game can hold x amount of clients
//and they have the choice to challenge anyone.
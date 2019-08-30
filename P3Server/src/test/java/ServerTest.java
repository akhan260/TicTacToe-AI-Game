import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

class ServerTest {
	private RPSLSServer server;//the gui server
	private Consumer<Serializable> callback;
	private Socket socket; // the socket
	String p1plays,p2plays;// t
	GameInfo gameInfo;
	int score; //the score
	Server test; //test is the server


	@BeforeEach
	void init(){
		server = new RPSLSServer();// the server class
		p1plays = "rock";// player one move
		p2plays = "paper";// player two move
		gameInfo = new GameInfo();
		test = new Server(callback, 5555);// the port number
		test.clientName = "client #1";
	}

	@Test
	void testInit_RPSLSServer(){// check if RPSLSServer class and if it's working properly 
		assertEquals("RPSLSServer",server.getClass().getName(),"did not initialize properly");
	}
	@Test
	void testInit_Server(){//check if the server is working properly
		assertEquals("Server",test.getClass().getName(),"did not initialize properly");
	}
	@Test
	void testGameInfoVariables(){ // test the plays of each players
		assertEquals("////", gameInfo.data);
	}
//
	@Test
	void test_ClientThread_Socket(){
		assertEquals("client #1", test.clientName);
		// check the moves of the players in the game
	}
//
	@Test
	void testInit_C1()
	{
		gameInfo = new GameInfo();
		gameInfo.data = "hello";//
		assertEquals(true,gameInfo.toString().startsWith("GameInfo"),"did not initialize properly");


	}
	@Test
	void test_S2(){// check the data and if it's working properly
		gameInfo.data = "data checking";
		assertEquals("data checking", gameInfo.data);
	}

}




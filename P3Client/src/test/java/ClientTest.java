import static org.junit.jupiter.api.Assertions.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {

	Consumer<Serializable> callback;
//	Socket socket;
//	ClientThread thread;
	String p1move, p2move;
	GameInfo info;
//	Server check;
	Client check;


	@BeforeEach
	void init() {

		info = new GameInfo(); // the GameInfo
		check = new Client(callback, 5555, "127.0.0.1"); // the port and IP 

	}

	//checking callback null case
	@Test
	void callBackTest()
	{
		assertNull(callback);
	}

	//checking server connection
	@Test
	void testPlayers() { // check if the clients has two players to play
		assertEquals(false, info.have2Players, "wrong");
	}

    @Test
    void testS() { // test the client port
        assertEquals(5555, check.port, "wrong");
    }

    @Test
    void testS2() { // check if there are 
        assertEquals(true, check.clients.isEmpty(), "wrong");
    }

    @Test
    void testInit_S4(){
		check.info.data = "data sent";
		check.info.data = "data recieved";
		assertEquals("data recieved", check.info.data, "wrong");
    }

    @Test
    void testInit_S3(){ // check if the Client class is working
        assertEquals("Client",check.getClass().getName(),"did not initialize properly");
    }

    @Test
	void testInit_C1()
	{
		info = new GameInfo();
		info.data = "hello";//
		assertEquals(true,info.toString().startsWith("GameInfo"),"did not initialize properly");


	}





}

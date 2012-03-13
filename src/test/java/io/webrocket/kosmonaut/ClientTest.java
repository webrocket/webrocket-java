package io.webrocket.kosmonaut;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.HashMap;

public class ClientTest extends TestCase{
    private Client client;
    private String uid;
    
    public ClientTest(String testName){
        super(testName);
        client = new Client("wr://51143c719c576f4018eef1a4f3c505a490ecf4ec@127.0.0.1:8081/test");
    }

    public static Test suite(){
        return new TestSuite(ClientTest.class);
    }
    
    public void testOpenChannel(){
        String response = client.openChannel("test");;
        assertEquals("0", response);
    }
    
    public void testBroadcast(){
        HashMap<String, String> data = new HashMap<String,String>();
        data.put("content", "Hello WebRocket!");
        String response = client.broadcast("test", "message", data);
        assertEquals("0", response);
    }
    
    public void testCloseChannel(){
    	String response = client.closeChannel("test");
    	assertEquals("0", response);
    }
    
}
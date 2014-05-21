package kvstore;
import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.Thread;
import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class MultipleClientTest {
    ServerRunner serverRunner;
    String hostname;
    @Before
    public void setUp() throws IOException, InterruptedException {
    	hostname = InetAddress.getLocalHost().getHostAddress();
        SocketServer ss = new SocketServer(hostname, 8080);
        ss.addHandler(new ServerClientHandler(new KVServer(100, 10)));
        serverRunner = new ServerRunner(ss, "server");
        serverRunner.start();
    }
    
	@Test
    public void testPutGetInTheSameProcess() throws KVException {
	    KVClient client1, client2;
        client1 = new KVClient(hostname, 8080);
        client2 = new KVClient(hostname, 8080);
        client1.put("foo", "bar");
        assertEquals(client2.get("foo"), "bar");       
    }
	
	@Test
    public void testPutGet1() throws KVException {
	    KVClient client;
		Thread thread1 = new Thread(
				new Runnable() {
					public void run() {
						KVClient client;
						try {
					        client = new KVClient(hostname, 8080);
					        client.put("foo", "bar");
						} catch (KVException e) {
							e.printStackTrace();
						}
					}
				});
		thread1.start();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        client = new KVClient(hostname, 8080);
        assertEquals(client.get("foo"), "bar");  
    }
	
	@Test
    public void testPutGet2() throws KVException {
	    KVClient client;
		Thread thread1 = new Thread(
				new Runnable() {
					public void run() {
						KVClient client;
						try {
							Thread.sleep(500);
					        client = new KVClient(hostname, 8080);
					        client.put("foo", "bar");
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (KVException e) {
							e.printStackTrace();
						}
					}
				});
		thread1.start();

        client = new KVClient(hostname, 8080);
        assertEquals(client.get("foo"), null);  
    }
	
	@Test
    public void threeThreads() throws KVException {
	    KVClient client  = new KVClient(hostname, 8080);
		Thread thread1 = new Thread(
				new Runnable() {
					public void run() {
						KVClient client;
						try {
					        client = new KVClient(hostname, 8080);
					        for (int i = 0; i < 10; i = i+2 ) {
					        	client.put("key", Integer.toString(i));
					        	//System.out.println("thread "+Integer.toString(i));
					        	//System.out.println("thread get "+client.get("key"));
					        	Thread.sleep(200);
					        }
						} catch (KVException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
		
		Thread thread2 = new Thread(
				new Runnable() {
					public void run() {
						KVClient client;
						try {
					        client = new KVClient(hostname, 8080);
							Thread.sleep(100);
					        for (int i = 1; i < 10; i = i+2 ) {
					        	client.put("key", Integer.toString(i));
					        	//System.out.println("thread "+ Integer.toString(i));
					        	//System.out.println("thread get "+client.get("key"));
					        	Thread.sleep(200);
					        }
						} catch (KVException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
		String value = "";
		int count = 0;
		thread1.start();
		thread2.start();
        while (true) {
        	try {
				Thread.sleep(10);
			} catch (InterruptedException e) { 
				e.printStackTrace();
			}
        	String tmp = client.get("key");
        	//System.out.println("main "+tmp);
        	if (tmp!=null && !value.equals(tmp)) {
        		value = tmp;
        		++count;
        		//// System.out.println(value);
        	}
        	if (count == 10) break;
        }
    }
	
	
    @After
    public void tearDown() throws InterruptedException {
        serverRunner.stop();
    }

}
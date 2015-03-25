package org.cld.rpc;
	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
	
public class Server implements Hello {
	
    public Server() {}

    public String sayHello() {
	return "Hello, world!";
    }
	
    public static void main(String args[]) {
    	try {
	
    		System.setProperty("java.rmi.server.hostname", "127.0.0.1");
    		System.err.println("java.rmi.server.hostname:" + System.getProperty("java.rmi.server.hostname"));
    		Server obj = new Server();
 		    Hello stub = (Hello) UnicastRemoteObject.exportObject(obj,0);
 		    System.err.println("after export object");
 		    
    		// Bind the remote object's stub in the registry
		    //Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
 		    Registry registry = LocateRegistry.createRegistry(1099); 
		    System.err.println("after get registry");
		    
		   
		    registry.rebind("Hello", stub);
		    System.err.println("after bind hello");
	
		    System.err.println("Server ready");
		} catch (Exception e) {
		    System.err.println("Server exception: " + e.toString());
		    e.printStackTrace();
		}
    }
}

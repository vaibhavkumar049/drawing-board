
import java.net.*;
import java.io.*;
import java.util.*;

public class server {
    //Vector to store client
    static Vector<ClientHandler> ar = new Vector<>();

    //Counter for client
    static int i=0;
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(50002);

        Socket s;
        while (true){
            s=ss.accept();
            System.out.println("New Connection accepted " + s);
            // DataInputStream dis=new DataInputStream(s.getInputStream());
            // DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            System.out.println("Creating a new Handler for the client");
            // Create a new handler object for handling this request.
            ClientHandler mtch= new ClientHandler (s,"client"+i);

            //Adding this client to actve site list
            System.out.println("Adding this client to active client list");

            ar.add(mtch);

            i++;

        }
    }
}

class ClientHandler implements Runnable{
    private int currentX,currentY,oldX,oldY;
    DataOutputStream dos;
    DataInputStream dis;
    Socket s;
    boolean isloggedin;
    private String name;

    //
    public ClientHandler(Socket s,String name)throws IOException{
    	this.s=s;
        this.dos=new DataOutputStream(s.getOutputStream());
        this.dis=new DataInputStream(s.getInputStream());
        this.isloggedin=true;
        this.name=name;
        new Thread(this, name).start();
    }
    public void run(){
        while(true) {
            try {
                oldX = dis.readInt();
                oldY = dis.readInt();
                currentX = dis.readInt();
                currentY = dis.readInt();
               	System.out.println(oldX+":"+oldY+":"+currentX+":"+currentY);
//               if (oldX!=0 && oldY!=0 && currentX!=0 && currentY!=0)
//                   break;
               	// System.out.println(this.dos);
                for (ClientHandler mc : server.ar) {
                	// System.out.println(mc.dos);
                    mc.dos.writeInt(oldX);
                    mc.dos.writeInt(oldY);
                    mc.dos.writeInt(currentX);
                    mc.dos.writeInt(currentY);
                }
            } catch (Exception e) {
                System.out.println("Closing Connection : " + this.s);
                server.ar.remove(this);
                break;
            }
        }
       try {
           this.dis.close();
           this.dos.close();

       }catch (IOException e){
           e.printStackTrace();
       }
    }

}
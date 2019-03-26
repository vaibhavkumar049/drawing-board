
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
            DataInputStream dis=new DataInputStream(s.getInputStream());
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            System.out.println("Creating a new Handler for the client");
            // Create a new handler object for handling this request.
            ClientHandler mtch= new ClientHandler (s,"client"+i,dis,dos);

            //Adding this client to actve site list
            System.out.println("Adding this client to active client list");

            ar.add(mtch);

            i++;

        }
    }
}

class ClientHandler implements Runnable{
    private int currentX,currentY,oldX,oldY;
    final DataOutputStream dos;
    final DataInputStream dis;
    Socket s;
    boolean isloggedin;
    private String name;

    //
    public ClientHandler(Socket s,String name,DataInputStream dis,DataOutputStream dos){
        this.dos=dos;
        this.dis=dis;
        this.isloggedin=true;
        this.s=s;
        this.name=name;
        new Thread(this).start();
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
                for (ClientHandler mc : server.ar) {
                    mc.dos.writeInt(oldX);
                    mc.dos.writeInt(oldY);
                    mc.dos.writeInt(currentX);
                    mc.dos.writeInt(currentY);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        try {
//            this.dis.close();
//            this.dos.close();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
    }

}
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.*;
import java.net.*;

public class board extends JComponent {
    private Image img;
    private Graphics2D g2;
    private int currentX,currentY,oldX,oldY;
    //change
    final static int ServerPort = 50002;
    public board() {
        setDoubleBuffered(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                oldX= mouseEvent.getX();
                oldY =mouseEvent.getY();
//                System.out.println(oldX);
//                System.out.println(oldY);

            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                currentX=mouseEvent.getX();
                currentY=mouseEvent.getY();
//                System.out.println(currentX);
               System.out.println(currentY);
               if(g2!=null){
                   g2.drawLine(oldX,oldY,currentX,currentY);
                    repaint();
                    oldX=currentX;
                    oldY=currentY;
                }
            }
        });
    }
    void paint(int oldX,int oldY,int currentX,int currentY){
        g2.drawLine(oldX,oldY,currentX,currentY);
        repaint();

    }
    protected void paintComponent(Graphics g){
        if(img==null){
            img=createImage(getSize().width,getSize().height);
            g2=(Graphics2D) img.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//            clear();
        }
        g.drawImage(img,0,0,null);
    }

    public static void main(String[] args){
        JFrame frame=new JFrame("Drawing board");
        Container content=frame.getContentPane();
        content.setLayout(new BorderLayout());
        board mdrawArea = new board();

//        DataInputStream dis;
//        DataOutputStream dos;
        //change it from here
        // getting localhost ip
        try {
            InetAddress ip = InetAddress.getByName("localhost");

            // establish the connection
            Socket s = new Socket(ip, ServerPort);

            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            Thread sendCoord = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        board drawArea = new board();
                        try {
                            System.out.println(drawArea.currentX);
                            System.out.println(drawArea.currentY);
//                            if(drawArea.g2!=null){
//                                drawArea.g2.drawLine(drawArea.oldX,drawArea.oldY,drawArea.currentX,drawArea.currentY);
//                                drawArea.repaint();
//                                drawArea.oldX=drawArea.currentX;
//                                drawArea.oldY=drawArea.currentY;
//                            }

                            dos.writeInt(drawArea.oldX);
                            dos.writeInt(drawArea.oldY);
                            dos.writeInt(drawArea.currentX);
                            dos.writeInt(drawArea.currentY);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            Thread rcvCoord = new Thread(new Runnable() {
                @Override
                public void run() {
                    board drawArea = new board();
                    try{
                        if (drawArea.g2 != null) {

                            int oldx = dis.readInt();
                            int oldy=dis.readInt();
                            int currentx = dis.readInt();
                            int currenty=dis.readInt();
                            drawArea.paint(oldx,oldy,currentx,currenty);
                            content.add(drawArea,BorderLayout.CENTER);
                            frame.setSize(600,600);
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setVisible(true);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            rcvCoord.start();
            sendCoord.start();
        }catch (IOException e){
            e.printStackTrace();
        }
        content.add(mdrawArea,BorderLayout.CENTER);
        frame.setSize(600,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }
}

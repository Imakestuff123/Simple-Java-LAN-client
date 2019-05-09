import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.net.ServerSocket;
import java.util.ArrayList;
public class Server
{
    //http://cs.lmu.edu/~ray/notes/javanetexamples/
    ArrayList <ClientReader> Clients;
    public Server() {
        Clients = new ArrayList<ClientReader>();
    }
    public void BroadcastMessage(String Sender, String Output) {
        for (int i = 0; i <= Clients.size() - 1; i++) {
            ClientReader currentclient = Clients.get(i);
            Socket currentsocket = currentclient.getSocket();
            if (currentclient.getclientNumber() != -1) {
                try {
                    PrintWriter out = new PrintWriter(currentsocket.getOutputStream(), true);
                    out.println(Sender + ": " + Output);
                } catch (IOException e) {
                    
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.print('\u000C');
        Server main = new Server();
        int clientNumber = 0;
        try (ServerSocket listener = new ServerSocket(8888)) {
            System.out.println(listener.getInetAddress().getLocalHost());
            ServerInput MainServerInput = new ServerInput(main);
            MainServerInput.start();
            while (true) {
               ClientReader S = new ClientReader(listener.accept(), clientNumber++, main);
               S.start();
               main.Clients.add(S);
               
            }
        }
    }
    
    public static class ClientReader extends Thread {
        private Socket socket;
        private int clientNumber;
        private Server parent;
        public ClientReader(Socket socket, int clientNumber, Server parent) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            this.parent = parent;
            
        }
        public Socket getSocket() {
            return socket;
        }
        public int getclientNumber() {
            return clientNumber;
        }
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Client: " + clientNumber);
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.isEmpty()) {
                        break;
                    } 
                    System.out.println("Client" + clientNumber + ": " + input);
                    parent.BroadcastMessage("You are Client" + clientNumber, input);
                }
                socket.close();
                System.out.println("Client" + clientNumber + " has quit");
                ClientReader Blank = new ClientReader(socket, -1, parent);
                parent.Clients.set(clientNumber, Blank);
            } catch (IOException e) {
                
            } finally {

            }
        }
        
    }
    public static class ServerInput extends Thread {
        private Server parent;
        private int Broadcast;
        private String Command;
        private String InputLine;
        public ServerInput(Server parent) {
            this.parent = parent;
            Broadcast = 0;
        }
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                Command = "-1";
                InputLine = "-1"; 
                for (int i = 0; i <= input.length() - 1; i++) {
                    if (input.charAt(i) == (' ')) {
                        Command = input.substring(0, i);
                        InputLine = input.substring(i + 1, input.length());
                        break;
                    }
                }
                //System.out.println(Command);
                //System.out.println(InputLine);
                
                //REmember to type a space after command if you want single commands
                if (!InputLine.equals("-1")) {
                    switch (Command.toLowerCase()) {
                        case "help": 
                            System.out.println("List of Commands");
                            System.out.println("- Clients");
                            System.out.println("- Stop");
                            System.out.println("- Broadcast (Message)");
                            break;
                        case "clients":
                            for (int i = 0; i <= parent.Clients.size() - 1; i++) {
                                ClientReader CurrentClient = parent.Clients.get(i);
                                System.out.println("Client " + CurrentClient.clientNumber);
                            }
                            break;
                        case "stop":
                            for (int i = 0; i <= parent.Clients.size() - 1; i++) {
                                ClientReader CurrentClient = parent.Clients.get(i);
                                try {
                                    CurrentClient.getSocket().close();
                                } catch (IOException e) {
                                    
                                }
                            }
                            System.exit(0);
                            break;
                        case "broadcast":
                            parent.BroadcastMessage("Server", InputLine);
                            break;
                    }
                }
            }
        }
    }
}

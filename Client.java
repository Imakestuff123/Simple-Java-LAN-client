import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
public class Client
{
    public static void main(String[] args) throws Exception {
        System.out.print('\u000C');
        System.out.println("Enter the IP address of a server");
        String serverAddress = new Scanner(System.in).nextLine();
        Socket socket = new Socket(serverAddress, 8888);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        ServerReader S = new ServerReader(socket);
        S.start();
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input == null || input.isEmpty()) {
                break;
            }
            out.println(input);
        }
        socket.close();
    }
    
    public static class ServerReader extends Thread {
        private Socket socket;
        public ServerReader(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    String input = in.readLine();
                    System.out.println(input);
                }
            } catch (IOException e) {
                
            }
        }
    }
}

package project;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Player {
    Socket player;
    boolean flag1 = false;
    public Player(){
        try {
            // connection to server on port 2000
            player = new Socket ( "127.0.0.1", 2000);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(player.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(player.getOutputStream()));
            // read from the server
            System.out.println(bufferedReader.readLine());
            Scanner s = new Scanner(System.in);
            // entering name
            String name = s.nextLine();
            //writing to server
            bufferedWriter.write(name+"\n");
            bufferedWriter.flush();
            // read from the server as long as the game is on
            while (true) {
                String x = bufferedReader.readLine();
                if(x!=null) {
                    System.out.println(x);
                }
                if (x.equals("Please enter a letter or make a guess")) {// if server asks for input
                    String guess = s.nextLine(); // enter the input
                    while (guess.length() == 0) {// if he missed the input, like an empty input then keep asking for input
                        System.out.println("Please enter a letter or make a guess");
                        guess = s.nextLine();
                    }
                    bufferedWriter.write(guess + "\n");
                    bufferedWriter.flush();
                }
                if (x.contains("Win")) { // if the string contains, win then finish the game
                    System.out.println("Game Over");
                    flag1 = true;
                    break;
                }
            }
        }catch (NullPointerException N){
            System.out.println("The Other Player Has Disconnected");
            System.out.println("Congratulations: You Win");
        }catch (SocketException s){
            if (!(player==null)){
                System.out.println("The Other Player Has Disconnected");
                System.out.println("Congratulations: You Win");
            }
            if (!flag1) System.out.print("No Service!");
        }
        catch (IOException e) {
            System.out.println("IO Error");
        }

    }
    public static void main(String[] args) {
        //create a player
        Player player = new Player();
    }
}
package project;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Server implements Runnable {
    //declare a server-socket
    public static ServerSocket server;
    // declare a shared variable between threads
    public static volatile boolean flag = false;
    //initialize the port to 2000
    public static final int SERVICE_PORT = 2000;
    private static String[] genres;
    private static String[] words;

    public Server() {
        //initialize a server-socket
        try {
            server = new ServerSocket(SERVICE_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //split the data to genres array and words array
        genres = getMyArrays()[0];
        words = getMyArrays()[1];
    }

    public static void connect() {
        try {
            System.out.println("Waiting for two players to connect");
            // accept a player
            Socket nextClient = server.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(nextClient.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(nextClient.getOutputStream()));
            bufferedWriter.write("Please enter your name:\n");
            bufferedWriter.flush();
            String playerOneName = bufferedReader.readLine();
            System.out.println(playerOneName + " Connected... waiting for player 2 to connect");
            bufferedWriter.write(playerOneName + " has joined\n");
            bufferedWriter.flush();
            bufferedWriter.write("Waiting for another player to join\n");
            bufferedWriter.flush();
            //accept player 2
            Socket nextClient1 = server.accept();
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(nextClient1.getInputStream()));
            BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(nextClient1.getOutputStream()));
            bufferedWriter1.write("Please enter your name:\n");
            bufferedWriter1.flush();
            String playerTwoName = bufferedReader1.readLine();
            System.out.println(playerTwoName + " Connected...\nStarting Game...");
            //assign the shared variable to true to accept multiple games at the same time
            flag = true;
            bufferedWriter.write(playerTwoName + " has joined\n");
            bufferedWriter1.write(playerOneName + " has joined\n");
            bufferedWriter1.write(playerTwoName + " has joined\n");
            bufferedWriter.flush();
            bufferedWriter1.flush();
            bufferedWriter.write("Starting Game...\n");
            bufferedWriter1.write("Starting Game...\n");
            bufferedWriter.flush();
            bufferedWriter1.flush();
            // get a random word
            String[] arr = generateHidden(genres, words);
            String hidden = arr[0];
            String genre = arr[1];
            String word = arr[2];
            char[] hiddenLettersOfWord = hidden.toCharArray();
            char[] lettersOfWord = word.toCharArray();
            ArrayList<Character> incorrectLetters = new ArrayList<Character>();
            // make a random player starts the game
            Random rand = new Random();
            int random = rand.nextInt(2);
            if (random == 0) {
                round(bufferedReader, bufferedReader1, bufferedWriter, bufferedWriter1, playerOneName, playerTwoName, genre, word, lettersOfWord, hiddenLettersOfWord, incorrectLetters, nextClient, nextClient1);
            } else {
                round(bufferedReader1, bufferedReader, bufferedWriter1, bufferedWriter, playerTwoName, playerOneName, genre, word, lettersOfWord, hiddenLettersOfWord, incorrectLetters, nextClient1, nextClient);
            }
        } catch (SocketException s) {
            // if we lose connection either start a new thread for 2 new players or just skip if the thread already started
            System.out.println("Connection lost ... Terminating Game");
            if(!flag)
                flag = true;
            Thread.currentThread().stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] generateHidden(String[] genres, String[] words) {
        String genre;
        String word;
        Random rand = new Random();
        int y = rand.nextInt(words.length);
        genre = genres[y];
        word = words[y];
        // hiding the word
        char[] lettersOfWord = new char[word.length()];
        char[] hiddenLettersOfWord = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            lettersOfWord[i] = word.charAt(i);
            if (lettersOfWord[i] != ' ') {
                hiddenLettersOfWord[i] = '-';
            } else {
                hiddenLettersOfWord[i] = ' ';
            }
        }
        return new String[]{new String(hiddenLettersOfWord), genre, word};
    }

    public static String[][] getMyArrays() {
        String[] genres;
        String[] words;
        try {
            FileInputStream in;
            in = new FileInputStream("hangman.out");
            ObjectInputStream s = new ObjectInputStream(in);
            ArrayList AnObject = (ArrayList) s.readObject();
            String A = AnObject.toString();
            String A1 = A.replaceAll("\\[", "").replaceAll("\\]", "");
            String[] A2 = A1.split("[-,-]");
            String[] A3 = new String[A2.length];
            int x = 0;
            for (String e : A2) {
                A3[x] = e.trim();
                x++;
            }
            genres = new String[A3.length / 2];
            words = new String[A3.length / 2];
            for (int i = 0, j = 0; i < A3.length; j++) {
                genres[j] = A3[i];
                words[j] = A3[i + 1];
                i += 2;
            }

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
        return new String[][]{genres, words};
    }

    public static boolean guessLetter(char letter_guess, char[] lettersOfWord, char[] hiddenLettersOfWord, ArrayList<Character> incorrectLetters) {
        boolean guess = false;
        for (int i = 0; i < lettersOfWord.length; i++) {
            if (Character.toLowerCase(lettersOfWord[i]) == Character.toLowerCase(letter_guess)) {
                hiddenLettersOfWord[i] = lettersOfWord[i];
                guess = true;
            }
        }
        if (!guess) {
            if (!incorrectLetters.contains(Character.toLowerCase(letter_guess))) {
                incorrectLetters.add(letter_guess);
            }
        }
        return guess;
    }

    public static boolean guessWord(String word_guess, String word) {
        return word.equalsIgnoreCase(word_guess);
    }

    public static void round(BufferedReader p1r, BufferedReader p2r, BufferedWriter p1w, BufferedWriter p2w, String playerOneName, String playerTwoName, String genre, String word, char[] lettersOfWord, char[] hiddenLettersOfWord, ArrayList<Character> incorrectLetters, Socket nextClient, Socket nextClient1) {
        try {
            //write to current player
            p1w.write("Genre: " + genre + "\n");
            p1w.write(new String(hiddenLettersOfWord) + "\n");
            p1w.write("Incorrect Letters: " + incorrectLetters + "\n");
            p1w.write("Its your turn\n");
            p1w.write("Please enter a letter or make a guess\n");
            p1w.flush();
            p2w.write("Genre: " + genre + "\n");
            p2w.write(new String(hiddenLettersOfWord) + "\n");
            p2w.write("Incorrect Letters: " + incorrectLetters + "\n");
            p2w.write("Its " + playerOneName + " turn\n");
            p2w.flush();
            String playerGuess = p1r.readLine();
            if (playerGuess.length() != 1 && guessWord(playerGuess, word)) { // if the guess is a word and is right
                //current player wins the game
                p1w.write("The Right Word is \"" + word + "\"\n");
                p1w.flush();
                p1w.write("Congratulations: You Win" + "\n");
                p1w.flush();
                p2w.write("The Right Word is \"" + word + "\"\n");
                p2w.flush();
                p2w.write("Better Luck Next Time ... " + playerOneName + " Wins " + "\n");
                p2w.flush();
                nextClient.close();
                nextClient1.close();
                return;
            } else if (playerGuess.length() == 1) {
                if (guessLetter(playerGuess.charAt(0), lettersOfWord, hiddenLettersOfWord, incorrectLetters)) {//if the guess is letter and is right
                    if (Arrays.equals(hiddenLettersOfWord, lettersOfWord)) {//if he guesses the last hidden letter
                        //current player wins the game
                        p1w.write("The Right Word is \"" + word + "\"\n");
                        p1w.flush();
                        p1w.write("Congratulations: You Win" + "\n");
                        p1w.flush();
                        p2w.write("The Right Word is \"" + word + "\"\n");
                        p2w.flush();
                        p2w.write("Better Luck Next Time ... " + playerOneName + " Wins " + "\n");
                        p2w.flush();
                        nextClient.close();
                        nextClient1.close();
                        return;
                    }// if it is not the last hidden, ask him to enter another letter
                    round(p1r, p2r, p1w, p2w, playerOneName, playerTwoName, genre, word, lettersOfWord, hiddenLettersOfWord, incorrectLetters, nextClient, nextClient1);
                } else {
                    p1w.write("Wrong guess\n");
                    p1w.flush();
                }
                //if it is wrong letter guess , then ask the other player to make a guess
                round(p2r, p1r, p2w, p1w, playerTwoName, playerOneName, genre, word, lettersOfWord, hiddenLettersOfWord, incorrectLetters, nextClient1, nextClient);
            } else {
                p1w.write("Wrong guess\n");
                p1w.flush();
            }
            //if it is a wrong word guess , then ask the other player to make a guess
            round(p2r, p1r, p2w, p1w, playerTwoName, playerOneName, genre, word, lettersOfWord, hiddenLettersOfWord, incorrectLetters, nextClient1, nextClient);
        } catch (IOException s) {
            try {
                nextClient.close();
                nextClient1.close();
                Thread.currentThread().stop();
            } catch (IOException e) {
                System.out.println("IO Error");
            }
        }
    }
    @Override
    public void run() {
        Server.connect();
    }

    public static void main(String[] args) {
        // start a server
        Server server = new Server();
        // start a thread
        Thread thread1 = new Thread(server);
        thread1.start();
        // as long as the flag is true, then start a thread then make it false again and wait for the connection to force it to false again
        while (true) {
            if (flag) {
                Thread thread = new Thread(server);
                thread.start();
                flag = false;
            }
        }
    }
}


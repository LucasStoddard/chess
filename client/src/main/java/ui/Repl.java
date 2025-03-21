package ui;
import com.sun.tools.javac.Main;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl{
    private GameClient gameClient;
    private LoginClient loginClient;
    private MainClient mainClient;
    private boolean loggedIn;
    private boolean gameMode;

    public Repl(String serverUrl) {
        gameClient = new GameClient(serverUrl, this);
        loginClient = new LoginClient(serverUrl, this);
        mainClient = new MainClient(serverUrl, this);
        loggedIn = false;
        gameMode = false;
    }

    public void run() {
        System.out.println(WHITE_KING + " Welcome to Chess, a game that exists. Sign in to start. " + WHITE_QUEEN);
        System.out.print(loginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        // This will go loginClient -> mainClient <-> gameClient
        // This run loop will need to be adjusted so that it appropriately changes clients
        // This would probably involve checking the result and if it involves logging in, game options, or logging out
        // it will transition which client is using eval on the lines that are scanned in.

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (loggedIn && !gameMode) {
                    result = mainClient.eval(line);
                } else if (gameMode) {
                    result = gameClient.eval(line);
                } else {
                    result = loginClient.eval(line);
                }

                System.out.print(BLUE + result);
                if (result.contains("Welcome back")) { // need to make sure things are updated accordingly here
                    loggedIn = true;
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public void notify(String notification) {
        System.out.println(RED + notification);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
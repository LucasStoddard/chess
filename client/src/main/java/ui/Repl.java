package ui;
import com.sun.tools.javac.Main;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl{
    private LoginClient loginClient;
    private MainClient mainClient;
    // private GameClient gameClient;
    private boolean loggedIn;
    private boolean gameMode;

    public Repl(ServerFacade serverFacade) {
        loginClient = new LoginClient(serverFacade);
        mainClient = new MainClient(serverFacade);
        // gameClient = new GameClient(loginClient.getServerFacade());
        loggedIn = false;
        gameMode = false;
    }

    public void run() {
        System.out.println(WHITE_KING + " Welcome to Chess, a game that exists. Sign in to start. " + WHITE_QUEEN);
        System.out.print(loginClient.help() + "\n");

        Scanner scanner = new Scanner(System.in);
        var result = "";

        // This will go loginClient <-> mainClient

        // For now no gameClient is needed

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (loggedIn && !gameMode) {
                    result = mainClient.eval(line);
                //} else if (gameMode) {
                    //result = gameClient.eval(line);
                } else {
                    result = loginClient.eval(line);
                }

                if (result.contains("help")) {
                    System.out.println(result);
                } else {
                    System.out.print(SET_TEXT_COLOR_CYAN + result);
                }

                if (result.contains("Welcome back") || result.contains("newfangled game")) {
                    loggedIn = true;
                    result += "\n" + mainClient.help();
                //} else if (result.contains("...")) {
                    //gameMode = true;
                } else if (result.contains("Successfully logged out")) {
                    loggedIn = false;
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
package ui;
import com.sun.tools.javac.Main;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private LoginClient loginClient;
    private MainClient mainClient;
    private boolean loggedIn;

    public Repl(ServerFacade serverFacade) {
        loginClient = new LoginClient(serverFacade);
        mainClient = new MainClient(serverFacade);
        gameClient = new GameClient(serverFacade);
        loggedIn = false;
    }

    public void run() {
        System.out.println("\n" + SET_TEXT_BOLD + WHITE_QUEEN +
                " Welcome to Chess, a game that exists. Sign in to start. " + WHITE_QUEEN + RESET_TEXT_BOLD_FAINT);
        System.out.print(loginClient.help() + "\n");

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("Quitting")) { // gameClient to be implemented later, not needed for now
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (loggedIn) {
                    result = mainClient.eval(line);
                } else {
                    result = loginClient.eval(line);
                }

                if (result.contains("help")) {
                    System.out.println(result);
                } else if (result.contains("Error")) {
                    System.out.print(SET_TEXT_COLOR_RED + result);
                } else {
                    System.out.print(SET_TEXT_COLOR_CYAN + result);
                }

                if (result.contains("Welcome back") || result.contains("newfangled game")) {
                    loggedIn = true;
                    System.out.println("\n" + mainClient.help());
                } else if (result.contains("Successfully logged out")) {
                    loggedIn = false;
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
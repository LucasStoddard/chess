package ui;
import com.sun.tools.javac.Main;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl{
    private GameClient gameClient;
    private LoginClient loginClient;
    private MainClient mainClient;

    public Repl(String serverUrl) {
        gameClient = new GameClient(serverUrl, this);
        loginClient = new LoginClient(serverUrl, this);
        mainClient = new MainClient(serverUrl, this);
    }

    public void run() {
        System.out.println(WHITE_KING + " Welcome to Chess, a game that exists. Sign in to start. " + WHITE_QUEEN);
        System.out.print(mainClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        // This run loop will need to be adjusted so that it appropriately changes clients
        // This would probably involve checking the result and if it involves logging in, game options, or logging out
        // it will transition which client is using eval on the lines that are scanned in.

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
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
package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    String color;

    public ConnectCommand(String authToken, Integer gameID, String color) {
        super(CommandType.CONNECT, authToken, gameID);
    }

    public String getColor() {
        return color;
    }
}

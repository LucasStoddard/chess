package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    String color;

    public ConnectCommand(String authToken, Integer gameID, String newColor) {
        super(CommandType.CONNECT, authToken, gameID);
        color = newColor;
    }

    public String getColor() {
        return color;
    }
}

package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String error;

    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        error = message;
    }

    public String getMessage() {
        return error;
    }
}

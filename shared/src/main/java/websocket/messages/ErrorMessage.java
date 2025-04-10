package websocket.messages;

public class ErrorMessage extends ServerMessage {
    String errorMessage;

    public ErrorMessage(String message) {
        super(ServerMessageType.ERROR);
        errorMessage = message;
    }

    public String getMessage() {
        return errorMessage;
    }
}

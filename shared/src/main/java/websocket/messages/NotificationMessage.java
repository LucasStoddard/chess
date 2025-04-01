package websocket.messages;

import websocket.commands.UserGameCommand;

public class NotificationMessage extends ServerMessage {
    String notification;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        notification = message;
    }

    public String getMessage() {
        return notification;
    }
}

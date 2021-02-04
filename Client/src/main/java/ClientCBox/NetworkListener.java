package ClientCBox;

import CoreCBox.CommandMessage;

public interface NetworkListener {
    public boolean isAuth (boolean auth, CommandMessage message);
}

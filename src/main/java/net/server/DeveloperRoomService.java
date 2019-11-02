package net.server;

public class DeveloperRoomService {
    private static final DeveloperRoomService instance = new DeveloperRoomService();
    public static DeveloperRoomService getInstance() {return instance;}

    private boolean availableDeveloperRoom = false;

    public void setAvailableDeveloperRoom() {
        availableDeveloperRoom = true;
    }

    @SuppressWarnings({"unused", "WeakerAccess"}) // used by event script
    public boolean canEnterDeveloperRoom() {
        return availableDeveloperRoom;
    }
}

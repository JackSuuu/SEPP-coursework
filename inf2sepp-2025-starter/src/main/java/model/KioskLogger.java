package model;

import org.tinylog.Logger;

import java.time.LocalDateTime;


public class KioskLogger {

    static KioskLogger logger;

    public static KioskLogger getInstance(){
        if(logger == null){
            logger = new KioskLogger();
        }
        return logger;
    }
    
    private KioskLogger() {
    }
    
    public void log(String email, String userID, String actionName, String status){
        LocalDateTime currentTime = LocalDateTime.now();
        Logger.info("LOGGER:" + "time: " + currentTime + "\nemail:" + email + "\nuserID: " + userID + "\n" 
            + actionName + "\nStatus: " + status);
    }
}

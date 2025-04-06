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
    public void log(LocalDateTime time, String userID, String actionName, String inputs, String status){
        Logger.info("time: " + time + "\nuserID: " + userID + "\n" +actionName + "\ninputs: " + inputs + "\nStatus:" + status);
    }


}

package log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
    private static Logger logger = Logger.getLogger("Mickey_jersey_log");
    public static void run(String s, Level level){
        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler("D:/MyLogs/Mickey_jersey_log.log",true);
            logger.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.log(level,s);
        }catch (Exception e){
            MyLogger.exceptionLogger(e);
        }
    }
    public static void exceptionLogger(Exception e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        MyLogger.run(sw.toString(),Level.SEVERE);
    }

}


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RobotChecker {
    public static boolean robotSafe(URL url)
    {
        String strHost = url.getHost();

        String strRobot = "http://" + strHost + "/robots.txt";
        System.out.println(strRobot);
        URL urlRobot;
        try { urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            // something weird is happening, so don't trust it
            return false;
        }

        String strCommands="";
        try
        {
            InputStream urlRobotStream = urlRobot.openStream();
//            System.out.println(urlRobotStream);
            byte b[] = new byte[1000];
            int numRead = urlRobotStream.read(b);
//            for (int i = 0; i < b.length; i++) {
//                System.out.println(Byte.toString(b[i]));
//            }
            if(numRead!=-1)
                strCommands = new String(b, 0, numRead);
            while (numRead != -1) {
                numRead = urlRobotStream.read(b);
                if (numRead != -1)
                {
                    String newCommands = new String(b, 0, numRead);
                    strCommands += newCommands;
                }
            }
            urlRobotStream.close();
        }
        catch (IOException e)
        {
            return true; // if there is no robots.txt file, it is OK to search
        }
//        System.out.println(strCommands);
        if (strCommands.contains("Disallow")) // if there are no "disallow" values, then they are not blocking anything.
        {
            String[] split = strCommands.split("\n");
            ArrayList<RobotRule> robotRules = new ArrayList<>();
            String mostRecentUserAgent = null;
            for (int i = 0; i < split.length; i++)
            {
                String line = split[i].trim();
                if (line.toLowerCase().startsWith("user-agent"))
                {
                    int start = line.indexOf(":") + 1;
                    int end   = line.length();
                    mostRecentUserAgent = line.substring(start, end).trim();
                }
                else if (line.startsWith("Disallow")) {
                    if (mostRecentUserAgent != null) {
                        RobotRule r = new RobotRule();
                        r.userAgent = mostRecentUserAgent;
                        int start = line.indexOf(":") + 1;
                        int end   = line.length();
                        r.rule = line.substring(start, end).trim();
                        robotRules.add(r);
                    }
                }
            }
//            for (RobotRule robotRule : robotRules) {
//                System.out.println(robotRule.rule+" "+robotRule.userAgent);
//            }
            for (RobotRule robotRule : robotRules)
            {
                String path = url.getPath();
                if (robotRule.rule.length() == 0) return true; // allows everything if BLANK
                if (robotRule.rule == "/") return false;       // allows nothing if /

                if (robotRule.rule.length() == path.length())
                {
                    if (path.equals(robotRule.rule)) return false;
                }
            }
        }
        return true;
    }
}
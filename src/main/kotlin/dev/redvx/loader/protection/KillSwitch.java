package dev.redvx.loader.protection;

import dev.redvx.loader.util.NoStackTrace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class KillSwitch {
    private static final String PASTEBIN_RAW_URL = "https://pastebin.com/raw/1mkhZ992";
    private static final String KILL_SWITCH_SIGNAL = "true";

    public static void check() {
        Timer timer = new Timer();
        timer.schedule(new KillSwitchTask(), 0, 5 * 60 * 1000);
    }

    private static class KillSwitchTask extends TimerTask {
        @Override
        public void run() {
            boolean shouldShutdown = checkKillSwitch();
            if (shouldShutdown) {
                System.out.println("Received kill switch signal. Shutting down the client...");
                try {
                    Method shutdownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", int.class);
                    shutdownMethod.setAccessible(true);
                    shutdownMethod.invoke(null, 0);
                } catch (Exception e) {
                    throw new NoStackTrace("Stop trying to crack the client.");
                }
            } else {
                System.out.println("Server is open");
            }
        }

        private boolean checkKillSwitch() {
            try {
                URL url = new URL(PASTEBIN_RAW_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        return KILL_SWITCH_SIGNAL.equals(reader.readLine().trim());
                    }
                } else {
                    System.out.println("Unexpected response code: " + responseCode);
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
    }
}
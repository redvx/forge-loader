package dev.redvx.loader.protection;

import dev.redvx.loader.util.Cryptography;
import dev.redvx.loader.util.NoStackTrace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.atomic.AtomicBoolean;

public class HWIDProtection {
    public static final AtomicBoolean hwid = new AtomicBoolean(false);

    public static void check() {
        try {
            URL url = new URL("https://pastebin.com/raw/RBVWejBz");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(encryptHWID(getHardWareID()))) {
                    hwid.set(true);
                    Authentication.auth();
                    break;
                } else {
                    try {
                        Method shutdownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", int.class);
                        shutdownMethod.setAccessible(true);
                        shutdownMethod.invoke(null, 0);
                    } catch (Exception e) {
                        throw new NoStackTrace("Stop trying to crack the client.");
                    }
                }
            }
        } catch (Exception e) {
            throw new NoStackTrace("Error contacting server");
        }
    }

    public static String getHardWareID() {
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            byte[] mac = network.getHardwareAddress();

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(mac);

            StringBuilder sb = new StringBuilder();
            for (byte b : md.digest()) {
                sb.append(String.format("%02X", b));
            }

            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String encryptHWID(String encode) {
        try {
            return Cryptography.encrypt(encode);
        } catch (Exception e) {
            throw new NoStackTrace("Error when encrypting Hardware ID");
        }
    }
}
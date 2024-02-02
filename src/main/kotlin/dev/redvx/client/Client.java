package dev.redvx.client;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.lang.reflect.Method;

@Mod(modid = Client.MOD_ID, name = Client.MOD_NAME, version = Client.VERSION)
public class Client {
    public static final String MOD_ID = "yourclient";
    public static final String MOD_NAME = "YourClient";
    public static final String VERSION = "1";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        try {
            Class<?> myClass = Class.forName("dev.redvx.loader.CoreMod");
            Method hasRunMethod = myClass.getMethod("hasRun");
            boolean invoked = (boolean) hasRunMethod.invoke(null);
            if (!invoked) {
                System.out.println("Authentication is invalid");
                Method shutdownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", int.class);
                shutdownMethod.setAccessible(true);
                shutdownMethod.invoke(null, 0);
            }
        } catch (Exception e) {
            throw new NoStackTraceCrash("Stop trying to crack the client");
        }
    }

    public static class NoStackTraceCrash extends RuntimeException {
        public NoStackTraceCrash(String msg) {
            super(msg);
            setStackTrace(new StackTraceElement[0]);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}

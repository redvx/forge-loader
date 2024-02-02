package dev.redvx.loader.antidump;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Based on code from both apangin and half-cambodian-hacker-man
 */
@SuppressWarnings("Duplicates")
public class StructDisassembler {
    private static final Unsafe unsafe = getUnsafe();
    private static Method findNative;
    private static ClassLoader classLoader;

    private static Unsafe getUnsafe() {
        try {
            java.lang.reflect.Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void resolveClassLoader() throws NoSuchMethodException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            String vmName = System.getProperty("java.vm.name");
            String dll = vmName.contains("Client VM") ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
            try {
                System.load(System.getProperty("java.home") + dll);
            } catch (UnsatisfiedLinkError e) {
                throw new RuntimeException(e);
            }
            classLoader = StructDisassembler.class.getClassLoader();
        } else {
            classLoader = null;
        }

        findNative = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);

        try {
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            unsafe.putObjectVolatile(cls, unsafe.staticFieldOffset(logger), null);
        } catch (Throwable ignored) {
        }

        findNative.setAccessible(true);
    }

    private static void setupIntrospection() throws Throwable {
        resolveClassLoader();
    }

    public static void disassembleStruct() {
        try {
            setupIntrospection();
            long entry = getSymbol();
            unsafe.putLong(entry, 0);
        } catch (Throwable t) {
            t.printStackTrace();
            CookieFuckery.Companion.shutdownHard();
        }
    }

    private static long getSymbol() throws InvocationTargetException, IllegalAccessException {
        long address = (Long) findNative.invoke(null, classLoader, "gHotSpotVMStructs");
        if (address == 0)
            throw new NoSuchElementException("gHotSpotVMStructs");

        return unsafe.getLong(address);
    }
}



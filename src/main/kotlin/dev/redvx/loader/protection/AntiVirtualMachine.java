package dev.redvx.loader.protection;

import dev.redvx.loader.util.NoStackTrace;

import java.net.InetAddress;
import java.net.NetworkInterface;

public class AntiVirtualMachine {

    public static boolean isRunningInsideVirtualMachine() {
        return isSystemPropertySet() || hasMacAddressPrefix();
    }

    private static boolean isSystemPropertySet() {
        return System.getProperty("java.vm.vendor").contains("VMWare")
                || System.getProperty("java.vm.name").contains("VirtualBox")
                || System.getProperty("java.vm.name").contains("Parallels")
                || System.getProperty("java.vm.name").contains("Xen")
                || System.getProperty("java.vm.name").contains("KVM");
    }

    private static boolean hasMacAddressPrefix() {
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            byte[] mac = network.getHardwareAddress();

            if (mac != null && mac.length > 1) {
                return (mac[0] == 0x00 && mac[1] == 0x05) // VMware
                        || (mac[0] == 0x00 && mac[1] == 0x1C) // VMware
                        || (mac[0] == 0x00 && mac[1] == 0x0C) // VMware
                        || (mac[0] == 0x00 && mac[1] == 0x50) // VMware
                        || (mac[0] == 0x08 && mac[1] == 0x00) // VirtualBox
                        || (mac[0] == 0x00 && mac[1] == 0x1A) // VirtualBox
                        || (mac[0] == 0x00 && mac[1] == 0x16) // Xen
                        || (mac[0] == 0x00 && mac[1] == 0x0F) // KVM
                        || (mac[0] == 0x00 && mac[1] == 0x03) // Parallels
                        || (mac[0] == 0x52 && mac[1] == 0x54); // Hyper-V
            }
        } catch (Exception e) {
            throw new NoStackTrace("Anti Virtual Machine failed to initialize");
        }
        return false;
    }
}

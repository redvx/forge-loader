package dev.redvx.loader;

import dev.redvx.loader.util.NoStackTrace;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import dev.redvx.loader.protection.AntiVirtualMachine;
import dev.redvx.loader.protection.HWIDProtection;
import dev.redvx.loader.protection.KillSwitch;

import java.lang.reflect.Method;
import java.util.Map;

public class CoreMod implements IFMLLoadingPlugin {

    public CoreMod() {
        KillSwitch.check();
        if (AntiVirtualMachine.isRunningInsideVirtualMachine()) {
            try {
                final Method shutdownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", int.class);
                shutdownMethod.setAccessible(true);
                shutdownMethod.invoke(null, 0);
            } catch (Exception e) {
                throw new NoStackTrace("Stop trying to crack da client.");
            }
        }
        HWIDProtection.check();
        LoaderKt.load();
    }

    public static boolean hasRun() {
        return HWIDProtection.hwid.get();
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{};
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

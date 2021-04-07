package eu.doppel_helix.dev.jdk.reproducecrash;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class TestBrowser {

    public static void main(String[] args) throws Exception {
        // Setup a module layer for OpenJFX and the test class

        // Hack to get the classes of this programm into a module layer
        Path selfPath = Paths.get(TestBrowser.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        ModuleFinder finder = ModuleFinder.of(
            Paths.get(System.getProperty("javafx.sdk.path")),
            selfPath
        );

        // Load the application as a named module and invoke it
        ModuleLayer parent = ModuleLayer.boot();
        Configuration cf = parent.configuration().resolve(finder, ModuleFinder.of(), Set.of("ReproduceOpenjfxCrash2"));
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        ModuleLayer layer = parent.defineModulesWithOneLoader(cf, scl);
        Class appClass = layer.findLoader("ReproduceOpenjfxCrash2").loadClass("javafx.application.Application");
        Class testClass = layer.findLoader("ReproduceOpenjfxCrash2").loadClass("eu.doppel_helix.dev.jdk.reproducecrash.TestBrowserImpl");
        Method launchMethod = appClass.getMethod("launch", Class.class, String[].class);
        launchMethod.invoke(null, new Object[]{testClass, args});
    }
}

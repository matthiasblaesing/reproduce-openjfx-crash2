package eu.doppel_helix.dev.jdk.reproducecrash;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class TestBrowser {

    public static void main(String[] args) throws Exception {
        System.setProperty("javafx.verbose", "true");
        System.setProperty("java.library.path", "");

        /*
         * Setup a module layer for OpenJFX and the test class
         */

        // Hack to get the classes of this programm into a module layer
        Path selfPath = Paths.get(TestBrowser.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path mavenRepositoryPath = Paths.get(System.getProperty("user.home"), ".m2/repository");
        ModuleFinder finder = ModuleFinder.of(
            mavenRepositoryPath.resolve("org/openjfx/javafx-base/13/javafx-base-13-linux.jar"),
            mavenRepositoryPath.resolve("org/openjfx/javafx-controls/13/javafx-controls-13-linux.jar"),
            mavenRepositoryPath.resolve("org/openjfx/javafx-graphics/13/javafx-graphics-13-linux.jar"),
            mavenRepositoryPath.resolve("org/openjfx/javafx-media/13/javafx-media-13-linux.jar"),
            // This crashes
            mavenRepositoryPath.resolve("org/openjfx/javafx-web/13/javafx-web-13-linux.jar"),
            // This points to a javafx-web-13-linux.jar, that is patched
            // it will not crash
//            Paths.get("/home/matthias/src/jfx/build/publications/javafx.web-linux.jar"),
            selfPath
        );

        /*
         * Load the application as a named module and invoke it
         */
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

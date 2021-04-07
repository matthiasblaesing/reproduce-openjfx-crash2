package eu.doppel_helix.dev.jdk.reproducecrash;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class TestBrowser {

    public static void main(String[] args) throws Exception {
        // Setup example server that serves a site, that uses local storage
        // to force instantiation of FileSystem
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test.html", (HttpExchange he) -> {
            byte[] result = "<html><body><script>localStorage.setItem('myCat', 'Tom');</script><h1>Done</h1></body></html>".getBytes(StandardCharsets.UTF_8);
            he.sendResponseHeaders(200, result.length);
            try(OutputStream os = he.getResponseBody()) {
                os.write(result);
            }
        });
        server.start();

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

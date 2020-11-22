package eu.doppel_helix.dev.jdk.reproducecrash;

import java.lang.module.ModuleDescriptor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class TestBrowserImpl extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Module module = Application.class.getModule();

        if (module != null) {
            ModuleDescriptor moduleDesc = module.getDescriptor();

            System.out.println("==========================================================================");
            System.out.println("==");
            System.out.printf("== Module is named:  %b%n", module.isNamed());
            System.out.printf("== Module name:      %s%n", module.getName());

            if (moduleDesc != null) {
                System.out.printf("== Automatic module: %b%n", moduleDesc.isAutomatic());
                System.out.printf("== Module is opened: %b%n", moduleDesc.isOpen());
            }
            System.out.println("==");
            System.out.println("==========================================================================");
        }

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);

        WebView webview = new WebView();
        root.setCenter(webview);

        String script
            = "<html><body><img src=\"data:image/gif;base64,R0lGODdhEAAQAMwAAPj7+FmhUYjNfGuxYYDJdYTIeanOpT+DOTuANXi/bGOrWj6CONzv2sPjv2CmV1unU4zPgISg6DJnJ3ImTh8Mtbs00aNP1CZSGy0YqLEn47RgXW8amasW7XWsmmvX2iuXiwAAAAAEAAQAAAFVyAgjmRpnihqGCkpDQPbGkNUOFk6DZqgHCNGg2T4QAQBoIiRSAwBE4VA4FACKgkB5NGReASFZEmxsQ0whPDi9BiACYQAInXhwOUtgCUQoORFCGt/g4QAIQA7\"/></body></html>";

        webview.getEngine().loadContent(script);

        primaryStage.setScene(scene);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.show();
    }
}

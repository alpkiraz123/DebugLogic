package com.example.debuglogic;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;

public class HelloController {

    @FXML
    private Button startButton;

    @FXML
    public TextArea codeArea;

    @FXML
    private TextFlow textFlow;

    private String getCodeArea() {
        return codeArea.getText();
    }

    @FXML
    private void initialize() {
        UnaryOperator<TextFormatter.Change> styleOperator = change -> {
            String text = change.getControlNewText();
            List<String> keywords = Arrays.asList("public", "class", "void", "String", "int", "if", "else", "for", "while");
            String keywordColor = "-fx-fill: blue;";

            Pattern pattern = Pattern.compile("\\b(" + String.join("|", keywords) + ")\\b");
            Matcher matcher = pattern.matcher(text);

            textFlow.getChildren().clear();

            int lastIndex = 0;
            while (matcher.find()) {
                Text plainText = new Text(text.substring(lastIndex, matcher.start()));
                Text keywordText = new Text(text.substring(matcher.start(), matcher.end()));
                keywordText.setStyle(keywordColor);

                textFlow.getChildren().addAll(plainText, keywordText);

                lastIndex = matcher.end();
            }
            System.out.println("test");

            Text remainingText = new Text(text.substring(lastIndex));
            textFlow.getChildren().add(remainingText);

            return change;
        };

        codeArea.setTextFormatter(new TextFormatter<>(styleOperator));
    }






    @FXML
    private void CompileCode() {
        String code = getCodeArea();

        try {
            // Create a temporary directory to store the user's code
            File tempDir = new File("temp");
            tempDir.mkdirs();

            // Write user code to a temporary .java file
            File javaFile = new File(tempDir, "UserCode.java");
            FileWriter writer = new FileWriter(javaFile);
            writer.write(code);
            writer.close();

            // Compile user code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int compilationResult = compiler.run(null, null, null, javaFile.getPath());

            if (compilationResult == 0) {
                // Compilation was successful, load and execute user code
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { tempDir.toURI().toURL() });
                Class<?> userClass = Class.forName("UserCode", true, classLoader);

                // Attempt to execute the user's code
                try {
                    Method mainMethod = userClass.getMethod("main", String[].class);
                    mainMethod.invoke(null, (Object) new String[0]);

                } catch (InvocationTargetException ite) {
                    // Handle exceptions in the Exceptions class
                    Exceptions exceptions = new Exceptions();
                    exceptions.setCodeException(ite.getCause().getClass().getSimpleName());
                    exceptions.returnException();
                }
            } else {
                // Compilation failed, handle the error
                Exceptions exceptions = new Exceptions();
                exceptions.setCodeException("CompilationFailed");
                exceptions.returnException();
            }
        } catch (Exception e) {
            // Handle generic exceptions here
            Exceptions exceptions = new Exceptions();
            exceptions.setCodeException(e.getClass().getSimpleName());
            exceptions.returnException();
        }
    }
}
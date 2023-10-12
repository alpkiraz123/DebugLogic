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
            // Create a new JavaCompiler
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            // Create a new DiagnosticCollector to collect compilation messages
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

            // Get the standard file manager from the compiler
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            // Create a new JavaFileObject from the code string
            JavaFileObject codeObject = new JavaSourceFromString("Main", code);

            // Create a list of JavaFileObjects to compile
            List<JavaFileObject> compilationUnits = Arrays.asList(codeObject);

            // Set the class output directory
            List<String> options = Arrays.asList("-d", "bin");

            // Create a new CompilationTask
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);

            // Compile the code
            boolean success = task.call();

            // Print the compilation messages
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(diagnostic.getMessage(null));
            }

            // Close the file manager
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
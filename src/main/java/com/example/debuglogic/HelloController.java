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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.Arrays;
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
        // Not sure what to do right now.
    }

    @FXML
    private void CompileCode() throws IOException {
        String code = getCodeArea();
        String className = null;
        Path javaFilePath = null;
        Path classFilePath = null;

        try {

            Path tempDir = Files.createTempDirectory("usercode");

            // Extract the class name from the user's code
            className = extractClassNameFromCode(code);


            javaFilePath = tempDir.resolve(className + ".java");
            Files.write(javaFilePath, code.getBytes());


            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int compilationResult = compiler.run(null, null, null, javaFilePath.toString());

            if (compilationResult == 0) {
                // Compilation was successful, load and execute user code
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{tempDir.toUri().toURL()});
                Class<?> userClass = classLoader.loadClass(className);


                Method mainMethod = userClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[0]);
            } else {
                // Compilation failed, handle the error
                Exceptions exceptions = new Exceptions();
                exceptions.setCodeException("CompilationFailed");
                exceptions.returnException();
            }
        } catch (InvocationTargetException ite) {

            Throwable cause = ite.getCause();
            Exceptions exceptions = new Exceptions();
            exceptions.setCodeException(cause.getClass().getSimpleName());
            exceptions.returnException();
        } catch (Exception e) {

            Exceptions exceptions = new Exceptions();
            exceptions.setCodeException(e.getClass().getSimpleName());
            exceptions.returnException();
        } finally {

            if (javaFilePath != null) {
                Files.delete(javaFilePath);
            }
            if (classFilePath != null) {
                Files.delete(classFilePath);
            }
        }
    }

    private String extractClassNameFromCode(String code) {

        String[] lines = code.split("\n");
        for (String line : lines) {
            if (line.contains("class")) {
                //Get class.
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    if (part.equals("class")) {
                        int index = Arrays.asList(parts).indexOf("class");
                        if (index + 1 < parts.length) {
                            return parts[index + 1].trim();
                        }
                    }
                }
            }
        }

        // Default to a generic class name if the class name is not found
        return "UserClass" + System.currentTimeMillis();
    }
}
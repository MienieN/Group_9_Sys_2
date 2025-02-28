package main.java.zenit;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.management.StringValueExp;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TextAreaOutputStreamTest {

    private TextArea textArea;

    private TextAreaOutputStream textAreaOutputStream;

    @BeforeEach
    void setup() {
        Platform.runLater(() -> {
            textArea = new TextArea();
            textAreaOutputStream = spy(new TextAreaOutputStream(textArea));
        });
    }

    @Test
    void testWriteSingleChar(){
        Platform.runLater(() -> {
            try {
                textAreaOutputStream.write('A');
                verify(textAreaOutputStream,times(1)).write('A');
                assertEquals("A",textArea.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testWriteWithCarriageReturn(){
        //The \r in the write method does nothing, In the case it just returns. (case '\r': return;)
        Platform.runLater(() -> {
            try{
                textAreaOutputStream.write('H');
                textAreaOutputStream.write('E');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('O');
                textAreaOutputStream.write(' ');
                textAreaOutputStream.write('\r');
                textAreaOutputStream.write('W');
                textAreaOutputStream.write('O');
                textAreaOutputStream.write('R');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('D');
                verify(textAreaOutputStream,times(1)).write('H');
                verify(textAreaOutputStream,times(1)).write('E');
                verify(textAreaOutputStream,times(3)).write('L');
                verify(textAreaOutputStream,times(2)).write('O');
                verify(textAreaOutputStream,times(1)).write('W');
                verify(textAreaOutputStream,times(1)).write('R');
                verify(textAreaOutputStream,times(1)).write('D');
                verify(textAreaOutputStream,times(1)).write('\r');
                assertEquals("HELLO WORLD", textArea.getText());
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testWriteNewLine(){
        Platform.runLater(() -> {
            try {
                textAreaOutputStream.write('H');
                textAreaOutputStream.write('E');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('O');
                textAreaOutputStream.write(' ');
                textAreaOutputStream.write('W');
                textAreaOutputStream.write('O');
                textAreaOutputStream.write('R');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('D');
                textAreaOutputStream.write('\n');
                verify(textAreaOutputStream,times(1)).write('H');
                verify(textAreaOutputStream,times(1)).write('E');
                verify(textAreaOutputStream,times(3)).write('L');
                verify(textAreaOutputStream,times(2)).write('O');
                verify(textAreaOutputStream,times(1)).write('W');
                verify(textAreaOutputStream,times(1)).write('R');
                verify(textAreaOutputStream,times(1)).write('D');
                verify(textAreaOutputStream,times(1)).write('\n');
                assertEquals("HELLO WORLDHELLO WORLD\n", textArea.getText());
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testWriteByteArray(){
        Platform.runLater(() -> {
        byte[] testArray = "Hello World testarray".getBytes();
            try {
                textAreaOutputStream.write(testArray, 0, testArray.length);
                verify(textAreaOutputStream, times(1)).write(testArray,0,testArray.length);
                assertEquals("Hello World testarray", textArea.getText());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testFlush() {
        Platform.runLater(() -> {
            try{
                textAreaOutputStream.write('H');
                textAreaOutputStream.write('E');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('L');
                textAreaOutputStream.write('O');
                textAreaOutputStream.flush();
                verify(textAreaOutputStream, times(1)).flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testClose(){
        Platform.runLater(() -> {
            textAreaOutputStream.close();
            verify(textAreaOutputStream, times(1)).close();
        });
    }

}
package main.java.zenit.ui.tree;


import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import main.java.zenit.ui.MainController;
import main.java.zenit.zencodearea.ZenCodeArea;


/**
 * This class is responsible for creating a context menu for the ZenCodeArea.
 * I know its messy, but it works and focus is needed elsewhere so this will have to do for now.
 * Good luck and Godspeed
 */
public class InsertMenu extends ContextMenu{
    MainController mainController;
    private ZenCodeArea codeArea;
    private ContextMenu insertMenu = new ContextMenu();
    private MenuItem mainmethod = new MenuItem("Main method");
    private MenuItem forLoop = new MenuItem("for-loop");
    private MenuItem whileLoop = new MenuItem("While-loop");
    private MenuItem switchCase = new MenuItem("Switch case");
    private MenuItem doWhileLoop = new MenuItem("do-while loop");
    private MenuItem foreachLoop = new MenuItem("for-each loop");
    private MenuItem ifelsecase = new MenuItem("if-else");
    private MenuItem elseifcase = new MenuItem("else-if");
    private Menu methodMenu = new Menu("Methods");
    private MenuItem returnStringMethod = new MenuItem("String return method");
    private MenuItem returnIntMethod = new MenuItem("int return method");
    private MenuItem returnVoidMethod = new MenuItem("void method");


    public InsertMenu(MainController mainController, ZenCodeArea codeArea){
        this.mainController = mainController;
        this.codeArea = codeArea;
        setup();
    }

    /**
     * Yes! I know what this looks like. Be happy and move on.
     */
    public void setup(){
        setForLoop();
        setWhileLoop();
        setSwitchCase();
        setDoWhileLoop();
        setForeachLoop();
        setIfelsecase();
        setElseifcase();
        setReturnStringMethod();
        setReturnIntMethod();
        setReturnVoidMethod();
        setMainMethod();
        methodMenu.getItems().addAll(returnStringMethod,
                returnIntMethod,
                returnVoidMethod);
        insertMenu.getItems().addAll(mainmethod,
                forLoop,
                whileLoop,
                switchCase,
                doWhileLoop,
                foreachLoop,
                ifelsecase,
                elseifcase,
                methodMenu);
        codeArea.setOnContextMenuRequested(event ->
                insertMenu.show(codeArea, event.getScreenX(), event.getScreenY())
        );
    }

    /**
     * Just accept it and move on already. If you are trying to format the text, you are wasting your time.
     */

    public void setForLoop(){
        String loop = "for (int i = 0; i < 'x'; i++){...}//Replace 0, x and ++ to modify your loop.";
        forLoop.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(),loop));
    }

    public void setWhileLoop(){
        String wloop = "while(condition){...} //Replace condition with your condition";
        whileLoop.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(), wloop));
    }

    public void setSwitchCase(){
        String scase = "switch(condition){\n\t\tcase x: break;\n\t\tcase y: break;\n\t\tdefaul: \n\t} //Replace the condition and add or remove cases as needed";
        switchCase.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(), scase));
    }

    public void setDoWhileLoop(){
        String dowhile = "do{...}\n\twhile (condition); //Replace condition with your own";
        doWhileLoop.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(), dowhile));
    }

    public void setForeachLoop(){
        String foreach = " for (var varname : arrayname){...} //replace varname and arrayname with your variable names (you should also replace var with the proper variable)";
        foreachLoop.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(),foreach));
    }

    public void setIfelsecase(){
        String ifelse = "if (condition){...}//Replace condition with your own\nelse{...}";
        ifelsecase.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(), ifelse));
    }

    public void setElseifcase(){
        String elseif = "if (condition){...}//Replace condition\nelse if(condition){...}//Replace condition\nelse{...}";
        elseifcase.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(), elseif));
    }

    public void setReturnStringMethod(){
        String stringret = "public String methodname(){//Replace methodName\n\tString str = \"...\"//replace with your string\n\t...//replace with logic\n\treturn str;\n\t}";
        returnStringMethod.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(), stringret));
    }

    public void setReturnIntMethod(){
        String intret = "public int methodname(){//Replace methodName\n\tint i = ?//replace with your int\n\t...//replace with logic\n\treturn i;\n\t}";
        returnIntMethod.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(),intret));

    }

    public void setReturnVoidMethod(){
        String voidret = "public void methodname(){...}//Replace methodname and logic";
        returnVoidMethod.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(),voidret));
    }

    public void setMainMethod(){
        String mainmethodstr = "public static void main(String[] args){...}";
        mainmethod.setOnAction(e -> mainController.getSelectedTab().getZenCodeArea().insertText(codeArea.getCaretPosition(),mainmethodstr));
    }

}

package main.java.zenit.zencodearea;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.concurrent.Task;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.zenit.ui.tree.InsertMenu;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.Nodes;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;

public class ZenCodeArea extends CodeArea {
	private ExecutorService executor;
	//private int fontSize;
	//private String font;

	private static final String[] KEYWORDS = new String[] {
		"abstract", "assert", "boolean", "break", "byte",
		"case", "catch", "char", "class", "const",
		"continue", "default", "do", "double", "else",
		"enum", "extends", "false", "final", "finally", "float",
		"for", "goto", "if", "implements", "import",
		"instanceof", "int", "interface", "long", "native",
		"new", "package", "private", "protected", "public",
		"return", "short", "static", "strictfp", "super",
		"switch", "synchronized", "this", "throw", "throws",
		"transient", "true", "try", "void", "volatile", "while"
	};
	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
	private static final Pattern PATTERN = Pattern.compile(
		"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
		+ "|(?<PAREN>" + PAREN_PATTERN + ")"
		+ "|(?<BRACE>" + BRACE_PATTERN + ")"
		+ "|(?<BRACKET>" + BRACKET_PATTERN + ")"
		+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
		+ "|(?<STRING>" + STRING_PATTERN + ")"
		+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
	);

	public ZenCodeArea() { this(14, "Times new Roman");}
	
	// YRJA: Refactoring this into multiple smaller methods to separate concerns.
	public ZenCodeArea(int textSize, String font) {
		initializeParagraphicFactory();
		initializeMultiPlainChanges();
		initializeExecutor();
		setInitialStyle(textSize, font);
	}

	private String insertForLoop(){
		return  "for (int i = 0; i < 'x'; i++){...} //Change 0, x and ++ if you want to alter the loop";

	}

	/**
	 * Sets up the paragraph graphic factory to display line numbers.
	 */
	private void initializeParagraphicFactory() {
		setParagraphGraphicFactory(LineNumberFactory.get(this));
	}

	/**
	 * Subscribes to the text changes and triggers syntax highlighting.
	 * It uses a debounce mechanism to reduce the frequency of updates.
	 */
	private void initializeMultiPlainChanges() {
		multiPlainChanges().successionEnds(Duration.ofMillis(100))
				.subscribe(ignore -> setStyleSpans(0, computeHighlighting(getText())));

		multiPlainChanges().successionEnds(Duration.ofMillis(500))
				.supplyTask(this::computeHighlightingAsync)
				.awaitLatest(multiPlainChanges())
				.filterMap(t -> {
					if(t.isSuccess()) {
						return Optional.of(t.get());
					} else {
						return Optional.empty();
					}
				}).subscribe(this::applyHighlighting);
	}

	/**
	 * Initializes a single-threaded executor for asynchronous tasks.
	 */
	private void initializeExecutor() {
		executor = Executors.newSingleThreadExecutor();
	}

	/**
	 * Sets the initial font size and family for the text area.
	 * @param textSize The size of the font.
	 * @param font The font family.
	 */
	private void setInitialStyle(int textSize, String font) {
		setStyle("-fx-font-size: " + textSize +";-fx-font-family: " + font);
	}

	/**
	 * Recomputed and applies the highlighting to the entire text.
	 */
	public void update() {
		var highlighting = computeHighlighting(getText());
		applyHighlighting(highlighting);
	}
	
	// public int getFontSize() { return fontSize; }

	// public String getFont() { return font; }

	/**
	 * This method is responsible for asynchronously computing syntax highlighting for the text in the CodeArea.
	 * @return A Task object that will compute the highlighting.
	 */
	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String text = getText();
		Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return computeHighlighting(text);
			}
		};
		executor.execute(task);
		return task;
	}

	/**
	 * Applies the highlighting to the text area.
	 * @param highlighting The highlighting to apply.
	 */
	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		setStyleSpans(0, highlighting);
		InputMap<KeyEvent> im = InputMap.consume(
			EventPattern.keyPressed(KeyCode.TAB), 
			e -> this.replaceSelection("    ")
			);
		Nodes.addInputMap(this, im);
	}


	/**
	 * This method is responsible for computing the highlighting of the text in the CodeArea.
	 * It uses a regular expression to match the different parts of the text and assigns a style class to each part.
	 * @param text The text to highlight.
	 * @return The highlighting of the text.
	 */
	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		int lastMatchedEnd = 0;

		while(matcher.find()) {
			String styleClass = getStyleClass(matcher);
			assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastMatchedEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastMatchedEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastMatchedEnd);
		return spansBuilder.create();
	}

	// YRJA: Created this method to be called from the method above. Separates the logic of getting the style class from the matcher.

	/**
	 * This method is responsible for getting the style class of a matched part of the text.
	 * It checks which group of the matcher was matched and returns the corresponding style class.
	 * @param matcher The matcher that was used to match the text.
	 * @return The style class of the matched text.
	 */
	private static String getStyleClass(Matcher matcher) {
		return  matcher.group("KEYWORD") != null ? "keyword" :
				matcher.group("PAREN") != null ? "paren" :
				matcher.group("BRACE") != null ? "brace" :
				matcher.group("BRACKET") != null ? "bracket" :
				matcher.group("SEMICOLON") != null ? "semicolon" :
				matcher.group("STRING") != null ? "string" :
				matcher.group("COMMENT") != null ? "comment" :
				null; /* never happens */
	}

	/**
	 * Sets the font size of the text area.
	 * @param newFontSize The new font size.
	 */
	public void setFontSize(int newFontSize) {
		//fontSize = newFontSize;
		setStyle("-fx-font-size: " + newFontSize);
	}

	/**
	 * Sets the font family of the text area.
	 * @param fontFamily The new font family.
	 * @param size The new font size.
	 */
	public void updateAppearance(String fontFamily, int size) {
		//font = fontFamily;
		setStyle("-fx-font-family: " + fontFamily + ";" + "-fx-font-size: " + size + ";");
	}

}

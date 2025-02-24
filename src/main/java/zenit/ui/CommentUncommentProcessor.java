package main.java.zenit.ui;

import main.java.zenit.zencodearea.ZenCodeArea;
import java.util.ArrayList;
import java.util.List;

/**
 * Processes comments and uncommenting of selected lines in a ZenCodeArea.
 * The class toggles comments for selected lines, ensuring the caret position
 * and selection range are properly handled while processing.
 */
public class CommentUncommentProcessor {
    private ZenCodeArea zenCodeArea; // Reference to the ZenCodeArea for interacting with the text area
    private int caretPosition; // Current position of the caret (cursor)
    private int caretColumn; // Column where the caret is located
    private int length; // Length of the text in the ZenCodeArea
    private int whereToReplaceFirstLine; // Starting position for comment/uncomment operation
    private int rowNumber; // Current row number of the caret position
    private int paragraphLength; // Length of the paragraph at the current row
    private List<Integer> whereToReplaceList; // List of positions where replacements need to be made
    private int n; // Counter for iterating over the lines
    private boolean[] addComment; // Array tracking whether to add a comment at specific positions
    private int stepsToMove; // Tracks movement of the caret when processing
    private int startOfSelection; // Start position of the selected text range
    private int endOfSelection; // End position of the selected text range
    private boolean topDown; // Flag indicating whether to process from top to bottom or bottom to top

    /**
     * Constructs a CommentUncommentProcessor with the given parameters.
     *
     * @param zenCodeArea     The ZenCodeArea where the text operations will take place
     * @param caretPosition   The position of the caret in the ZenCodeArea
     * @param caretColumn     The column of the caret position
     * @param length          The length of the text in the ZenCodeArea
     * @param startOfSelection The start position of the selected text
     * @param endOfSelection The end position of the selected text
     */
    public CommentUncommentProcessor(ZenCodeArea zenCodeArea, int caretPosition, int caretColumn, int length, int startOfSelection, int endOfSelection) {
        this.zenCodeArea = zenCodeArea;
        this.caretPosition = caretPosition;
        this.caretColumn = caretColumn;
        this.length = length;
        this.startOfSelection = startOfSelection;
        this.endOfSelection = endOfSelection;

        this.whereToReplaceFirstLine = caretPosition - caretColumn; // The position where comment/uncomment starts
        this.rowNumber = zenCodeArea.getCurrentParagraph(); // Get the current paragraph where the caret is located
        this.paragraphLength = zenCodeArea.getParagraphLength(rowNumber); // Length of the paragraph in the ZenCodeArea

        this.whereToReplaceList = new ArrayList<>();
        this.whereToReplaceList.add(whereToReplaceFirstLine); // Initialize the list of positions to replace

        this.n = 1; // Set counter to 1 for first iteration
        this.addComment = new boolean[whereToReplaceList.size()]; // Initialize the addComment array
        this.stepsToMove = 0; // Initialize steps to move caret
        this.topDown = true; // Default to processing from top to bottom
    }

    /**
     * Main method that processes the comment/uncomment operation. It determines whether the
     * processing should be top-down or bottom-up based on caret and selection positions.
     */
    public void process() {
        determineDirection(); // Determines the direction (top-down or bottom-up) based on caret position and selection
        if (topDown) {
            processTopDown(); // Process the lines from top to bottom if flag is true
        } else {
            processBottomUp(); // Otherwise, process the lines from bottom to top
        }
    }

    /**
     * Determines the direction of processing (top-down or bottom-up).
     * It checks the caret position relative to the selection range to decide.
     */
    private void determineDirection() {
        // Check if caret is at the end of selection and the position to replace is beyond the start of selection
        boolean isCaretAtEnd = (caretPosition == endOfSelection);
        boolean isReplaceBeyondStart = (whereToReplaceFirstLine > startOfSelection);
        if (isCaretAtEnd && isReplaceBeyondStart) {
            topDown = true; // Set direction to top-down
            addWhereToReplaceForTopDown(); // Add positions for top-down processing
        }

        // Check if caret is at the start of selection and the position to replace is within the selection
        boolean isCaretAtStart = (caretPosition == startOfSelection);
        boolean isReplaceWithinSelection = (whereToReplaceFirstLine + paragraphLength < endOfSelection);
        if (isCaretAtStart && isReplaceWithinSelection) {
            topDown = false; // Set direction to bottom-up
            addWhereToReplaceForBottomUp(); // Add positions for bottom-up processing
        }
    }

    /**
     * Adds positions for replacement in top-down direction.
     * The method iterates upward to add positions where replacement needs to occur, adjusting for paragraph lengths.
     */
    private void addWhereToReplaceForTopDown() {
        do {
            whereToReplaceFirstLine -= (1 + zenCodeArea.getParagraphLength(rowNumber - n)); // Adjust by paragraph length
            n++; // Increment counter to move to the next line
            whereToReplaceList.add(whereToReplaceFirstLine); // Add the new position to the list
        } while (whereToReplaceFirstLine > startOfSelection); // Continue until the first line to replace is beyond selection
    }

    /**
     * Adds positions for replacement in bottom-up direction.
     * The method iterates downward to add positions where replacement needs to occur, adjusting for paragraph lengths.
     */
    private void addWhereToReplaceForBottomUp() {
        do {
            whereToReplaceFirstLine += 1 + zenCodeArea.getParagraphLength(rowNumber + n - 1); // Adjust by paragraph length
            n++; // Increment counter to move to the next line
            whereToReplaceList.add(whereToReplaceFirstLine); // Add the new position to the list
        } while (whereToReplaceFirstLine + zenCodeArea.getParagraphLength(rowNumber + n - 1) < endOfSelection); // Continue until the first line to replace is beyond selection
    }

    /**
     * Processes the lines in top-down order, handling the commenting and uncommenting logic for each line.
     * It checks if a line is already commented and toggles the comment appropriately.
     */
    private void processTopDown() {
        for (int i = 0; i < n; i++) {
            int whereToReplace = whereToReplaceList.get(i); // Get the current position to replace
            handleCaretInsert(whereToReplace); // Ensure there is space for comment markers

            // If the current line is already commented, uncomment it; otherwise, add a comment
            if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ")) {
                handleUncomment(whereToReplace, i); // Uncomment the line
            } else {
                handleComment(whereToReplace, i); // Add a comment to the line
            }
        }

        adjustCaretPositionForTopDown(); // Adjust the caret position after processing
    }

    /**
     * Processes the lines in bottom-up order, handling the commenting and uncommenting logic for each line.
     * Similar to processTopDown, but works from bottom to top.
     */
    private void processBottomUp() {
        for (int i = whereToReplaceList.size() - 1; i >= 0; i--) {
            int whereToReplace = whereToReplaceList.get(i); // Get the current position to replace
            handleCaretInsert(whereToReplace); // Ensure there is space for comment markers

            // If the current line is already commented, uncomment it; otherwise, add a comment
            if (zenCodeArea.getText(whereToReplace, whereToReplace + 3).equals("// ")) {
                handleUncomment(whereToReplace, i); // Uncomment the line
            } else {
                handleComment(whereToReplace, i); // Add a comment to the line
            }
        }

        adjustCaretPositionForBottomUp(); // Adjust the caret position after processing
    }

    /**
     * Inserts additional spaces before the caret if necessary to accommodate comment markers.
     * It ensures the caret is in a valid position before replacing text.
     */
    private void handleCaretInsert(int whereToReplace) {
        if (caretPosition > length - 3) {
            zenCodeArea.insertText(caretPosition, "  "); // Insert spaces for the comment
            zenCodeArea.moveTo(caretPosition); // Move the caret to the correct position
        }
    }

    /**
     * Handles uncommenting a line. This method looks for lines with specific comment markers
     * (like "// *") and removes or modifies them accordingly to uncomment the line.
     *
     * @param whereToReplace The position to modify
     * @param i The index of the line in the replacement list
     */
    private void handleUncomment(int whereToReplace, int i) {
        // If the line contains a special comment marker ("// *"), remove it
        if (zenCodeArea.getText(whereToReplace, whereToReplace + 4).equals("// *")) {
            zenCodeArea.deleteText(whereToReplace, whereToReplace + 2); // Remove the comment marker
            stepsToMove -= 2; // Adjust caret movement due to the removal
        } else {
            zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "  "); // Replace "//" with spaces to uncomment
        }
        addComment[i] = false; // Update the status that comment was removed
    }

    /**
     * Handles commenting a line. This method checks for existing comment markers and adds or
     * modifies them accordingly. It looks for "//" or indentation and adds the correct comment syntax.
     *
     * @param whereToReplace The position to modify
     * @param i The index of the line in the replacement list
     */
    private void handleComment(int whereToReplace, int i) {
        String currentText = zenCodeArea.getText(whereToReplace, whereToReplace + 4); // Get the text at the current position
        if (currentText.startsWith("//")) {
            zenCodeArea.deleteText(whereToReplace, whereToReplace + 2); // Remove the existing "//" comment marker
            adjustSteps(whereToReplace); // Adjust caret movement based on position
        } else if (currentText.startsWith("    ")) {
            zenCodeArea.replaceText(whereToReplace, whereToReplace + 2, "//"); // Replace indentation with comment marker
        } else {
            zenCodeArea.insertText(whereToReplace, "//"); // Add comment marker at the beginning of the line
            stepsToMove += 2; // Increase the caret movement due to adding text
        }
        addComment[i] = true; // Mark the line as commented
    }

    /**
     * Adjusts the steps to move the caret based on the position of the modification.
     * This ensures the caret is properly placed after the comment or uncomment operation.
     *
     * @param pos The position to adjust steps for
     */
    private void adjustSteps(int pos) {
        if (pos == caretPosition) {
            // No adjustment if caret is at the exact modification position
        } else if (pos + 1 == caretPosition) {
            stepsToMove--; // Adjust for caret being right after the comment marker
        } else {
            stepsToMove -= 2; // Adjust for caret being further away
        }
    }

    /**
     * Adjusts the caret position for top-down processing after the comment/uncomment operation.
     * This ensures the caret stays in the correct location after modifying the lines.
     */
    private void adjustCaretPositionForTopDown() {
        if (whereToReplaceList.size() < 2) {
            zenCodeArea.moveTo(caretPosition + stepsToMove); // If only one line, move the caret based on steps
        } else {
            boolean firstCommented = addComment[0]; // Check if the first line is commented
            boolean lastCommented = addComment[n - 1]; // Check if the last line is commented

            // Adjust caret position based on comment status of first and last lines
            int startAdjust = firstCommented ? 2 : -2;
            int endAdjust = lastCommented ? 2 : 0;

            zenCodeArea.selectRange(startOfSelection + startAdjust, endOfSelection + stepsToMove + endAdjust);
        }
    }

    /**
     * Adjusts the caret position for bottom-up processing after the comment/uncomment operation.
     * Similar to top-down adjustment, but works for bottom-up direction.
     */
    private void adjustCaretPositionForBottomUp() {
        // Handles multiple cases for different combinations of comment statuses at the start and end of the selection
        if (addComment[0] && addComment[whereToReplaceList.size() - 1]) {
            zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
                    endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) + 2,
                    rowNumber, caretColumn + 2);
        } else if (addComment[0] && !addComment[whereToReplaceList.size() - 1]) {
            zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
                    endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) - 2,
                    rowNumber, caretColumn + 2);
        } else if (!addComment[0] && addComment[whereToReplaceList.size() - 1]) {
            zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
                    endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) + 2,
                    rowNumber, caretColumn - 2);
        } else {
            zenCodeArea.selectRange(rowNumber + whereToReplaceList.size() - 1,
                    endOfSelection - whereToReplaceList.get(whereToReplaceList.size() - 1) - 2,
                    rowNumber, caretColumn - 2);
        }
    }
}

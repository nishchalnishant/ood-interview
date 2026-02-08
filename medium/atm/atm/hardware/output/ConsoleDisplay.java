package atm.hardware.output;

public class ConsoleDisplay implements Display {
    // Stores the current message to be displayed
    private String message;

    @Override
    public void showMessage(String message) {
        this.message = message;
    }

    // Returns the currently stored message
    public String getDisplayedMessage() {
        return message;
    }
}

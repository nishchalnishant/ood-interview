package atm.hardware.output;

public interface Display {
    // Displays a message to the user on the ATM screen
    void showMessage(String message);

    // Returns the currently displayed message
    String getDisplayedMessage();
}

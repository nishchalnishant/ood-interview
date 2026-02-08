package atm.hardware.input;

import atm.ATMMachine;

// Interface for card processor hardware component
public interface CardProcessor {
    // Handles card insertion by storing card number and notifying ATM
    void handleCardInsertion(String cardNumber, ATMMachine atmMachine);

    // Handles card ejection by clearing stored card number and notifying ATM
    void handleCardEjection(ATMMachine atmMachine);

    // Returns the currently stored card number
    String getCardNumber();
}

package atm.hardware.input;

import atm.ATMMachine;

public class SimulatedCardProcessor implements CardProcessor {
    // Stores the currently inserted card number
    private String cardNumber;

    // Simulates card insertion by storing card number and notifying ATM
    @Override
    public void handleCardInsertion(String cardNumber, ATMMachine atmMachine) {
        this.cardNumber = cardNumber;
        atmMachine.insertCard(cardNumber);
    }

    // Simulates card ejection by clearing stored card number and notifying ATM
    @Override
    public void handleCardEjection(ATMMachine atmMachine) {
        this.cardNumber = null;
        atmMachine.ejectCard();
    }

    // Returns the currently stored card number
    @Override
    public String getCardNumber() {
        return cardNumber;
    }
}

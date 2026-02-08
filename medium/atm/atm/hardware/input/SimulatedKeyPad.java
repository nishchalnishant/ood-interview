package atm.hardware.input;

import atm.ATMMachine;
import atm.bank.enums.TransactionType;

import java.math.BigDecimal;

public class SimulatedKeyPad implements Keypad {
    // Simulates PIN entry by forwarding the input to ATM machine
    @Override
    public void handlePinEntry(String pin, ATMMachine atmMachine) {
        atmMachine.enterPin(pin);
    }

    // Simulates amount entry by forwarding the input to ATM machine
    @Override
    public void handleAmountEntry(BigDecimal amount, ATMMachine atmMachine) {
        atmMachine.enterAmount(amount);
    }

    // Simulates transaction selection by forwarding the choice to ATM machine
    @Override
    public void handleSelectTransaction(TransactionType transactionType, ATMMachine atmMachine) {
        switch (transactionType) {
            case WITHDRAW:
                atmMachine.withdrawRequest();
                break;
            case DEPOSIT:
                atmMachine.depositRequest();
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type: " + transactionType);
        }
    }
}

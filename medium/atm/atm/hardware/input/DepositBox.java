package atm.hardware.input;

import atm.ATMMachine;

import java.math.BigDecimal;

// Interface for handling cash deposits into the ATM
public interface DepositBox {
    // Accepts cash deposit and notifies ATM of the deposited amount
    void acceptDeposit(BigDecimal amount, ATMMachine machine);
}

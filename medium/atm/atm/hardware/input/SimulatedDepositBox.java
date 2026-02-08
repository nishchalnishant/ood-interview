package atm.hardware.input;

import atm.ATMMachine;

import java.math.BigDecimal;

public class SimulatedDepositBox implements DepositBox {
    // Simulates accepting cash deposit by notifying the ATM machine
    @Override
    public void acceptDeposit(BigDecimal amount, ATMMachine atmMachine) {
        atmMachine.collectDeposit(amount);
    }
}

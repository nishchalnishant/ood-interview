package atm.hardware.output;

import java.math.BigDecimal;

public class SimulatedCashDispenser implements CashDispenser {

    /**
     * In the real implementation, this would control the hardware to dispense the cash of given amount.
     */
    @Override
    public void dispenseCash(BigDecimal amount) {
        // display on the screen that the cash is being dispensed
    }
}

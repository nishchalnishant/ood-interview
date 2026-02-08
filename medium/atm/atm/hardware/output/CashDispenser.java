package atm.hardware.output;

import java.math.BigDecimal;

public interface CashDispenser {
    // Dispenses the specified amount of cash to the user
    void dispenseCash(BigDecimal amount);
}

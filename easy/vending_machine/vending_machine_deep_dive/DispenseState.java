package vendingmachine;

import java.math.BigDecimal;

public class DispenseState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine VM, double amount) {
        VM.getPaymentProcessor().processPayment(new BigDecimal(amount));
    }

    @Override
    public void selectProductByCode(VendingMachine VM, String productCode) {
        throw new InvalidStateException("Cannot select another product while dispensing.");
    }

    @Override
    public void dispenseProduct(VendingMachine VM) {
        try {
            // Step 1: Validate the transaction
            if (VM.getSelectedProduct() == null) {
                throw new InvalidStateException("Invalid product selection");
            }

            // Step 2: Charge the customer
            double productPrice = VM.getProductPrice(VM.getSelectedProduct());
            if (VM.getPaymentProcessor().getCurrentBalance().compareTo(new BigDecimal(productPrice)) < 0) {
                throw new InvalidStateException("Insufficient funds");
            }
            VM.getPaymentProcessor().charge(new BigDecimal(productPrice));

            // Step 3: Dispense the product
            VM.getInventoryManager().dispenseProductFromRack(VM.getSelectedProduct());

            // Step 4: Return to initial state
            VM.setState(new NoMoneyInsertedState());
        } catch (Exception e) {
            throw new InvalidStateException("Error during transaction: " + e.getMessage());
        }
    }

    @Override
    public String getStateDescription() {
        return "Dispense State";
    }
} 

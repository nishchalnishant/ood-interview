package vendingmachine;

public class NoMoneyInsertedState implements VendingMachineState {
    @Override
    public void insertMoney(VendingMachine VM, double amount) {
        VM.addBalance(amount);
        VM.setState(new MoneyInsertedState());
    }

    @Override
    public void selectProductByCode(VendingMachine VM, String productCode) throws InvalidStateException {
        throw new InvalidStateException("Cannot select a product without inserting money.");
    }

    @Override
    public void dispenseProduct(VendingMachine VM) throws InvalidStateException {
        throw new InvalidStateException("Cannot dispense product without inserting money.");
    }

    @Override
    public String getStateDescription() {
        return "No Money Inserted State - Please insert money to proceed";
    }
} 

package vendingmachine;

public interface VendingMachineState {
    void insertMoney(VendingMachine VM, double amount);
    void selectProductByCode(VendingMachine VM, String productCode) throws InvalidStateException;
    void dispenseProduct(VendingMachine VM) throws InvalidStateException;
    String getStateDescription();
} 

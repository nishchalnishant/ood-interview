package vendingmachine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class VendingMachine {
    final private List<Transaction> transactionHistory;
    final private InventoryManager inventoryManager;
    final private PaymentProcessor paymentProcessor;

    private Transaction currentTransaction;

    public VendingMachine() {
        transactionHistory = new ArrayList<>();
        currentTransaction = new Transaction();
        inventoryManager = new InventoryManager();
        paymentProcessor = new PaymentProcessor();
    }

    void setRack(Map<String, Rack> rack) {
        inventoryManager.updateRack(rack);
    }
    
    void insertMoney(final BigDecimal amount) {
        paymentProcessor.addBalance(amount);
    }

    void chooseProduct(String rackId) {
        final Product product = inventoryManager.getProductInRack(rackId);
        currentTransaction.setRack(inventoryManager.getRack(rackId));
        currentTransaction.setProduct(product);
    }

    Transaction confirmTransaction() throws InvalidTransactionException {
        // Step 1: Validate the transaction before processing
        validateTransaction();

        // Step 2: Charge the customer for the product
        paymentProcessor.charge(currentTransaction.getProduct().getUnitPrice());

        // Step 3: Dispense the product from the rack
        inventoryManager.dispenseProductFromRack(currentTransaction.getRack());

        // Step 4: Return the change to the customer
        currentTransaction.setTotalAmount(paymentProcessor.returnChange());

        // Step 5: Add the completed transaction to the history
        transactionHistory.add(currentTransaction);
        Transaction completedTransaction = currentTransaction;
        
        // Reset the current transaction for the next purchase.
        currentTransaction = new Transaction(); 
        return completedTransaction;
    }

    private void validateTransaction() throws InvalidTransactionException {
        if (currentTransaction.getProduct() == null) {
            throw new InvalidTransactionException("Invalid product selection");
        } else if (currentTransaction.getRack().getProductCount() == 0) {
            // Insufficient inventory for product.  
            throw new InvalidTransactionException("Insufficient inventory for product.");
        } else if (paymentProcessor.getCurrentBalance().compareTo(currentTransaction.getProduct().getUnitPrice()) < 0) {
            throw new InvalidTransactionException("Insufficient fund");
        }
    }


    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public void cancelTransaction() {
        paymentProcessor.returnChange();
        currentTransaction = new Transaction(); // Reset the current transaction for the next purchase.
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}


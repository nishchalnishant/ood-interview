package atm.states;

import atm.ATMMachine;
import atm.bank.Account;
import atm.bank.DepositTransaction;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class DepositCollectionState extends ATMState {
    // Handles card ejection by canceling transaction and returning to idle state
    @Override
    public void processCardEjection(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Transaction cancelled, card ejected");
        atmMachine.transitionToState(new IdleState());
    }

    /**
     * Processes deposit by updating account balance and returning to transaction selection
     */
    @Override
    public void processDepositCollection(ATMMachine atmMachine, BigDecimal amount) {
        String cardNumber = atmMachine.getCardProcessor().getCardNumber();
        Account account = atmMachine.getBankInterface().getAccountByCard(cardNumber);
        DepositTransaction transaction = new DepositTransaction(account, amount);
        transaction.executeTransaction();
        atmMachine.getDisplay().showMessage(
                        MessageFormat.format("Deposit successful. Deposited amount: {0} to account: {1}",
                                amount,
                                account.getAccountNumber()));

        atmMachine.transitionToState(new TransactionSelectionState());
    }
}

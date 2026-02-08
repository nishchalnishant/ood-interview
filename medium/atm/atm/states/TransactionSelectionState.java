package atm.states;

import atm.ATMMachine;

public class TransactionSelectionState extends ATMState {

    /**
     * Handles card ejection by returning to idle state and canceling transaction
     */
    @Override
    public void processCardEjection(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Card ejected, transaction cancelled.");
        atmMachine.transitionToState(new IdleState());
    }

    /**
     * Initiates withdrawal process by transitioning to amount entry state
     */
    @Override
    public void processWithdrawalRequest(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Enter amount to withdraw:");
        atmMachine.transitionToState(new WithdrawAmountEntryState());
    }

    /**
     * Initiates deposit process by transitioning to deposit collection state
     */
    @Override
    public void processDepositRequest(ATMMachine atmMachine) {
        atmMachine.getDisplay().showMessage("Please deposit cash into the deposit box.");
        atmMachine.transitionToState(new DepositCollectionState());
    }
}

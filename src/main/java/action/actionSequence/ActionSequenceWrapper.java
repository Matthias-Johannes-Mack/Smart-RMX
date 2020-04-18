package action.actionSequence;

/**
 * Class that holds the ActionSequences for an field in the matrix
 */
public class ActionSequenceWrapper {

    /**
     * holds the ActionSequences for a field in the matrix
     * index 0 = ActionSequence for combination rowindex value = 0 and the column index value = 0 for the bit values
     * index 1 = ActionSequence for combination rowindex value = 0 and the column index value = 1 for the bit values
     * index 2 = ActionSequence for combination rowindex value = 1 and the column index value = 0 for the bit values
     * index 3 = ActionSequence for combination rowindex value = 1 and the column index value = 1 for the bit values
     */
    private ActionSequence[] actionSequences= new ActionSequence[4];


    /* --- getter ---*/

    /**
     *
     * @return ActionSequence for combination rowindex value = 0 and the column index value = 0 for the bit values
     */
    public ActionSequence getActionSequence0And0() {
        return actionSequences[0];
    }

    /**
     *
     * @return ActionSequence for combination rowindex value = 0 and the column index value = 1 for the bit values
     */
    public ActionSequence getActionSequence0And1() {
        return actionSequences[1];
    }

    /**
     *
     * @return ActionSequence for combination rowindex value = 1 and the column index value = 0 for the bit values
     */
    public ActionSequence getActionSequence1And0() {
        return actionSequences[2];
    }

    /**
     *
     * @return ActionSequence for combination rowindex value = 1 and the column index value = 1 for the bit values
     */
    public ActionSequence getActionSequence1And1() {
        return actionSequences[3];
    }

    /* --- setter ---*/

    /**
     * adds the ActionSequence to the given Condition in the ActionSequenceWrapper
     * @param rowIndexBitValue bit value of the row in the matix
     * @param columnIndexBitValue bit value of the column in the matix
     * @param actionSequence action sequence to be added
     */
    public void setActionSequence(int rowIndexBitValue, int columnIndexBitValue, ActionSequence actionSequence) {
        if(rowIndexBitValue == 0) {
            if(columnIndexBitValue == 0) {
                // rowIndexValue 0, columnIndexBitValue 0
                setActionSequence0And0(actionSequence);
            } else {
                //rowIndexValue 0 columnIndexBitValue 1
                setActionSequence0And1(actionSequence);
            }
        } else {
            if(columnIndexBitValue == 0) {
                // rowIndexValue 1, columnIndexBitValue 0
                setActionSequence1And0(actionSequence);
            } else {
                //rowIndexValue 1 columnIndexBitValue 1
                setActionSequence1And1(actionSequence);
            }
        }
    }


    /**
     * Set the ActionSequence for combination rowindex value = 0 and the column index value = 0 for the bit values
     * @param actionSequence ActionSequence to add
     */
    private void setActionSequence0And0(ActionSequence actionSequence) {
        this.actionSequences[0] = actionSequence;
    }

    /**
     * Set the ActionSequence for combination rowindex value = 0 and the column index value = 1 for the bit values
     * @param actionSequence ActionSequence to add
     */
    private void setActionSequence0And1(ActionSequence actionSequence) {
        this.actionSequences[1] = actionSequence;
    }

    /**
     * Set the ActionSequence for combination rowindex value = 1 and the column index value = 0 for the bit values
     * @param actionSequence ActionSequence to add
     */
    private void setActionSequence1And0(ActionSequence actionSequence) {
        this.actionSequences[2] = actionSequence;
    }

    /**
     * Set the ActionSequence for combination rowindex value = 1 and the column index value = 1 for the bit values
     * @param actionSequence ActionSequence to add
     */
    private void setActionSequence1And1(ActionSequence actionSequence) {
        this.actionSequences[3] = actionSequence;
    }
}

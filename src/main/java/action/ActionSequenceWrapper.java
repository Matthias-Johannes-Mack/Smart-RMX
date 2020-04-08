package action;

/**
 * class that holds the action sequences for an array point in the matrix
 */
public class ActionSequenceWrapper {

    /**
     * holds the action sequences for a field in the matrix
     * index 0 = actionsequence for combination rowindex value = 0 and the column index value =  0 for the bit values
     * index 1 = actionsequence for combination rowindex value = 0 and the column index value =  1 for the bit values
     * index 2 = actionsequence for combination rowindex value = 1 and the column index value =  0 for the bit values
     * index 3 = actionsequence for combination rowindex value = 1 and the column index value =  1 for the bit values
     */
    private ActionSequence[] actionSequences= new ActionSequence[4];


    /* --- getter ---*/

    /**
     *
     * @return actionsequence for combination rowindex value = 0 and the column index value =  0 for the bit values
     */
    public ActionSequence getActionSequence0And0() {
        return actionSequences[0];
    }

    /**
     *
     * @return actionsequence for combination rowindex value = 0 and the column index value =  1 for the bit values
     */
    public ActionSequence getActionSequence0And1() {
        return actionSequences[1];
    }

    /**
     *
     * @return actionsequence for combination rowindex value = 1 and the column index value =  0 for the bit values
     */
    public ActionSequence getActionSequence1And0() {
        return actionSequences[2];
    }

    /**
     *
     * @return actionsequence for combination rowindex value = 1 and the column index value =  1 for the bit values
     */
    public ActionSequence getActionSequence1And1() {
        return actionSequences[3];
    }

    /* --- setter ---*/

    /**
     * adds the action sequence to the given Condition in the ActionSequence Wrapper
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


    private void setActionSequence0And0(ActionSequence seq) {
        this.actionSequences[0] = seq;
    }

    private void setActionSequence0And1(ActionSequence seq) {
        this.actionSequences[1] = seq;
    }

    private void setActionSequence1And0(ActionSequence seq) {
        this.actionSequences[2] = seq;
    }

    private void setActionSequence1And1(ActionSequence seq) {
        this.actionSequences[3] = seq;
    }
}

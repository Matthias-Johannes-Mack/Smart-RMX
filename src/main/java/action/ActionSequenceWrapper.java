package action;

/**
 * class that holds the action sequences for a array point in the matrix
 */
public class ActionSequenceWrapper {

    /**
     * holds the action sequences for a field in the matrix
     * index 0 = action sequence for combination i am 0 and the other is 0 for the bit values
     * index 1 = action sequence for combination i am 1 and the other is 0 for the bit values
     * index 2 = action sequence for combination i am 0 and the other is 1 for the bit values
     * index 3 = action sequence for combination i am 1 and the other is 1 for the bit values
     */
    private ActionSequence[] actionSequences= new ActionSequence[4];


    /* --- getter ---*/
    public ActionSequence getActionSequence0And0() {
        return actionSequences[0];
    }

    public ActionSequence getActionSequence0And1() {
        return actionSequences[1];
    }

    public ActionSequence getActionSequence1And0() {
        return actionSequences[2];
    }

    public ActionSequence gerActionSequence1And1() {
        return actionSequences[3];
    }

    /* --- setter ---*/

    public void setActionSequence(int me, int other, ActionSequence actionSequence) {
        if(me == 0) {
            if(other == 0) {
                // me 0, other 0
                setActionSequence0And0(actionSequence);
            } else {
                //me 0 other 1
                setActionSequence0And1(actionSequence);
            }
        } else {
            if(other == 0) {
                // me 1, other 0
                setActionSequence1And0(actionSequence);
            } else {
                //me 1 other 1
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

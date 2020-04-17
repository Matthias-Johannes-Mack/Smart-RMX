package schedular;

import action.ActionSequence;

/**
 * runnable that is executed after a specified waitTime of an ActionWait
 * continues to process the given actionSequence at the specified startIndex
 */
public class ActionWaitRunnable implements Runnable {

    /**
     * the actionSequence to be continued to be processed
     */
    ActionSequence actionSequence;

    /**
     * the startIndex to start processing the given actionSequence
     */
    int startIndex;

    /**
     * constructor for an ActionWaitRunnable that continues to process the actionSequence starting from the startIndex
     * @param actionSequence the actionSequence to be continued to be processed
     * @param startIndex the startIndex to start processing the given actionSequence
     */
    public ActionWaitRunnable(ActionSequence actionSequence, int startIndex) {
        this.actionSequence = actionSequence;
        this.startIndex = startIndex;
    }

    @Override
    public void run() {
        // continues to process the actionSequence starting from the startIndex
        SchedularUtil.processActionSequence(actionSequence, startIndex);
    }


}

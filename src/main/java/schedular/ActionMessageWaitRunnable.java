package schedular;

import action.ActionSequence;

public class ActionMessageWaitRunnable implements Runnable {

        ActionSequence actionSequence;

        int startIndex;

        public ActionMessageWaitRunnable(ActionSequence actionSequence, int startIndex) {
            this.actionSequence = actionSequence;
            this.startIndex = startIndex;
        }

        @Override
        /**
         * after the given delay at initialization of the Runnable the run method will be executed
         */
        public void run() {
            SchedularUtil.processActionSequence(actionSequence, startIndex);
        }


}

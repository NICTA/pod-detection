package au.com.nicta.ssrg.pod.cfmchecker.io;

import au.com.nicta.ssrg.pod.cfmchecker.newcore.ConformanceCheckResult;

public interface ProcessRepository {
    public enum ProcessActivityInstanceStatus {
        OK(100),
        ERROR(200);

        public int value() {
            return value;
        }

        private ProcessActivityInstanceStatus(int value) {
            this.value = value;
        }

        private int value;
    }

    public enum ProcessModelActivityState {
        INACTIVE(0),
        ACTIVE(100),
        COMPLETED(200),
        ERROR(300);

        public int value() {
            return value;
        }

        private ProcessModelActivityState(int value) {
            this.value = value;
        }

        private int value;
    }

    void storeConformanceResult(ConformanceCheckResult event);
}

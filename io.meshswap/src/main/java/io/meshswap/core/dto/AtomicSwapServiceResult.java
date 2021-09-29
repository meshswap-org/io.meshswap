package io.meshswap.core.dto;

public class AtomicSwapServiceResult {
    public Integer errorCode;
    public static AtomicSwapServiceResult error(int error) {
        AtomicSwapServiceResult result = new AtomicSwapServiceResult();
        result.errorCode = error;
        return result;
    }
}

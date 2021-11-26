package io.meshswap.core.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AtomicSwapServiceResult {
    public Integer errorCode;
    public String errorMsg;
    public static AtomicSwapServiceResult error(int error) {
        AtomicSwapServiceResult result = new AtomicSwapServiceResult();
        result.errorCode = error;
        return result;
    }
}

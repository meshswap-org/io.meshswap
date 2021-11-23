package io.meshswap.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiateResult extends AtomicSwapServiceResult{
    public String contractTx;
    public String contractTxId;
    public String contractScript;
    public String redeemTx;
    public String refundTx;
    public String secret;
    public String secretHash;
    public Boolean signed = false;
}

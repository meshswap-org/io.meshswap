package io.meshswap.core.dto;

public class InitiateResult extends AtomicSwapServiceResult{
    public String contractHex;
    public String contractTx;
    public String redeemTx;
    public String refundTx;
    public String secret;
    public String secretHash;
}

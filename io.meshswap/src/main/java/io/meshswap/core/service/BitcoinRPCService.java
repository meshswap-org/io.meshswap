package io.meshswap.core.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Coin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;
import wf.bitcoin.javabitcoindrpcclient.GenericRpcException;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class BitcoinRPCService {

    public static class FeePerKbResult {
        public Coin useFee = Coin.ZERO;
        public Coin relayFee = Coin.ZERO;

        public FeePerKbResult(Coin useFee, Coin relayFee) {
            this.useFee = useFee;
            this.relayFee = relayFee;
        }
    }
    public static class FundRawTransactionResult {
        public Coin fee;
        public String hex;
        public Long changepos;
    }

    BitcoinJSONRPCClient bitcoindRpcClient;

    public void init(String rpcUrl) {
        try {
            bitcoindRpcClient = new BitcoinJSONRPCClient(rpcUrl);
            bitcoindRpcClient.query("loadwallet", "wallet");
        } catch (BitcoinRPCException | MalformedURLException e) {
            log.error("Load wallet error {}", e.getMessage());
        }
        BigDecimal balance = bitcoindRpcClient.getBalance();
        log.info("Wallet balance {}", balance);
    }
    public String getRawChangeAddress() {
        return bitcoindRpcClient.getRawChangeAddress();
    }

    public String getPrivateKey(String address) {
        return  bitcoindRpcClient.dumpPrivKey(address);
    }


    public String publishTransaction(String hex) {
        return bitcoindRpcClient.sendRawTransaction(hex);
    }

    public FundRawTransactionResult fundRawTransaction(String txHex) {
        FundRawTransactionResult result = new FundRawTransactionResult();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            resultMap = (Map<String, Object>) bitcoindRpcClient.query("fundrawtransaction", txHex);
            if (Objects.nonNull(resultMap.get("hex"))) {
                result.fee = Coin.parseCoin((String) resultMap.get("fee").toString());
                result.hex = (String) resultMap.get("hex");
                result.changepos = (Long) resultMap.get("changepos");
            }
        } catch (BitcoinRPCException e) {
            log.error("query error: {}",e.getMessage());
        }

        return result;
    }

    public Map<String,Object> signRawTransaction(String txHex) {
        return (Map<String, Object>) bitcoindRpcClient.query("signrawtransactionwithwallet", txHex);
    }

    public FeePerKbResult getFeePerKb() {
        try {
            BitcoindRpcClient.NetworkInfo networkInfo = bitcoindRpcClient.getNetworkInfo();
            BitcoindRpcClient.WalletInfo walletInfo = bitcoindRpcClient.getWalletInfo();
            Coin relayFee = Coin.parseCoin(networkInfo.relayFee().toPlainString());
            Coin payTxFee = Coin.parseCoin(walletInfo.payTxFee().toPlainString());
            if (!BigDecimal.ZERO.equals(payTxFee)) {
                Coin maxFee = payTxFee;
                if (relayFee.compareTo(maxFee) > 0) {
                    maxFee = relayFee;
                }
                return new FeePerKbResult(maxFee, relayFee);
            }
            BitcoindRpcClient.SmartFeeResult smartFeeResult = bitcoindRpcClient.estimateSmartFee(6);
            if (smartFeeResult.feeRate().compareTo(BigDecimal.ZERO) > 0) {
                Coin useFee = Coin.parseCoin(smartFeeResult.feeRate().toPlainString());
                if (relayFee.compareTo(useFee) > 0) {
                    useFee = relayFee;
                }
                return new FeePerKbResult(useFee, relayFee);
            }
            log.warn("warning: falling back to mempool relay fee policy");
            return new FeePerKbResult(relayFee, relayFee);
        } catch (GenericRpcException e) {
            log.error("RPC error.",e);
        }
        return new FeePerKbResult(Coin.ZERO, Coin.ZERO);
    }

}

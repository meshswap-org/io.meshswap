package io.meshswap.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.net.MalformedURLException;

@Configuration
public class BeansConfiguration {

    @Value("${bitcoin.rpc.url}")
    private String rpcUrl;

    @Bean
    public BitcoinJSONRPCClient bitcoindRpcClient() throws MalformedURLException {
      return new BitcoinJSONRPCClient(rpcUrl);
    }
}

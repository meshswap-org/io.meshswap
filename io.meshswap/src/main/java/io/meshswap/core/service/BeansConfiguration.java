package io.meshswap.core.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;

import java.net.MalformedURLException;

@Configuration
public class BeansConfiguration {

    @Value("${initiator.bitcoin.rpc.url}")
    private String initiatorRpcUrl;

    @Value("${participant.bitcoin.rpc.url}")
    private String participantRpcUrl;

    @Bean
    @Qualifier("initiator")
    public BitcoinJSONRPCClient initiatorRpcClient() throws MalformedURLException {
      return new BitcoinJSONRPCClient(initiatorRpcUrl);
    }

    @Bean
    @Qualifier("initiator")
    public BitcoinJSONRPCClient participantRpcClient() throws MalformedURLException {
      return new BitcoinJSONRPCClient(participantRpcUrl);
    }

    @Bean
    @Qualifier("initiator")
    @Primary
    public BitcoinRPCService initiatorRpcService() throws MalformedURLException {
      BitcoinRPCService service = new BitcoinRPCService();
      service.init(initiatorRpcUrl);
      return service;
    }

    @Bean
    @Qualifier("participant")
    public BitcoinRPCService participantRpcService() throws MalformedURLException {
        BitcoinRPCService service = new BitcoinRPCService();
        service.init(participantRpcUrl);
        return service;
    }
}

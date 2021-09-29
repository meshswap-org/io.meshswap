package io.meshswap.core.util;
import com.google.common.io.CharStreams;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.asList;

@Service
@Slf4j
public class RunCommandViaSsh {

    @Value("${atomicswap.remote.ssh.host}")
    private String sshHost;
    @Value("${atomicswap.remote.ssh.login}")
    private String sshLogin;
    @Value("${atomicswap.remote.ssh.port}")
    private Integer sshPort;

    @Value("${atomicswap.remote.ssh.pkeyfile}")
    String privateKey;

    public List<String> runCommand(String command) {
        Session session = null;
        ChannelExec channel = null;
        try {
            session = setupSshSession();
            session.connect();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            InputStream output = channel.getInputStream();
            channel.connect();

            String result = CharStreams.toString(new InputStreamReader(output));
            return asList(result.split("\n"));

        } catch (JSchException | IOException e) {
            log.error("ssh error: ",e);
        } finally {
            closeConnection(channel, session);
        }
        return null;
    }

    private Session setupSshSession() throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity(privateKey);
        Session session = jsch.getSession(sshLogin, sshHost, sshPort);
        session.setConfig("StrictHostKeyChecking", "no"); // disable check for RSA key
        return session;
    }

    private void closeConnection(ChannelExec channel, Session session) {
        try {
            channel.disconnect();
        } catch (Exception ignored) {
        }
        session.disconnect();
    }
}
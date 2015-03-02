package star.loader;

import java.io.IOException;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.ByteStreams;

public class StarLoderIT {
	
	private static SSHClient ssh;

	@BeforeClass
	public static void start() throws Exception {
		ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
		ssh.connect("localhost", 30122);
		ssh.authPassword("root", "");
		ssh("/etc/init.d/star-loader start");
		
	}

	private static void ssh(String command) throws ConnectionException, TransportException,
			IOException {
		Session session = ssh.startSession();
		Command cmd = session.exec(command);
		ByteStreams.copy(cmd.getInputStream(), System.out);
		session.close();
	}
	
	@AfterClass
	public static void stop() throws Exception {
		ssh("/etc/init.d/star-loader stop");
		ssh.disconnect();
		ssh.close();
	}
	
	
	@Test
	public void testEmodnet() throws Exception {
	}

}

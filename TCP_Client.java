import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class TCP_Client {
	public static void main(String[] args) throws IOException {

		InetAddress addr = null;
		int buffer = 0;
		String com = null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		int port = -1;

		try {
			if (args.length == 2) {
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);

				if (port < 1024 || port > 65535) {
					System.out.println("Fuori range ammissibile");
					System.exit(1);
				}
			} else {
				System.out.println("Errore nei parametri");
				System.exit(1);
			}
		}

		catch (Exception e) {
			System.out.println("Errore nei parametri");
			System.exit(1);
		}

		Socket socket = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		try {
			socket = new Socket(addr, port);
			System.out.println("Creata la socket: " + socket);
		} catch (Exception e) {
			System.err.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
		}

		try {
			inSock = new DataInputStream(socket.getInputStream());
			outSock = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Problemi nella creazione degli stream su socket: ");
			e.printStackTrace();
		}

		try {

			System.out.println("Comando?  c--> chiedi / m--> manda");
			while ((com = stdIn.readLine()) != null) {
				if (!com.equalsIgnoreCase("c") && !com.equalsIgnoreCase("m")) {
					System.out.println("Errato");
					System.out.println("Comando?  c--> chiedi / m--> manda");
					continue;
				}
				outSock.writeUTF(com);

				if (com.equalsIgnoreCase("c")) {
					System.out.println("Nome file? ");
					String nomeFile = stdIn.readLine();

					outSock.writeUTF(nomeFile);

					long dimFile = inSock.readLong();

					if (dimFile < 0)
						System.out.println("File non presente");
					else {
						DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(nomeFile));

						for (int i = 0; i < dimFile; i++) {
							buffer = inSock.read();
							outputStream.write(buffer);
						}

						outputStream.flush();
						outputStream.close();
					}
				} else {
					String nomeFile = inSock.readUTF();
					System.out.println("Richiesto file: " + nomeFile);
					File file = new File(nomeFile);

					if (!file.exists() || !file.isFile())
						outSock.writeLong(-1);
					else {

						outSock.writeLong(file.length());
						System.out.println("File: " + file.getName() + " ,dimensione:" + file.length() + " byte");

						DataInputStream inputStream = new DataInputStream(new FileInputStream(nomeFile));

						while ((buffer = inputStream.read()) >= 0)
							outSock.write(buffer);
						outSock.flush();
						inputStream.close();
					}
				}

				System.out.println("----------------------------------");
				System.out.println("Comando?  c--> chiedi / m--> manda");
			} // loop dir

			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();

		} catch (IOException ste) {
			System.err.println("errore in stream ");
			ste.printStackTrace();
		}
		System.out.println("Terminato con successo");
	}
}

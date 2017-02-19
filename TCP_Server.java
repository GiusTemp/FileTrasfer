import java.io.*;
import java.net.*;

public class TCP_Server {
	private static final int PORT = 54321;

	public static void main(String[] args) throws IOException, InterruptedException {
		int port = -1;
		ServerSocket serverSocket = null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		try {
			if (args.length == 1) {
				port = Integer.parseInt(args[0]);
				if (port < 1024 || port > 65535) {
					System.out.println("Range porta errato");
					System.exit(1);
				}
			} else if (args.length == 0) {
				port = PORT;
			}
		} catch (Exception e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.exit(1);
		}

		try {
			try {
				System.out.println("Connesso su porta " + port);
				serverSocket = new ServerSocket(port);
				serverSocket.setReuseAddress(true);
				System.out.println("Server avviato correttamente");
			} catch (Exception e) {
				System.err.println("Problemi nella creazione della server socket: ");
				e.printStackTrace();
				System.exit(2);
			}

			while (true) {
				Socket clientSocket = null;

				System.out.println("In attesa di richieste...");
				try {
					clientSocket = serverSocket.accept();
					System.out.println("Connessione accettata");

					DataInputStream inSock = new DataInputStream(clientSocket.getInputStream());
					DataOutputStream outSock = new DataOutputStream(clientSocket.getOutputStream());

					String com = inSock.readUTF();

					while( !com.equalsIgnoreCase("S") ) {

					if (com.equalsIgnoreCase("C")) {
						String nomeFile = inSock.readUTF();
						System.out.println("Richiesto file: " + nomeFile);
						File file = new File(nomeFile);
						
						if (!file.exists() || !file.isFile())
							outSock.writeLong(-1);
						else {
							outSock.writeLong(file.length());

							System.out.println("File: " + file.getName() + " ,dimensione: " + file.length() + " byte");
							DataInputStream src = new DataInputStream(new FileInputStream(nomeFile));

							int buffer;
							double perc = 0;
							double trasf = 0;
							GUI gui = new GUI("Name file: " + file.getName() + " ( " + file.length() + " byte)");

							while ((buffer = src.read()) >= 0) {
								outSock.write(buffer);
								trasf++;

								perc = ((trasf / file.length()) * 100);
								gui.refresh(perc);
							}
							outSock.flush();
							gui.exit();
							src.close();
						}

					}

					if (com.equalsIgnoreCase("M")) {
						String nomeFile = null;
						System.out.println("Nome file?");
						nomeFile = stdIn.readLine();
						outSock.writeUTF(nomeFile);

						long dimFile = inSock.readLong();

						if (dimFile < 0) {
							System.out.println("File non presente");
						} else {
							System.out.println("File: " + nomeFile + " ,dimensione: " + dimFile + " byte");
							DataOutputStream outFile = new DataOutputStream(new FileOutputStream(nomeFile));
							int buf = 0;
							for (int i = 0; i < dimFile; i++) {
								buf = inSock.read();
								outFile.write(buf);
							}
							outFile.flush();
							outFile.close();
						}

					}
					try {
						System.out.println("------------------------------");
						com = inSock.readUTF();
					}catch (EOFException e) {
						com = "s";
						System.out.println("Connessione chiusa con successo");
					}
				}//loop2

				} catch (Exception e) {
					System.err.println("Problemi riscontrati: " + e.getMessage());
					e.printStackTrace();
				}
			} // loop

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

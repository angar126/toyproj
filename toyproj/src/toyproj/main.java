package toyproj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {
	
	static String cartellaIn = "..\\in";
	static String cartellaOut = "..\\out";
	static String daCercare = "->";
	static String rxTestata = "-> \\{(\\d+)\\} (.+)";
	
	public static void main(String[] args) throws Exception {
		leggicartella();
	}

	public static void leggicartella() throws FileNotFoundException, IOException {
		File[] inputFiles = new File(cartellaIn).listFiles();

		if (inputFiles != null) {
			for (File inputFile : inputFiles) {
				leggifile(inputFile, cartellaOut);
			}
		} else {
			System.out.println("Nessun file trovato");
		}
	}

	public static void leggifile(File percorso, String cartellaOut) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(percorso));
		String line = reader.readLine();
		String matricola = "";
		String identificativo = "";
		StringBuffer testo = new StringBuffer();
		boolean scartotesto = true;
		while (line != null) {
			if (scartotesto) {
				testo = new StringBuffer();
			}
			boolean testata = boolTestata(daCercare, line);
			
			if (testata && !scartotesto) {
				String percorsoMatricolaOut = creaCartella(matricola);
				creaFileTesto(percorsoMatricolaOut, identificativo, testo.toString());
				testo = new StringBuffer();}
			
			if (testata) {
				Pattern pattern = Pattern.compile(rxTestata);
				Matcher matcher = pattern.matcher(line);

				if (matcher.matches()) {
					matricola = matcher.group(1);
					identificativo = matcher.group(2);
				}
				invioHttpPost(matricola, identificativo);
				scartotesto = false;
			} else {
				testo.append(line).append("\n");
			}
			line = reader.readLine();
		}
		String percorsoMatricolaOut = creaCartella(matricola);
		creaFileTesto(percorsoMatricolaOut, identificativo, testo.toString());

	}

	public static void creaFileTesto(String percorsoMatricolaOut, String identificativo, String testo) throws IOException {
		String outputFilePath = percorsoMatricolaOut + File.separator + identificativo + ".txt";
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
			writer.write(testo.toString());
		}
	}

	public static String creaCartella(String nomeCartella) {
		String percorsoMatricolaOut = cartellaOut + File.separator + nomeCartella;
		File fileMatricolaOut = new File(percorsoMatricolaOut);
		fileMatricolaOut.mkdirs();
		return percorsoMatricolaOut;
	}

	public static Boolean boolTestata(String daCercare, String testo) {
		if (testo.startsWith(daCercare)) {
			return true;
		}
		return false;
	}

	public static void invioHttpPost(String matricola, String id) throws IOException {
		String url = "http://localhost:8080/api/scambio";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		String jsonInputString = String.format("{\"matricola\": \"%s\", \"id\": \"%s\"}", matricola, id);
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		// Invia il JSON nel corpo della richiesta HTTP
		con.setDoOutput(true);

		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);
		}
		System.out.println("URL: " + url);
		System.out.println("JSON: " + jsonInputString);
		int responseCode = con.getResponseCode();
		String responseMessage = con.getResponseMessage();
		System.out.println("Response Code: " + responseCode);
		System.out.println("Response Message: " + responseMessage);

		con.disconnect();

	}
}

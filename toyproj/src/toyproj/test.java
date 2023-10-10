package toyproj;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import org.junit.Test;
public class test {
	@Test
    public void testBoolTestata() {
        assertTrue(main.boolTestata("->", "-> {0001}"));
        assertFalse(main.boolTestata("->", "{0001}"));
    }

    @Test
    public void testCreaCartella() {
        String cartella = main.creaCartella("0001");
        assertNotNull(cartella);
        assertTrue(new File(cartella).exists());
    }

    @Test
    public void testCreaFileTesto() throws IOException {
        String percorsoMatricolaOut = "..//out";
        String identificativo = "idTest";
        String testo = "testotestotestotestotestotesto";
        String outputFilePath = percorsoMatricolaOut + File.separator + identificativo + ".txt";
        main.creaFileTesto(percorsoMatricolaOut, identificativo, testo);
        assertTrue(new File(outputFilePath).exists());
    }
}

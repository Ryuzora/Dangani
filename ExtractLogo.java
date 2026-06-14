import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractLogo {
    public static void main(String[] args) {
        String transcriptPath = "C:\\Users\\ASUS\\.gemini\\antigravity\\brain\\b68f19c1-546b-4b56-9099-30919d14c596\\.system_generated\\logs\\transcript_full.jsonl";
        Pattern pattern = Pattern.compile("data:image/png;base64,([A-Za-z0-9+/=]+)");
        try (BufferedReader reader = new BufferedReader(new FileReader(transcriptPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<svg width=\\\"1020\\\"") && line.contains("data:image/png;base64,")) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String base64Data = matcher.group(1);
                        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
                        try (FileOutputStream fos = new FileOutputStream("app_logo_original.png")) {
                            fos.write(decodedBytes);
                        }
                        System.out.println("Successfully extracted app_logo_original.png");
                        return;
                    }
                }
            }
            System.out.println("Could not find base64 data in transcript.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

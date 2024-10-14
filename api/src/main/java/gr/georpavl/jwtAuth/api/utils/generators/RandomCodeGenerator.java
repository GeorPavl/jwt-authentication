package gr.georpavl.jwtAuth.api.utils.generators;

import java.time.LocalDateTime;
import java.util.Random;

public class RandomCodeGenerator {

  private static final int BOUND = 123;
  private static final Random random = new Random();

  public static Integer generateRandomCode() {
    return BOUND * random.nextInt(BOUND);
  }

  public static String generateReportCode() {
    String code = "";

    String firstChar;
    String secondChar;

    // 2 πρώτοι χαρακτήρες -> 2 τυχαία γράμματα από α εως ω
    String alphabet = "ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩ";

    firstChar = String.valueOf(alphabet.charAt(random.nextInt(alphabet.length())));
    secondChar = String.valueOf(alphabet.charAt(random.nextInt(alphabet.length())));

    // Μετατρέπω το Date στο string που θέλω
    LocalDateTime date = LocalDateTime.now();

    String dateToString = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear();

    code = code + firstChar + secondChar + random.nextInt(1000) + "-" + dateToString;

    return code;
  }
}

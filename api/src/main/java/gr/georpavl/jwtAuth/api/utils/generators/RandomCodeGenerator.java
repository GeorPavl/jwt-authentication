package gr.georpavl.jwtAuth.api.utils.generators;

import java.util.Random;

public class RandomCodeGenerator {

  private static final int BOUND = 123;
  private static final Random random = new Random();

  public static Integer generateRandomCode() {
    return BOUND * random.nextInt(BOUND);
  }
}

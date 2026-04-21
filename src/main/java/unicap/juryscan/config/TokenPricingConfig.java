package unicap.juryscan.config;


public class TokenPricingConfig {

    // R$ 0,20 por token
    public static final int PRICE_PER_TOKEN_IN_CENTS = 20;

    public static int calculateTokens(long amountInCents) {
        return (int) (amountInCents / PRICE_PER_TOKEN_IN_CENTS);
    }

    public static long calculateAmount(int tokens) {
        return (long) tokens * PRICE_PER_TOKEN_IN_CENTS;
    }
}


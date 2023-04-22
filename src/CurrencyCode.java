public enum CurrencyCode {

    DKK(100f),
    NOK(73.50f),
    SEK(70.23f),
    GBP(891.07f),
    EUR(743.93f);

    private float rate;

    CurrencyCode(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }
}

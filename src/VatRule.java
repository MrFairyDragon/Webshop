public class VatRule {

    private CountryCode countryCode;
    private OrderType type;
    private float vatRate;

    VatRule(CountryCode code, OrderType type, float vatRate) {
        this.countryCode = code;
        this.type = type;
        this.vatRate = vatRate;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public float getVatRate() {
        return vatRate;
    }

    public OrderType getType() {
        return type;
    }
}

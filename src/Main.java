import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final float BASE_FREIGHTS = 50;
    public static final float ADDITIONAL_FREIGHTS  = 25;
    public static List<VatRule> vatRuleList = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Please provide atleast 3 arguments");
            return;
        }
        if (!isInteger(args[0], 10)) {
            System.out.println("Please provide a whole number no decimals as first parameter");
            return;
        }
        if (!isFloat(args[1], 10)) {
            System.out.println("Please provide a number as a second parameter");
            return;
        }

        //default values initialized
        CurrencyCode from = CurrencyCode.DKK;
        CurrencyCode too = CurrencyCode.DKK;
        CountryCode vat = CountryCode.NO_WAT;

        for (int i = 3; i < args.length; i++) {
            String[] strings = args[i].split("=");
            switch (strings[0].toUpperCase()) {
                case "--OUTPUT-CURRENCY" -> {
                    too = getEnumFromString(strings[1].toUpperCase(), CurrencyCode.class);
                }
                case "--INPUT-CURRENCY" -> {
                    from = getEnumFromString(strings[1].toUpperCase(), CurrencyCode.class);
                }
                case "--VAT" -> {
                    vat = getEnumFromString(strings[1].toUpperCase(), CountryCode.class);
                }
            }
        }

        //Preferably this would be a SQL database. However, sense I don't have a public server available,
        // it would cause you having to fiddle around with my code or potential config files inorder to get things to work.
        initializeVatRules();

        OrderType type = getEnumFromString(args[2].toUpperCase(), OrderType.class);
        if (type == OrderType.ALL) {
            System.out.println("Third parameter must be of a valid type (Book, Online)");
            return;
        }
        int amount = Integer.parseInt(args[0]);
        float price = Float.parseFloat(args[1]);
        if ((type != null) && (from != null) && (too != null) && (vat != null)) {
            float finalPrice = calcPrice(amount, price, type, from, too, vat);
            finalPrice = Math.round(finalPrice * 100);
            finalPrice = finalPrice / 100;
            DecimalFormat df = new DecimalFormat("#.00");
            System.out.println("The final price is: " + df.format(finalPrice) + " " + too);
        }
    }

    /**
     * @return the enum type of the specified enum from the string
     */
    private static <T extends Enum<T>> T getEnumFromString(String vat, Class<T> enumclass) {
        T code = null;
        try {
            code = T.valueOf(enumclass, vat);
        } catch (Exception exception) {
            System.out.println(enumclass.getName() + " must have a valid parameter");
        }
        return code;
    }

    /**
     * @return the freight cost of all the combined items
     */
    private static float calcFreight(int amount) {
        if (amount <= 10) {
            return BASE_FREIGHTS;
        } else {
            return BASE_FREIGHTS + ((amount / 10) * ADDITIONAL_FREIGHTS);
        }
    }

    /**
     * @return the price of the ordered items
     */
    private static float calcPrice(int amount, float price, OrderType type, CurrencyCode from, CurrencyCode too, CountryCode code) {
        switch (type) {
            case ONLINE -> {
                return price * amount * getTranslationRate(from, too) * getVatRate(code, type);
            }
            case BOOK -> {
                return (calcFreight(amount) + (price * amount)) * getTranslationRate(from, too) * getVatRate(code, type);
            }
            default -> {
                assert false : "Invalid Order Type";
            }
        }
        //This should be impossible in the current code, but future development might change that hence the assertion.
        return price * amount * getTranslationRate(from, too) * getVatRate(code, type);
    }

    /**
     * @return the conversation multiplier based on the two country's
     */
    private static float getTranslationRate(CurrencyCode from, CurrencyCode too) {
        return from.getRate() / too.getRate();
    }

    /**
     * @return the price multiplier based on the country code and the ItemType. If the rule doesn't exist it will return 1.
     */
    private static float getVatRate(CountryCode code, OrderType type) {
        for (VatRule rule : vatRuleList) {
            if ((!rule.getType().equals(OrderType.ALL)) && (!rule.getType().equals(type))) {
                continue;
            }
            if (!rule.getCountryCode().equals(code)) {
                continue;
            }
            return rule.getVatRate();
        }
        return 1f;
    }

    private static void initializeVatRules() {
        vatRuleList.add(new VatRule(CountryCode.DK, OrderType.ALL, 1.25f));
        vatRuleList.add(new VatRule(CountryCode.NO, OrderType.ALL, 1.25f));
        vatRuleList.add(new VatRule(CountryCode.SE, OrderType.ALL, 1.25f));
        vatRuleList.add(new VatRule(CountryCode.GB, OrderType.ALL, 1.20f));
        vatRuleList.add(new VatRule(CountryCode.DE, OrderType.ONLINE, 1.19f));
        vatRuleList.add(new VatRule(CountryCode.DE, OrderType.BOOK, 1.12f));
    }

    //Used to determine if a string is a integer

    /**
     * @return true if string is a integer.
     */
    public static boolean isInteger(String numberString, int radix) {
        if (numberString.isEmpty()) {
            return false;
        }
        for (int i = 0; i < numberString.length(); i++) {
            if (i == 0 && numberString.charAt(i) == '-') {
                if (numberString.length() == 1) {
                    return false;
                } else continue;
            }
            if (Character.digit(numberString.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

    //Used to determine if a string is a float

    /**
     * @return true if string is a float.
     */
    public static boolean isFloat(String numberString, int radix) {
        if (numberString.isEmpty()) {
            return false;
        }
        boolean decimal = true;
        for (int i = 0; i < numberString.length(); i++) {
            if ((i == 0) && (numberString.charAt(i) == '-')) {
                if (numberString.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if ((i != 0) && (numberString.charAt(i) == '.' && decimal)) {
                decimal = false;
                continue;
            }
            if (Character.digit(numberString.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }
}
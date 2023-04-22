public class Main {
    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("Please provide atleast 3 arguments");
            return;
        }
        if(!isInteger(args[0], 10)) {
            System.out.println("Please provide a whole number no decimals as first parameter");
            return;
        }
        if(!isFloat(args[1], 10)) {
            System.out.println("Please provide a number as a second parameter");
            return;
        }
        ItemType type = null;
        try {
            type = ItemType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException exception) {
            System.out.println("Third parameter must be of a valid type (Book, Online)");
        }
        int amount = Integer.parseInt(args[0]);
        float price = Float.parseFloat(args[1]);
        if(type != null) {
            System.out.println("The final price is: "+calcPrice(amount, price, type)+" DKK");
        }
    }

    private static float calcFreight(int amount) {
        if(amount <= 10) {
            return 50f;
        } else {
            return 50f + ((amount/10)*25);
        }
    }

    private static float calcPrice(int amount, float price, ItemType type) {
        switch (type) {
            case ONLINE -> {return price*amount;}
            case BOOK -> {return calcFreight(amount)+(price*amount);}
        }
        System.out.println("Error invalid ItemType");
        return price;
    }

    //Used to determine if a string is a integer

    /**
     *
     * @param numberString
     * @param radix
     * @return true if string is a integer.
     */
    public static boolean isInteger(String numberString, int radix) {
        if(numberString.isEmpty()) {
            return false;
        }
        for(int i = 0; i < numberString.length(); i++) {
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
     *
     * @param numberString
     * @param radix
     * @return true if string is a float.
     */
    public static boolean isFloat(String numberString, int radix) {
        if(numberString.isEmpty()) {
            return false;
        }
        boolean decimal = true;
        for(int i = 0; i < numberString.length(); i++) {
            if (i == 0 && numberString.charAt(i) == '-') {
                if (numberString.length() == 1) {
                    return false;
                } else continue;
            }
            if(i != 0 && numberString.charAt(i) == '.' && decimal) {
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
import java.util.*;
import java.text.*;

/**
* Containing items and calculating price.
*/
public class ShoppingCart{
    /**
     * Tests all class methods.
     */
    public static void main(String[] args){
        // TODO: add tests here
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.99, 5, Item.Type.REGULAR);
        cart.addItem("Banana", 20.00, 4, Item.Type.DISCOUNT);
        cart.addItem("A long piece of toilet paper", 17.20, 1, Item.Type.SALE);
        cart.addItem("Nails", 2.00, 500, Item.Type.REGULAR);
        System.out.println(cart.toString());
    }

    /**
     * Adds new item.
     *
     * @param title item title 1 to 32 symbols
     * @param price item price in cents, > 0, < 1000
     * @param quantity item quantity, from 1 to 1000
     * @param type item type, on enum Item.Type
     *
     * @throws IndexOutOfBoundsException if total items added over 99
     * @throws IllegalArgumentException if some value is wrong
     */
    public void addItem(String title, double price, int quantity, Item.Type type){
        if (title == null || title.length() == 0 || title.length() > 32)
                throw new IllegalArgumentException("Illegal title");
        if (price < 0.01 || price >= 1000.00)
                throw new IllegalArgumentException("Illegal price");
        if (quantity <= 0 || quantity > 1000)
                throw new IllegalArgumentException("Illegal quantity");
        if (items.size() == 99)
            throw new IndexOutOfBoundsException("No more space in cart");
        Item item = new Item();
        item.title = title;
        item.price = price;
        item.quantity = quantity;
        item.type = type;
        items.add(item);
    }

    /**
     * Formats shopping price.
     *
     * @return string as lines, separated with \n,
     * first line: # Item Price Quan. Discount Total
     * second line: ---------------------------------------------------------
     * next lines: NN Title $PP.PP Q DD% $TT.TT
     * 1 Some title $.30 2 - $.60
     * 2 Some very long ti... $100.00 1 50% $50.00
     * ...
     * 31 Item 42 $999.00 1000 - $999000.00
     * end line: ---------------------------------------------------------
     * last line: 31 $999050.60
     *
     * Item title is trimmed to 20 chars adding '...'
     *
     * if no items in cart returns "No items." string.
     */
     public String toString(){
         StringBuffer sb = new StringBuffer();
         if (items.size() == 0)
             return "No items.";
         double total = 0.00;
         sb.append(" # Item Price Quan. Discount Total\n");
         sb.append("---------------------------------------------------------\n");
         for (int i = 0; i < items.size(); i++) {
             Item item = (Item) items.get(i);
             int discount = calculateDiscount(item);
             double itemTotal = item.price * item.quantity * (100.00 - discount) / 100.00;
             appendPaddedRight(sb, String.valueOf(i + 1), 2);
             sb.append(" ");
             appendPaddedLeft(sb, item.title, 20);
             sb.append(" ");
             appendPaddedRight(sb, MONEY.format(item.price), 7);
             sb.append(" ");
             appendPaddedRight(sb, String.valueOf(item.quantity), 4);
             sb.append(" ");
             if (discount == 0)
                appendPaddedRight(sb, " -", 8);
             else {
                 appendPaddedRight(sb, String.valueOf(discount), 7);
                 sb.append("%");
             }
             sb.append(" ");
             appendPaddedRight(sb, MONEY.format(itemTotal), 10);
             sb.append("\n");
             total += itemTotal;
         }
         sb.append("---------------------------------------------------------\n");
         appendPaddedRight(sb, String.valueOf(items.size()), 2);
         sb.append(" ");
         appendPaddedRight(sb, MONEY.format(total), 10);
         return sb.toString();
     }

    // --- private section -----------------------------------------------------
    private static final NumberFormat MONEY;
        static {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            MONEY = new DecimalFormat("$#.00", symbols);
        }
    
    /**
     * Adds to string buffer given string, padded with spaces.
     * @return " str".length() == width
     */
    private static void appendPaddedRight(StringBuffer sb, String str, int width){
        for (int i = str.length(); i < width; i++)
            sb.append(" ");
        sb.append(str);
    }

    /**
     * Adds string to buffer, wills spaces to width.
     * If string is longer than width it is trimmed and ends with '...'
     */
    private static void appendPaddedLeft(StringBuffer sb, String str, int width){
        if (str.length() > width) {
            sb.append(str, 0, width-3);
            sb.append("...");
        } else {
            sb.append(str);
            for (int i = str.length(); i < width; i++)
                sb.append(" ");
        }
    }

    /**
     * Calculates item's discount.
     * For Item.Type.REGULAR discount is 0%;
     * For Item.Type.SECOND discount is 50% if quantity > 1
     * For Item.Type.DISCOUNT discount is 10% + 10% for each full 10 items, but not more than 50%
     * total
     * For Item.Type.SALE discount is 90%
     * For each full 100 items item gets additional 10%, but not more than 80% total
     */
    public static int calculateDiscount(Item item){
        int discount = 0;
        switch (item.type) {
            case SECOND:
                if (item.quantity > 1)
                    discount = 50;
                break;
            case DISCOUNT:
                discount = 10 + item.quantity / 10 * 10;
                if (discount > 50)
                    discount = 50;
                break;
            case SALE:
                discount = 90;
                break;
            default:
                break;
        }
        discount += item.quantity / 100 * 10;
        if (discount > 80)
            discount = 80;
        return discount;
    }

    /** Container for added items */
    private final List items = new ArrayList();
}
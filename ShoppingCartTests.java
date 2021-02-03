import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ShoppingCartTests {

    @ParameterizedTest
    @ValueSource(strings = {"","Very long name___________________________"})
    public void AddItem_InvalidName_AddItemFails()  {
        ShoppingCart cart = new ShoppingCart();

        Exception thrown = assertThrows(IllegalArgumentException.class, () ->
                        cart.addItem("", 10,1,  Item.Type.DISCOUNT));
        assertTrue(thrown.getMessage().contains("Illegal title"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -3, 1050, Integer.MAX_VALUE})
    public void AddItem_InvalidPrice_AddItemFails(double price) {

        ShoppingCart cart = new ShoppingCart();

        Exception thrown = assertThrows(IllegalArgumentException.class, () ->
           cart.addItem("Product", price,1,  Item.Type.DISCOUNT));
        assertTrue(thrown.getMessage().contains("Illegal price"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -3, 1050, Integer.MAX_VALUE})
    public void AddItem_InvalidQuantity_AddItemFails(int quantity)  {

        ShoppingCart cart = new ShoppingCart();

        Exception thrown = assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Test product", 50,quantity,  Item.Type.DISCOUNT));
        assertTrue(thrown.getMessage().contains("Illegal quantity"));

    }

    @Test
    public void AddItem_CartExceedsProductLimit_AddItemFails() {

        ShoppingCart cart = new ShoppingCart();

        Exception thrown = assertThrows(IndexOutOfBoundsException.class, () -> {
            for (int i = 0; i < 100; i++)
            cart.addItem("Test product", 50,1,  Item.Type.DISCOUNT);

        });

        assertTrue(thrown.getMessage().contains("No more space in cart"));
    }


    @ParameterizedTest
    @MethodSource("getDiscountTestCases")
    public void TestCalculateDiscount(Item.Type type, int quantity, int expectedDiscount) {

        var item = new Item();
        item.type = type;
        item.title = "test product";
        item.price = 1000;
        item.quantity = quantity;

        var discount  = ShoppingCart.calculateDiscount(item);
        assertEquals(expectedDiscount,discount);

    }

    private static Stream<Arguments> getDiscountTestCases() {
        return Stream.of(
                arguments(Item.Type.REGULAR,1,0), // there is no discount for REGULAR goods;
                arguments(Item.Type.SECOND,2,50),   //for SECOND goods the discount is 50% if they are bought by more than one;
                arguments(Item.Type.SALE,1,80),
                arguments(Item.Type.REGULAR,100,10),//for each full hundred goods a 10% discount is added, but so that the total discount does not exceed 80%
                arguments(Item.Type.DISCOUNT,1,10), //for DISCOUNT goods the discount is 10% and 10% for each full dozen goods, but not more than 50% in total
                arguments(Item.Type.DISCOUNT,12,20),
                arguments(Item.Type.DISCOUNT,24,30),
                arguments(Item.Type.REGULAR,900,80) //for SALE goods the discount is 80%;
        );
    }

    @Test
    public  void testShoppingCartToString()
    {
        var cart = new ShoppingCart();
        var s  = cart.toString();

        assertEquals("No items.",s);

    }
    private static final NumberFormat MONEY;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        MONEY = new DecimalFormat("$#.00", symbols);
    }

    @Test
    public  void testShoppingCartToStringHasValidFormatting()
    {
        var cart = new ShoppingCart();

        var items = new Item[]
                {
                        new Item("Coffee latte macchiato",10,Item.Type.REGULAR,1),
                        new Item("Cake",15,Item.Type.DISCOUNT,2),
                        new Item("Pizza",200,Item.Type.DISCOUNT,1)
                };

        for (int i = 0; i < items.length;i++)
            cart.addItem(items[i].title,items[i].price,items[i].quantity, items[i].type);

        var s  = cart.toString();

        var lines = s.split("\\r?\\n");

        var headerLine = lines[1];

        System.out.println(s);

        for (int l = 0; l< items.length;l++)
        {

            var lineNumberExpected = String.valueOf(l+ 1);

            var line = lines[l+2];

            var lineNumberActual = line.substring(0,2).trim();
            var itemNameExpected = items[l].title.length()>20?
                    items[l].title.substring(0,17):
                    items[l].title;

            var itemNameActual = line.substring(2,20).trim();

            var priceExpected  = MONEY.format(items[l].price);
            var priceActual  = line.substring(24,31).trim();

            var quantityExpected = String.valueOf(items[l].quantity);
            var quantityActual = line.substring(31,38).trim();

            var discount  = ShoppingCart.calculateDiscount(items[l]);
            var discountExpected = discount ==0 ? "-":String.valueOf(discount)+"%";
            var discountActual  = line.substring(38,45).trim();

            assertEquals(lineNumberExpected,lineNumberActual);
            assertEquals(itemNameExpected,itemNameActual);
            assertEquals(priceExpected, priceActual);
            assertEquals(quantityExpected, quantityActual);
            assertEquals(discountExpected, discountActual);

        }
    }
}

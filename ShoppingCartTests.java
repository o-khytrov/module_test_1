import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ShoppingCartTests {

    @ParameterizedTest
    @ValueSource(strings = {"","Very long name___________________________"})

    public void AddItem_InvalidName_AddItemFails()  {
        ShoppingCart cart = new ShoppingCart();

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("", 10,1,  Item.Type.DISCOUNT),"Illegal title");
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
    public void CalculateDiscount_NoDiscountForRegularGoods(Item.Type type, int quantity, int expectedDiscount) {

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

}

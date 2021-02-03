import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;



public class ShoppingCartTests {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void AddItem_NameIsEmpty_AddItemFails() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Illegal title");

        ShoppingCart cart = new ShoppingCart();
        Item i = new Item();

        cart.addItem("", 10,1,  Item.Type.DISCOUNT);

    }

}

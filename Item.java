public class Item {
    public enum Type { SECOND, REGULAR, SALE, DISCOUNT };
    public String title;
    public float price;
    public int quantity;
    public Type type;

    public Item(String title, float price, Item.Type type, int quantity){
        this.title = title;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
    }
}
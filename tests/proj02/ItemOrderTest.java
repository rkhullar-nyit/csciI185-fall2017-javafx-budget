package proj02;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ItemOrderTest
{
    @Test
    public void build_item()
    {
        ItemOrderBuilder builder = new ItemOrderBuilder();
        ItemOrder itemOrder = builder.setName("oranges").setQuantity(4).setUnitPrice(2.00).build();
        assertEquals("oranges", itemOrder.getName());
        assertEquals(4, itemOrder.getQuantity());
        assertEquals(2, itemOrder.getUnitPrice(), 1e-3);
    }

    @Test
    public void to_string()
    {
        String expected = "ItemOrder {name: oranges, quantity: 4, unitPrice: 2.00}";
        String actual = new ItemOrderBuilder().setName("oranges").setQuantity(4).setUnitPrice(2.00).build().toString();
        assertEquals(expected, actual);
    }

    @Test
    public void equals()
    {
        ItemOrderBuilder builder = new ItemOrderBuilder();
        ItemOrder a = builder.setName("oranges").setQuantity(4).setUnitPrice(2.00).build();
        ItemOrder b = builder.setName("oranges").setQuantity(4).setUnitPrice(2.00).build();
        assertEquals(a, b);
    }

    @Test
    public void not_equals()
    {
        ItemOrderBuilder builder = new ItemOrderBuilder();
        ItemOrder a = builder.setName("oranges").setQuantity(4).setUnitPrice(2.00).build();
        ItemOrder b = builder.setName("apples").setQuantity(4).setUnitPrice(2.00).build();
        assertNotEquals(a, b);
    }

    @Test
    public void parse_string()
    {
        ItemOrderBuilder builder = new ItemOrderBuilder();
        String test_input = "bread,2,4.50";
        ItemOrder expected = builder.setName("bread").setQuantity(2).setUnitPrice(4.50).build();
        ItemOrder actual = ItemOrder.parseString(test_input);
        assertEquals(expected, actual);
    }

    @Test
    public void write_and_read_csv()
    {
        ItemOrderBuilder builder = new ItemOrderBuilder();

        String path = "tests/files/item-orders.csv";

        ItemOrder[] itemOrders = (ItemOrder[]) Arrays.asList(
                builder.setName("apple").setQuantity(2).setUnitPrice(1.5).build(),
                builder.setName("milk").setQuantity(3).setUnitPrice(2.5).build()
        ).toArray();

        ItemOrder.writeCSV(itemOrders, path);
        ItemOrder[] itemOrders2 = ItemOrder.readCSV(path);
        assertArrayEquals(itemOrders, itemOrders2);
    }

    @Test
    public void write_and_read_serial()
    {
        ItemOrderBuilder builder = new ItemOrderBuilder();

        String path = "tests/files/item-orders.ser";

        ItemOrder[] itemOrders = (ItemOrder[]) Arrays.asList(
                builder.setName("apple").setQuantity(2).setUnitPrice(1.5).build(),
                builder.setName("milk").setQuantity(3).setUnitPrice(2.5).build()
        ).toArray();

        ItemOrder.writeSerial(itemOrders, path);
        ItemOrder[] itemOrders2 = ItemOrder.readSerial(path);
        assertArrayEquals(itemOrders, itemOrders2);
    }

}
package proj02;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ItemOrder implements Serializable
{
    private String name = "";
    private double unitPrice = 0;
    private int quantity = 0;

    public String getName()
    {
        return name;
    }

    public double getUnitPrice()
    {
        return unitPrice;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public double getSubTotal()
    {
        return unitPrice * quantity;
    }

    @Override
    public String toString()
    {
        // your code here
        // look at unit test for to_string
        return null;
    }

    @Override
    public boolean equals(Object other)
    {
        // do not modify this method
        return other instanceof ItemOrder && equals((ItemOrder) other);
    }

    public boolean equals(ItemOrder other)
    {
        // your code here
        return false;
    }

    public static ItemOrder parseString(String line)
    {
        // your code here
        // look at unit test for parse_string
        return null;
    }

    private String toCSVString()
    {
        return String.format("%s,%d,%f", name, quantity, unitPrice);
    }

    public static ItemOrder[] readCSV(String path)
    {
        // your code here
        return null;
    }

    public static void writeCSV(ItemOrder[] itemOrders, String path)
    {
        // your code here
    }

    public static ItemOrder[] readSerial(String path)
    {
        // your code here
        return null;
    }

    public static void writeSerial(ItemOrder[] itemOrders, String path)
    {
        // your code here
    }
}

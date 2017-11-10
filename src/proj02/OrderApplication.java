package proj02;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderApplication extends Application
{
    /**
     * TODO
     *
     * Use controllers instead of calling scene lookup to acquire node objects.
     * In the fxml set fx id for nodes as needed, and set the controller for the root element.
     *
     * e.g.: https://stackoverflow.com/questions/36769899/javafx-node-lookup-returning-null-only-for-some-elements-in-parent-loaded-with
     *
     */


    private List<ItemOrder> itemOrderList = new ArrayList<>();
    private ItemOrderBuilder builder = new ItemOrderBuilder();

    private Stage stage;
    private Scene scene;
    private TableView<ItemOrder> table;
    private Button addButton;
    private Text totalText;

    public static void main(String[] args)
    {
        launch(args);
    }

    private void setupTable()
    {
        // lookup table view node
        table = (TableView<ItemOrder>) scene.lookup("#table");
        TableColumn[] columns = new TableColumn[table.getColumns().size()];
        table.getColumns().toArray(columns);

        // acquire table columns
        TableColumn<ItemOrder, String> name_column = columns[0];
        TableColumn<ItemOrder, Integer> quantity_column = columns[1];
        TableColumn<ItemOrder, Double> unit_price_column = columns[2];
        TableColumn<ItemOrder, Double> subtotal_column = columns[3];

        // prep to make column data editable
        name_column.setCellFactory(TextFieldTableCell.forTableColumn());
        quantity_column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        unit_price_column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // make column data viewable
        name_column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getName()));
        quantity_column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getQuantity()));
        unit_price_column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getUnitPrice()));
        subtotal_column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSubTotal()));

        // make column data editable
        name_column.setOnEditCommit(event -> table.getItems().get(event.getTablePosition().getRow()).setName(event.getNewValue()));
        quantity_column.setOnEditCommit(event ->
        {
            int pos = event.getTablePosition().getRow();
            ItemOrder itemOrder = itemOrderList.get(pos);
            itemOrder.setQuantity(event.getNewValue());
            table.getItems().set(pos, itemOrder);
        });
        unit_price_column.setOnEditCommit(event ->
        {
            int pos = event.getTablePosition().getRow();
            ItemOrder itemOrder = itemOrderList.get(pos);
            itemOrder.setUnitPrice(event.getNewValue());
            table.getItems().set(pos, itemOrder);
        });

        table.setItems(FXCollections.observableList(itemOrderList));
    }

    private void setupAddButton()
    {
        addButton = (Button) scene.lookup("#add");
        addButton.setOnAction(event ->
        {
            TextField add_item = (TextField) scene.lookup("#add_item");
            TextField add_quantity = (TextField) scene.lookup("#add_quantity");
            TextField add_unit_price = (TextField) scene.lookup("#add_unit_price");

            String name=""; int quantity=0; double unit_price=0; boolean test=true;

            try
            {
                name = add_item.getText();
                quantity = Integer.parseInt(add_quantity.getText());
                unit_price = Double.parseDouble(add_unit_price.getText());
            } catch (Exception e)
            {
                test = false;
            }
            test &= name.length() > 0;

            if (!test) { System.err.printf("cannot add item\n"); return; }
            add_item.clear(); add_quantity.clear(); add_unit_price.clear();
            ItemOrder itemOrder = builder.setName(name).setQuantity(quantity).setUnitPrice(unit_price).build();
            table.getItems().add(itemOrder);
        });
    }

    private void setupTotal()
    {
        table.getItems().addListener((ListChangeListener<ItemOrder>) change ->
        {
            // totalText would be null until css is applied to node
            totalText = (Text) scene.lookup("#total");
            double total = itemOrderList.stream().mapToDouble(ItemOrder::getSubTotal).sum();
            totalText.setText(String.format("$%.2f", total));
        });
    }

    private void simulateAddItem(String name, double unitPrice, int quantity)
    {
        new Thread(() ->
        {
            TextField add_item = (TextField) scene.lookup("#add_item");
            TextField add_quantity = (TextField) scene.lookup("#add_quantity");
            TextField add_unit_price = (TextField) scene.lookup("#add_unit_price");
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            add_item.setText(name);
            add_quantity.setText(quantity+"");
            add_unit_price.setText(unitPrice+"");
            addButton.fire();
        }).start();
    }

    private void setupMenu()
    {
        MenuBar menuBar = (MenuBar) scene.lookup("#menubar");
        Menu menu = menuBar.getMenus().get(0);

        MenuItem[] menuItems = new MenuItem[menu.getItems().size()];
        menu.getItems().toArray(menuItems);
        MenuItem menu_open = menuItems[0], menu_save = menuItems[1], menu_close = menuItems[2];

        menu_close.setOnAction(event -> System.exit(0));

        menu_open.setOnAction(event ->
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Budget File");
            FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter("Budget Files (*.csv, *.ser)", "*.csv", "*.ser");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null)
                openFile(file);
        });

        menu_save.setOnAction(event ->
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Budget File");
            FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter("Budget Files (*.csv, *.ser)", "*.csv", "*.ser");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File file = fileChooser.showSaveDialog(stage);
            if (file != null)
                saveFile(file);
        });
    }

    private void setPrimaryStage(Stage stage)
    {
        this.stage = stage;
    }

    public void openFile(File file)
    {
        String path = file.getPath();
        String[] arr = path.split("\\.");
        if (arr.length < 2) {System.err.printf("%s has no extension", path); return;}
        ItemOrder[] itemOrders = null;
        switch (arr[arr.length-1])
        {
            case "csv": itemOrders = ItemOrder.readCSV(path); break;
            case "ser": itemOrders = ItemOrder.readSerial(path); break;
        }
        if (itemOrders != null)
        {
            table.getItems().clear();
            Collections.addAll(table.getItems(), itemOrders);
        }
    }

    public void saveFile(File file)
    {
        String path = file.getPath();
        String[] arr = path.split("\\.");

        ItemOrder[] itemOrders = new ItemOrder[itemOrderList.size()];
        itemOrderList.toArray(itemOrders);

        switch (arr[arr.length-1])
        {
            case "csv": ItemOrder.writeCSV(itemOrders, path); break;
            case "ser": ItemOrder.writeSerial(itemOrders, path); break;
        }
    }

    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Budget Assistant");
        Pane root = FXMLLoader.load(getClass().getResource("order-invoice.fxml"));
        scene = new Scene(root, 500,400);
        stage.setScene(scene);
        setPrimaryStage(stage);

        setupTable();
        setupTotal();
        setupAddButton();
        setupMenu();

        stage.show();
        simulateAddItem("milk", 3.00, 4);
    }
}
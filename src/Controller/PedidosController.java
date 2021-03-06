package Controller;

import java.util.HashSet;
import java.util.Set;
import Model.Dialogs;
import Model.Pedidos;
import Model.PedidosModel;
import Model.Venta;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PedidosController implements Initializable {
    // Declaración/Instanciación de variables
    private PedidosModel modelo = new PedidosModel();
    final private Dialogs dialogs = new Dialogs();
    // Declaración de componentes
    @FXML
    private JFXTreeTableView table;
    @FXML
    private JFXDatePicker txtFechaInicial;
    @FXML
    private JFXDatePicker txtFechaFinal;
    @FXML
    private JFXTextField searchField;
    @FXML
    private JFXButton btnFiltrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createTable();
        createTableView();
    }    
    // Campo de búsqueda
    private ChangeListener<String> setupSearchField(final JFXTreeTableView<Pedidos> tableView) {
        return (o, oldVal, newVal) ->
            tableView.setPredicate(personProp -> {
                final Pedidos pedido = personProp.getValue();
                return pedido.nombre.get().contains(newVal);
            });
    }
    // Se crea la estructura de la tabla
    public void createTable() {
        // Se agrega el evento de doble click a la fila, en caso de que se haga, se muestra un modal con los detalles de la venta
        table.setRowFactory( tv -> {
            TreeTableRow<Pedidos> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    try {
                        // Se obtienen los datos de la fila seleccionada
                        Pedidos rowData = row.getItem();
                        // Se prepara la vista DetalleVentas
                        Stage stage = new Stage();
                        FXMLLoader modal = new FXMLLoader(getClass().getResource("/View/DetalleVentas.fxml"));
                        Parent root = modal.load();
                        root.getStylesheets().add("/Resources/main.css");
                        DetalleVentasController dvc = modal.getController();
                         // Envio el ID del pedido para generar factura en caso de querer
                        dvc.setId_venta( Integer.parseInt(rowData.getId_pedido().get() ) );
                        // Se mandan los datos al controlador de la vista
                        dvc.setValues(rowData.getId_pedido().get(), rowData.getNombre().get(), rowData.getFecha().get(), rowData.getTotal().get());
                        stage.setScene(new Scene(root));
                        stage.setTitle("Datalles del pedido");
                        stage.initStyle(StageStyle.TRANSPARENT);
                        stage.getScene().setFill(Color.TRANSPARENT);
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(((Node)btnFiltrar).getScene().getWindow());
                        stage.showAndWait();
                        createTableView();
                    } catch (IOException ex) {
                        Logger.getLogger(PedidosController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            return row ;
        });
        // Evento para el funcionamiento del campo de búsqueda
        searchField.textProperty().addListener(setupSearchField(table));
        // Estructura de la tabla
        JFXTreeTableColumn<Pedidos, String> id = new JFXTreeTableColumn("ID");
        id.setPrefWidth(200);
        id.setCellValueFactory((TreeTableColumn.CellDataFeatures<Pedidos, String> param) -> param.getValue().getValue().id_pedido);

        JFXTreeTableColumn<Pedidos, String> nombre = new JFXTreeTableColumn("Nombre");
        nombre.setPrefWidth(350);
        nombre.setCellValueFactory((TreeTableColumn.CellDataFeatures<Pedidos, String> param) -> param.getValue().getValue().nombre);

        JFXTreeTableColumn<Pedidos, String> fecha = new JFXTreeTableColumn("Fecha");
        fecha.setPrefWidth(350);
        fecha.setCellValueFactory((TreeTableColumn.CellDataFeatures<Pedidos, String> param) -> param.getValue().getValue().fecha);

        JFXTreeTableColumn<Pedidos, String> total = new JFXTreeTableColumn("Total");
        total.setPrefWidth(200);
        total.setCellValueFactory((TreeTableColumn.CellDataFeatures<Pedidos, String> param) -> param.getValue().getValue().total);

        table.getColumns().setAll(id, nombre, fecha, total);
    }
    // Despliegue de información
    public void createTableView() {
        TreeItem<Pedidos> root = new RecursiveTreeItem<>(tableInformation(), RecursiveTreeObject::getChildren);
        table.setRoot(root);
        table.setShowRoot(false);
    }
    // Se obtiene la información por defecto
    public ObservableList<Pedidos> tableInformation() {
        ObservableList<Pedidos> pedidos = FXCollections.observableArrayList();
        modelo.getPedidos().forEach((x) -> {
            pedidos.add(new Pedidos(x.getId_pedido(), x.getNombre(), x.getFecha(), x.total));
        });
        return pedidos;
    }
    // Se asigna la información en un rango de fechas
    public void createTableView(String fechaInicio, String fechaFinal) {
        TreeItem<Pedidos> root = new RecursiveTreeItem<>(tableInformation(fechaInicio, fechaFinal), RecursiveTreeObject::getChildren);
        table.setRoot(root);
        table.setShowRoot(false);
    }
    // Se obtiene la información en un rango de fechas
    public ObservableList<Pedidos> tableInformation(String fechaInicio, String fechaFinal) {
        ObservableList<Pedidos> pedidos = FXCollections.observableArrayList();
        modelo.getPedidos(fechaInicio, fechaFinal).forEach((x) -> {
            pedidos.add(new Pedidos(x.getId_pedido(), x.getNombre(), x.getFecha(), x.total));
        });
        return pedidos;
    }
    // Acción del botón filtrar
    @FXML
    private void btnFiltrarAction(ActionEvent event) {
        // Valida que los campos esten llenos
        if(txtFechaInicial.getValue() != null && txtFechaFinal.getValue() != null) {
            // Valida que la fecha inicial sea antes que la fecha final
            if(txtFechaInicial.getValue().isBefore(txtFechaFinal.getValue())) {
                createTableView(txtFechaInicial.getValue().toString(), txtFechaFinal.getValue().toString());
            } else {
                dialogs.displayMessage((Stage) btnFiltrar.getScene().getWindow(), "Advertencia", "La fecha final no puede ser menor que la fecha inicial", "Ok");
            }
        } else {
                dialogs.displayMessage((Stage) btnFiltrar.getScene().getWindow(), "Advertencia", "Ocupas seleccionar una fecha de inicio y una fecha final para filtrar", "Ok");
        }
    }
    
}

package com.inventory.appinventario.controller;

import animatefx.animation.Tada;
import com.inventory.appinventario.dao.ProductoDAO;
import com.inventory.appinventario.model.Producto;
import com.inventory.appinventario.util.ConexionBD;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.github.sarxos.webcam.Webcam;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrarProductoController implements Initializable {

    @FXML
    private Button btnBuscarImagen;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnTomarFoto;

    @FXML
    private TextField cjcodigo;

    @FXML
    private TextField cjdescripcion;

    @FXML
    private ComboBox<String> cjgenero;

    @FXML
    private TextField cjnombre;

    @FXML
    private TextField cjpreciodistribuidor;

    @FXML
    private TextField cjpreciomayorista;

    @FXML
    private TextField cjpreciounitario;

    @FXML
    private TextField cjreferencia;

    @FXML
    private TextField cjstock;

    @FXML
    private TextField cjstockminimo;

    @FXML
    private TextField cjtalla;

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane root;

    private boolean estadoCamara = false;
    private Webcam selWebCam = null;
    private BufferedImage bufferImage;
    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    private ConexionBD conexionBD = new ConexionBD();
    private ProductoDAO productoDAO;

    private Producto producto = null;

    private boolean STATUS = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        cjgenero.getItems().addAll("HOMBRE", "MUJER", "NIÑO", "NIÑA");

        cjpreciounitario.setTextFormatter(new TextFormatter<>(new StringConverter<Double>(){
            @Override
            public String toString(Double object) {
                if (object != null) {
                    return NumberFormat.getCurrencyInstance().format(object);
                }
                return NumberFormat.getCurrencyInstance().format(0.0);
            }

            @Override
            public Double fromString(String valor) {
                try {
                    return NumberFormat.getCurrencyInstance().parse(valor).doubleValue();
                } catch (ParseException e) {
                    try {
                        return NumberFormat.getCurrencyInstance().parse("$".concat(valor)).doubleValue();
                    } catch (ParseException ex) { ex.printStackTrace(); }
                }
                return 0.0;
            }
        }));

        cjpreciomayorista.setTextFormatter(new TextFormatter<>(new StringConverter<Double>(){

            @Override
            public String toString(Double object) {
                if (object != null) {
                    return NumberFormat.getCurrencyInstance().format(object);
                }
                return NumberFormat.getCurrencyInstance().format(0.0);
            }

            @Override
            public Double fromString(String valor) {
                try {
                    return NumberFormat.getCurrencyInstance().parse(valor).doubleValue();
                } catch (ParseException e) {
                    try {
                        return NumberFormat.getCurrencyInstance().parse("$".concat(valor)).doubleValue();
                    } catch (ParseException ex) { ex.printStackTrace(); }
                }
                return 0.0;
            }
        }));


        cjpreciodistribuidor.setTextFormatter(new TextFormatter<>(new StringConverter<Double>(){

            @Override
            public String toString(Double object) {
                if (object != null) {
                    return NumberFormat.getCurrencyInstance().format(object);
                }
                return NumberFormat.getCurrencyInstance().format(0.0);
            }

            @Override
            public Double fromString(String valor) {
                try {
                    return NumberFormat.getCurrencyInstance().parse(valor).doubleValue();
                } catch (ParseException e) {
                    try {
                        return NumberFormat.getCurrencyInstance().parse("$".concat(valor)).doubleValue();
                    } catch (ParseException ex) { ex.printStackTrace(); }
                }
                return 0.0;
            }
        }));
    }

    @FXML
    void abrirCamara(ActionEvent event) {

        if (Webcam.getWebcams().size() < 1) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "NO HAY CAMARAS DISPONIBLES", ButtonType.OK);
            a.showAndWait();
            return;
        }

        if (!estadoCamara) {
            imageView.imageProperty().unbind();
            imageView.setImage(new Image(getClass().getResourceAsStream("/img/load.gif")));
            Task<Void> webCamIntilizer = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (selWebCam == null) {
                        selWebCam = Webcam.getWebcams().get(0);
                        selWebCam.open();
                    }

                    estadoCamara = true;
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            while (estadoCamara) {
                                try {
                                    if ((bufferImage = selWebCam.getImage()) != null) {
                                        Platform.runLater(() -> {
                                            final Image mainiamge = SwingFXUtils.toFXImage(bufferImage, null);
                                            imageProperty.set(mainiamge);
                                        });
                                        bufferImage.flush();
                                    }
                                } catch (Exception e) {
                                } finally {
                                }
                            }
                            return null;
                        }
                    };
                    Thread th = new Thread(task);
                    th.setDaemon(true);
                    th.start();
                    imageView.imageProperty().bind(imageProperty);

                    return null;
                }
            };
            new Thread(webCamIntilizer).start();
        } else {
            estadoCamara = false;
        }
    }

    @FXML
    void buscarImagen(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(btnBuscarImagen.getScene().getWindow());

        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
            } catch (IOException e) {
                Logger.getLogger(RegistrarProductoController.class.getName()).log(Level.SEVERE, null, e);
                org.controlsfx.control.Notifications.create()
                        .title("Error")
                        .text("No se pudo cargar la imagen")
                        .position(Pos.CENTER)
                        .showError();
            }
        }

    }


    @FXML
    void guardar(ActionEvent event) {

        if (cjnombre.getText().isEmpty()) {
            new Tada(cjnombre).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el nombre del producto").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjcodigo.getText().isEmpty()) {
            new Tada(cjcodigo).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese el codigo del producto").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjreferencia.getText().isEmpty()) {
            new Tada(cjreferencia).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese la referencia del producto").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjdescripcion.getText().isEmpty()) {
            new Tada(cjdescripcion).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese la escripcion del producto").position(Pos.CENTER).showWarning();
            return;
        }
        if (cjtalla.getText().isEmpty()) {
            new Tada(cjtalla).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Ingrese la talla del producto").position(Pos.CENTER).showWarning();
            return;
        }

        double preciounitario = 0;
        double preciomayorista = 0;
        double preciodistribuidor = 0;
        try {
            preciounitario = (NumberFormat.getCurrencyInstance().parse((cjpreciounitario.getText())).doubleValue());
        } catch (ParseException ex) {
            new Tada(cjpreciounitario).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Precio no valido\n" + ex.getMessage()).position(Pos.CENTER).showError();
            return;
        }
        try {
            preciomayorista = (NumberFormat.getCurrencyInstance().parse((cjpreciomayorista.getText())).doubleValue());
        } catch (ParseException ex) {
            new Tada(cjpreciomayorista).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Precio no valido\n" + ex.getMessage()).position(Pos.CENTER).showError();
            return;
        }
        try {
            preciodistribuidor = (NumberFormat.getCurrencyInstance().parse((cjpreciodistribuidor.getText())).doubleValue());
        } catch (ParseException ex) {
            new Tada(cjpreciodistribuidor).play();
            org.controlsfx.control.Notifications.create().title("Aviso").text("Precio no valido\n" + ex.getMessage()).position(Pos.CENTER).showError();
            return;
        }

        if (getProducto() == null) {
            this.producto = new Producto();
        }


        producto.setNombreproducto(cjnombre.getText().trim());
        producto.setCodigodebarras(cjcodigo.getText().trim());
        producto.setReferencia(cjreferencia.getText().trim());
        producto.setStock(Double.parseDouble(cjstock.getText()));
        producto.setStockminimo(Double.parseDouble(cjstockminimo.getText()));
        producto.setDescripcion(cjdescripcion.getText().trim());
        producto.setTalla(cjtalla.getText().trim());
        producto.setGenero(cjgenero.getSelectionModel().getSelectedItem());
        producto.setPreciounitario(preciounitario);
        producto.setPreciodistribuidor(preciodistribuidor);
        producto.setPreciomayorista(preciomayorista);
        producto.setFechaderegistro(LocalDate.now().atStartOfDay());
        producto.setImagen(com.inventory.appinventario.util.Metodos.ImageToByte(imageView.getImage()));

        conexionBD.conectar();
        productoDAO = new ProductoDAO(conexionBD);
        try {
            int guardar = productoDAO.guardar(producto);


            if ( guardar > 0 ){

                setSTATUS(true);
                com.inventory.appinventario.util.Metodos.closeEffect(root);

            }

        } catch (SQLException ex) {
            Logger.getLogger(RegistrarClienteController.class.getName()).log(Level.SEVERE, null, ex);
            org.controlsfx.control.Notifications.create().title("Aviso").text("NO SE PUDO GUARDAR EL PRODCUTO\n" + ex.getMessage()).position(Pos.CENTER).showError();
        }finally {
            this.conexionBD.CERRAR();
        }

    }

    public boolean isSTATUS() {
        return STATUS;
    }

    public void setSTATUS(boolean STATUS) {
        this.STATUS = STATUS;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;

        cjnombre.setText(producto.getNombreproducto().trim());
        cjcodigo.setText(producto.getCodigodebarras());
        cjreferencia.setText(producto.getReferencia());
        cjstock.setText(""+ producto.getStock());
        cjstockminimo.setText(""+ producto.getStockminimo());
        cjdescripcion.setText(producto.getReferencia());
        cjtalla.setText(producto.getReferencia());
        cjgenero.setValue(producto.getGenero());
        cjpreciounitario.setText((NumberFormat.getCurrencyInstance().format(producto.getPreciounitario())));
        cjpreciomayorista.setText((NumberFormat.getCurrencyInstance().format(producto.getPreciomayorista())));
        cjpreciodistribuidor.setText((NumberFormat.getCurrencyInstance().format(producto.getPreciodistribuidor())));
        try {
            if (producto.getImagen() != null) {
                imageView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(producto.getImagen())), null));
            }
        } catch (IOException ex) {
            Logger.getLogger(RegistrarProductoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
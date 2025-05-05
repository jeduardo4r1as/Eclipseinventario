package com.inventory.appinventario.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class SearchComboBox<T> extends TextField {
    private final ObjectProperty<T> selectedItem = new SimpleObjectProperty<>();
    private final SearchPopup<T> popup;

    public SearchComboBox() {
        this.popup = new SearchPopup<>(this);
        this.setPromptText("Seleccionar...");

        // Mostrar el popup al hacer clic
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (!popup.isShowing()) {
                popup.show(this, this.localToScreen(0, this.getHeight()).getX(),
                        this.localToScreen(0, this.getHeight()).getY());
            } else {
                popup.hide();
            }
        });

        // Vincular selección
        popup.selectedItemProperty().addListener((obs, old, nuevo) -> {
            setText(nuevo != null ? nuevo.toString() : "");
            selectedItem.set(nuevo);
        });
    }

    public void setItems(java.util.List<T> items) {
        popup.setItems(items);
    }

    public ObjectProperty<T> selectedItemProperty() {
        return selectedItem;
    }

    public T getSelectedItem() {
        return selectedItem.get();
    }
}
package com.inventory.appinventario.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

public class SearchPopup<T> extends Popup {
    private final TextField searchBox = new TextField();
    private final ListView<T> listView = new ListView<>();
    private final FilteredList<T> filteredList;
    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final ObjectProperty<T> selectedItem = new SimpleObjectProperty<>();

    public SearchPopup(SearchComboBox<T> owner) {
        super();
        filteredList = new FilteredList<>(items, s -> true);
        listView.setItems(filteredList);

        searchBox.setPromptText("Buscar...");
        searchBox.textProperty().addListener((obs, old, text) -> {
            filteredList.setPredicate(item -> item.toString().toLowerCase().contains(text.toLowerCase()));
        });

        listView.setOnMouseClicked(e -> {
            T selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedItem.set(selected);
                this.hide();
            }
        });

        VBox box = new VBox(searchBox, listView);
        box.setPrefSize(250, 200);
        this.getContent().add(box);
        this.setAutoHide(true);
    }

    public void setItems(java.util.List<T> items) {
        this.items.setAll(items);
    }

    public ObjectProperty<T> selectedItemProperty() {
        return selectedItem;
    }
}
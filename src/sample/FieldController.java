package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import static sample.ConnexionMySQL.connectDb;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class FieldController {

    @FXML
    private Button Exit;

    @FXML
    private TableColumn<Field, Integer> ColFldID;

    @FXML
    private TextField FieldID;

    @FXML
    private TextField FldName;

    @FXML
    private TableView<Field> FldTab;

    @FXML
    private TableColumn<Field, String> colName;


    @FXML
    private Button home;

    @FXML
    private TextField search;

    @FXML
    private Button searchBtn;


    public void initialize() {
        //Platform.runLater(() -> FieldID.requestFocus());
        initializeTableColumns();
        displayFields();


    }

    public void updatetable(){

        ColFldID.setCellValueFactory(new PropertyValueFactory<Field, Integer>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<Field, String>("name"));

        fieldList =  getAllFields();
        FldTab.setItems(fieldList);

    }



    private ObservableList<Field> fieldList;
    private int index = -1;
    private Connection conn = null;
    private ResultSet rs = null;
    private PreparedStatement pst = null;



    private void initializeTableColumns() {
        ColFldID.setCellValueFactory(new PropertyValueFactory<>("FieldID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("Name"));
    }

    private void displayFields() {
        ObservableList<Field> fields = getAllFields();
        FldTab.setItems(fields);
    }


    public static ObservableList<Field> getAllFields() {
        Connection conn = connectDb();
        ObservableList<Field> list = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM field");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Field(
                        rs.getInt("Field_ID"),
                        rs.getString("Name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return list;
    }




    private void clearFields() {
        FieldID.clear();
        FldName.clear();
    }

    public void setfieldList(ObservableList<Field> fieldList) {
        this.fieldList = fieldList;
    }

    @FXML
    void OnActionAddBtn(ActionEvent event) {
        conn = connectDb();
        String sql = "INSERT INTO Field Name VALUES (?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, FldName.getText());
            pst.execute();

            JOptionPane.showMessageDialog(null, "Field added");
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        updatetable();
    }



    @FXML
    void OnActionDeleteBtn(ActionEvent event) {

        conn = connectDb();
        String sql = "DELETE FROM Field WHERE Field_ID = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, FieldID.getText());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Field deleted");
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    @FXML
    void OnActionExit(MouseEvent event) {
        Platform.exit();
    }


    @FXML
    void OnActionHomee(MouseEvent event) throws IOException {
        JFxUtils.changeScene(Main.stage, "Home.fxml");
    }

    @FXML
    void OnActionFldTab(ActionEvent event) {

    }

    @FXML
    void OnActionSearchBtn(ActionEvent event) {

        conn = connectDb();
        String searchText = search.getText();

        try {
            String sql = "SELECT * FROM Field WHERE Name LIKE ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, "%" + searchText + "%");
            rs = pst.executeQuery();

            fieldList = FXCollections.observableArrayList();

            while (rs.next()) {
                int FieldID = rs.getInt("Field_ID");
                String FldName = rs.getString("Name");

                Field field = new Field(FieldID, FldName);
                fieldList.add(field);
            }

            FldTab.setItems(fieldList);
            clearFields();

            FldTab.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    Field selectedField = newSelection;
                    FieldID.setText(String.valueOf(selectedField.getFieldID()));
                    FldName.setText(selectedField.getName());
                } else {
                    clearFields();
                }
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    @FXML
    void OnActionUpdateBtn(ActionEvent event) {

        try{
            conn = ConnexionMySQL.connectDb();
            String value1 = FieldID.getText();
            String value2 = FldName.getText();

            String sql = "update Field set Field_ID='"+value1+"',Name = '"+value2+ "' WHERE Field_ID = '"+value1+"'";

            pst = conn.prepareStatement(sql);
            pst.execute();

            JOptionPane.showMessageDialog(null,"Field updated");
            clearFields();
        }catch (Exception e){

            JOptionPane.showMessageDialog(null,e);

        }
    }



    @FXML
    void GetSelected(MouseEvent event) {
        index = FldTab.getSelectionModel().getSelectedIndex();

        if(index <= -1){

            return;
        }

        FieldID.setText(ColFldID.getCellData(index).toString());
        FldName.setText(colName.getCellData(index));

    }


    @FXML
    void handleKeyPress(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        if (keyCode == KeyCode.UP) {
            focusPreviousField();
        } else if (keyCode == KeyCode.DOWN) {
            focusNextField();
        }
    }

    private void focusPreviousField() {
        if (FldName.isFocused()) {
            FieldID.requestFocus();
        } else if (FieldID.isFocused()) {

        }
    }

    private void focusNextField() {
        if (FieldID.isFocused()) {
            FldName.requestFocus();
        } else if (FldName.isFocused()) {

        }
    }


}

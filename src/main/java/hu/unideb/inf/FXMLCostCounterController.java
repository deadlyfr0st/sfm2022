package hu.unideb.inf;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;

import java.time.LocalDate;

public class FXMLCostCounterController implements Initializable {

    PersonData personData;
    FinancialData financialData;
    JpaPersonDataDAO jpaPersonDataDAO = new JpaPersonDataDAO();

    JpaFinancialDataDAO jpaFinancialDataDAO = new JpaFinancialDataDAO();

    @FXML
    private Button handleAverageButtonPushed;
    @FXML
    private Button handleDataUpLoadButtonPushed;
    @FXML
    private Button handleSearchButtonPushed;
    @FXML
    private Button handleRegisterButtonPushed;
    @FXML
    private TextField handleNameTyping;
    @FXML
    private TextField handleDateFromTyping;
    @FXML
    private TextField handleDateToTyping;
    @FXML
    private TextField handleDateTyping;
    @FXML
    private TextField handlePriceTyping;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // CostTypeChoiceBox.getItems().addAll(Arrays.toString(financialData.getTypeOfCost()));
        CostTypeChoiceBox.getItems().addAll(costType);
        // CostTypeChoiceBox1.getItems().addAll(Arrays.toString(financialData.getTypeOfCost()));
        CostTypeChoiceBox1.getItems().addAll(costType);
        nameChoiceBox.getItems().addAll(jpaPersonDataDAO.getPersonData());
        nameChoiceBox1.getItems().addAll(jpaPersonDataDAO.getPersonData());
        nameChoiceBox.setConverter(new StringConverter<PersonData>() {
            @Override
            public String toString(PersonData personData) {
                return personData.getName();
            }

            @Override
            public PersonData fromString(String s) {
                return jpaPersonDataDAO.getPersonData().stream().filter(personData -> s.equals(personData.getName())).findAny().orElse(null);
            }
        });

        /*
        NameColumn.setCellFactory(new PropertyValueFactory<PersonData, String>(name));
        TypeColumn.setCellFactory(new PropertyValueFactory<FinancialData, String>(type));
        PriceColumn.setCellFactory(new PropertyValueFactory<FinancialData, Integer>(price));
        DateFromColumn.setCellFactory(new PropertyValueFactory<FinancialData, LocalDate>(dateFrom));
        DateToColumn.setCellFactory(new PropertyValueFactory<FinancialData, LocalDate>(dateFrom));
*/
    }


    //végleges adatokra kell cserélni ez csak a teszthez van
    private String[] costType = {"Élelmiszer", "TRAVEL", "Szórakozás"};

    private String nameInput;
    private LocalDate dateInput;
    private int priceInput;
    private String tyepchoice;
    private String namechoice;


    // választható opciók (lenyíló fülek)

    @FXML
    private ChoiceBox<String> CostTypeChoiceBox; //költség típus lenyíló menü a költség felvétel fülön

    @FXML
    private ChoiceBox<String> CostTypeChoiceBox1;  //költség típus lenyíló menü a keresés fülön

    @FXML
    private ChoiceBox<PersonData> nameChoiceBox; // név lenyíló menü a költség felvétel fülön

    @FXML
    private ChoiceBox<PersonData> nameChoiceBox1; // név lenyíló menü a keresés fülön

    // A táblázatok vezérlése

    @FXML
    private TableView<PersonData> tableView;
    @FXML
    private TableColumn<FinancialData, LocalDate> DateFromColumn;
    @FXML
    private TableColumn<FinancialData, LocalDate> DateToColumn;
    @FXML
    private TableColumn<PersonData, String> NameColumn;
    @FXML
    private TableColumn<FinancialData, Integer> PriceColumn;
    @FXML
    private TableColumn<FinancialData, String> TypeColumn;

    Alert alert = new Alert(AlertType.INFORMATION);

    // Gombok vezérlése

    @FXML
    void handleRegisterButtonPushed(ActionEvent event) throws Exception {
        if (!handleNameTyping.getText().isEmpty()) {
            nameInput = handleNameTyping.getText();
            if (!jpaPersonDataDAO.getAllPersonName().contains(nameInput)) {
                personData = new PersonData();
                personData.setName(nameInput);
                jpaPersonDataDAO.savePersonData(personData);
                alert.setTitle("Sikeresen regisztráltál!");
                alert.setContentText("Navigálj a feltöltés oldalra és válaszd ki a neved a lenyíló menüből.");
                alert.showAndWait();
//                nameChoiceBox.getItems().addAll(jpaPersonDataDAO.getAllPersonName());
//                nameChoiceBox1.getItems().addAll(jpaPersonDataDAO.getAllPersonName());
            } else {
                alert.setTitle("Már regisztráltál!");
                alert.setContentText("Navigálj a feltöltés oldalra és válaszd ki a neved a lenyíló menüből.");
                alert.showAndWait();
            }
        } else {
            alert.setTitle("Hiányzó adat!");
            alert.setHeaderText(null);
            alert.setContentText("Név megadása szükséges!");
            alert.showAndWait();
        }
    }

    @FXML
    void handleDataUpLoadButtonPushed(ActionEvent event) throws Exception {
        if (!handleDateTyping.getText().isEmpty() && !CostTypeChoiceBox.getSelectionModel().isEmpty()
                && !handlePriceTyping.getText().isEmpty() && !nameChoiceBox.getSelectionModel().isEmpty()) {

            dateInput = LocalDate.parse(handleDateTyping.getText());
            priceInput = Integer.parseInt(handlePriceTyping.getText());

            tyepchoice = CostTypeChoiceBox.getValue();


            /////Meg kell keresni azt a persont akit kiválasztottunk a listából,
            // és annak a financialdatalist-jét kell beállítani

            financialData = new FinancialData();

            financialData.setCost(priceInput);
            financialData.setCostType(FinancialData.typeOfCost.valueOf(tyepchoice));
            jpaFinancialDataDAO.saveFinancialData(financialData);

            this.personData = nameChoiceBox.getValue();
            this.personData.getFinancialDataList().add(financialData);

            jpaPersonDataDAO.savePersonData(personData);
            nameChoiceBox.getValue().getFinancialDataList().add(financialData);

            //financialData.setDateOfPurchase(LocalDate.parse());

            // olyan metódus kell ami a táblázatba rögzíti a kapott értékeket !!!
        } else {
            if (nameChoiceBox1.getSelectionModel().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Név megadása szükséges!");
                alert.setContentText("Kérjük a lenyíló listából válasza ki a megfelő nevet!");
                alert.showAndWait();
            }
            if (CostTypeChoiceBox1.getSelectionModel().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Költség kategória megadása szükséges!");
                alert.setContentText("Kérjük a lenyíló listából válaszon kategóriát!");
                alert.showAndWait();
            }
            if (handleDateTyping.getText().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Dátum megadása szükséges!");
                alert.setContentText("Dátum megadásánál figyeljen a helyes formátumra!\n Példa: 2022.01.01");
                alert.showAndWait();
            }
            if (handlePriceTyping.getText().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Összeg megadása szükséges!");
                alert.setContentText("Összeg megadásánál figyeljen a helyes formátumra!\n Példa: 12500");
                alert.showAndWait();
            }
        }

    }

    /*
    @FXML
    void handleDateFromTyping(ActionEvent event) {
    }
    @FXML
    void handlePriceTyping(ActionEvent event) {
    }
    @FXML
    void handleDateToTyping(ActionEvent event) {
    }
    @FXML
    void handleDateTyping(ActionEvent event) {
    }
    @FXML
    void handleNameTyping(ActionEvent event) {
    }
    */
    @FXML
    void handleSearchButtonPushed(ActionEvent event) {
        if (!handleDateFromTyping.getText().isEmpty() && !CostTypeChoiceBox1.getSelectionModel().isEmpty()
                && !handleDateToTyping.getText().isEmpty() && !nameChoiceBox1.getSelectionModel().isEmpty()) {
            //NameColumn.setText(jpaPersonDataDAO.);
            System.out.println("műxik");


            // olyan metódus kell ami a táblázatba  !!!


        } else {
            if (nameChoiceBox1.getSelectionModel().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Név megadása szükséges!");
                alert.setContentText("Kérjük a lenyíló listából válasza ki a megfelő nevet!");
                alert.showAndWait();
            }
            if (CostTypeChoiceBox1.getSelectionModel().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Költség kategória megadása szükséges!");
                alert.setContentText("Kérjük a lenyíló listából válaszon kategóriát!");
                alert.showAndWait();
            }
            if (handleDateFromTyping.getText().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Kezdő dátum megadása szükséges!");
                alert.setContentText("Dátum megadásánál figyeljen a helyes formátumra!\n Példa: 2022.01.01");
                alert.showAndWait();
            }
            if (handleDateToTyping.getText().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Befejező dátum megadása szükséges!");
                alert.setContentText("Dátum megadásánál figyeljen a helyes formátumra!\n Példa: 2022.01.01");
                alert.showAndWait();
            }
        }
    }

    @FXML
    void handleAverageButtonPushed(ActionEvent event) {
        if (!handleDateFromTyping.getText().isEmpty() && !CostTypeChoiceBox1.getSelectionModel().isEmpty()
                && !handleDateToTyping.getText().isEmpty() && !nameChoiceBox1.getSelectionModel().isEmpty()) {


            // olyan metódus kell ami a táblázatba  !!!


        } else {
            if (nameChoiceBox1.getSelectionModel().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Név megadása szükséges!");
                alert.setContentText("Kérjük a lenyíló listából válasza ki a megfelő nevet!");
                alert.showAndWait();
            }
            if (CostTypeChoiceBox1.getSelectionModel().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Költség kategória megadása szükséges!");
                alert.setContentText("Kérjük a lenyíló listából válaszon kategóriát!");
                alert.showAndWait();
            }
            if (handleDateFromTyping.getText().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Kezdő dátum megadása szükséges!");
                alert.setContentText("Dátum megadásánál figyeljen a helyes formátumra!\n Példa: 2022.01.01");
                alert.showAndWait();
            }
            if (handleDateToTyping.getText().isEmpty()) {
                alert.setTitle("Hiányzó adat!");
                alert.setHeaderText("Befejező dátum megadása szükséges!");
                alert.setContentText("Dátum megadásánál figyeljen a helyes formátumra!\n Példa: 2022.01.01");
                alert.showAndWait();
            }
        }
    }
}

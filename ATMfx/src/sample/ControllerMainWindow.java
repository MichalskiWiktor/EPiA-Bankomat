package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class ControllerMainWindow {
    @FXML private Label boxID;
    @FXML private Label boxMoney;
    @FXML private Label boxCardNumber;
    @FXML private TextField moneyAddTextField;
    @FXML private TextField moneyRemoveTextField;
    @FXML private TextField moneyRemoveToAnotherAccountTextField;
    @FXML private TextField getID;
    @FXML private PasswordField boxOldPin;
    @FXML private PasswordField boxNewPin;
    @FXML private ListView listOperationHistory;
    private int pin;
    private int id;
    private int card_number;
    private int[][] data;
    ArrayList<Integer> ids = new ArrayList();
    public void loadData(){
        this.boxID.setText(String.valueOf(this.id));
        for(int i=0;i<this.data.length;i++){
            if(this.id==this.data[i][0]){
                this.boxMoney.setText(String.valueOf(this.data[i][3]));
            }
        }
        this.boxCardNumber.setText(String.valueOf(this.card_number));
    }
    public void getDataFromDatabase(){
        try{
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
            Statement myStat = myConn.createStatement();
            ResultSet myRes = myStat.executeQuery("select * from customers");
            ArrayList <Integer> cardNumber = new ArrayList();
            ArrayList <Integer> pin = new ArrayList();
            ArrayList <Integer> ammount = new ArrayList();
            while(myRes.next()){
                ids.add(myRes.getInt("id"));
                cardNumber.add(myRes.getInt("card_number"));
                pin.add(myRes.getInt("pin"));
                ammount.add(myRes.getInt("ammount"));
            }
            this.data = new int[ids.size()][4];
            for(int i=0;i<ids.size();i++){
                this.data[i][0] = ids.get(i);
                this.data[i][1] = cardNumber.get(i);
                this.data[i][2] = pin.get(i);
                this.data[i][3] = ammount.get(i);
            }
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public void transferMessage(int pin, int id, int card_number){
        this.pin = pin;
        this.id = id;
        this.card_number = card_number;
        this.getDataFromDatabase();
        this.loadData();
        this.loadOperationHistory();
    }
    public void addMoneyToAccount(){
        this.moneyOperation('+');
    }
    public void removeMoneyFromAccount(){
        this.moneyOperation('-');
    }
    public void moneyOperation(char operation){
        try{
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
            Statement myStat = myConn.createStatement();
            if(operation=='+'){
                if(Integer.parseInt(this.moneyAddTextField.getText())>0){
                    for(int i=0;i<this.data.length;i++){
                        if(this.pin==this.data[i][2] && this.card_number == this.data[i][1]){
                            this.data[i][3] += Integer.parseInt(this.moneyAddTextField.getText());
                            myStat.executeUpdate("update customers SET ammount="+ this.data[i][3] +" where id=" + this.data[i][0]);
                            this.addOperationToList("brak", "wplata", this.data[i][3], this.id);
                        }
                    }
                }
                else{
                    this.createPopUpWindow("Błędna ilość środków!!");
                }
            }else if(operation=='-'){
                if(Integer.parseInt(this.moneyRemoveTextField.getText())>0){
                    for(int i=0;i<this.data.length;i++){
                        if(this.pin==this.data[i][2] && this.card_number == this.data[i][1]){
                            if(this.data[i][3] - Integer.parseInt(this.moneyRemoveTextField.getText()) < 0){
                                this.createPopUpWindow("Brak środków!!!");
                            }else{
                                this.data[i][3] -= Integer.parseInt(this.moneyRemoveTextField.getText());
                                myStat.executeUpdate("update customers SET ammount="+ this.data[i][3] +" where id=" + this.data[i][0]);
                                this.addOperationToList("brak", "wyplata", this.data[i][3], this.id);
                            }
                        }
                    }
                }
                else{
                    this.createPopUpWindow("Błędna ilość środków!!");
                }
            }
            this.loadData();
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public void makeTransfer(){
        try {
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
            Statement myStat = myConn.createStatement();
            if(this.ids.contains(Integer.parseInt(this.getID.getText()))){
                if(Integer.parseInt(this.moneyRemoveToAnotherAccountTextField.getText())>0){
                    for(int i=0;i<this.data.length;i++){
                        if(this.pin == this.data[i][2] && this.card_number == this.data[i][1]) {
                            if (this.data[i][3] - Integer.parseInt(this.moneyRemoveToAnotherAccountTextField.getText()) < 0) {
                                this.createPopUpWindow("Brak środków!!!");
                            } else {
                                this.data[i][3] -= Integer.parseInt(this.moneyRemoveToAnotherAccountTextField.getText());
                                myStat.executeUpdate("update customers SET ammount=" + this.data[i][3] + " where id=" + this.id);
                                this.addOperationToList("brak", "przelew wychodzący",this.data[i][3], this.id);
                                int k=0;
                                for(int j=0;j<this.data.length;j++){
                                    if (Integer.parseInt(this.getID.getText()) == this.data[j][0]) {
                                        this.data[j][3] += Integer.parseInt(this.moneyRemoveToAnotherAccountTextField.getText());
                                        myStat.executeUpdate("update customers SET ammount=" + this.data[j][3] + " where id=" + Integer.parseInt(this.getID.getText()));
                                        this.addOperationToList("brak", "przelew przychodzący",this.data[i][3], Integer.parseInt(this.getID.getText()));
                                    }
                                }
                            }
                        }
                    }
                }else{
                    this.createPopUpWindow("Błędna kwota!!");
                }
            }else{
                this.createPopUpWindow("Błędne ID!!");
            }
            this.loadData();
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public void loadOperationHistory(){
        try{
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
            Statement myStat = myConn.createStatement();
            ResultSet myRes = myStat.executeQuery("select * from operation_history where id_customer="+ this.id);
            ArrayList<Integer> id = new ArrayList();
            ArrayList <String> name = new ArrayList();
            ArrayList <Integer> ammount = new ArrayList();
            ArrayList <String> type = new ArrayList();
            ArrayList <String> date = new ArrayList();
            while(myRes.next()){
                id.add(myRes.getInt("id"));
                name.add(myRes.getString("name"));
                ammount.add(myRes.getInt("ammount"));
                type.add(myRes.getString("type"));
                date.add(myRes.getString("date"));
            }
            this.listOperationHistory.getItems().clear();
            for(int i=0;i<id.size();i++){
                this.listOperationHistory.getItems().add("ID:" + id.get(i) + "  Nazwa:" + name.get(i) + "  Środki:" + ammount.get(i) + "  Rodzaj:" + type.get(i) + "  Data:"+ date.get(i));
                this.listOperationHistory.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            }
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public void addOperationToList(String text, String operation_type, int ammount, int id){
        try {
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
            Statement myStat = myConn.createStatement();
            LocalDate myObj = LocalDate.now();
            myStat.execute("INSERT INTO operation_history(name, ammount, type, date, id_customer) VALUES('" + text + "', " + ammount + ", '" + operation_type + "', '"+myObj+"', "+ id +");");
            this.loadOperationHistory();
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public void changePin(){
        if(Integer.parseInt(this.boxOldPin.getText()) == this.pin){
            for(int i=0;i<this.data.length;i++){
                if(this.id==this.data[i][0] && this.card_number == this.data[i][1]){
                    try{
                        Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
                        Statement myStat = myConn.createStatement();
                        if(this.boxNewPin.getText().length()==4 && Integer.parseInt(this.boxNewPin.getText())>=1000 &&  Integer.parseInt(this.boxNewPin.getText())<=9999){
                            this.pin  = Integer.parseInt(this.boxNewPin.getText());
                            this.data[i][2] = this.pin;
                            myStat.executeUpdate("update customers SET pin="+ this.data[i][2] +" where id=" + this.data[i][0]);
                        }
                        else{
                            this.createPopUpWindow("Błędny nowy pin");
                        }
                    }
                    catch(Exception exc){
                        exc.printStackTrace();
                    }

                }
            }
        }
        else{
            this.createPopUpWindow("Błędny stary pin");
        }
    }
    public void createPopUpWindow(String message){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PopUpWindow.fxml"));
            Parent rootl = fxmlLoader.load();

            ControllerPopUpWindow scene4Controller = fxmlLoader.getController();
            scene4Controller.transferMessage(message);

            Stage stage = new Stage();
            stage.setTitle("PopUp Window");
            stage.setScene(new Scene(rootl, 235, 92));
            stage.getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.show();
        } catch(Exception e){
            System.out.print("New window can not be load!!!");
        }
    }
}

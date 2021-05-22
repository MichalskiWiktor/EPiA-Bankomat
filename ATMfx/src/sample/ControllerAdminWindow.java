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
public class ControllerAdminWindow {
    @FXML private TextField boxMoneyAdd;
    @FXML private TextField boxMoneyRemove;
    @FXML private TextField boxMoneyAddID;
    @FXML private TextField boxMoneyRemoveID;
    @FXML private TextField boxIDToAccountOfNewPin;
    @FXML private PasswordField boxNewPin;
    @FXML private ListView listAccounts;
    private int[][] data;
    ArrayList<Integer> ids = new ArrayList();
    @FXML public void initialize(){
        this.getDataFromDatabase();
        this.loadUsersList();
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
    public void loadUsersList(){
        for(int i=0;i<this.data.length;i++){
            this.listAccounts.getItems().add("ID:" + this.data[i][0] + "  Numer karty:" + this.data[i][1] + "  PIN:" + this.data[i][2] + "  Środki:" + this.data[i][3]);
            this.listAccounts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
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
                if(this.ids.contains(Integer.parseInt(this.boxMoneyAddID.getText()))){
                    if(Integer.parseInt(this.boxMoneyAdd.getText())>0){
                        for(int i=0;i<this.data.length;i++){
                            if(Integer.parseInt(this.boxMoneyAddID.getText()) == this.data[i][0]){
                                this.data[i][3] += Integer.parseInt(this.boxMoneyAdd.getText());
                                myStat.executeUpdate("update customers SET ammount="+ this.data[i][3] +" where id=" + this.data[i][0]);
                                this.addOperationToList("brak", "wplata", Integer.parseInt(this.boxMoneyAdd.getText()), this.data[i][0]);
                            }
                        }
                    }
                    else{
                        this.createPopUpWindow("Błędna ilość środków!!");
                    }
                }
                else{
                    this.createPopUpWindow("Błędne ID!!");
                }
            }else if(operation=='-'){
                if(this.ids.contains(Integer.parseInt(this.boxMoneyRemoveID.getText()))){
                    if(Integer.parseInt(this.boxMoneyRemove.getText())>0){
                        for(int i=0;i<this.data.length;i++){
                            if(Integer.parseInt(this.boxMoneyRemoveID.getText()) == this.data[i][0]){
                                if(this.data[i][3] - Integer.parseInt(this.boxMoneyRemove.getText()) < 0){
                                    this.createPopUpWindow("Brak środków!!");
                                }else{
                                    this.data[i][3] -= Integer.parseInt(this.boxMoneyRemove.getText());
                                    myStat.executeUpdate("update customers SET ammount="+ this.data[i][3] +" where id=" + this.data[i][0]);
                                    this.addOperationToList("brak", "wyplata", Integer.parseInt(this.boxMoneyRemove.getText()), this.data[i][0]);
                                }
                            }
                        }
                    }
                    else{
                        this.createPopUpWindow("Błędna ilość środków!!");
                    }
                }
                else{
                    this.createPopUpWindow("Błędne ID!!");
                }
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
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public void addUser(){
    }
    public void delUser(){
        String selectedItem = String.valueOf(this.listAccounts.getSelectionModel().getSelectedItem());
        this.listAccounts.getItems().remove(selectedItem);
        String id="";
        for(int i=3;i<selectedItem.length();i++){
            if(selectedItem.charAt(i)==' ')break;
            else id += String.valueOf(selectedItem.charAt(i));
        }
        for(int i=0;i<this.data.length;i++){
            if(this.data[i][0]==Integer.parseInt(id)){
                try {
                    Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
                    Statement myStat = myConn.createStatement();
                    myStat.execute("DELETE FROM customers WHERE id=" + this.data[i][0]);
                    this.resetWindow();
                }
                catch(Exception exc){
                    exc.printStackTrace();
                }
            }
        }
    }
    public void changePin(){
        try{
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
            Statement myStat = myConn.createStatement();
            if(this.boxNewPin.getText().length()==4 && Integer.parseInt(this.boxNewPin.getText())>=1000 &&  Integer.parseInt(this.boxNewPin.getText())<=9999){
                if(this.ids.contains(Integer.parseInt(this.boxIDToAccountOfNewPin.getText()))){
                    myStat.executeUpdate("update customers SET pin="+ Integer.parseInt(this.boxNewPin.getText()) +" where id=" + Integer.parseInt(this.boxIDToAccountOfNewPin.getText()));
                    createPopUpWindow("PIN został zmieniony!!");
                }
                else{
                    createPopUpWindow("Błędne ID!!");
                }
            }
            else{
                createPopUpWindow("Błędne PIN!!");
            }
        }
        catch(Exception exc){
            exc.printStackTrace();
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
    public void resetWindow(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdminWindow.fxml"));
            Parent rootl = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Bankomat-Konto Administracyjne");
            stage.setScene(new Scene(rootl, 480, 225));
            stage.getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.show();
        } catch(Exception e){
            System.out.print("New window can not be load!!!");
        }
        this.closeThisWindow();
    }
    public void closeThisWindow(){
        Stage stage2 = (Stage) this.boxMoneyAdd.getScene().getWindow();
        stage2.close();
    }
}

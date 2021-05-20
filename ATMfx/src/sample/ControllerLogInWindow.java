package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ControllerLogInWindow {
    @FXML
    private Label pinLabel;
    @FXML
    private Label cardNumberLabel;
    @FXML
    private PasswordField pinTextField;
    @FXML
    private TextField cardNumberTextField;
    private int pin;
    private int id;
    private int card_number;
    private int[][] data;
    public void openNewWindow(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent rootl = (Parent) fxmlLoader.load();

            ControllerMainWindow scene2Controller = fxmlLoader.getController();
            scene2Controller.transferMessage(this.pin, this.id, this.card_number);

            Stage stage = new Stage();
            stage.setTitle("Bankomat");
            stage.setScene(new Scene(rootl, 480, 225));
            stage.getScene().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            stage.show();
        } catch(Exception e){
            System.out.print("New window can not be load!!!");
        }
    }
    public void getDataFromDatabase(){
        try{
            Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "");
            Statement myStat = myConn.createStatement();
            ResultSet myRes = myStat.executeQuery("select * from customers");
            ArrayList<Integer> id = new ArrayList();
            ArrayList <Integer> cardNumber = new ArrayList();
            ArrayList <Integer> pin = new ArrayList();
            ArrayList <Integer> ammount = new ArrayList();
            while(myRes.next()){
                id.add(myRes.getInt("id"));
                cardNumber.add(myRes.getInt("card_number"));
                pin.add(myRes.getInt("pin"));
                ammount.add(myRes.getInt("ammount"));
            }
            this.data = new int[id.size()][4];
            for(int i=0;i<id.size();i++){
                this.data[i][0] = id.get(i);
                this.data[i][1] = cardNumber.get(i);
                this.data[i][2] = pin.get(i);
                this.data[i][3] = ammount.get(i);
            }
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public void getPinAndCardNumber(){
        this.getDataFromDatabase();
        this.pin  = Integer.parseInt(this.pinTextField.getText());
        this.card_number = Integer.parseInt(this.cardNumberTextField.getText());
        if(!this.isPinAndCardNumberCorrect()){
            this.pinLabel.setText("!!Zły PIN!!");
            this.cardNumberLabel.setText("!!Lub Zły Numer!!");
        }
        else{
            this.openNewWindow();
        }
    }
    private boolean isPinAndCardNumberCorrect(){
        for(int i=0;i<this.data.length;i++){
            if(this.pin==this.data[i][2] && this.card_number == this.data[i][1]){
                this.id=this.data[i][0];
                return true;
            }
        }
        return false;
    }
}

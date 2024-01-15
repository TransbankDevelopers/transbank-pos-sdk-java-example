package cl.transbank.possdk.example;

import cl.transbank.pos.exceptions.common.*;
import cl.transbank.pos.exceptions.integrado.*;
import cl.transbank.pos.responses.common.*;
import cl.transbank.pos.responses.integrado.*;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {

    private int total = 0;

    private final Random rng = new Random();

    private boolean connectState = false;

    private String selectedPort;

    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);

    @FXML
    private TextArea txtAreaRegister;

    @FXML
    private TextField txtAmount;

    @FXML
    private TextField txtVoucher;

    @FXML
    private TextField txtRefundTicket;

    @FXML
    private Label lblSelectedPort;

    @FXML
    private Label lblStatusMessage;

    @FXML
    private Pane menuPane;

    @FXML
    private Pane salePane;

    @FXML
    private Pane refundPane;

    @FXML
    private Pane otherPane;

    @FXML
    private Button btnConnect;

    @FXML
    private ComboBox<String> cmbListPorts;

    @FXML
    private VBox everything;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getPorts();
        App.getPos().setOnIntermediateMessageReceivedListener(this::onIntermediateMessageReceived);
    }

    private void onIntermediateMessageReceived(IntermediateResponse response) {
        print(response.getResponseMessage());
        Platform.runLater(() -> alert.setHeaderText(response.getResponseMessage()));
    }

    @FXML
    private void connectDisconnect() {
        if(!connectState) {
            openPort(selectedPort);
            return;
        }

        closePort();
    }

    @FXML
    private void getPorts() {
        List<String> ports = App.getPos().listPorts();
        ObservableList<String> items = FXCollections.observableArrayList(ports);

        if(!items.isEmpty()) {
            cmbListPorts.setItems(items);
            return;
        }

        cmbListPorts.setPromptText("No hay puertos");
    }

    @FXML
    private void addCombo() {
        total += 5000;
        txtAmount.setText("$ " + total);
    }

    @FXML
    private void addHamburguesa() {
        total += 3500;
        txtAmount.setText("$ " + total);
    }

    @FXML
    private void addTaco() {
        total += 2000;
        txtAmount.setText("$ " + total);
    }

    @FXML
    private void addCoffee() {
        total += 1000;
        txtAmount.setText("$ " + total);
    }

    @FXML
    private void resetSale() {
        total = 0;
        txtAmount.setText("$ " + total);
        txtVoucher.setText("");
    }

    @FXML
    private void doSale() {
        if (total > 0) {
            int randomTicketNumber = getRandomTicket();
            String randomTicket = randomTicketNumber + "T";
            txtVoucher.setText(randomTicket);
            BusinessRunnable actualBusinessLogic = new BusinessRunnable() {
                @Override
                public void run() {
                    try {
                        SaleResponse sale = App.getPos().sale(total, randomTicket, true);
                        setData(sale);
                    } catch (TransbankSaleException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void updateInterface() {
                    SaleResponse sale = (SaleResponse) data;
                    txtAreaRegister.setText(sale.toString());
                    lblStatusMessage.setText(sale.getResponseMessage());
                }

            };
            makeTheUserLookAtThePOS(actualBusinessLogic, "Debe realizar la venta en el POS!");
        } else {
            moveTextField(txtAmount);
        }
    }

    @FXML
    private void onRefund() {
        try {
            int operationNumber = Integer.parseInt(txtRefundTicket.getText());
            if (operationNumber > 0) {
                BusinessRunnable actualBusinessLogic = new BusinessRunnable() {
                    @Override
                    public void run() {
                        try {
                            RefundResponse refund = App.getPos().refund(operationNumber);
                            setData(refund);
                        } catch (TransbankRefundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void updateInterface() {
                        RefundResponse refund = (RefundResponse) data;
                        txtAreaRegister.setText(refund.toString());
                        lblStatusMessage.setText(refund.getResponseMessage());
                    }

                };
                makeTheUserLookAtThePOS(actualBusinessLogic, "Debe realizar la devolución en el POS!");
            } else {
                moveTextField(txtRefundTicket);
            }
        }
        catch (NumberFormatException e) {
            moveTextField(txtRefundTicket);
        }
    }

    private void makeTheUserLookAtThePOS(BusinessRunnable actualBusinessLogic, String text) {
        alert.setTitle("Advertencia");
        alert.setHeaderText(text);
        //Hack. Escondemos el botón de "ok"
        alert.getDialogPane().lookupButton(ButtonType.OK).setScaleX(0.0);
        alert.getDialogPane().lookupButton(ButtonType.OK).setScaleY(0.0);
        alert.show();

        everything.setDisable(true);
        Runnable r = () -> {
            try {
                actualBusinessLogic.run();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> {
                    alert.close();
                    actualBusinessLogic.updateInterface();
                    everything.setDisable(false);
                });
            }
        };
        new Thread(r).start();
    }

    private void moveTextField(TextField textField) {
        SequentialTransition seq = new SequentialTransition();
        double angle = 3.0;
        double duration = 40;
        float expand = 1.1f;
        float contract = 0.9f;
        boolean first = true;
        for (int index = 0; index < 8; index++) {
            {
                RotateTransition rotateTransition = new RotateTransition(Duration.millis(duration), textField);
                rotateTransition.setByAngle(first ? angle : 2 * angle);
                seq.getChildren().add(rotateTransition);
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), textField);
                scaleTransition.setToX(expand);
                scaleTransition.setToY(contract);
                seq.getChildren().add(scaleTransition);
            }
            {
                RotateTransition rotateTransition = new RotateTransition(Duration.millis(duration), textField);
                rotateTransition.setByAngle(-2 * angle);
                seq.getChildren().add(rotateTransition);
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), textField);
                scaleTransition.setToX(contract);
                scaleTransition.setToY(expand);
                seq.getChildren().add(scaleTransition);
            }
            first = false;
        }
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(duration), textField);
        rotateTransition.setToAngle(0.0f);
        seq.getChildren().add(rotateTransition);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), textField);
        scaleTransition.setToX(1.0f);
        scaleTransition.setToY(1.0f);
        seq.getChildren().add(scaleTransition);
        seq.play();
    }

    private int getRandomTicket() {
        int low = 1;
        int high = 99999;
        return rng.nextInt(high - low) + low;
    }

    @FXML
    private void closePort() {
        toggleAllPane(true);
        App.getPos().closePort();
        lblSelectedPort.setText("Desconectado");
        btnConnect.setText("Conectar");
        lblStatusMessage.setText("");
        connectState = false;
    }

    @FXML
    private void openPort(String portName) {
        print("+ abriendo puerto: " + portName);
        try {
            App.getPos().openPort(portName);
            lblSelectedPort.setText(portName);
            connectState = true;
            btnConnect.setText("Desconectar");
            print("+ puerto abierto");
            boolean polled = App.getPos().poll();
            txtAreaRegister.setText("Polled: " + polled);
            btnConnect.setDisable(false);
            toggleAllPane(!polled);
            if (!polled) {
                showAlert("El puerto no respondió.");
            }
        } catch (Exception e) {
            txtAreaRegister.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String text) {
        Alert al = new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("Advertencia");
        al.setHeaderText(text);
        al.showAndWait();
    }

    @FXML
    private void onPollPort() {
        print("+ polling port");
        try {
            boolean polled = App.getPos().poll();
            String resultText = String.format("polled: %s", polled ? "Ok" : "NOk");
            print(resultText);
            txtAreaRegister.setText(resultText);
            toggleAllPane(!polled);
        } catch (TransbankException e) {
            print("Error when polling");
            e.printStackTrace();
        }
    }

    private void toggleAllPane(boolean disable) {
        salePane.setDisable(disable);
        menuPane.setDisable(disable);
        otherPane.setDisable(disable);
        refundPane.setDisable(disable);
    }

    @FXML
    private void onBusinessClose() {
        try {
            CloseResponse closeResponse = App.getPos().close();
            txtAreaRegister.setText(closeResponse.toString());
            lblStatusMessage.setText(closeResponse.getResponseMessage());
        } catch (TransbankCloseException e) {
            print("Error when closing the day.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onTransactionList() {
        try {
            List<DetailResponse> detailResponse = App.getPos().details(false);
            if (detailResponse.isEmpty()) {
                txtAreaRegister.setText("No hay transacciones previas.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (DetailResponse dr: detailResponse) {
                sb.append(dr.toString()).append("\n");
            }
            txtAreaRegister.setText(sb.toString());
        } catch (TransbankDetailException e) {
            print("Error when get transaction list.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onKeysLoad() {
        try {
            LoadKeysResponse loadKeysResponse = App.getPos().loadKeys();
            txtAreaRegister.setText(loadKeysResponse.toString());
            lblStatusMessage.setText(loadKeysResponse.getResponseMessage());
        } catch (TransbankLoadKeysException e) {
            print("Error in load keys.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onCmbListPortsChanged() {
        selectedPort = cmbListPorts.getSelectionModel().getSelectedItem();
        btnConnect.setDisable(false);
    }

    @FXML
    private void onSetNormalMode() {
        print("+ set normal mode");
        try {
            boolean result = App.getPos().setNormalMode();
            String resultText = String.format("normal mode: %s", result ? "Ok" : "NOk");
            print(resultText);
            txtAreaRegister.setText(resultText);
            toggleAllPane(!result);
        } catch (TransbankException e) {
            print("Error when change to normal mode");
            e.printStackTrace();
        }
    }

    @FXML
    private void onTotalSale() {
        try {
            TotalsResponse totalsResponse = App.getPos().totals();
            txtAreaRegister.setText(totalsResponse.toString());
            lblStatusMessage.setText(totalsResponse.getResponseMessage());
        } catch (TransbankTotalsException e) {
            print("Error in total sale.");
            e.printStackTrace();
        }
    }

    private void print(String text) {
        System.out.println(text);
    }
}

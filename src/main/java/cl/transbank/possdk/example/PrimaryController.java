package cl.transbank.possdk.example;

import cl.transbank.pos.exceptions.TransbankException;
import cl.transbank.pos.exceptions.TransbankPortNotConfiguredException;
import cl.transbank.pos.helper.StringUtils;
import cl.transbank.pos.responses.CloseResponse;
import cl.transbank.pos.responses.DetailResponse;
import cl.transbank.pos.responses.KeysResponse;
import cl.transbank.pos.responses.RefundResponse;
import cl.transbank.pos.responses.SaleResponse;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

public class PrimaryController {

    private int total = 0;

    private Random rng = new Random();

    @FXML
    private Label operationLabel;

    @FXML
    private TextField operationNumber;

    @FXML
    private Label responseCode;

    @FXML
    private Label responseMessage;

    @FXML
    private VBox listPorts;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField monto;

    @FXML
    private TextField boleta;

    @FXML
    private TextField refundTicket;

    @FXML
    private TextField usedPort;

    @FXML
    private Pane menuPane;

    @FXML
    private Pane ventaPane;

    @FXML
    private Pane devolucionPane;

    @FXML
    private Pane otrosPane;

    @FXML
    private Button cerrarPuertoButton;

    @FXML
    private VBox everything;

    @FXML
    private void addPorts() {
        listPorts.setSpacing(10.0);
        listPorts.getChildren().clear();
        try {
            List<String> ports = App.getPos().listPorts();
            for (String portName : ports) {
                Button button = new Button("Usar puerto " + portName);
                button.setOnAction(new EventHandler<ActionEvent>() {
                                       @Override
                                       public void handle(ActionEvent actionEvent) {
                                           openPort(portName);
                                       }
                                   }
                );
                listPorts.getChildren().add(button);
            }
        } catch (TransbankException e) {
            System.out.println("ports error: " + e);
            e.printStackTrace();
        }
    }

    @FXML
    private void addCombo() {
        total += 5000;
        monto.setText("$ " + total);
        showOperation(false);
    }

    @FXML
    private void addHamburguesa() {
        total += 3500;
        monto.setText("$ " + total);
        showOperation(false);
    }

    @FXML
    private void addTaco() {
        total += 2000;
        monto.setText("$ " + total);
        showOperation(false);
    }

    @FXML
    private void addCoffee() {
        total += 1000;
        monto.setText("$ " + total);
        showOperation(false);
    }

    @FXML
    private void resetSale() {
        total = 0;
        monto.setText("$ " + total);
        boleta.setText("");
        showOperation(false);
    }

    @FXML
    private void doSale() {
        if (total > 0) {
            int randomTicketNumber = getRandomTicket();
            String randomTicket = randomTicketNumber + "T";
            boleta.setText(StringUtils.padStr(randomTicket, 6));
            BusinessRunnable actualBusinessLogic = new BusinessRunnable() {
                @Override
                public void run() {
                    try {
                        SaleResponse sale = App.getPos().sale(total, randomTicket);
                        setData(sale);
                    } catch (TransbankPortNotConfiguredException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void updateInterface() {
                    SaleResponse sale = (SaleResponse) data;
                    textArea.setText(sale.toString());
                    responseCode.setText(sale.getResponseCode() + "");
                    responseMessage.setText(sale.getResponseMessage());
                    if (sale.isSuccessful()) {
                        showOperation(true);
                        operationNumber.setText(sale.getOperationNumber() + "");
                    }
                }

            };
            makeTheUserLookAtThePOS(actualBusinessLogic, "Debe realizar la venta en el POS!");
        } else {
            moveMonto(monto);
        }
    }

    private void showOperation(boolean doShow) {
        operationLabel.setVisible(doShow);
        operationNumber.setVisible(doShow);
    }

    @FXML
    private void onRefund() {
        int ticket = StringUtils.parseInt(refundTicket.getText());
        if (ticket > 0) {
            showOperation(false);
            BusinessRunnable actualBusinessLogic = new BusinessRunnable() {
                @Override
                public void run() {
                    try {
                        RefundResponse refund = App.getPos().refund(ticket);
                        setData(refund);
                    } catch (TransbankPortNotConfiguredException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void updateInterface() {
                    RefundResponse refund = (RefundResponse) data;
                    textArea.setText(refund.toString());
                    responseCode.setText(refund.getResponseCode() + "");
                    responseMessage.setText(refund.getResponseMessage());
                }

            };
            makeTheUserLookAtThePOS(actualBusinessLogic, "Debe realizar la devolucion en el POS!");
        } else {
            moveMonto(refundTicket);
        }
    }

    private void makeTheUserLookAtThePOS(BusinessRunnable actualBusinessLogic, String text) {
        showOperation(false);

        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText(text);
        //hack. Escondemos el boton de "ok"
        alert.getDialogPane().lookupButton(ButtonType.OK).setScaleX(0.0);
        alert.getDialogPane().lookupButton(ButtonType.OK).setScaleY(0.0);
        alert.show();
        everything.setDisable(true);
        Runnable r = new Runnable() {
            @Override
            public void run() {
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
            }
        };
        new Thread(r).start();
    }

    private void moveMonto(TextField textField) {
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
    private void cerrarPuerto() {
        togglearTodo(true);
        usedPort.setText("");
        App.getPos().closePort();
    }

    private void openPort(String portName) {
        System.out.println("+ abriendo puerto: " + portName);
        try {
            App.getPos().openPort(portName);
            usedPort.setText(portName);
            System.out.println("+ puerto abierto");
            boolean polled = App.getPos().poll();
            textArea.setText("Polled: " + polled);
            cerrarPuertoButton.setDisable(false);
            togglearTodo(!polled);
            if (!polled) {
                showAlert("El puerto no respondio.");
            }
        } catch (Exception e) {
            textArea.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    @FXML
    private void pollPort() {
        System.out.println("+ polling port");
        try {
            boolean polled = App.getPos().poll();
            System.out.println("polled: " + polled);
            textArea.setText("Polled: " + polled);
            togglearTodo(!polled);
        } catch (TransbankPortNotConfiguredException e) {
            System.out.println("Error when polling");
            e.printStackTrace();
        }
    }

    private void togglearTodo(boolean disable) {
        ventaPane.setDisable(disable);
        menuPane.setDisable(disable);
        otrosPane.setDisable(disable);
        devolucionPane.setDisable(disable);
        cerrarPuertoButton.setDisable(disable);
    }

    @FXML
    private void onBusinessClose() {
        try {
            CloseResponse closeResponse = App.getPos().close();
            textArea.setText(closeResponse.toString());
            responseCode.setText(closeResponse.getResponseCode() + "");
            responseMessage.setText(closeResponse.getResponseMessage());
        } catch (TransbankPortNotConfiguredException e) {
            System.out.println("Error when closing the day.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onTransactionList() {
        try {
            List<DetailResponse> detailResponse = App.getPos().details(false);
            if (detailResponse.isEmpty()) {
                textArea.setText("No hay transacciones previas.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (DetailResponse dr: detailResponse) {
                sb.append(dr.toString() + "\n");
            }
            textArea.setText(sb.toString());
        } catch (TransbankPortNotConfiguredException e) {
            System.out.println("Error when closing the day.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onKeysLoad() {
        try {
            KeysResponse keysResponse = App.getPos().loadKeys();
            textArea.setText(keysResponse.toString());
            responseCode.setText(keysResponse.getResponseCode() + "");
            responseMessage.setText(keysResponse.getResponseMessage());
        } catch (TransbankPortNotConfiguredException e) {
            System.out.println("Error when closing the day.");
            e.printStackTrace();
        }
    }

}

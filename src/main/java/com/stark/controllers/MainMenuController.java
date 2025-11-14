package com.stark.controllers;

import com.stark.entities.ModbusEntity;
import com.stark.services.ModbService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MainMenuController {

    @FXML private Button btnMotor1;
    @FXML private Button btnStart;
    @FXML private Button btnReset;

    @FXML private Circle ledMotor1;

    private final ModbService modbus = new ModbService();

    private boolean motorState = false;

    @FXML
    public void initialize() {

        try {
            ModbusEntity entity = new ModbusEntity();
            entity.setPort("/dev/ttyUSB0");
            entity.setBaudRate(19200);
            entity.setDataBits(8);
            entity.setStopBits(1);
            entity.setParity("E");

            modbus.connect(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lê estado do motor a cada 300ms
        startReadingLoop();

        // Ação do Motor 1
        btnMotor1.setOnAction(e -> {
            try {
                motorState = !motorState;
                modbus.writeHolding(1, 0, motorState ? 1 : 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnStart.setOnAction(e -> {
            try {
                modbus.writeHolding(1, 1, 1); // START
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnReset.setOnAction(e -> {
            try {
                modbus.writeHolding(1, 2, 1); // RESET
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }


    private void startReadingLoop() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    int value = modbus.readHolding(1, 0); // lê M1

                    Platform.runLater(() -> {
                        if (value == 1)
                            ledMotor1.setFill(Color.GREEN);
                        else
                            ledMotor1.setFill(Color.RED);
                    });

                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }
}

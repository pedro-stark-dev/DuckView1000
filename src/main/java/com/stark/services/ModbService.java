package com.stark.services;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import com.stark.entities.ModbusEntity;

public class ModbService {

    private SerialConnection connection;

    public void connect(ModbusEntity cfg) {
        try {
            connection = new SerialConnection(new SerialParameters() {{
                setPortName(cfg.getPort());
                setBaudRate(cfg.getBaudRate());
                setDatabits(cfg.getDataBits());
                setStopbits(cfg.getStopBits());
                setParity(cfg.getParity());
                setEncoding(Modbus.SERIAL_ENCODING_RTU);
                setEcho(false);
            }});

            System.out.println("➡ Abrindo porta: " + cfg.getPort());
            connection.open();
            System.out.println("✔ Porta aberta com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Falha ao abrir porta serial!");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }

    public int readHolding(int slave, int address) throws Exception {
        ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest(address, 1);
        req.setUnitID(slave);

        ModbusSerialTransaction trans = new ModbusSerialTransaction(connection);
        trans.setRequest(req);
        trans.execute();

        ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) trans.getResponse();
        return res.getRegister(0).getValue();
    }

    public void writeHolding(int slave, int address, int value) throws Exception {
        WriteSingleRegisterRequest req =
                new WriteSingleRegisterRequest(address, new SimpleRegister(value));

        req.setUnitID(slave);

        ModbusSerialTransaction trans = new ModbusSerialTransaction(connection);
        trans.setRequest(req);
        trans.execute();
    }
}

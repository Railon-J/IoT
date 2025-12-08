package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.fazecast.jSerialComm.SerialPort;

import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TelaController {

    private SerialPort porta;
    private InputStream entrada;
    private OutputStream saida;

    @FXML private Button btnAcionar;
    @FXML private Button btnDesativar;
    @FXML private Button btnConectar;
    @FXML private Label lblStatus;
    @FXML private Button btnDataHora;
    
    public boolean conectar() {// conectar com dispositivo na porta serial
        porta = SerialPort.getCommPort("COM9"); // ajuste conforme necessário
        porta.setBaudRate(9600);
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);//Configurar timeout de leitura
        
        if (porta.openPort()) {
            System.out.println("Porta aberta com sucesso!");
            lblStatus.setText("Porta aberta com sucesso!");
            saida = porta.getOutputStream();
            entrada = porta.getInputStream();
            
            return true;
        } else {
                desconectar();    
                conectar();
            System.out.println("Falha ao abrir porta.");
            lblStatus.setText("Falha ao abrir porta.");
            return false;
        }
    }

    @FXML
    public void initialize() {
        btnAcionar.setOnAction(e -> {
                enviarComando("ligar\n");
        });
        
        btnDataHora.setOnAction(e -> {
            atualizaDataHora();
        });
        
        btnDesativar.setOnAction(e -> {
            enviarComando("desligar\n");
        });

        btnConectar.setOnAction(e -> {
            if (porta == null || !porta.isOpen()) {
                if (conectar()) {
                    btnConectar.setText("Desconectar");
                    btnDataHora.setVisible(true);
                    new Thread(this::receberDados).start();
                }
            } else {
                desconectar();
                btnConectar.setText("Conectar");
                lblStatus.setText("Arduino Desconectado");
            }
        });
    }

    private void enviarComando(String comando) {// envia comando para porta com
        try {
            if (saida != null) {
                saida.write(comando.getBytes());
                saida.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void receberDados() {  // receber dados da porta serial
         StringBuilder acumulador = new StringBuilder();
        try {
            byte[] buffer = new byte[1024];
            while (porta != null && porta.isOpen()) {
                if (entrada.available() > 0) { // evita timeout (timeout é um limite de tempo definido para esperar uma operação terminar.)
                    int len = entrada.read(buffer);
                    if (len > 0) {
                        String recebido = new String(buffer, 0, len);
                        acumulador.append(recebido);
                        
                        // Verifica se chegou um fim de linha
                        int index;
                        while ((index = acumulador.indexOf("\n")) != -1) {
                            String mensagem = acumulador.substring(0, index).trim();
                            acumulador.delete(0, index + 1);


                            javafx.application.Platform.runLater(() -> {
                                    lblStatus.setText(mensagem);
                            });
                        }

                    }
                }
                Thread.sleep(50); // evita loop rodando em busy-wait
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void desconectar() {// desconecta porta serial
        try {
            if (entrada != null) entrada.close();
            if (saida != null) saida.close();
            if (porta != null && porta.isOpen()) porta.closePort();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void atualizaDataHora() {
        if (porta == null || !porta.isOpen()) {            
            lblStatus.setText("Arduino Desconectado!");
        } else {
        LocalDateTime agora = LocalDateTime.now(); // pega data/hora atual do PC    
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yy MM dd HH mm ss");
        String comando = "atualiza " + agora.format(fmt) + "\n";
        enviarComando(comando);
        btnDataHora.setVisible(false);
        }
    }

}
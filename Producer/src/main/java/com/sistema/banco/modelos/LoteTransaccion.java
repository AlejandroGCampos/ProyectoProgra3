package com.sistema.banco.modelos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoteTransaccion {
    public String loteId;
    public String fechaGeneracion;
    public List<Transaccion> transacciones;
}

//Se debe registrar en consola: idTransaccion, monto, estado9(ACEPTADO/RECHAZADO)
//debe evidenciarse en consola que cola esta siendo atendida y el ID de la soilicitud procesada.

//evidencias requeridas> Captura mostrando consumo de prioridad alta, de prioridad n
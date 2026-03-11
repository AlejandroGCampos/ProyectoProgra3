package com.sistema.banco.modelos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoteTransaccion {
    public String loteId;
    public String fechaGeneracion;
    public List<Transaccion> transacciones;
}

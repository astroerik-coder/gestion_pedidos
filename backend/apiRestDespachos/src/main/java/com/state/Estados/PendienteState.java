package com.state.Estados;

import com.example.demo.models.Despacho;
import com.example.demo.models.EstadoDespacho;
import com.state.EstadoDespachoState;


public class PendienteState implements EstadoDespachoState {
    @Override
    public boolean avanzar(Despacho d) {
        d.setEstado(EstadoDespacho.EN_PREPARACION);
        System.out.println("🔄 Despacho pasó a EN_PREPARACION.");
        return true;
    }

    @Override
    public boolean fallar(Despacho d) {
        d.setEstado(EstadoDespacho.FALLIDO);
        System.out.println("❌ Despacho falló desde PENDIENTE.");
        return true;
    }

    @Override
    public boolean reiniciar(Despacho d) {
        System.out.println("⚠️ Ya está en PENDIENTE.");
        return false;
    }
}
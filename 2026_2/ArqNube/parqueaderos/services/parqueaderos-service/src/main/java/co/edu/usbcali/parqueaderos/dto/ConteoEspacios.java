package co.edu.usbcali.parqueaderos.dto;

public record ConteoEspacios(long total, long disponibles, long ocupados, long fueraDeServicio) {

    public static final ConteoEspacios VACIO = new ConteoEspacios(0, 0, 0, 0);

    public ConteoEspacios sumar(ConteoEspacios otro) {
        return new ConteoEspacios(total + otro.total, disponibles + otro.disponibles,
                ocupados + otro.ocupados, fueraDeServicio + otro.fueraDeServicio);
    }
}

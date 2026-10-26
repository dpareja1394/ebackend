import { formatearHorario } from './parqueadero.model';

describe('formatearHorario', () => {
  it('muestra 24 h si abre todo el día', () => {
    expect(formatearHorario({ abierto24h: true, horaApertura: null, horaCierre: null })).toBe('24 h');
  });

  it('muestra el rango en horario diurno', () => {
    expect(formatearHorario({ abierto24h: false, horaApertura: '06:00', horaCierre: '22:00' })).toBe('06:00–22:00');
  });

  it('marca el cierre al día siguiente', () => {
    expect(formatearHorario({ abierto24h: false, horaApertura: '18:00', horaCierre: '02:00' })).toBe(
      '18:00–02:00 (+1 día)',
    );
  });
});

import { normalizarPlaca } from './vehiculo.model';

describe('normalizarPlaca', () => {
  it('usa la misma normalización que el servidor', () => {
    expect(normalizarPlaca(' abc-123 ')).toBe('ABC123');
    expect(normalizarPlaca('a b-c')).toBe('ABC');
    expect(normalizarPlaca('')).toBe('');
  });
});

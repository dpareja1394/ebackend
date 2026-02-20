import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ObtenerInvitados } from './obtener-invitados';

describe('ObtenerInvitados', () => {
  let component: ObtenerInvitados;
  let fixture: ComponentFixture<ObtenerInvitados>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ObtenerInvitados]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ObtenerInvitados);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

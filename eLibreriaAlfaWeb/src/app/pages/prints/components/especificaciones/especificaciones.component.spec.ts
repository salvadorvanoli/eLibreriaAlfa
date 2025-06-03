import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EspecificacionesComponent } from './especificaciones.component';

describe('EspecificacionesComponent', () => {
  let component: EspecificacionesComponent;
  let fixture: ComponentFixture<EspecificacionesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EspecificacionesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EspecificacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

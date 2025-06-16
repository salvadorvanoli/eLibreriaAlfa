import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestrictedOverlayComponent } from './restricted-overlay.component';

describe('RestrictedOverlayComponent', () => {
  let component: RestrictedOverlayComponent;
  let fixture: ComponentFixture<RestrictedOverlayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestrictedOverlayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestrictedOverlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

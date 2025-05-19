import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoOrderYetComponent } from './no-order-yet.component';

describe('NoOrderYetComponent', () => {
  let component: NoOrderYetComponent;
  let fixture: ComponentFixture<NoOrderYetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoOrderYetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NoOrderYetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

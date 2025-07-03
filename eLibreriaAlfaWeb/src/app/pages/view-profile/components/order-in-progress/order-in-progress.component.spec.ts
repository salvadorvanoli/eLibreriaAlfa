import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderInProgressComponent } from './order-in-progress.component';

describe('OrderInProgressComponent', () => {
  let component: OrderInProgressComponent;
  let fixture: ComponentFixture<OrderInProgressComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderInProgressComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderInProgressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

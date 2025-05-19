import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecifyOrderComponent } from './specify-order.component';

describe('SpecifyOrderComponent', () => {
  let component: SpecifyOrderComponent;
  let fixture: ComponentFixture<SpecifyOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpecifyOrderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SpecifyOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

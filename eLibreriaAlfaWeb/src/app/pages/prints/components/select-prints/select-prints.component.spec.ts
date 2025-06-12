import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectPrintsComponent } from './select-prints.component';

describe('SelectPrintsComponent', () => {
  let component: SelectPrintsComponent;
  let fixture: ComponentFixture<SelectPrintsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectPrintsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectPrintsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

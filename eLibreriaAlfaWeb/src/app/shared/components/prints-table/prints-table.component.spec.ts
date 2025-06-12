import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrintsTableComponent } from './prints-table.component';

describe('PrintsTableComponent', () => {
  let component: PrintsTableComponent;
  let fixture: ComponentFixture<PrintsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PrintsTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrintsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

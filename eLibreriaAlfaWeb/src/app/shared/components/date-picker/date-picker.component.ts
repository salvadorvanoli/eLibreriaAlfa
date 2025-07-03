import { Component, forwardRef, Input } from '@angular/core';
import { DatePickerModule } from 'primeng/datepicker';
import { FormsModule, NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';

@Component({
    selector: 'app-date-picker',
    templateUrl: './date-picker.component.html',
    standalone: true,
    imports: [DatePickerModule, FormsModule],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => DatePickerComponent),
            multi: true
        }
    ]
})
export class DatePickerComponent implements ControlValueAccessor {
    @Input() minDate: Date | null = null; 
    @Input() maxDate: Date | null = null; 
    
    date: Date | null = null;
    disabled = false;
    
    constructor() {
        this.minDate = new Date();
        this.minDate.setHours(0, 0, 0, 0); 
    }
    
    onChange = (_: any) => {};
    onTouched = () => {};
    
    writeValue(value: Date | null): void {
        this.date = value;
    }
    
    registerOnChange(fn: any): void {
        this.onChange = fn;
    }
    
    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }
    
    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }
    
    onDateSelect(event: Date | null) {
        this.date = event;
        this.onChange(this.date);
        this.onTouched();
    }
}
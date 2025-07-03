import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-modal',
    templateUrl: './modal.component.html',
    standalone: true,
    imports: [CommonModule, DialogModule, ButtonModule]
})
export class ModalComponent {
    @Input() visible: boolean = false;
    @Input() style: any = {};
    @Input() breakpoints: any = {};
    @Input() classes: string = '';
    @Input() actionClasses: string = '';
    
    @Output() closed = new EventEmitter<void>();
    
    close() {
        this.closed.emit();
    }
}
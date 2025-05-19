import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-no-order-yet',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './no-order-yet.component.html',
  styleUrls: ['./no-order-yet.component.scss']
})
export class NoOrderYetComponent {}

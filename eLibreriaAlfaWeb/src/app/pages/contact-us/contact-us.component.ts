import { Component } from '@angular/core';
import { TitleAndDescriptionComponent } from '../../shared/components/title-and-description/title-and-description.component';
import { ContactUsFormComponent } from './components/contact-us-form/contact-us-form.component';

@Component({
  selector: 'app-contact-us',
  imports: [ 
    TitleAndDescriptionComponent,
    ContactUsFormComponent
  ],
  templateUrl: './contact-us.component.html',
  styleUrls: ['./contact-us.component.scss']
})
export class ContactUsComponent {

}

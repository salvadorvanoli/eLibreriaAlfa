import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileFormComponent } from './components/profile-form/profile-form.component';
import { TitleComponent } from './components/title/title.component';
import { NoOrderYetComponent } from './components/no-order-yet/no-order-yet.component';
import { OrderInProgressComponent } from './components/order-in-progress/order-in-progress.component';
import { OrderSubmittedComponent } from './components/order-submitted/order-submitted.component';
import { SelectProfileComponent } from './components/select-profile/select-profile.component';
import { OrderService, EncargueEstado } from '../../core/services/order.services';
import { SecurityService } from '../../core/services/security.service';
import { switchMap, catchError, map } from 'rxjs/operators';
import { of, timer, forkJoin } from 'rxjs';
import { OrderTableComponent } from '../../shared/components/order-table/order-table.component';

@Component({
  selector: 'app-view-profile',
  standalone: true,
  imports: [
    CommonModule,
    ProfileFormComponent,
    TitleComponent,
    NoOrderYetComponent,
    OrderInProgressComponent,
    OrderSubmittedComponent,
    SelectProfileComponent,
    OrderTableComponent
  ],
  templateUrl: './view-profile.component.html',
  styles: [`
    :host {
      width: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1.5rem;
      padding: 0.5rem 0;
    }
  `]
})
export class ViewProfileComponent implements OnInit {
  orderState: 'none' | 'in-progress' | 'submitted' | 'loading' = 'loading';
  selectedSection: string = 'info'; 
  
  constructor(
    private orderService: OrderService,
    private securityService: SecurityService
  ) {}
  
  ngOnInit() {
    this.checkOrderState();
  }
  
  updateToLoading() {
    console.log('ViewProfileComponent: Actualizando estado a "loading"');
    this.orderState = 'loading';
    
  
    setTimeout(() => {
      this.updateToNoProducts();
    }, 800); 
  }
  
  updateToNoProducts() {
    console.log('ViewProfileComponent: Actualizando estado a "none" porque no hay productos');
    this.orderState = 'none';
  }
  
  updateToSubmitted() {
    console.log('ViewProfileComponent: Actualizando estado a "submitted" porque se ha enviado el pedido');
    this.orderState = 'loading';
    
    setTimeout(() => {
      this.orderState = 'submitted';
    }, 800);
  }

  onProfileOptionSelected(option: string) {
    this.selectedSection = option;
  }

  checkOrderState() {
    this.orderState = 'loading'; 

    const minLoaderTime = 500; 

    const orderCheckLogic$ = this.securityService.getActualUser().pipe(
      catchError(error => {
        console.error('Error obteniendo usuario:', error);
        return of(null);
      }),
      switchMap(usuarioRaw => {
        const usuario = usuarioRaw as { id?: number };
        if (!usuario || !usuario.id) {
          console.log('No hay usuario autenticado o falta ID');
          return of(null);
        }

        console.log('Usuario obtenido:', usuario.id);
        return this.orderService.usuarioTieneEncargueEnCreacion(usuario.id).pipe(
          catchError(error => {
            console.error('Error verificando encargue en creación:', error);
            return of(null);
          }),
          switchMap(tieneEncargueEnCreacion => {
            if (tieneEncargueEnCreacion === null) {
              return of(null);
            }
            console.log('¿Tiene encargue en creación?', tieneEncargueEnCreacion);

            if (!tieneEncargueEnCreacion) {
              return of('submitted' as const);
            }

            return this.orderService.listarProductosEncarguePorUsuarioYEstado(
              usuario.id!!,
              EncargueEstado.EN_CREACION,
              0,
              1
            ).pipe(
              map(response => {
                console.log('Productos encontrados en EN_CREACION:', response.totalElements);
                if (response.totalElements > 0) {
                  return 'in-progress' as const;
                } else {
                  return 'none' as const;
                }
              }),
              catchError(error => {
                console.error('Error obteniendo productos del encargue EN_CREACION:', error);
                return of(null);
              })
            );
          })
        );
      })
    );

    forkJoin([
      orderCheckLogic$,
      timer(minLoaderTime)
    ]).subscribe({
      next: ([determinedState, _timerResult]) => {
        console.log('Estado determinado por la lógica:', determinedState);
        this.orderState = determinedState as 'none' | 'in-progress' | 'submitted';
        console.log('Estado final después de forkJoin:', this.orderState);
      },
      error: (error) => {
        console.error('Error en la subscripción principal de forkJoin:', error);
        this.orderState = 'none'; 
      }
    });
  }
}
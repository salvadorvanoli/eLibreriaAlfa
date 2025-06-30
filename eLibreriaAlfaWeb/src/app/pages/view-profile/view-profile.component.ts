import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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
import { UsuarioSimple } from '../../core/models/usuario';

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
  usuario: UsuarioSimple | null = null;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private securityService: SecurityService
  ) {}
  
  ngOnInit() {
    this.securityService.getActualUser().subscribe({
      next: (usuario) => {
        if (usuario) {
          this.usuario = usuario;
        }
      },
      error: (error) => {
        console.error('Error al obtener el usuario actual:', error);
      }
    });

    this.route.queryParams.subscribe(params => {
      const section = params['section'];
      if (section && ['info', 'actual', 'historial'].includes(section)) {
        this.selectedSection = section;
      }
    });

    this.checkOrderState();
  }
  
  updateToLoading() {
    this.orderState = 'loading';
    
  
    setTimeout(() => {
      this.updateToNoProducts();
    }, 800); 
  }
  
  updateToNoProducts() {
    this.orderState = 'none';
  }
  
  updateToSubmitted() {
    this.orderState = 'loading';
    
    setTimeout(() => {
      this.orderState = 'submitted';
    }, 800);
  }

  onProfileOptionSelected(option: string) {
    this.selectedSection = option;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { section: option },
      queryParamsHandling: 'merge'
    });
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
          return of(null);
        }

        return this.orderService.usuarioTieneEncargueEnCreacion(usuario.id).pipe(
          catchError(error => {
            console.error('Error verificando encargue en creación:', error);
            return of(null);
          }),
          switchMap(tieneEncargueEnCreacion => {
            if (tieneEncargueEnCreacion === null) {
              return of(null);
            }

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
        this.orderState = determinedState as 'none' | 'in-progress' | 'submitted';
      },
      error: (error) => {
        console.error('Error en la subscripción principal de forkJoin:', error);
        this.orderState = 'none'; 
      }
    });
  }
}
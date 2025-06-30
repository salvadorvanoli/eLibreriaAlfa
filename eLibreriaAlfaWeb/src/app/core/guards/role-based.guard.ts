import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, map } from 'rxjs';
import { SecurityService } from '../services/security.service';
import { RoleGuardConfig } from '../models/role-guard-config';


@Injectable({
  providedIn: 'root'
})
export class RoleBasedGuard implements CanActivate {
  
  constructor(
    private securityService: SecurityService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const config: RoleGuardConfig = route.data['roleConfig'] || {};
    const {
      allowedRoles,
      blockedRoles,
      requireAuth = true,
      redirectTo = '/inicio'
    } = config;

    return this.securityService.getActualUser().pipe(
      map(user => {
        if (requireAuth && !user) {
          this.router.navigate(['/inicio-sesion']);
          return false;
        }

        if (!requireAuth && !user) {
          return true;
        }

        if (user && !user.rol && requireAuth) {
          this.router.navigate(['/inicio-sesion']);
          return false;
        }

        if (blockedRoles && user?.rol && blockedRoles.includes(user.rol)) {
          this.router.navigate([redirectTo]);
          return false;
        }

        if (allowedRoles && user?.rol && !allowedRoles.includes(user.rol)) {
          this.router.navigate([redirectTo]);
          return false;
        }

        return true;
      })
    );
  }
}

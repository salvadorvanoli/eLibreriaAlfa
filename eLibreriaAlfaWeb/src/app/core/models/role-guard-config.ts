import { Rol } from './rol';

export interface RoleGuardConfig {
  allowedRoles?: Rol[];
  blockedRoles?: Rol[];
  requireAuth?: boolean;
  redirectTo?: string;
}

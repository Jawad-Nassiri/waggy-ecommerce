import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, of } from 'rxjs';
import { API_URL } from '../core/api.config';

export const resetPasswordGuard: CanActivateFn = (route) => {
  const http = inject(HttpClient);
  const router = inject(Router);

  const token = route.queryParamMap.get('token');

  if (!token) {
    return router.createUrlTree(['/login']);
  }

  return http.get(`${API_URL}/auth/validate-reset-token?token=${token}`).pipe(
    map(() => true),
    catchError(() => of(router.createUrlTree(['/login']))),
  );
};
